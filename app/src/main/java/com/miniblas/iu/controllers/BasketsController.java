package com.miniblas.iu.controllers;

import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.fragments.base.OrdenableElementsFragment;
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
        if(instance == null) {
            instance = new BasketsController(_aplicacionPrincipal);
        }
        return instance;
    }

    private BasketsController(AplicacionPrincipal _aplicacionPrincipal){
        super(_aplicacionPrincipal);
    }

    @Override
    public void resetController() {

    }

    public void onViewChange(OrdenableElementsFragment _vista, int _id_profile){
        try {
            profile = application.getProfileStorage().getProfileByid(_id_profile);
        } catch (BdException e) {
            e.printStackTrace();
        }
        super.onViewChange(_vista);
        /*******************Conexion a cosme **********************/
        //esta tarea requiere de tienpo para establecer el socket
        application.connect(profile.getId());
        application.setIconObserver(this);
    }


    @Override
    protected void loadPreferences(){
        if(application.getSettingStorage().getPrefAutoConexion()){
            int idProfile = application.getSettingStorage().getPrefAutoConexionIdProfile();
            //vista.goToBasketsIU(idProfile);
        }
	}

    @Override
    protected List<MiniBlasCesta> getElementsToRepository() throws BdException {
        return application.getBasketStorage().getBasketsByProfile(profile);
    }

    @Override
    protected void saveElementsToRepository(List<MiniBlasCesta> _elements) throws BdException {
        application.getBasketStorage().persistCollection(_elements);
    }

    @Override
    protected void deleteElements(List<MiniBlasCesta> elements) throws BdException {
        application.getBasketStorage().deleteBaskets(elements);
    }

    public MiniBlasPerfil getProfile(){
        return profile;
    }

    @Override
    public void OnConnect() {
        vista.setConnectIcon();
    }

    @Override
    public void OnDisconnect() {
        vista.setDisconnectIcon();
    }
}
