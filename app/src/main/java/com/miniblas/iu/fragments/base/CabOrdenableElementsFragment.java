package com.miniblas.iu.fragments.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.miniblas.app.R;
import com.miniblas.iu.FabActivity;
import com.miniblas.iu.cab.base.BaseCab;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.controllers.base.SerializableSparseBooleanArrayContainer;
import com.miniblas.iu.utils.SeleccionableRendererAdapter;
import com.miniblas.model.ISortElement;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

/**
 * Created by alberto on 13/11/14.
 */
public abstract class CabOrdenableElementsFragment<T extends ISortElement> extends ListFragment {

    private SeleccionableRendererAdapter<T> adapter;
    private Bundle savedInstance=null;
    public static final String SELECTED_ELEMENTS = "SELECTED_ELEMENTS";
    private ProgressBar progressBar;
    private static ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        if(getListView() instanceof DragSortListView){
            ((DragSortListView) getListView()).setDropListener(new onDropListener(adapter));
        }
        act.attachFabToListView(getListView());
        act.disableFab(false);
        act.setImageFab(R.drawable.ic_action_content_new);
        progressBar = (ProgressBar) getView().findViewById(android.R.id.progress);
        listView = getListView();
    }
    public void setCabInFragment(BaseCab baseCab){
        ((FabActivity) getActivity()).setCab(baseCab);
        getListView().setMultiChoiceModeListener(((FabActivity) getActivity()).getCab());
    }

    public void loadState(){
        this.runOnIuThread(new Runnable() {
            public void run() {
                if (savedInstance != null) {
                    getListView().clearChoices();
                    SerializableSparseBooleanArrayContainer datos = (SerializableSparseBooleanArrayContainer) savedInstance.get(SELECTED_ELEMENTS);
                    SparseBooleanArray sparseBooleanArrayContainer = datos.getSparseArray();
                    for (int i = 0; i < adapter.getCount(); i++) {
                        listView.setItemChecked(i, sparseBooleanArrayContainer.get(i));
                    }
                    ((FabActivity) getActivity()).disableFab(true);
                }
            }
        });

    }

    public void showIconLoading(){
        this.runOnIuThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }
    public void dismissIconLoading(){
        this.runOnIuThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
    @Override
    public void onSaveInstanceState(Bundle _savedInstanceState) {
        super.onSaveInstanceState(_savedInstanceState);
        SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
        SerializableSparseBooleanArrayContainer sparseBooleanArraySerializable = new SerializableSparseBooleanArrayContainer(sparseBooleanArray);
        _savedInstanceState.putSerializable(SELECTED_ELEMENTS, sparseBooleanArraySerializable);

    }

    public View provideDropView(){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.lyt_fragment_ordenable, null, false);
        DragSortListView mDslv = (DragSortListView) view.findViewById(android.R.id.list);
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

        return view;

    }


    public SeleccionableRendererAdapter<T> getAdapter(){
        return adapter;
    }
    public void setAdapter(SeleccionableRendererAdapter<T> adapter){
        this.adapter=adapter;
    }
    public void refreshList(){
            this.runOnIuThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });

    }
    public void setTitle(final String title) {
        this.runOnIuThread(new Runnable() {
            @Override
            public void run() {
                getActivity().setTitle(title);
            }
        });
    }
    /*
        *Abstract functions, it depend of the view
     */
    public abstract void setConnectIcon();
    public abstract void setDisconnectIcon();
    public abstract void msgErrorSavingElementsToBD();
    public abstract void msgErrorGettingElementsInBD();

    public abstract void msgButtonNewSave();
    public abstract void msgButtonNewCancel();
    public abstract void msgButtonEditSave();
    public abstract void msgButtonEditCancel();
    public abstract void msgErrorDeleteElementsInBD();
    public abstract BaseController<T> getController();


    private void runOnIuThread(Runnable runnable){
        if(getActivity()!= null){
            getActivity().runOnUiThread(runnable);

        }
    }
}
