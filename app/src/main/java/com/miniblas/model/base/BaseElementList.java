package com.miniblas.model.base;

import com.pedrogomez.renderers.ListAdapteeCollection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by alberto on 21/03/15.
 */
public class BaseElementList <T extends BaseElement> extends ListAdapteeCollection<T> implements Serializable{

	public BaseElementList() {
		super();
	}

	public BaseElementList(List<T> list) {
		super(list);
	}

	public Collection<String> getNameList(){
		List<String> nameElements = new ArrayList<>();
		for(T element: this){
			nameElements.add(element.getNameElement());
		}
		return nameElements;
	}
	public Collection<String> getIdsStringList(){
		List<String> idElements = new ArrayList<>();
		for(T element: this){
			idElements.add(String.valueOf(element.getId()));
		}
		return idElements;
	}



	//sobreescribir los metodos de entrada, salida y modificacion para tener la cuenta

}
