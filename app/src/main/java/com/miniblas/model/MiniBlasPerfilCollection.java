package com.miniblas.model;

import com.miniblas.model.base.IOrdenableAdapteeCollection;

import java.util.Collection;
import java.util.List;

/**
 * Created by alberto on 13/11/14.
 */
public class MiniBlasPerfilCollection implements IOrdenableAdapteeCollection<MiniBlasPerfil>{
	private final List<MiniBlasPerfil> perfiles;

	public MiniBlasPerfilCollection(List<MiniBlasPerfil> perfiles){
		this.perfiles = perfiles;
	}

	@Override
	public int size(){
		return perfiles.size();
	}

	@Override
	public MiniBlasPerfil get(final int index){
		return perfiles.get(index);
	}

	@Override
	public void add(MiniBlasPerfil element){
		perfiles.add(element);
	}

	@Override
	public void remove(MiniBlasPerfil element){
		perfiles.remove(element);
	}

	@Override
	public void addAll(Collection<MiniBlasPerfil> elements){
		perfiles.addAll(elements);
	}

	@Override
	public void removeAll(Collection<MiniBlasPerfil> elements){
		perfiles.removeAll(elements);
	}

	@Override
	public void clear(){
		perfiles.clear();
	}

	@Override
	public void add(int index, MiniBlasPerfil element){
		perfiles.add(index, element);
	}

	@Override
	public int indexOf(MiniBlasPerfil variable){
		return perfiles.indexOf(variable);
	}
}
