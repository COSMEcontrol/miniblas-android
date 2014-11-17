package com.miniblas.iu.cab.base;

import android.util.Log;
import android.view.Menu;


import com.miniblas.app.R;
import com.miniblas.iu.fragments.base.OrdenableElementsFragment;
import com.miniblas.model.ISortElement;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseOrdenableElementsCab extends BaseCab {

    private android.view.Menu menu;


    public BaseOrdenableElementsCab() {
        super();
        mElements = new ArrayList<ISortElement>();
    }

    private final List<ISortElement> mElements;

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

    public final BaseOrdenableElementsCab addElement(ISortElement element) {
        getFragment().getListView().setItemChecked(element.getOrden(),true);
        mElements.add(element);
        invalidate();
        return this;
    }

    public final BaseOrdenableElementsCab addFiles(List<ISortElement> elements) {
        for(ISortElement element: elements){
            getFragment().getListView().setItemChecked(element.getOrden(), true);
        }
        mElements.addAll(elements);
        invalidate();
        return this;
    }


    public final BaseOrdenableElementsCab removeFile(ISortElement element) {
        getFragment().getListView().setItemChecked(element.getOrden(), false);
        for (int i = 0; i < mElements.size(); i++) {
            mElements.remove(i);
            invalidate();
        }
        return this;
    }

    public final BaseOrdenableElementsCab setFile(ISortElement element) {
        getFragment().getListView().clearChoices();
        getFragment().getListView().setItemChecked(element.getOrden(), true);
        clearElements();
        mElements.add(element);
        invalidate();
        return this;
    }

    public final BaseOrdenableElementsCab setFiles(List<ISortElement> elements) {
        getFragment().getListView().clearChoices();
        for(ISortElement element: elements){
            getFragment().getListView().setItemChecked(element.getOrden(), true);
        }
        clearElements();
        mElements.addAll(elements);
        invalidate();
        return this;
    }

    public final void clearElements() {
        mElements.clear();
    }


    @Override
    public final void invalidate() {
        if (getFiles().size() == 0) finish();
        else super.invalidate();
    }

    public final List<ISortElement> getFiles() {
        return mElements;
    }


    @Override
    public void onDestroyActionMode(android.view.ActionMode actionMode) {
        clearElements();
        getFragment().getListView().clearChoices();
        getContext().disableFab(false);
        super.onDestroyActionMode(actionMode);
        getFragment().getAdapter().notifyDataSetChanged();
    }
    @Override
    public void onItemCheckedStateChanged(android.view.ActionMode mode,
                                          int position, long id, boolean checked) {
        mode.setTitle( getFragment().getListView().getCheckedItemCount()+ " "+
                getFragment().getActivity().getResources().getString(R.string.elementosSeleccionados));

        android.view.MenuInflater inflater = mode.getMenuInflater();
        menu.clear();
        if (getMultipleElementMenu() != -1 && mElements.size() >1)
            mode.getMenuInflater().inflate(getMultipleElementMenu(), menu);

        if(getFragment().getListView().getCheckedItemCount()==1){
            inflater.inflate(R.menu.menu_seleccion, menu);
        }else{
            inflater.inflate(R.menu.menu_seleccion_noeditar, menu);
        }
        getFragment().getAdapter().notifyDataSetChanged();
    }
}
