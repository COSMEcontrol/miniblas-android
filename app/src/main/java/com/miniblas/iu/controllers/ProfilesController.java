package com.miniblas.iu.controllers;
import java.util.ArrayList;
import java.util.List;
import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.iu.alertdialog.AlertDialogEditarPerfil;
import com.miniblas.iu.alertdialog.AlertDialogNuevoPerfil;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.fragments.base.OrdenableElementsFragment;
import com.miniblas.model.MiniBlasPerfil;
import com.miniblas.persistence.BdException;

public class ProfilesController extends BaseController<MiniBlasPerfil> {

   // private ProfilesFragment profilesView;
   public static ProfilesController instance;

    public static ProfilesController getInstance(AplicacionPrincipal _aplicacionPrincipal){
        if(instance == null) {
            instance = new ProfilesController(_aplicacionPrincipal);
        }
        return instance;
    }

    private ProfilesController(AplicacionPrincipal _aplicacionPrincipal){
        super(_aplicacionPrincipal);
    }

    @Override
    public void resetController() {

    }

    public void onViewChange(OrdenableElementsFragment _vista){
		super.onViewChange(_vista);
		if(application.getSettingStorage().getPrefAutoConexion()){
			int idProfile = application.getSettingStorage().getPrefAutoConexionIdProfile();
			//vista.goToBasketsIU(idProfile);
		}
	}


    @Override
    protected void loadPreferences(){
		application.addGlobalTask(new Runnable() {
			@Override
			public void run() {
				MiniBlasPerfil.CONTRASEÑA_POR_DEFECTO = application.getSettingStorage().getPrefDefaultPassword();
				MiniBlasPerfil.PUERTO_POR_DEFECTO = application.getSettingStorage().getPrefDefaultPort();
			}
		});
	}

    @Override
    protected List<MiniBlasPerfil> getElementsToRepository() throws com.miniblas.persistence.BdException {
        return application.getProfileStorage().getProfilesOrdered();
    }

    @Override
    protected void saveElementsToRepository(List<MiniBlasPerfil> _elements) throws com.miniblas.persistence.BdException {
        application.getProfileStorage().persistCollection(_elements);
    }

    @Override
    protected void deleteElements(List<MiniBlasPerfil> elements) throws BdException {
        application.getProfileStorage().deleteProfiles(elements);
    }
}
