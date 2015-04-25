package com.miniblas.persistence.ormlite;

public class Contract{

	/**
	 * Constantes de la base de datos
	 */



	/*
		Abstract element
	 */
	public static final String ID = "_id";
	public static final String ORDER = "Order";
	public static final String NAME = "Name";


	/*
		Profile table
	 */
	public static final String PROFILE_TABLE_NAME = "Profile";
	public static final String PROFILE_IP = "Ip";
	public static final String PROFILE_PORT = "Port";
	public static final String PROFILE_PASSWORD = "Password";
	/*
		Basket table
	 */
	public static final String BASKET_TABLE_NAME = "Basket";
	public static final String BASKET_REFRESHPERIOD = "RefreshPeriod";
	public static final String BASKET_PROFILE = "Profile";
	public static final String BASKET_VARIABLE_LIST = "ListVariable";
	/*
		Base variable
	 */
	public static final String VARIABLE_BAG = "VariableBag";
	public static final String VARIABLE_TYPE = "Type";
	public static final String VARIABLE_NAME = "VariableName";
	/*
		VariableWidgetSwitchWidget
	 */
	public static final String VARIABLE_WIDGET_SWITCH_NAME = "SwitchWidget";
	public static final String VARIABLE_WIDGET_VALUEON = "ValueOn";
	public static final String VARIABLE_WIDGET_VALUEOFF = "ValueOff";
	/*
		VariableWidgetSeekWidget
	 */
	public static final String VARIABLE_WIDGET_SEEK_NAME = "SeekWidget";
	public static final String VARIABLE_WIDGET_MAXVALUE = "MaxValue";
	public static final String VARIABLE_WIDGET_MINVALUE = "MinValue";
	public static final String VARIABLE_WIDGET_SALTVALUE = "SaltValue";
	/*
		VariableValueWidget
	 */
	public static final String VARIABLE_WIDGET_VALUE_NAME = "ValueWidget";



}