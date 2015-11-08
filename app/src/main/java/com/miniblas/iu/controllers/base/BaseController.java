package com.miniblas.iu.controllers.base;

import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.iu.dialog.interfaces.IObservadorEditDialog;
import com.miniblas.iu.dialog.interfaces.IObservadorNewDialog;
import com.miniblas.iu.fragments.base.CabOrdenableElementsFragment;
import com.miniblas.iu.utils.SeleccionableBaseElementsListRendererAdapter;
import com.miniblas.model.base.BaseElement;
import com.miniblas.model.base.BaseElementList;
import com.miniblas.persistence.BdException;

import java.util.ArrayList;
/**
 *
 * @author A. Azuara
 */
public abstract class BaseController <T extends BaseElement> implements IObservadorNewDialog<T>, IObservadorEditDialog<T>{
	protected CabOrdenableElementsFragment<T> vista;
	private SeleccionableBaseElementsListRendererAdapter<T> adapter;
	protected AplicacionPrincipal application;


	protected BaseController(AplicacionPrincipal _aplicacionPrincipal){
		this.application = _aplicacionPrincipal;
		application.startService();
	}

	public void onViewChange(CabOrdenableElementsFragment<T> _vista){
		this.vista = _vista;
		adapter = vista.getAdapter();
		loadPreferences();
		adapter.clearCollection();
		application.addSingleTask(new Runnable(){
			@Override
			public void run(){
				vista.showIconLoading();
				//    			try {
				//			    Thread.sleep(5000);
				//			} catch (InterruptedException e) {
				//				e.printStackTrace();
				//			}
				try{
					adapter.addAll(getElementsToRepository());
					vista.dismissIconLoading();
					//actualizar lista
					vista.refreshList();
					vista.loadState();
				}catch(BdException e){
					vista.msgErrorGettingElementsInBD();
					e.printStackTrace();
					adapter.addAll(new ArrayList<T>());
				}

			}
		});
	}

	public void saveElements(){
		final SeleccionableBaseElementsListRendererAdapter<T> old_adapter = (SeleccionableBaseElementsListRendererAdapter<T>) vista.getListAdapter();
		application.addSingleTask(new Runnable(){
			@Override
			public void run(){
				BaseElementList<T> objectsList = new BaseElementList<T>();
				for(int i = 0; i < old_adapter.getCount(); i++){
					T element = (T) old_adapter.getItem(i);
					element.setOrder(i);
					objectsList.add((T) old_adapter.getItem(i));
				}
				try{
					saveElementsToRepository(objectsList);

				}catch(BdException e){
					e.printStackTrace();
					vista.msgErrorSavingElementsToBD();
				}
			}

		});

	}

	@Override
	public void OnButtonNewSave(final BaseElementList<T> elements){
		application.addSingleTask(new Runnable(){
			@Override
			public void run(){
				for(T element : elements){
					element.setOrder(adapter.getCount() + 1);
					adapter.add(element);
					//actualizar lista
					vista.refreshList();
					vista.msgButtonNewSave();
					saveElements();
				}
			}
		});
	}

	@Override
	public void OnButtonNewCancel(BaseElementList<T> element){
		vista.msgButtonNewCancel();
	}

	@Override
	public void OnButtonEditSave(final BaseElementList<T> elements){
		application.addSingleTask(new Runnable(){
			@Override
			public void run(){
				for(T element : elements){
					adapter.remove(element);
					adapter.insert(element, element.getOrder());
					vista.msgButtonEditSave();
					vista.refreshList();
				}

			}
		});

	}

	@Override
	public void OnButtonEditCancel(BaseElementList<T> element){
		vista.msgButtonEditCancel();
	}

	public void OnButtonDelete(final BaseElementList<T> _elementos){
		for(final T element : _elementos){
			adapter.remove(element);
		}
		application.addSingleTask(new Runnable(){

			@Override
			public void run(){
				try{
					deleteElements(_elementos);
				}catch(BdException e){
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

	protected abstract BaseElementList<T> getElementsToRepository() throws BdException;

	protected abstract void saveElementsToRepository(BaseElementList<T> _elements) throws BdException;

	protected abstract void deleteElements(BaseElementList<T> elements) throws BdException;

}