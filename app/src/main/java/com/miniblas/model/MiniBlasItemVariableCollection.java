package com.miniblas.model;

import com.miniblas.model.base.IOrdenableAdapteeCollection;

import java.util.Collection;
import java.util.List;

/**
 * Created by alberto on 13/11/14.
 */
public class MiniBlasItemVariableCollection implements IOrdenableAdapteeCollection<MiniBlasItemVariable>{
	private final List<MiniBlasItemVariable> itemVariables;

	public MiniBlasItemVariableCollection(List<MiniBlasItemVariable> itemVariables){
		this.itemVariables = itemVariables;
	}

	@Override
	public int size(){
		return itemVariables.size();
	}

	@Override
	public MiniBlasItemVariable get(final int index){
		return itemVariables.get(index);
	}

	@Override
	public void add(MiniBlasItemVariable element){
		itemVariables.add(element);
	}

	@Override
	public void remove(MiniBlasItemVariable element){
		itemVariables.remove(element);
	}

	@Override
	public void addAll(Collection<MiniBlasItemVariable> elements){
		itemVariables.addAll(elements);
	}

	@Override
	public void removeAll(Collection<MiniBlasItemVariable> elements){
		itemVariables.removeAll(elements);
	}

	@Override
	public void clear(){
		itemVariables.clear();
	}


	@Override
	public void add(int index, MiniBlasItemVariable element){
		itemVariables.add(index, element);
	}

	@Override
	public int indexOf(MiniBlasItemVariable variable){
		return itemVariables.indexOf(variable);
	}
}
