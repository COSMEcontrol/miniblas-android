package com.miniblas.iu.controllers;

import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.fragments.BasketsElementsFragmentCab;
import com.miniblas.iu.fragments.base.CabOrdenableElementsFragment;
import com.miniblas.model.MiniBlasCesta;
import com.miniblas.model.MiniBlasPerfil;
import com.miniblas.persistence.BdException;

import java.util.List;

public class BasketsController extends BaseController<MiniBlasCesta> implements ConnectionIconListener.ObservadorConnectionIcon{

	// private ProfilesFragment profilesView;
	private static BasketsController instance;
	private MiniBlasCesta basket = null;
	private MiniBlasPerfil profile = null;

	public static BasketsController getInstance(AplicacionPrincipal _aplicacionPrincipal){
		if(instance == null){
			instance = new BasketsController(_aplicacionPrincipal);
		}
		return instance;
	}

	private BasketsController(AplicacionPrincipal _aplicacionPrincipal){
		super(_aplicacionPrincipal);
	}

	@Override
	public void resetController(){

	}

	public void onClickAutoConexion(){
		if(application.getSettingStorage().getPrefAutoConexionIdProfile()==profile.getId()){
			//desmarcar
			((BasketsElementsFragmentCab) vista).setNotChekedAutoConect();
			application.getSettingStorage().setPrefAutoConexion(false);
			application.getSettingStorage().setPrefAutoConexionIdProfile(0);
		}else {
			System.out.println(profile.getId());
			((BasketsElementsFragmentCab) vista).setChekedAutoConect();
			application.getSettingStorage().setPrefAutoConexion(true);
			application.getSettingStorage().setPrefAutoConexionIdProfile(profile.getId());
		}
	}
	public void autoConexionControl(){
		if(application.getSettingStorage().getPrefAutoConexion()){
			if(application.getSettingStorage().getPrefAutoConexionIdProfile()==profile.getId()){
				((BasketsElementsFragmentCab) vista).setChekedAutoConect();
			}else {
				((BasketsElementsFragmentCab) vista).setNotChekedAutoConect();
			}
		}else{
			((BasketsElementsFragmentCab) vista).setNotChekedAutoConect();
		}
	}
	public void onViewChange(CabOrdenableElementsFragment _vista, int _id_profile){
		try{
			profile = application.getProfileStorage().getProfileById(_id_profile);
		}catch(BdException e){
			e.printStackTrace();
		}
		super.onViewChange(_vista);
		/*******************Conexion a cosme **********************/
		//esta tarea requiere de tienpo para establecer el socket
		application.connect(profile.getId());
	}


	@Override
	protected void loadPreferences(){
		if(application.getSettingStorage().getPrefAutoConexion()){
			int idProfile = application.getSettingStorage().getPrefAutoConexionIdProfile();
			//vista.goToBasketsIU(idProfile);
		}
	}

	@Override
	protected List<MiniBlasCesta> getElementsToRepository() throws BdException{
		return application.getBasketStorage().getBasketsByProfile(profile);
	}

	@Override
	protected void saveElementsToRepository(List<MiniBlasCesta> _elements) throws BdException{
		application.getBasketStorage().persistCollection(_elements);
	}

	@Override
	protected void deleteElements(List<MiniBlasCesta> elements) throws BdException{
		application.getBasketStorage().deleteBaskets(elements);
	}

	public MiniBlasPerfil getProfile(){
		return profile;
	}

	@Override
	public void OnConnect(){
		System.out.println("poner el semaforo en verde");
		vista.setConnectIcon();
	}

	@Override
	public void OnDisconnect(){
		vista.setDisconnectIcon();
	}
}
