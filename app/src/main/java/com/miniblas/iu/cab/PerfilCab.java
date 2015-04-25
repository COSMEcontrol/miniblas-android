package com.miniblas.iu.cab;


import android.support.v7.view.ActionMode;

import com.miniblas.app.R;
import com.miniblas.iu.cab.base.BaseOrdenableElementsCab;
import com.miniblas.iu.dialog.alert.AlertDialogEditProfile;
import com.miniblas.iu.fragments.ProfilesElementsFragmentCab;
import com.miniblas.model.MiniBlasProfile;
import com.miniblas.model.base.BaseElementList;

import java.util.ArrayList;

/**
 * Created by alberto on 16/11/14.
 */
public class PerfilCab extends BaseOrdenableElementsCab{

	@Override
	public CharSequence getTitle() {
		int size = getFragment().getAdapter().getSizeCurrentChecketItems();
		if (size == 1)
			return size+" "+getContext().getString(R.string.perfil)+" "
					+getContext().getString(R.string.seleccionado);
		return size+" "+getContext().getString(R.string.perfiles)+" "
				+getContext().getString(R.string.seleccionados);
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
				MiniBlasProfile perfil = null;
				for(int i = 0; i < getFragment().getListView().getCheckedItemPositions().size(); i++){
					if(getFragment().getListView().getCheckedItemPositions().valueAt(i)){
						perfil = (MiniBlasProfile) getFragment().getListView().getItemAtPosition(getFragment().getListView().getCheckedItemPositions().keyAt(i));
						break;
					}
				}
				AlertDialogEditarPerfil.newInstance(getFragment().getController(), perfil, new ArrayList<MiniBlasProfile>()).show(getContext().getFragmentManager(), "tag");
				*/
				MiniBlasProfile newSelected = (MiniBlasProfile) getElements().get(0);
				BaseElementList elementList = ((ProfilesElementsFragmentCab) getFragment()).getAdapter().getElements();
				AlertDialogEditProfile.newInstance(getFragment().getController(), newSelected, elementList).show(getContext().getFragmentManager(), "tag");
				mode.finish();
				return true;
			default:
				return false;
		}
	}


}
