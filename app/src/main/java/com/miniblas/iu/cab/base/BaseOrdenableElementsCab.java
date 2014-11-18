package com.miniblas.iu.cab.base;

import android.util.Log;
import android.view.Menu;


import com.miniblas.app.R;
import com.miniblas.iu.fragments.base.OrdenableElementsFragment;
public abstract class BaseOrdenableElementsCab extends BaseCab {

    private android.view.Menu menu;


    public abstract boolean canShowFab();

    public abstract int getMultipleElementMenu();

    @Override
    public final boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
        boolean result = super.onCreateActionMode(actionMode, menu);
        this.menu=menu;
        invalidateFab();
        return result;
    }

    @Override
    public BaseOrdenableElementsCab setFragment(OrdenableElementsFragment fragment) {
        super.setFragment(fragment);
        //invalidateFab();
        return this;
    }
    public BaseOrdenableElementsCab invalidateFab() {
        Log.v("Fab", "invalidateFab()");
        boolean hide = false;
        if (!canShowFab() && isActive()) {
            Log.v("Fab", "Cannot use the FAB in the current mode.");
            hide = true;
        }
        getContext().disableFab(hide);
        return this;
    }



    @Override
    public void onDestroyActionMode(android.view.ActionMode actionMode) {
        super.onDestroyActionMode(actionMode);
        getContext().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getFragment().getListView().clearChoices();
                getContext().disableFab(false);
                getFragment().getAdapter().notifyDataSetChanged();
            }
        });
    }
    @Override
    public void onItemCheckedStateChanged(android.view.ActionMode mode,
                                          int position, long id, boolean checked) {
        mode.setTitle(getFragment().getListView().getCheckedItemCount() + " " +
                getFragment().getActivity().getResources().getString(R.string.elementosSeleccionados));

        menu.clear();
        if (getMultipleElementMenu() != -1 && getFragment().getListView().getCheckedItemCount() > 1){
            mode.getMenuInflater().inflate(getMultipleElementMenu(), menu);

        }else{
             mode.getMenuInflater().inflate(getMenu(), menu);
        }
        getFragment().getAdapter().notifyDataSetChanged();
    }
}
