package com.miniblas.iu.controllers;

import com.arcadio.common.VariablesList;
import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.fragments.base.CabOrdenableElementsFragment;
import com.miniblas.model.MiniBlasCesta;
import com.miniblas.model.MiniBlasItemVariable;
import com.miniblas.persistence.BdException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VariablesController extends BaseController<MiniBlasItemVariable> implements ObservadorVariables.IObservadorVariables, ConnectionIconListener.ObservadorConnectionIcon{

	// private ProfilesFragment profilesView;
	public static VariablesController instance;
	private int id_profile;
	private MiniBlasCesta basket;

	public static VariablesController getInstance(AplicacionPrincipal _aplicacionPrincipal){
		if(instance == null){
			instance = new VariablesController(_aplicacionPrincipal);
		}
		return instance;
	}

	private VariablesController(AplicacionPrincipal _aplicacionPrincipal){
		super(_aplicacionPrincipal);
	}

	@Override
	public void resetController(){

	}

	public void onViewChange(CabOrdenableElementsFragment _vista, int _id_profile, int _id_basket){
		this.id_profile = _id_profile;
		if(basket != null){
			try{
				application.getArcadioService().deleteBag(basket.getNombre());
			}catch(com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException e){
				e.printStackTrace();
			}catch(com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException e){
				e.printStackTrace();
			}
		}
		try{
			basket = application.getBasketStorage().getBasketById(_id_basket);
			basket.setPerfil(application.getProfileStorage().getProfileByid(basket.getPerfil().getId()));
		}catch(BdException e){
			e.printStackTrace();
		}
		if(basket != null){
			application.connect(_id_profile);
			application.deleteAllVariablesObservers();
			application.setVariablesObserver(this);
			try{
				application.getArcadioService().createBag(basket.getNombre());
				application.getArcadioService().setBagPeriod(basket.getNombre(),basket.getPeriodoRefresco());
			}catch(com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException e){
				e.printStackTrace();
			}catch(com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException e){
				e.printStackTrace();
			}
			super.onViewChange(_vista);
		}
		application.setIconObserver(this);
	}

	@Override
	public void saveElements(){
		if(basket != null){
			try{
				application.getArcadioService().deleteBag(basket.getNombre());
			}catch(com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException e){
				e.printStackTrace();
			}catch(com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException e){
				e.printStackTrace();
			}
		}
		super.saveElements();
		application.deleteVariablesObserver(this);
	}


	@Override
	protected void loadPreferences(){
	}

	@Override
	protected List<MiniBlasItemVariable> getElementsToRepository() throws BdException{
		ArrayList<MiniBlasItemVariable> variables = basket.getVariables();
		Collections.sort(variables);
		//conexion con cosme
		List<String> listaNombreVariables = new ArrayList<String>();
		for(MiniBlasItemVariable variable : variables){
			listaNombreVariables.add(variable.getNombre());
		}
		System.out.println("estas son las variables: " + listaNombreVariables);
		if(!listaNombreVariables.isEmpty()){
			try{
				application.getArcadioService().addNamesToBag(basket.getNombre(), listaNombreVariables);
			}catch(com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException e){
				e.printStackTrace();
			}catch(com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException e){
				e.printStackTrace();
			}
		}
		return variables;
	}

	@Override
	protected void saveElementsToRepository(List<MiniBlasItemVariable> _elements) throws BdException{
		application.getVariableStorage().persistCollection(_elements);
	}

	@Override
	protected void deleteElements(List<MiniBlasItemVariable> elements) throws BdException{
		application.getVariableStorage().deleteItemVariables(elements);
	}

	@Override
	public void onNotifyVariables(String _nombreCesta, VariablesList _listaVariables){
			vista.getAdapter().getItem(0).setValor(String.valueOf(_listaVariables.getValue("ondacuadrada.seno")));
		//		vista.getAdapter().getItem(1).setValor(_listaVariables.get(1).getValor());
		//		vista.getAdapter().getItem(2).setValor(_listaVariables.get(2).getValor());
		//		for(MiniBlasItemVariable itemVariable:_listaVariables){
		//			vista.getAdapter().setItemByKey(itemVariable.getNombre(), element);
		//		}
		System.out.println(_listaVariables);
		vista.refreshList();
	}

	@Override
	public void OnConnect(){
		vista.setConnectIcon();
	}

	@Override
	public void OnDisconnect(){
		vista.setDisconnectIcon();
	}

	public int getIdProfile(){
		return id_profile;
	}

	public MiniBlasCesta getBasket(){
		return basket;
	}
}
