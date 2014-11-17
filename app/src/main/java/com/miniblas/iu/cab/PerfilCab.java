package com.miniblas.iu.cab;


import com.miniblas.app.R;
import com.miniblas.iu.cab.base.BaseOrdenableElementsCab;

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

}
