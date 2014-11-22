package com.miniblas.iu.cab;


import com.miniblas.app.R;
import com.miniblas.iu.alertdialog.AlertDialogEditarCesta;
import com.miniblas.iu.cab.base.BaseOrdenableElementsCab;
import com.miniblas.model.MiniBlasCesta;


import java.util.ArrayList;

/**
 * Created by alberto on 16/11/14.
 */
public class BasketCab extends BaseOrdenableElementsCab{

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
                MiniBlasCesta cesta = null;
                for(int i =0; i<getFragment().getListView().getCheckedItemPositions().size();i++){
                    if (getFragment().getListView().getCheckedItemPositions().valueAt(i)) {
                        cesta = (MiniBlasCesta) getFragment().getListView().getItemAtPosition(getFragment().getListView().getCheckedItemPositions().keyAt(i));
                        break;
                    }
                }
			    AlertDialogEditarCesta.newInstance(getFragment().getController(), cesta, new ArrayList<MiniBlasCesta>()).show(getContext().getSupportFragmentManager(), "tag");
                mode.finish();
                return true;
            default:
                return false;
        }
    }



}
