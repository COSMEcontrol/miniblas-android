package com.miniblas.iu.controllers.base;

import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.iu.alertdialog.interfaces.IObservadorEditAlertDialog;
import com.miniblas.iu.alertdialog.interfaces.IObservadorNewAlertDialog;
import com.miniblas.iu.fragments.base.OrdenableElementsFragment;
import com.miniblas.iu.utils.SeleccionableRendererAdapter;
import com.miniblas.model.ISortElement;
import com.miniblas.persistence.BdException;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseController<T extends ISortElement> implements IObservadorNewAlertDialog<T>, IObservadorEditAlertDialog<T> {
    protected OrdenableElementsFragment<T> vista;
    private SeleccionableRendererAdapter<T> adapter;
    protected AplicacionPrincipal application;



    protected BaseController(AplicacionPrincipal _aplicacionPrincipal){
        this.application=_aplicacionPrincipal;
        System.out.println("iniciando servicio arcadio");
        application.startService();
    }

    public void onViewChange(OrdenableElementsFragment<T> _vista){
        this.vista=_vista;
        adapter = vista.getAdapter();
        loadPreferences();
        application.addSingleTask(new Runnable() {
            @Override
            public void run() {
                System.out.println("Recuperando elementos de la bd");
                vista.showIconLoading();
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
                try {
                    System.out.println("antes de  clearcollection");
                    adapter.clearCollection();
                    System.out.println("antes de  cargados");
                    adapter.addAll(getElementsToRepository());
                    vista.dismissIconLoading();
                    //actualizar lista
                    vista.refreshList();
                    System.out.println("elementos cargados");
                    vista.recuperarEstado();
                } catch (BdException e) {
                    vista.msgErrorGettingElementsInBD();
                    e.printStackTrace();
                    adapter.addAll(new ArrayList<T>());
                }
            }
        });
    }

    public void saveElements(){
        final SeleccionableRendererAdapter<T> old_adapter = (SeleccionableRendererAdapter<T>) vista.getListAdapter();
        application.addSingleTask(new Runnable() {
            @Override
            public void run() {
//						try {
//							Thread.sleep(5000);
//							System.out.println("aquiiiiiiiiiiiiiiiii");
//						} catch (InterruptedException e1) {
//							e1.printStackTrace();
//						}
                System.out.println("guardandooooooooooooooooooooooooooooo");
                ArrayList<T> objectsList = new ArrayList<T>();
                for(int i=0;i<old_adapter.getCount();i++) {
                    T element = (T) old_adapter.getItem(i);
                    element.setOrden(i);
                    objectsList.add((T) old_adapter.getItem(i));
                }
                try {
                    saveElementsToRepository(objectsList);

                } catch (BdException e) {
                    e.printStackTrace();
                    vista.msgErrorSavingElementsToBD();
                }
            }

        });

    }
    @Override
    public void OnButtonNewSave(final T element) {
        System.out.println("Guardando en base controller");
        application.addSingleTask(new Runnable() {
            @Override
            public void run() {
                element.setOrden(adapter.getCount()+1);
                adapter.add(element);
                //actualizar lista
                System.out.println("onbuttonnewsave singletask en base controller");
                vista.refreshList();
                vista.msgButtonNewSave();
                saveElements();
            }
        });
    }

    @Override
    public void OnButtonNewCancel(T element) {
        vista.msgButtonNewCancel();

    }

    @Override
    public void OnButtonEditSave(final T element) {
        application.addSingleTask(new Runnable() {
            @Override
            public void run() {
                adapter.remove(element);
                //adapter.insert(element, perfil.getOrden());
                vista.msgButtonEditSave();
                vista.refreshList();
                vista.clearSelecction();
            }
        });

    }

    @Override
    public void OnButtonEditCancel(T element) {
                vista.msgButtonEditCancel();
                vista.clearSelecction();
    }

    public void OnButtonDelete(final List<T> _elementos) {
        for(final T element:_elementos){
            adapter.remove(element);
        }
        application.addSingleTask(new Runnable() {

            @Override
            public void run() {
                try {
                    deleteElements(_elementos);
                } catch (BdException e) {
                    e.printStackTrace();
                    vista.msgErrorDeleteElementsInBD();
                }
            }
        });
        adapter.clearSelection();
        vista.refreshList();

    }

    public abstract void resetController();
    protected abstract void loadPreferences();
    protected abstract List<T> getElementsToRepository() throws BdException;
    protected abstract void saveElementsToRepository(List<T> _elements) throws BdException;
    protected abstract void deleteElements(List<T> elements) throws BdException;

}