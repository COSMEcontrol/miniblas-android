package com.miniblas.model.variableWidgets;

import com.j256.ormlite.table.DatabaseTable;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;
import com.miniblas.persistence.ormlite.Contract;

/**
 * Created by alberto on 21/03/15.
 */
@DatabaseTable(tableName = Contract.VARIABLE_WIDGET_VALUE_NAME)
public class VariableValueWidget extends BaseVariableWidget{

	public static final int REQUEST_CODE_NEW_VALUE = 78686;

	private int min_value;
	private int max_value;

	public VariableValueWidget(){
		super();
	}

	public VariableValueWidget(String _nombre, int _max_value,int _min_value){
		super(_nombre);
		this.min_value=_min_value;
		this.max_value=_max_value;
	}

	public int getMin_value(){
		return min_value;
	}

	public void setMin_value(int _min_value){
		this.min_value = _min_value;
	}

	public int getMax_value(){
		return max_value;
	}

	public void setMax_value(int _max_value){
		this.max_value = _max_value;
	}
}
