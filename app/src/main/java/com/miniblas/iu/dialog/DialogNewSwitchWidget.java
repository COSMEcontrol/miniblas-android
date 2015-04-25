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
import com.miniblas.iu.dialog.interfaces.IObservadorNewDialog;
import com.miniblas.iu.fragments.NewVariableElementsFragmentCab;
import com.miniblas.model.base.BaseElementList;
import com.miniblas.model.variableWidgets.VariableSwitchWidget;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DialogNewSwitchWidget extends DialogFragment{

	public static IObservadorNewDialog<VariableSwitchWidget> observador;
	public static final int RESULT_OK = 1;
	private static VariablesController controller;
	private static ArrayList<BaseVariableWidget> lista_variables;

	public static DialogNewSwitchWidget newInstance(IObservadorNewDialog _observador, ArrayList<BaseVariableWidget> _lista_variables){
		DialogNewSwitchWidget fragment = new DialogNewSwitchWidget();
		observador = _observador;
		controller= (VariablesController) _observador;
		lista_variables=_lista_variables;
		return fragment;
	}

	private AplicacionPrincipal application;
	private MaterialDialog.Builder alertBuilder;
	private MaterialDialog alertdialog;


	public static final String STATE_VALORON = "STATE_VALOR_MAX";
	public static final String STATE_VALOROFF = "STATE_VALOR_MIN";
	public static final String STATE_BUTTON_SAVE = "STATE_BUTTON_SAVE";


	private boolean bt_state = false;
	private boolean completadoValorOn = false;
	private boolean completadoValorOff = false;
	private static View bt_guardar;
	private Activity activity;
	/*
	 * Get widgets
	*/
/*
	@InjectView(R.id.et_nombre_variable)
	EditText et_nombre_variable;
*/


	@InjectView(R.id.et_switch_on)
	EditText et_switch_on;

	@InjectView(R.id.et_switch_off)
	EditText et_switch_off;



	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			bt_state = savedInstanceState.getBoolean(STATE_BUTTON_SAVE);
			completadoValorOn = savedInstanceState.getBoolean(STATE_VALORON);
			completadoValorOff = savedInstanceState.getBoolean(STATE_VALOROFF);
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
		View view = inflater.inflate(R.layout.lyt_new_switch_widget, null);
		alertBuilder.customView(view,true);
		ButterKnife.inject(this, view);
		alertBuilder.positiveText(R.string.Guardar);
		alertBuilder.negativeText(android.R.string.cancel);
		alertBuilder.callback(new MaterialDialog.ButtonCallback(){
			@Override
			public void onPositive(MaterialDialog materialDialog){
					BaseElementList<VariableSwitchWidget> switchWidgetVariable = new BaseElementList<VariableSwitchWidget>();
					String value_switch_off = et_switch_off.getText().toString();
					String value_switch_on =  et_switch_on.getText().toString();
					for(BaseVariableWidget variable:lista_variables){
						VariableSwitchWidget var = new VariableSwitchWidget(variable.getNameElement(),value_switch_on,value_switch_off);
						var.setBag(variable.getBag());
						switchWidgetVariable.add(var);
					}

					notificarOK(switchWidgetVariable);

			}

			@Override
			public void onNegative(MaterialDialog materialDialog){
				nonotificarCancel(null);
			}
		});




		et_switch_off.addTextChangedListener(new TextWatcher(){


			@Override
			public void afterTextChanged(Editable s){
					String value_switch_off = et_switch_off.getText().toString();
					String value_switch_on = et_switch_on.getText().toString();

					if(!value_switch_off.equals(value_switch_on) && !value_switch_off.isEmpty()){
					et_switch_off.setError(null);
						completadoValorOff = true;
						if(completadoValorOn && completadoValorOff){
							bt_guardar.setEnabled(true);
						}
					}else{
						completadoValorOff = false;
						bt_guardar.setEnabled(false);
						et_switch_off.setError(activity.getResources().getString(R.string.datoIncorrecto));
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
						completadoValorOn=true;
						if(completadoValorOn && completadoValorOff){
							bt_guardar.setEnabled(true);
						}
					}else{
						et_switch_on.setError(activity.getResources().getString(R.string.datoIncorrecto));
						bt_guardar.setEnabled(false);
						completadoValorOn=false;
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
		saveInstanceState.putBoolean(STATE_VALORON, completadoValorOn);
		saveInstanceState.putBoolean(STATE_VALOROFF, completadoValorOff);

	}

	public void notificarOK(BaseElementList<VariableSwitchWidget> data){
		if(observador != null){
			observador.OnButtonNewSave(data);
		}

	}

	public void nonotificarCancel(BaseElementList<VariableSwitchWidget> data){
		if(observador != null){
			observador.OnButtonNewCancel(data);
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
