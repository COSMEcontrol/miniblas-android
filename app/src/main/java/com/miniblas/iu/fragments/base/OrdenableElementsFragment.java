package com.miniblas.iu.fragments.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.miniblas.app.R;
import com.miniblas.iu.FabActivity;
import com.miniblas.iu.controllers.base.SerializableSparseBooleanArrayContainer;
import com.miniblas.iu.utils.SeleccionableRendererAdapter;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

/**
 * Created by alberto on 13/11/14.
 */
public abstract class OrdenableElementsFragment<T> extends ListFragment {

    public SeleccionableRendererAdapter<T> adapter;
    public Bundle savedInstance=null;
    public static final String SELECTED_ELEMENTS = "SELECTED_ELEMENTS";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("Haciendo la vista");
        return provideDropView();
    }
    @Override
    public void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        this.savedInstance=null;
        this.savedInstance=_savedInstanceState;
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


    public void loadState(){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (savedInstance != null) {
                    getListView().clearChoices();
                    SerializableSparseBooleanArrayContainer datos = (SerializableSparseBooleanArrayContainer) savedInstance.get(SELECTED_ELEMENTS);
                    SparseBooleanArray sparseBooleanArrayContainer = datos.getSparseArray();
                    for (int i = 0; i < adapter.getCount(); i++) {
                        getListView().setItemChecked(i, sparseBooleanArrayContainer.get(i));
                    }
                    ((FabActivity) getActivity()).disableFab(true);
                }
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle _savedInstanceState) {
        super.onSaveInstanceState(_savedInstanceState);
        SparseBooleanArray sparseBooleanArray = getListView().getCheckedItemPositions();
        SerializableSparseBooleanArrayContainer sparseBooleanArraySerializable = new SerializableSparseBooleanArrayContainer(sparseBooleanArray);
        System.out.println(getListView().getCheckedItemPositions().toString());
        _savedInstanceState.putSerializable(SELECTED_ELEMENTS, sparseBooleanArraySerializable);
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
        controller.setBackgroundColor(getResources().getColor(R.color.miniblas_color_status_bar));
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
    //public abstract void recuperarEstado();
    public abstract void msgErrorSavingElementsToBD();
    public abstract void msgErrorGettingElementsInBD();
    public abstract void clearSelecction();
    public abstract void msgButtonNewSave();
    public abstract void msgButtonNewCancel();
    public abstract void msgButtonEditSave();
    public abstract void msgButtonEditCancel();
    public abstract void msgErrorDeleteElementsInBD();

}
