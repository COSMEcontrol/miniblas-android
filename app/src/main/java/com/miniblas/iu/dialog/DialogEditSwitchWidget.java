package com.miniblas.iu.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.controllers.VariablesController;
import com.miniblas.iu.dialog.interfaces.IObservadorEditDialog;
import com.miniblas.iu.fragments.NewVariableElementsFragmentCab;
import com.miniblas.model.base.BaseElementList;
import com.miniblas.model.variableWidgets.VariableSwitchWidget;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DialogEditSwitchWidget extends DialogFragment{

	public static IObservadorEditDialog<VariableSwitchWidget> observador;
	public static final int RESULT_OK = 1;
	private static VariablesController controller;
	private static VariableSwitchWidget variable;
	public static ArrayList<String> nameList;

	public static DialogEditSwitchWidget newInstance(IObservadorEditDialog _observador, VariableSwitchWidget _variable, ArrayList<String> _nameList){
		DialogEditSwitchWidget fragment = new DialogEditSwitchWidget();
		observador = _observador;
		controller= (VariablesController) _observador;
		nameList=_nameList;
		variable=_variable;
		return fragment;
	}

	private AplicacionPrincipal application;
	private MaterialDialog.Builder alertBuilder;
	private MaterialDialog alertdialog;

	public static final String STATE_VALUE_NAME = "STATE_VALUE_NAME";
	public static final String STATE_VALORON = "STATE_VALOR_MAX";
	public static final String STATE_VALOROFF = "STATE_VALOR_MIN";
	public static final String STATE_BUTTON_SAVE = "STATE_BUTTON_SAVE";
	public static final String STATE_LIST_VARIABLES = "STATE_LIST_VARIABLES";


	private boolean bt_state = false;
	private boolean completeName = true;
	private boolean completeValueOn = true;
	private boolean completeValueOff = true;
	private static View bt_guardar;
	private Activity activity;
	/*
	 * Get widgets
	*/

	@InjectView(R.id.et_name)
	EditText et_name;

	@InjectView(R.id.et_switch_on)
	EditText et_switch_on;

	@InjectView(R.id.et_switch_off)
	EditText et_switch_off;



	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			bt_state = savedInstanceState.getBoolean(STATE_BUTTON_SAVE);
			completeValueOn = savedInstanceState.getBoolean(STATE_VALORON);
			completeValueOff = savedInstanceState.getBoolean(STATE_VALOROFF);
			completeName = savedInstanceState.getBoolean(STATE_VALUE_NAME);
			nameList = savedInstanceState.getStringArrayList(STATE_LIST_VARIABLES);
		}
	}

	@Override
	public void onAttach(Activity _activity){
		super.onAttach(_activity);
		this.activity = _activity;
		application = (AplicacionPrincipal) _activity.getApplicationContext();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		alertBuilder = new MaterialDialog.Builder(activity);
		alertBuilder.title(activity.getResources().getString(R.string.new_switch));

		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.lyt_edit_switch_widget, null);
		alertBuilder.customView(view,true);
		ButterKnife.inject(this, view);
		alertBuilder.positiveText(R.string.Guardar);
		alertBuilder.negativeText(android.R.string.cancel);
		et_name.setText(variable.getWidgetName());
		et_switch_on.setText(variable.getValue_on());
		et_switch_off.setText(variable.getValue_off());
		alertBuilder.callback(new MaterialDialog.ButtonCallback(){
			@Override
			public void onPositive(MaterialDialog materialDialog){
				BaseElementList<VariableSwitchWidget> switchWidgetVariable = new BaseElementList<VariableSwitchWidget>();
				String name_value = et_name.getText().toString();
				String value_switch_off = et_switch_off.getText().toString();
				String value_switch_on = et_switch_on.getText().toString();
				variable.setWidgetName(name_value);
				variable.setValue_on(value_switch_on);
				variable.setValue_off(value_switch_off);
				switchWidgetVariable.add(variable);
				notificarOK(switchWidgetVariable);

			}

			@Override
			public void onNegative(MaterialDialog materialDialog){
				nonotificarCancel(null);
			}
		});


		et_name.addTextChangedListener(new TextWatcher(){

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
				String value_name = et_name.getText().toString();

				if(nameList.contains(value_name)){
					completeName = false;
					et_name.setError(activity.getResources().getString(R.string.existeNombre));
					bt_guardar.setEnabled(false);
				}else if(value_name.isEmpty()){
					completeName = false;
					et_name.setError(activity.getResources().getString(R.string.requiereNombre));
					bt_guardar.setEnabled(false);
				}else{
					et_name.setError(null);
					completeName = true;
					if(completeValueOn && completeValueOff && completeName){
						bt_guardar.setEnabled(true);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s){
				// TODO Auto-generated method stub
			}
		});

		et_switch_off.addTextChangedListener(new TextWatcher(){


			@Override
			public void afterTextChanged(Editable s){
					String value_switch_off = et_switch_off.getText().toString();
					String value_switch_on = et_switch_on.getText().toString();

					if(!value_switch_off.equals(value_switch_on) && !value_switch_off.isEmpty()){
						et_switch_off.setError(null);
						completeValueOff = true;
						if(completeValueOn && completeValueOff && completeName){
							bt_guardar.setEnabled(true);
						}
					}else{
						et_switch_off.setError(activity.getResources().getString(R.string.datoIncorrecto));
						completeValueOff = false;
						bt_guardar.setEnabled(false);
					}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
				// TODO Auto-generated method stub
			}
		});
		et_switch_on.addTextChangedListener(new TextWatcher(){

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
					String value_switch_off = et_switch_off.getText().toString();
					String value_switch_on = et_switch_on.getText().toString();

					if(!value_switch_on.equals(value_switch_off) && !value_switch_on.isEmpty()){
						et_switch_off.setError(null);
						completeValueOn =true;
						if(completeValueOn && completeValueOff && completeName){
							bt_guardar.setEnabled(true);
						}
					}else{
						et_switch_on.setError(activity.getResources().getString(R.string.datoIncorrecto));
						bt_guardar.setEnabled(false);
						completeValueOn =false;
					}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){
				// TODO Auto-generated method stub		
			}

			@Override
			public void afterTextChanged(Editable s){
				// TODO Auto-generated method stub
			}
		});

		alertdialog = alertBuilder.build();
		return alertdialog;
	}

	@Override
	public void onResume(){
		super.onResume();
		bt_guardar = this.alertdialog.getActionButton(DialogAction.POSITIVE);
		bt_guardar.setEnabled(bt_state);
	}

	public void onSaveInstanceState(Bundle saveInstanceState){
		super.onSaveInstanceState(saveInstanceState);
		saveInstanceState.putBoolean(STATE_BUTTON_SAVE, bt_guardar.isEnabled());
		saveInstanceState.putBoolean(STATE_VALORON, completeValueOn);
		saveInstanceState.putBoolean(STATE_VALOROFF, completeValueOff);
		saveInstanceState.putBoolean(STATE_VALUE_NAME, completeName);
		saveInstanceState.putStringArrayList(STATE_LIST_VARIABLES, nameList);
	}

	public void notificarOK(BaseElementList<VariableSwitchWidget> data){
		if(observador != null){
			observador.OnButtonEditSave(data);
		}

	}

	public void nonotificarCancel(BaseElementList<VariableSwitchWidget> data){
		if(observador != null){
			observador.OnButtonEditCancel(data);
		}
	}
	public void gotoNewVariableFragment(){

		//getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		getFragmentManager().executePendingTransactions();
		FragmentTransaction trans = getFragmentManager().beginTransaction();
		trans.setCustomAnimations(R.anim.left_in, R.anim.left_out, R.anim.right_in, R.anim.right_out);
		NewVariableElementsFragmentCab fragment = new NewVariableElementsFragmentCab();
		//setTargetFragment(fragment, REQUEST_CODE);
		trans.replace(R.id.container, fragment);
		trans.addToBackStack(null);
		trans.commit();
	}
}
