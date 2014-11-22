package com.miniblas.iu.cab;


import android.util.SparseBooleanArray;

import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.alertdialog.AlertDialogEditarPerfil;
import com.miniblas.iu.alertdialog.interfaces.IObservadorEditAlertDialog;
import com.miniblas.iu.cab.base.BaseOrdenableElementsCab;
import com.miniblas.iu.controllers.ProfilesController;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.model.MiniBlasCesta;
import com.miniblas.model.MiniBlasPerfil;

import java.util.ArrayList;

import static com.miniblas.iu.alertdialog.AlertDialogEditarPerfil.*;

/**
 * Created by alberto on 16/11/14.
 */
public class PerfilCab extends BaseOrdenableElementsCab{

    @Override
    public boolean canShowFab() {
        return false;
    }

    @Override
    public int getMultipleElementMenu() {
        return R.menu.menu_seleccion_noeditar;
    }

    @Override
    public int getMenu() {
        return R.menu.menu_seleccion;
    }


    @Override
    public boolean onActionItemClicked(android.view.ActionMode mode, android.view.MenuItem item) {
        super.onActionItemClicked(mode, item);
        switch (item.getItemId()) {
            case R.id.menu_editar:
                MiniBlasPerfil perfil = null;
                for(int i =0; i<getFragment().getListView().getCheckedItemPositions().size();i++){
                    if (getFragment().getListView().getCheckedItemPositions().valueAt(i)) {
                        perfil = (MiniBlasPerfil) getFragment().getListView().getItemAtPosition(getFragment().getListView().getCheckedItemPositions().keyAt(i));
                        break;
                    }
                }
			    AlertDialogEditarPerfil.newInstance(getFragment().getController(),perfil,new ArrayList<MiniBlasPerfil>()).show(getContext().getFragmentManager(), "tag");
                mode.finish();
                return true;
            default:
                return false;
        }
    }



}
