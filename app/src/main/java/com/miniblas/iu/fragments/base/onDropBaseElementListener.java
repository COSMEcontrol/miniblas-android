package com.miniblas.iu.fragments.base;

import com.miniblas.iu.utils.SeleccionableBaseElementsListRendererAdapter;
import com.miniblas.model.base.BaseElement;
import com.mobeta.android.dslv.DragSortListView;

class onDropBaseElementListener <T extends BaseElement> implements DragSortListView.DropListener{
	public SeleccionableBaseElementsListRendererAdapter<T> adapter;


	public onDropBaseElementListener(SeleccionableBaseElementsListRendererAdapter<T> adapter){
		this.adapter = adapter;
	}


	@Override
	public void drop(int from, int to){
		if(from != to){
			T item = adapter.getItem(from);
			adapter.remove(item);
			adapter.insert(item, to);
			adapter.notifyDataSetChanged();
		}

	}

}
