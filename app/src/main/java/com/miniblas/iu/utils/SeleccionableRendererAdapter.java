package com.miniblas.iu.utils;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.miniblas.app.R;
import com.miniblas.model.base.IOrdenableAdapteeCollection;
import com.pedrogomez.renderers.AdapteeCollection;
import com.pedrogomez.renderers.RendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;

public class SeleccionableRendererAdapter <T> extends RendererAdapter<T>{

	/** Selected items in the list */
	private int mSelection;

	public SeleccionableRendererAdapter(LayoutInflater layoutInflater, RendererBuilder rendererBuilder, AdapteeCollection<T> collection){
		super(layoutInflater, rendererBuilder, collection);
		mSelection = -1;
	}

	/**
	 * Adds an element in selection and updates the view
	 *
	 * @param position Item position
	 */
	public void addNewSelection(int position){
		mSelection = position;
	}

	/**
	 * Get current selected items
	 *
	 * @return list of items
	 */
	public T getCurrentCheckedItem(){
		return super.getItem(mSelection);
	}


	/**
	 * Clear current selection
	 */
	public void clearSelection(){
		mSelection = -1;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = super.getView(position, convertView, parent);

		v.setBackgroundColor(parent.getResources().getColor(android.R.color.transparent)); // Default color
		if(((ListView) parent).getCheckedItemPositions() != null || position == mSelection){
			SparseBooleanArray sparseBooleanArray = ((ListView) parent).getCheckedItemPositions();
			if(sparseBooleanArray.get(position) || position == mSelection){
				v.setBackgroundColor(parent.getResources().getColor(R.color.itemSelected)); // color when selected
			}

		}
		return v;
	}

	public void clearCollection(){
		super.getCollection().clear();
	}

	public void insert(T object, int index){
		((IOrdenableAdapteeCollection<T>) getCollection()).add(index, object);
	}

	public int indexOf(T element){
		return ((IOrdenableAdapteeCollection<T>) getCollection()).indexOf(element);
	}

}