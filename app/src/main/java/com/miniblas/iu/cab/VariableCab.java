package com.miniblas.iu.cab;


import android.support.v7.view.ActionMode;

import com.miniblas.app.R;
import com.miniblas.iu.cab.base.BaseOrdenableElementsCab;
import com.miniblas.iu.dialog.DialogEditSeekbarWidget;
import com.miniblas.iu.dialog.DialogEditSwitchWidget;
import com.miniblas.iu.dialog.DialogEditValueWidget;
import com.miniblas.model.variableWidgets.VariableSeekWidget;
import com.miniblas.model.variableWidgets.VariableSwitchWidget;
import com.miniblas.model.variableWidgets.VariableValueWidget;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;

import java.util.ArrayList;

/**
 * Created by alberto on 16/11/14.
 */
public class VariableCab extends BaseOrdenableElementsCab{

	@Override
	public CharSequence getTitle() {
		int size = getFragment().getAdapter().getSizeCurrentChecketItems();
		if (size == 1)
			return size+" "+getContext().getString(R.string.variable)+" "
					+getContext().getString(R.string.seleccionada);
		return size+" "+getContext().getString(R.string.variables)+" "
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
				BaseVariableWidget variable = null;
				for(int i = 0; i < getFragment().getListView().getCheckedItemPositions().size(); i++){
					if(getFragment().getListView().getCheckedItemPositions().valueAt(i)){
						variable = (BaseVariableWidget) getFragment().getListView().getItemAtPosition(getFragment().getListView().getCheckedItemPositions().keyAt(i));
						break;
					}
				}
				*/
				BaseVariableWidget variable = (BaseVariableWidget) getElements().get(0);
				if(variable instanceof VariableSeekWidget){
					DialogEditSeekbarWidget.newInstance(getFragment().getController(), ((VariableSeekWidget)variable), new ArrayList<String>()).show(getContext().getSupportFragmentManager(), "tag");
				}else if(variable instanceof VariableSwitchWidget){
					DialogEditSwitchWidget.newInstance(getFragment().getController(), ((VariableSwitchWidget) variable), new ArrayList<String>()).show(getContext().getSupportFragmentManager(), "tag");
				}else if(variable instanceof VariableValueWidget){
					DialogEditValueWidget.newInstance(getFragment().getController(), ((VariableValueWidget) variable), new ArrayList<String>()).show(getContext().getSupportFragmentManager(), "tag");
				}
				mode.finish();
				return true;
			default:
				return false;
		}
	}


}
