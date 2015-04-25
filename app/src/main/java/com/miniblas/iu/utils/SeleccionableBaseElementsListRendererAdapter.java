package com.miniblas.iu.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miniblas.app.R;
import com.miniblas.model.base.BaseElement;
import com.miniblas.model.base.BaseElementList;
import com.pedrogomez.renderers.AdapteeCollection;
import com.pedrogomez.renderers.RendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class SeleccionableBaseElementsListRendererAdapter <T extends BaseElement> extends RendererAdapter<T> implements StickyListHeadersAdapter{

	/** Selected items in the list */
	private BaseElementList mSelection;
	private String[] countries = {"teruel", "zaragoza"};
	private LayoutInflater inflater;

	public SeleccionableBaseElementsListRendererAdapter(LayoutInflater layoutInflater, RendererBuilder rendererBuilder, AdapteeCollection<T> collection){
		super(layoutInflater, rendererBuilder, collection);
		mSelection = new BaseElementList();
		inflater = layoutInflater;
	}

	/**
	 * Adds an element in selection and updates the view
	 *
	 * @param _element Item position
	 */
	public void addNewSelection(BaseElement _element){
		mSelection.add(_element);
		notifyDataSetChanged();
	}
	public void removeSelection(BaseElement _element){
		mSelection.remove(_element);
		notifyDataSetChanged();
	}

	/**
	 * Get current selected items
	 *
	 * @return list of items
	 */
	public BaseElementList<T> getCurrentCheckedItems(){
		return mSelection;
	}

	public BaseElementList<T> getElements(){
		return (BaseElementList) getCollection();
	}
	public boolean isSelected(BaseElement _eleElement){
		return mSelection.contains(_eleElement);
	}

	/**
	 * Clear current selection
	 */
	public void clearSelection(){
		mSelection.clear();
	}
	public int getSizeCurrentChecketItems(){
		return mSelection.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = super.getView(position, convertView, parent);
		if(mSelection.size() > 0 && mSelection.contains(getCollection().get(position))){
			v.setBackgroundColor(parent.getResources().getColor(R.color.itemSelected)); // color when selected
		}else{
			v.setBackgroundColor(parent.getResources().getColor(android.R.color.transparent)); // Default color
		}
		/*
		v.setBackgroundColor(parent.getResources().getColor(android.R.color.transparent)); // Default color
		if(((ListView) parent).getCheckedItemPositions() != null || position == mSelection){
			SparseBooleanArray sparseBooleanArray = ((ListView) parent).getCheckedItemPositions();
			if(sparseBooleanArray.get(position) || position == mSelection){
				v.setBackgroundColor(parent.getResources().getColor(R.color.itemSelected)); // color when selected
			}
		}
		*/
		return v;
	}
	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder holder;
		if (convertView == null) {
			holder = new HeaderViewHolder();
			convertView = inflater.inflate(R.layout.header, parent, false);
			holder.text = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (HeaderViewHolder) convertView.getTag();
		}
		//set header text as first char in name
		String headerText = "" + countries[position].subSequence(0, 1).charAt(0);
		holder.text.setText(headerText);
		return convertView;
	}
	class HeaderViewHolder {
		TextView text;
	}

	@Override
	public long getHeaderId(int position){
		return countries[position].subSequence(0, 1).charAt(0);
	}

	public void clearCollection(){
		super.getCollection().clear();
	}

	public void insert(T object, int index){
		((BaseElementList<T>) getCollection()).add(index, object);
	}

	public int indexOf(T element){
		return ((BaseElementList<T>) getCollection()).indexOf(element);
	}

}