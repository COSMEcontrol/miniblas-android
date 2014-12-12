package com.miniblas.iu.controllers;

import android.util.SparseBooleanArray;

import com.arcadio.api.v1.service.CosmeStates;
import com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException;
import com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException;
import com.arcadio.common.NamesList;
import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.fragments.NewVariableElementsFragmentCab;
import com.miniblas.iu.fragments.VariablesElementsFragmentCab;
import com.miniblas.iu.fragments.base.CabOrdenableElementsFragment;
import com.miniblas.model.MiniBlasCesta;
import com.miniblas.model.MiniBlasItemVariable;
import com.miniblas.persistence.BdException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NewVariablesController extends BaseController<MiniBlasItemVariable> implements ObservadorState.IObservadorState{

	// private ProfilesFragment profilesView;
	public static NewVariablesController instance;
	private MiniBlasCesta basket;

	public static NewVariablesController getInstance(AplicacionPrincipal _aplicacionPrincipal){
		if(instance == null){
			instance = new NewVariablesController(_aplicacionPrincipal);
		}
		return instance;
	}

	private NewVariablesController(AplicacionPrincipal _aplicacionPrincipal){
		super(_aplicacionPrincipal);
	}

	@Override
	public void resetController(){

	}

	public void onViewChange(CabOrdenableElementsFragment _vista, int _id_profile, int _id_basket){
		try{
			basket = application.getBasketStorage().getBasketById(_id_basket);
			basket.setPerfil(application.getProfileStorage().getProfileById(basket.getPerfil().getId()));
		}catch(BdException e){
			e.printStackTrace();
		}
		super.onViewChange(_vista);
		try{
			application.getArcadioService().requestNamesList();
		}catch(com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException e){
			e.printStackTrace();
		}catch(com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException e){
			e.printStackTrace();
		}
		vista.showIconLoading();
		application.deleteAllStateObservers();
		application.setStateObserver(this);
		System.out.println("Estableciendo state observer");
	}


	@Override
	protected void loadPreferences(){
	}

	@Override
	protected List<MiniBlasItemVariable> getElementsToRepository() throws BdException{
		return new ArrayList<MiniBlasItemVariable>();
	}

	@Override
	protected void saveElementsToRepository(List<MiniBlasItemVariable> _elements) throws BdException{
		//application.getVariableStorage().persistCollection(_elements);
	}

	@Override
	protected void deleteElements(List<MiniBlasItemVariable> elements) throws BdException{
		//application.getVariableStorage().deleteItemVariables(elements);
	}

	public void guardarVariables(){
		final SparseBooleanArray sba = vista.getListView().getCheckedItemPositions();
		application.addSingleTask(new Runnable(){
			@Override
			public void run(){
				try{
					application.getVariableStorage().deleteItemVariables(basket.getVariables());
				}catch(BdException e){
					e.printStackTrace();
				}

				for(int i = 0; i < sba.size(); i++){
					if(sba.valueAt(i)){
						try{
							MiniBlasItemVariable variable = vista.getAdapter().getItem(sba.keyAt(i));
							variable.setCesta(basket);
							application.getVariableStorage().persist(variable);

						}catch(BdException e){
							e.printStackTrace();
						}
					}
				}
			}
		});
		((NewVariableElementsFragmentCab) vista).setResultIU(VariablesElementsFragmentCab.RESULT_OK);
	}

	@Override
	public void onNotifyNewState(CosmeStates _state){
		try{
			System.out.println("Recibido estado :" + _state);
			if(_state == CosmeStates.NAMES_LIST_RECEIVED){
				NamesList namesList = null;
				try{
					namesList = application.getArcadioService().getNamesList();
				}catch(ServiceDisconnectedArcadioException e){
					e.printStackTrace();
				}catch(NoConnectedArcadioException e){
					e.printStackTrace();
				}
				ArrayList<MiniBlasItemVariable> variablesList = new ArrayList<>();
				for(String variable : namesList.getNameList()){
					variablesList.add(new MiniBlasItemVariable(variable));
				}
				Comparator<MiniBlasItemVariable> comparator = new Comparator<MiniBlasItemVariable>(){

					@Override
					public int compare(MiniBlasItemVariable lhs, MiniBlasItemVariable rhs){
						return rhs.getNombre().compareTo(lhs.getNombre());
					}
				};
				Collections.sort(variablesList, comparator);
				vista.getAdapter().clearCollection();
				vista.getAdapter().clearSelection();
				vista.getAdapter().addAll(variablesList);
				//consultar base de datos para ver cuales tiene seleccionadas
				int i = 0;
				for(MiniBlasItemVariable variable : basket.getVariables()){
					if((i = ((NewVariableElementsFragmentCab) vista).getAdapter().indexOf(variable)) != -1){
						((NewVariableElementsFragmentCab) vista).addItemSelected(i);
					}
				}
				vista.refreshList();
				vista.dismissIconLoading();
			}
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}
}
