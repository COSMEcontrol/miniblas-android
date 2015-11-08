package com.miniblas.iu.controllers;

import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.fragments.BagElementsFragmentCab;
import com.miniblas.iu.fragments.base.CabOrdenableElementsFragment;
import com.miniblas.model.MiniBlasBag;
import com.miniblas.model.MiniBlasProfile;
import com.miniblas.model.base.BaseElementList;
import com.miniblas.persistence.BdException;
/**
 *
 * @author A. Azuara
 */
public class BagController extends BaseController<MiniBlasBag> implements ConnectionIconListener.ObservadorConnectionIcon{

	// private ProfilesFragment profilesView;
	private static BagController instance;
	private MiniBlasBag basket = null;
	private MiniBlasProfile profile = null;

	public static BagController getInstance(AplicacionPrincipal _aplicacionPrincipal){
		if(instance == null){
			instance = new BagController(_aplicacionPrincipal);
		}
		return instance;
	}

	private BagController(AplicacionPrincipal _aplicacionPrincipal){
		super(_aplicacionPrincipal);
	}

	@Override
	public void resetController(){

	}

	public void onClickAutoConexion(){
		if(application.getSettingStorage().getPrefAutoConexionIdProfile()==profile.getId()){
			//desmarcar
			((BagElementsFragmentCab) vista).setNotChekedAutoConect();
			application.getSettingStorage().setPrefAutoConexion(false);
			application.getSettingStorage().setPrefAutoConexionIdProfile(0);
		}else {
			((BagElementsFragmentCab) vista).setChekedAutoConect();
			application.getSettingStorage().setPrefAutoConexion(true);
			application.getSettingStorage().setPrefAutoConexionIdProfile(profile.getId());
		}
	}
	public void autoConexionControl(){
		if(application.getSettingStorage().getPrefAutoConexion()){
			if(application.getSettingStorage().getPrefAutoConexionIdProfile()==profile.getId()){
				((BagElementsFragmentCab) vista).setChekedAutoConect();
			}else {
				((BagElementsFragmentCab) vista).setNotChekedAutoConect();
			}
		}else{
			((BagElementsFragmentCab) vista).setNotChekedAutoConect();
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
	protected BaseElementList<MiniBlasBag> getElementsToRepository() throws BdException{
		return application.getBasketStorage().getBagsByProfile(profile);
	}

	@Override
	protected void saveElementsToRepository(BaseElementList<MiniBlasBag> _elements) throws BdException{
		application.getBasketStorage().persistCollection(_elements);
	}

	@Override
	protected void deleteElements(BaseElementList<MiniBlasBag> elements) throws BdException{
		application.getBasketStorage().deleteBags(elements);
	}

	public MiniBlasProfile getProfile(){
		return profile;
	}

	@Override
	public void OnConnect(){
		vista.setConnectIcon();
	}

	@Override
	public void OnDisconnect(){
		vista.setDisconnectIcon();
	}
}
