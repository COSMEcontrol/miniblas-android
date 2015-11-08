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
import com.miniblas.model.variableWidgets.VariableValueWidget;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
/**
 *
 * @author A. Azuara
 */
public class DialogNewValueWidget extends DialogFragment{

	public static IObservadorNewDialog<VariableValueWidget> observador;
	public static final int RESULT_OK = 1;
	private static VariablesController controller;
	private static ArrayList<BaseVariableWidget> variableList;

	public static DialogNewValueWidget newInstance(IObservadorNewDialog _observador, ArrayList<BaseVariableWidget> _variableList){
		DialogNewValueWidget fragment = new DialogNewValueWidget();
		observador = _observador;
		controller= (VariablesController) _observador;
		variableList =_variableList;
		return fragment;
	}

	private AplicacionPrincipal application;
	private MaterialDialog.Builder alertBuilder;
	private MaterialDialog alertdialog;


	public static final String STATE_VALOR_MAX = "STATE_VALOR_MAX";
	public static final String STATE_VALOR_MIN = "STATE_VALOR_MIN";
	public static final String STATE_BUTTON_SAVE = "STATE_BUTTON_SAVE";


	private boolean bt_state = false;
	private boolean completadoValorMax = false;
	private boolean completadoValorMin = false;
	private static View bt_guardar;
	private Activity activity;
	/*
	 * Get widgets
	*/
/*
	@InjectView(R.id.et_nombre_variable)
	EditText et_nombre_variable;
*/


	@InjectView(R.id.et_switch_max)
	EditText et_switch_max;

	@InjectView(R.id.et_switch_min)
	EditText et_switch_min;



	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			bt_state = savedInstanceState.getBoolean(STATE_BUTTON_SAVE);
			completadoValorMax = savedInstanceState.getBoolean(STATE_VALOR_MAX);
			completadoValorMin = savedInstanceState.getBoolean(STATE_VALOR_MIN);
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
		View view = inflater.inflate(R.layout.lyt_new_visualizador_widget, null);
		alertBuilder.customView(view,true);
		ButterKnife.inject(this, view);
		alertBuilder.positiveText(R.string.Guardar);
		alertBuilder.negativeText(android.R.string.cancel);
		alertBuilder.callback(new MaterialDialog.ButtonCallback(){
			@Override
			public void onPositive(MaterialDialog materialDialog){
				BaseElementList<VariableValueWidget> valueWidgetVariable = new BaseElementList<VariableValueWidget>();
				int value_max = Integer.valueOf(et_switch_max.getText().toString());
				int value_min = Integer.valueOf(et_switch_min.getText().toString());
				for(BaseVariableWidget variable: variableList){
					VariableValueWidget var = new VariableValueWidget(variable.getNameElement(),value_max,value_min);
					var.setBag(variable.getBag());
					valueWidgetVariable.add(var);
				}

				notificarOK(valueWidgetVariable);

			}

			@Override
			public void onNegative(MaterialDialog materialDialog){
				nonotificarCancel(null);
			}
		});




		et_switch_min.addTextChangedListener(new TextWatcher(){


			@Override
			public void afterTextChanged(Editable s){
				int value_max = 0;
				int value_min = 0;
				try{
					value_max = Integer.valueOf(et_switch_max.getText().toString());
				}catch(NumberFormatException e){
					value_max = 0;
				}
				try{
					value_min = Integer.valueOf(et_switch_min.getText().toString());

					if(value_min < value_max && value_max != value_min){
						completadoValorMin = true;
						et_switch_min.setError(null);
						if(completadoValorMax && completadoValorMin){
							bt_guardar.setEnabled(true);
						}
					}else{
						throw new NumberFormatException();
					}

				}catch(NumberFormatException e){
					et_switch_min.setError(activity.getResources().getString(R.string.datoIncorrecto));
					bt_guardar.setEnabled(false);
					completadoValorMin = false;
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
		et_switch_max.addTextChangedListener(new TextWatcher(){

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
				int value_max = 0;
				int value_min = 0;
				try{
					value_min = Integer.valueOf(et_switch_min.getText().toString());
				}catch(NumberFormatException e){
					value_min = 0;
				}
				try{
					value_max = Integer.valueOf(et_switch_max.getText().toString());

					if(value_max > value_min && value_max != value_min){
						completadoValorMax = true;
						et_switch_max.setError(null);
						if(completadoValorMax && completadoValorMin){
							bt_guardar.setEnabled(true);
						}
					}else{
						throw new NumberFormatException();
					}

				}catch(NumberFormatException e){
					et_switch_max.setError(activity.getResources().getString(R.string.datoIncorrecto));
					bt_guardar.setEnabled(false);
					completadoValorMax = false;
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
		saveInstanceState.putBoolean(STATE_VALOR_MAX, completadoValorMax);
		saveInstanceState.putBoolean(STATE_VALOR_MIN, completadoValorMin);

	}

	public void notificarOK(BaseElementList<VariableValueWidget> data){
		if(observador != null){
			observador.OnButtonNewSave(data);
		}

	}

	public void nonotificarCancel(BaseElementList<VariableValueWidget> data){
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
