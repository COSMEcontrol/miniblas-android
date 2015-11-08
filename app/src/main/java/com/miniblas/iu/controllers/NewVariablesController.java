package com.miniblas.iu.controllers;

import com.arcadio.api.v1.service.CosmeStates;
import com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException;
import com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException;
import com.arcadio.common.NamesList;
import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.fragments.NewVariableElementsFragmentCab;
import com.miniblas.iu.fragments.VariablesElementsFragmentCab;
import com.miniblas.iu.fragments.base.CabOrdenableElementsFragment;
import com.miniblas.model.MiniBlasBag;
import com.miniblas.model.base.BaseElementList;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;
import com.miniblas.persistence.BdException;

import java.util.Collections;
import java.util.Comparator;
/**
 *
 * @author A. Azuara
 */
public class NewVariablesController extends BaseController<BaseVariableWidget> implements ObservadorState.IObservadorState{

	public static NewVariablesController instance;
	private MiniBlasBag bag;

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
			bag = application.getBasketStorage().getBagById(_id_basket);
			bag.setProfile(application.getProfileStorage().getProfileById(bag.getProfile().getId()));
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
	}


	@Override
	protected void loadPreferences(){
	}

	@Override
	protected BaseElementList<BaseVariableWidget> getElementsToRepository() throws BdException{
		return new BaseElementList<BaseVariableWidget>();
	}

	@Override
	protected void saveElementsToRepository(BaseElementList<BaseVariableWidget> _elements) throws BdException{
		//application.getVariableStorage().persistCollection(_elements);
	}

	@Override
	protected void deleteElements(BaseElementList<BaseVariableWidget> elements) throws BdException{
		//application.getVariableStorage().deleteItemVariables(elements);
	}

	public void guardarVariables(){
		BaseElementList<BaseVariableWidget> variableWidgetList = vista.getAdapter().getCurrentCheckedItems();
		for(BaseVariableWidget variable : variableWidgetList){
			variable.setBag(bag);
		}
		((NewVariableElementsFragmentCab)vista).setResultCode(VariablesElementsFragmentCab.RESULT_OK,variableWidgetList);

	}

	@Override
	public void onNotifyNewState(CosmeStates _state){
			if(_state == CosmeStates.NAMES_LIST_RECEIVED){
				NamesList namesList = null;
				try{
					namesList = application.getArcadioService().getNamesList();
				}catch(ServiceDisconnectedArcadioException e){
					e.printStackTrace();
				}catch(NoConnectedArcadioException e){
					e.printStackTrace();
				}
				BaseElementList variablesList = new BaseElementList();
				for(String variable_name : namesList.getNameList()){
					BaseVariableWidget var = new BaseVariableWidget(variable_name);
					variablesList.add(var);
				}
				Comparator<BaseVariableWidget> comparator = new Comparator<BaseVariableWidget>(){

					@Override
					public int compare(BaseVariableWidget lhs, BaseVariableWidget rhs){
						return rhs.getWidgetName().compareTo(lhs.getWidgetName());
					}
				};
				Collections.sort(variablesList, comparator);
				vista.getAdapter().clearCollection();
				vista.getAdapter().clearSelection();
				vista.getAdapter().addAll(variablesList);
				vista.refreshList();
				vista.dismissIconLoading();
			}
	}
}
