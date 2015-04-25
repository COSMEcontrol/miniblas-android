package com.miniblas.iu.cab.base;

import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.miniblas.app.R;
import com.miniblas.iu.fragments.base.CabOrdenableElementsFragment;
import com.miniblas.model.base.BaseElement;
import com.miniblas.model.base.BaseElementList;

public abstract class BaseOrdenableElementsCab extends BaseCab{

	private Menu menu;
	private ActionMode aMode;
	private BaseElementList<BaseElement> elementsSelected;

	public BaseOrdenableElementsCab() {
		super();
		elementsSelected = new BaseElementList();
	}

	public abstract boolean canShowFab();

	public abstract int getMultipleElementMenu();

	@Override
	public final boolean onCreateActionMode(ActionMode actionMode, Menu menu){
		boolean result = super.onCreateActionMode(actionMode, menu);
		getContext().hideFab();
		this.menu = menu;
		this.aMode=actionMode;
		elementsSelected = new BaseElementList();
		return result;
	}

	@Override
	public BaseOrdenableElementsCab setFragment(CabOrdenableElementsFragment fragment){
		super.setFragment(fragment);
		return this;
	}

	@Override
	public final void invalidate() {
		if (getElements().size() == 0) finish();
		else super.invalidate();
	}

	@Override
	public void onDestroyActionMode(ActionMode actionMode){
		super.onDestroyActionMode(actionMode);
		getContext().runOnUiThread(new Runnable(){
			@Override
			public void run(){
				getListView().clearChoices();
				getFragment().getAdapter().clearSelection();
				getContext().showFab();
				getFragment().getAdapter().notifyDataSetChanged();
			}
		});
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item){
		super.onActionItemClicked(mode, item);
		switch(item.getItemId()){
			case R.id.menu_eliminar:
				getFragment().getController().OnButtonDelete(getElements());
				mode.finish();
				return true;
			default:
				return false;
		}
	}
	public BaseElementList getElements() {
		return elementsSelected;
	}
	public void addElement(BaseElement elementSelected){
		elementsSelected.add(elementSelected);
		refreshToolbar();
		invalidate();
	}
	public void removeElement(BaseElement elementSelected){
		elementsSelected.remove(elementSelected);
		refreshToolbar();
		invalidate();
	}
	public void refreshToolbar(){
		if(elementsSelected.size()>1){
			menu.clear();
			aMode.getMenuInflater().inflate(getMultipleElementMenu(), menu);
		}else{
			menu.clear();
			aMode.getMenuInflater().inflate(getMenu(), menu);
		}
	}
}
