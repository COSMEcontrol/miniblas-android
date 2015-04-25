package com.miniblas.model.variableWidgets;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;
import com.miniblas.persistence.ormlite.Contract;

/**
 * Created by alberto on 20/03/15.
 */
@DatabaseTable(tableName = Contract.VARIABLE_WIDGET_SEEK_NAME)
public class VariableSeekWidget extends BaseVariableWidget{

	public static final int REQUEST_CODE_NEW_SEEKBAR = 1242686;

	@DatabaseField(columnName = Contract.VARIABLE_WIDGET_MAXVALUE)
	private int value_max;
	@DatabaseField(columnName = Contract.VARIABLE_WIDGET_MINVALUE)
	private int value_min;
	@DatabaseField(columnName = Contract.VARIABLE_WIDGET_SALTVALUE)
	private int value_salt;

	public VariableSeekWidget(){
		super();
	}

	public VariableSeekWidget(String _nameVariable, int _value_max, int _value_min, int _value_salt){
		super(_nameVariable);
		this.value_max = _value_max;
		this.value_min = _value_min;
		this.value_salt = _value_salt;
	}

	public int getValue_max(){
		return value_max;
	}

	public void setValue_max(int value_max){
		this.value_max = value_max;
	}

	public int getValue_min(){
		return value_min;
	}

	public void setValue_min(int value_min){
		this.value_min = value_min;
	}

	public int getValue_salt(){
		return value_salt;
	}

	public void setValue_salt(int value_salt){
		this.value_salt = value_salt;
	}


	@Override
	public String toString(){
		return "VariableSeekWidget{" +
				"value_max=" + value_max +
				", value_min=" + value_min +
				", value_salt=" + value_salt +
				'}';
	}
}
