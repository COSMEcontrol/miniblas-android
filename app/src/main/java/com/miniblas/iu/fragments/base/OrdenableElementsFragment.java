package com.miniblas.iu.fragments.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.FabActivity;
import com.miniblas.iu.alertdialog.AlertDialogNuevoPerfil;
import com.miniblas.iu.utils.SeleccionableRendererAdapter;
import com.miniblas.model.MiniBlasPerfil;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by alberto on 13/11/14.
 */
public abstract class OrdenableElementsFragment<T> extends ListFragment {

    public SeleccionableRendererAdapter<T> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return provideDropView();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FabActivity act = (FabActivity) getActivity();
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener(((FabActivity) getActivity()).getCab());
        ((DragSortListView) getListView()).setDropListener(new onDropListener(adapter));
        act.attachFabToListView(getListView());
    }


    private View provideDropView(){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DragSortListView mDslv = (DragSortListView) inflater.inflate(R.layout.lyt_fragment_ordenable, null, false);

        DragSortController controller = new DragSortController(mDslv);
        //determinacion de la imagen que hace que muevas el elemento
        controller.setDragHandleId(R.id.drag_handle);
////    controller.setClickRemoveId(R.id.click_remove);
        controller.setRemoveEnabled(false);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_DOWN);
        controller.setRemoveMode(DragSortController.FLING_REMOVE);

        mDslv.setFloatViewManager(controller);
        mDslv.setOnTouchListener(controller);
        mDslv.setDragEnabled(true);
        return mDslv;

    }
    public SeleccionableRendererAdapter<T> getAdapter(){
        return adapter;
    }
    public void setAdapter(SeleccionableRendererAdapter<T> adapter){
        this.adapter=adapter;
    }
    public abstract void refreshList();
   // public abstract void setSubtitle(final String title);
    public abstract void setTitle(final String title);
    public abstract void setConnectIcon();
    public abstract void setDisconnectIcon();
    public abstract void showIconLoading();
    public abstract void dismissIconLoading();
    public abstract void recuperarEstado();
    public abstract void msgErrorSavingElementsToBD();
    public abstract void msgErrorGettingElementsInBD();
    public abstract void clearSelecction();
    public abstract void msgButtonNewSave();
    public abstract void msgButtonNewCancel();
    public abstract void msgButtonEditSave();
    public abstract void msgButtonEditCancel();
    public abstract void msgErrorDeleteElementsInBD();

}
