package com.miniblas.iu.cab;


import android.support.v7.view.ActionMode;

import com.miniblas.app.R;
import com.miniblas.iu.cab.base.BaseOrdenableElementsCab;
import com.miniblas.iu.dialog.alert.AlertDialogEditBag;
import com.miniblas.iu.fragments.BagElementsFragmentCab;
import com.miniblas.model.MiniBlasBag;
import com.miniblas.model.base.BaseElementList;

import java.util.ArrayList;

/**
 * Created by alberto on 16/11/14.
 */
public class BasketCab extends BaseOrdenableElementsCab{

	@Override
	public CharSequence getTitle() {
		int size = getFragment().getAdapter().getSizeCurrentChecketItems();
		if (size == 1)
			return size+" "+getContext().getString(R.string.cesta)+" "
					+getContext().getString(R.string.seleccionada);
		return size+" "+getContext().getString(R.string.cestas)+" "
				+getContext().getString(R.string.seleccionadas);
	}

	@Override
	public boolean canShowFab(){
		return false;
	}

	@Override
	public int getMultipleElementMenu(){
		return R.menu.menu_seleccion_noeditar;
	}

	@Override
	public int getMenu(){
		return R.menu.menu_seleccion;
	}


	@Override
	public boolean onActionItemClicked(ActionMode mode, android.view.MenuItem item){
		super.onActionItemClicked(mode, item);
		switch(item.getItemId()){
			case R.id.menu_editar:
				/*
				MiniBlasBag cesta = null;
				for(int i = 0; i < getFragment().getListView().getCheckedItemPositions().size(); i++){
					if(getFragment().getListView().getCheckedItemPositions().valueAt(i)){
						cesta = (MiniBlasBag) getFragment().getListView().getItemAtPosition(getFragment().getListView().getCheckedItemPositions().keyAt(i));
						break;
					}
				}
				AlertDialogEditarCesta.newInstance(getFragment().getController(), cesta, new ArrayList<MiniBlasBag>()).show(getContext().getSupportFragmentManager(), "tag");
				*/
				MiniBlasBag newSelected = (MiniBlasBag) getElements().get(0);
				BaseElementList elementList = ((BagElementsFragmentCab) getFragment()).getAdapter().getElements();
				AlertDialogEditBag.newInstance(getFragment().getController(), newSelected, elementList).show(getContext().getSupportFragmentManager(), "tag");
				mode.finish();
				return true;
			default:
				return false;
		}
	}


}
