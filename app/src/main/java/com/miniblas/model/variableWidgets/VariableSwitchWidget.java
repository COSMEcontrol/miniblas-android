package com.miniblas.model.variableWidgets;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;
import com.miniblas.persistence.ormlite.Contract;


/**
 * Created by alberto on 20/03/15.
 */
@DatabaseTable(tableName = Contract.VARIABLE_WIDGET_SWITCH_NAME)
public class VariableSwitchWidget extends BaseVariableWidget{

	public static final int REQUEST_CODE_NEW_SWITCH = 12453786;

	@DatabaseField(columnName = Contract.VARIABLE_WIDGET_VALUEON)
	private String value_on;
	@DatabaseField(columnName = Contract.VARIABLE_WIDGET_VALUEOFF)
	private String value_off;


	public VariableSwitchWidget(){
		super();
	}


	public VariableSwitchWidget(String _nameVariable, String _value_on, String _value_off){
		super(_nameVariable);
		this.value_on = _value_on;
		this.value_off = _value_off;
	}

	public String getValue_on(){
		return value_on;
	}

	public void setValue_on(String value_on){
		this.value_on = value_on;
	}

	public String getValue_off(){
		return value_off;
	}

	public void setValue_off(String value_off){
		this.value_off = value_off;
	}


	@Override
	public String toString(){
		return "VariableSwitchWidget{" +
				"value_on='" + value_on + '\'' +
				", value_off='" + value_off + '\'' +
				'}'+
				"bag=" + getBag() +
				", type='" + getType() + '\'' +
				", widgetName='" + getWidgetName() + '\'' +
				", value='" + getValue() + '\'' +
				'}';
	}
}
