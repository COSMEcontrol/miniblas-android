package com.miniblas.model.variableWidgets.base;

import com.j256.ormlite.field.DatabaseField;
import com.miniblas.model.MiniBlasBag;
import com.miniblas.model.base.BaseElement;
import com.miniblas.persistence.ormlite.Contract;
/**
 *
 * @author A. Azuara
 */
public class BaseVariableWidget extends BaseElement{


	@DatabaseField(columnName = Contract.VARIABLE_BAG,foreign = true, foreignAutoRefresh = true, canBeNull = true)
	private MiniBlasBag bag;
	@DatabaseField(columnName = Contract.VARIABLE_TYPE)
	private String type;
	@DatabaseField(columnName = Contract.VARIABLE_NAME)
	private String widgetName;
	private String value = "0";

	/*
		public static final MiniBlasItemVariable convertToMiniBlasVariable(ItemVariable _variable){
			MiniBlasItemVariable iv = new MiniBlasItemVariable(_variable.getNameElement());
			iv.setType(_variable.getType());
			iv.setValue(_variable.get);
			return iv;
		}

	*/
	public BaseVariableWidget(){
		super();
	}


	public BaseVariableWidget(String _nameVariable){
		super(_nameVariable);
		widgetName=_nameVariable;
	}

	public String getType(){
		return type;
	}


	public void setType(String type){
		this.type = type;
	}

	public MiniBlasBag getBag(){
		return bag;
	}


	public void setBag(MiniBlasBag bag){
		this.bag = bag;
	}


	public String getValue(){
		return value;
	}


	public void setValue(String value){
		this.value = value;
	}

	public String getWidgetName(){
		return widgetName;
	}

	public void setWidgetName(String widgetName){
		this.widgetName = widgetName;
	}
/*
	@Override
	public boolean equals(Object object){
		boolean same = false;
		if(object != null){
			same = getNameElement().equals(((BaseVariableWidget) object).getNameElement());
		}

		return same;
	}
*/
	@Override
	public String toString(){
		return "BaseVariableWidget{" +
				"bag=" + bag +
				", type='" + type + '\'' +
				", widgetName='" + widgetName + '\'' +
				", value='" + value + '\'' +
				'}';
	}
}
