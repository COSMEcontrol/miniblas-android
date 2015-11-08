package com.miniblas.iu.controllers;

import android.util.Log;
import android.widget.ListView;

import com.arcadio.common.ItemVariable;
import com.arcadio.common.NumericVariable;
import com.arcadio.common.TextVariable;
import com.arcadio.common.VariablesList;
import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.dialog.interfaces.IObservadorEditBasket;
import com.miniblas.iu.fragments.VariablesElementsFragmentCab;
import com.miniblas.iu.fragments.base.CabOrdenableElementsFragment;
import com.miniblas.model.MiniBlasBag;
import com.miniblas.model.base.BaseElement;
import com.miniblas.model.base.BaseElementList;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;
import com.miniblas.persistence.BdException;

import java.util.ArrayList;
import java.util.Collection;
/**
 *
 * @author A. Azuara
 */
public class VariablesController extends BaseController<BaseVariableWidget> implements ObservadorVariables.IObservadorVariables, ConnectionIconListener.ObservadorConnectionIcon, IObservadorEditBasket{

	public static VariablesController instance;
	private int id_profile;
	private MiniBlasBag basket;

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
				application.getArcadioService().deleteBag(basket.getNameElement());
			}catch(com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException e){
				e.printStackTrace();
			}catch(com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException e){
				e.printStackTrace();
			}
		}
		try{
			basket = application.getBasketStorage().getBagById(_id_basket);
			basket.setProfile(application.getProfileStorage().getProfileById(basket.getProfile().getId()));
		}catch(BdException e){
			e.printStackTrace();
		}
		if(basket != null){
			//application.connect(_id_profile);
			application.deleteVariablesObserver();
			application.setVariablesObserver(this);
			try{
				application.getArcadioService().createBag(basket.getNameElement());
				application.getArcadioService().setBagPeriod(basket.getNameElement(),basket.getRefreshPeriod());
			}catch(com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException e){
				e.printStackTrace();
			}catch(com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException e){
				e.printStackTrace();
			}
			super.onViewChange(_vista);
		}
	}

	public void exit(){
		if(basket != null){
			try{
				application.getArcadioService().deleteBag(basket.getNameElement());
			}catch(com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException e){
				e.printStackTrace();
			}catch(com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException e){
				e.printStackTrace();
			}
		}
		super.saveElements();
		application.deleteVariablesObserver();
	}

	@Override
	protected void loadPreferences(){
	}

	@Override
	protected BaseElementList<BaseVariableWidget> getElementsToRepository() throws BdException{
		BaseElementList<BaseVariableWidget> variables = application.getVariableWidgetsStorage().getItemVariableOrdered(basket.getId());
		//conexion con cosme
		Collection<String> listaNombreVariables = variables.getNameList();
		if(!listaNombreVariables.isEmpty()){
			try{
				application.getArcadioService().addNamesToBag(basket.getNameElement(), new ArrayList<String>(listaNombreVariables));
			}catch(com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException e){
				e.printStackTrace();
			}catch(com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException e){
				e.printStackTrace();
			}
		}
		return variables;
	}

	@Override
	protected void saveElementsToRepository(BaseElementList<BaseVariableWidget> _elements) throws BdException{
			application.getVariableWidgetsStorage().persistCollection(_elements);
	}

	@Override
	protected void deleteElements(BaseElementList<BaseVariableWidget> elements) throws BdException{
		for(BaseElement element: elements){
			try{
				application.getArcadioService().removeNameFromBag(basket.getNameElement(), element.getNameElement());
			}catch(com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException e){
				e.printStackTrace();
			}catch(com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException e){
				e.printStackTrace();
			}
		}

		application.getVariableWidgetsStorage().deleteItemVariables(elements);
	}

	@Override
	public void onNotifyVariables(String _nombreCesta, VariablesList _listaVariables){
		BaseElementList<BaseVariableWidget> variablesList = vista.getAdapter().getElements();
		for(BaseVariableWidget variableWidget: variablesList){
			variableWidget.setValue(String.valueOf(_listaVariables.getValue(variableWidget.getNameElement())));
		}
		((VariablesElementsFragmentCab)vista).refreshElement();
	}

	@Override
	public void OnConnect(){
		vista.setConnectIcon();
		((VariablesElementsFragmentCab)vista).showFab();
	}

	@Override
	public void OnDisconnect(){
		vista.setDisconnectIcon();
		((VariablesElementsFragmentCab)vista).hideFab();
	}

	public int getIdProfile(){
		return id_profile;
	}

	public MiniBlasBag getBasket(){
		return basket;
	}

	@Override
	public void OnButtonEditSave(MiniBlasBag data){
		try{
			application.getArcadioService().setBagPeriod(basket.getNameElement(),basket.getRefreshPeriod());
		}catch(com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException e){
			e.printStackTrace();
		}catch(com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException e){
			e.printStackTrace();
		}
		try{
			application.getBasketStorage().persist(basket);
			vista.msgButtonEditSave();
		}catch(BdException e){
			vista.msgErrorSavingElementsToBD();
			vista.msgButtonEditCancel();
		}
	}

	@Override
	public void OnButtonEditCancel(MiniBlasBag data){
		vista.msgButtonEditCancel();
	}
	@Override
	public void OnButtonNewSave(final BaseElementList<BaseVariableWidget> elements){
		super.OnButtonNewSave(elements);
		try{
			application.getArcadioService().addNamesToBag(basket.getNameElement(), new ArrayList<String>(elements.getNameList()));
		}catch(com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException e){
			e.printStackTrace();
		}catch(com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException e){
			e.printStackTrace();
		}
	}
}
