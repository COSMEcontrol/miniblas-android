package com.miniblas.iu.fragments.base;

import com.miniblas.iu.utils.SeleccionableRendererAdapter;
import com.mobeta.android.dslv.DragSortListView;

public class onDropListener <T> implements DragSortListView.DropListener{
	public SeleccionableRendererAdapter<T> adapter;


	public onDropListener(SeleccionableRendererAdapter<T> adapter){
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
