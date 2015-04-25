package com.miniblas.model.base;

import com.j256.ormlite.field.DatabaseField;
import com.miniblas.persistence.ormlite.Contract;

/**
 * Created by alberto on 20/03/15.
 */
public class BaseElement implements Comparable{

	@DatabaseField(columnName = Contract.ID, generatedId = true)
	public int id;
	@DatabaseField(columnName = Contract.ORDER,index = true)
	public int order;
	@DatabaseField(columnName = Contract.NAME,canBeNull = false)
	private String nameElement;


	public BaseElement(){
		// needed by ormlite
		super();
	}

	public BaseElement(String _nameElement){
		super();
		this.nameElement = _nameElement;
	}

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getOrder(){
		return order;
	}

	public void setOrder(int _order){
		this.order = _order;
	}

	public String getNameElement(){
		return nameElement;
	}

	public void setNameElement(String _name){
		this.nameElement = _name;
	}

	@Override
	public int compareTo(Object _opject){
		BaseElement _abstacElement = (BaseElement) _opject;
		if(getOrder() < _abstacElement.getOrder()){
			return -1;
		}
		if(getOrder() == _abstacElement.getOrder()){
			return 0;
		}
		return 1;
	}
/*
	@Override
	public boolean equals(Object object){
		boolean same = false;
		if(object != null){
			same = nameElement.equals(((BaseElement) object).getNameElement());
		}

		return same;
	}
	*/
}
