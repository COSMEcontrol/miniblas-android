package com.miniblas.iu.controllers;

import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.fragments.ProfilesElementsFragmentCab;
import com.miniblas.iu.fragments.base.CabOrdenableElementsFragment;
import com.miniblas.model.MiniBlasPerfil;
import com.miniblas.persistence.BdException;

import java.util.List;

public class ProfilesController extends BaseController<MiniBlasPerfil> implements ConnectionListener.IObservadorConnection{

	// private ProfilesFragment profilesView;
	public static ProfilesController instance;

	public static ProfilesController getInstance(AplicacionPrincipal _aplicacionPrincipal){
		if(instance == null){
			instance = new ProfilesController(_aplicacionPrincipal);
		}
		return instance;
	}

	private ProfilesController(AplicacionPrincipal _aplicacionPrincipal){
		super(_aplicacionPrincipal);
	}

	@Override
	public void resetController(){

	}

	public void onViewChange(CabOrdenableElementsFragment _vista){
		super.onViewChange(_vista);
		application.setConnectionObserver(this);
		try{
			if(application.getArcadioService().isConnected()){
				application.getArcadioService().disconnect();
			}
		}catch(com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException e){
			e.printStackTrace();
		}catch(com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException e){
			e.printStackTrace();
		}
	}


	@Override
	protected void loadPreferences(){
		application.addGlobalTask(new Runnable(){
			@Override
			public void run(){
				MiniBlasPerfil.CONTRASEÑA_POR_DEFECTO = application.getSettingStorage().getPrefDefaultPassword();
				MiniBlasPerfil.PUERTO_POR_DEFECTO = application.getSettingStorage().getPrefDefaultPort();
			}
		});
	}

	@Override
	protected List<MiniBlasPerfil> getElementsToRepository() throws com.miniblas.persistence.BdException{
		return application.getProfileStorage().getProfilesOrdered();
	}

	@Override
	protected void saveElementsToRepository(List<MiniBlasPerfil> _elements) throws com.miniblas.persistence.BdException{
		application.getProfileStorage().persistCollection(_elements);
	}

	@Override
	protected void deleteElements(List<MiniBlasPerfil> elements) throws BdException{
		application.getProfileStorage().deleteProfiles(elements);
	}

	@Override
	public void OnConnect(){
		vista.showIconLoading();
		if(application.getSettingStorage().getPrefAutoConexion()){
			int idProfile = application.getSettingStorage().getPrefAutoConexionIdProfile();
			((ProfilesElementsFragmentCab) vista).gotoBagFragment(idProfile);
		}
		vista.dismissIconLoading();
	}

	@Override
	public void OnDisconnect(){

	}
}
