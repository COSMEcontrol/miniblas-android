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
import com.miniblas.model.variableWidgets.VariableValueWidget;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
/**
 *
 * @author A. Azuara
 */
public class DialogEditValueWidget extends DialogFragment{

	public static IObservadorEditDialog<VariableValueWidget> observador;
	public static final int RESULT_OK = 1;
	private static VariablesController controller;
	private static VariableValueWidget variable;
	public static ArrayList<String> nameList;

	public static DialogEditValueWidget newInstance(IObservadorEditDialog _observador, VariableValueWidget _variable, ArrayList<String> _nameList){
		DialogEditValueWidget fragment = new DialogEditValueWidget();
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
	public static final String STATE_VALOR_MAX = "STATE_VALOR_MAX";
	public static final String STATE_VALOR_MIN = "STATE_VALOR_MIN";
	public static final String STATE_BUTTON_SAVE = "STATE_BUTTON_SAVE";
	public static final String STATE_VALUE_NAME_LIST = "STATE_VALUE_NAME_LIST";


	private boolean bt_state = false;
	private boolean completeName = true;
	private boolean completeValueMax = true;
	private boolean completeValueMin = true;
	private static View bt_save;
	private Activity activity;
	/*
	 * Get widgets
	*/

	@InjectView(R.id.et_name)
	EditText et_name;

	@InjectView(R.id.et_switch_max)
	EditText et_value_max;

	@InjectView(R.id.et_switch_min)
	EditText et_value_min;



	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			bt_state = savedInstanceState.getBoolean(STATE_BUTTON_SAVE);
			completeValueMax = savedInstanceState.getBoolean(STATE_VALOR_MAX);
			completeValueMin = savedInstanceState.getBoolean(STATE_VALOR_MIN);
			completeName = savedInstanceState.getBoolean(STATE_VALUE_NAME);
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
		View view = inflater.inflate(R.layout.lyt_edit_visualizador_widget, null);
		alertBuilder.customView(view,true);
		ButterKnife.inject(this, view);
		alertBuilder.positiveText(R.string.Guardar);
		alertBuilder.negativeText(android.R.string.cancel);
		et_name.setText(variable.getWidgetName());
		et_value_max.setText(String.valueOf(variable.getMax_value()));
		et_value_min.setText(String.valueOf(variable.getMin_value()));
		alertBuilder.callback(new MaterialDialog.ButtonCallback(){
			@Override
			public void onPositive(MaterialDialog materialDialog){
				BaseElementList<VariableValueWidget> valueWidgetVariable = new BaseElementList<VariableValueWidget>();
				String name_value = et_name.getText().toString();
				int value_max = Integer.valueOf(et_value_max.getText().toString());
				int value_min = Integer.valueOf(et_value_min.getText().toString());
				variable.setWidgetName(name_value);
				variable.setMax_value(value_max);
				variable.setMin_value(value_min);
				valueWidgetVariable.add(variable);
				notificarOK(valueWidgetVariable);
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
					bt_save.setEnabled(false);
				}else if(value_name.isEmpty()){
					completeName = false;
					et_name.setError(activity.getResources().getString(R.string.requiereNombre));
					bt_save.setEnabled(false);
				}else{
					et_name.setError(null);
					completeName = true;
					if(completeValueMax && completeValueMin && completeName){
						bt_save.setEnabled(true);
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

		et_value_min.addTextChangedListener(new TextWatcher(){


			@Override
			public void afterTextChanged(Editable s){
				int value_max = 0;
				int value_min = 0;
				try{
					value_max = Integer.valueOf(et_value_max.getText().toString());
				}catch(NumberFormatException e){
					value_max=0;
				}
				try{
					value_min = Integer.valueOf(et_value_min.getText().toString());

					if(value_min < value_max && value_max != value_min){
						completeValueMin = true;
						et_value_min.setError(null);
						if(completeValueMax && completeValueMin && completeName){
							bt_save.setEnabled(true);
						}
					}else{
						throw new NumberFormatException();
					}

				}catch(NumberFormatException e){
					et_value_min.setError(activity.getResources().getString(R.string.datoIncorrecto));
					bt_save.setEnabled(false);
					completeValueMin = false;
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
		et_value_max.addTextChangedListener(new TextWatcher(){

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
				int value_max = 0;
				int value_min = 0;
				try{
					value_min = Integer.valueOf(et_value_min.getText().toString());
				}catch(NumberFormatException e){
					value_min=0;
				}
				try{
					value_max = Integer.valueOf(et_value_max.getText().toString());

					if(value_max > value_min && value_max != value_min){
						completeValueMax = true;
						et_value_max.setError(null);
						if(completeValueMax && completeValueMin && completeName){
							bt_save.setEnabled(true);
						}
					}else{
						throw new NumberFormatException();
					}

				}catch(Exception e){
					et_value_max.setError(activity.getResources().getString(R.string.datoIncorrecto));
					bt_save.setEnabled(false);
					completeValueMax = false;
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
		bt_save = this.alertdialog.getActionButton(DialogAction.POSITIVE);
		bt_save.setEnabled(bt_state);
	}

	public void onSaveInstanceState(Bundle saveInstanceState){
		super.onSaveInstanceState(saveInstanceState);
		saveInstanceState.putBoolean(STATE_BUTTON_SAVE, bt_save.isEnabled());
		saveInstanceState.putBoolean(STATE_VALOR_MAX, completeValueMax);
		saveInstanceState.putBoolean(STATE_VALOR_MIN, completeValueMin);
		saveInstanceState.putBoolean(STATE_VALUE_NAME, completeName);
		saveInstanceState.putStringArrayList(STATE_VALUE_NAME_LIST, nameList);
	}

	public void notificarOK(BaseElementList<VariableValueWidget> data){
		if(observador != null){
			observador.OnButtonEditSave(data);
		}

	}

	public void nonotificarCancel(BaseElementList<VariableValueWidget> data){
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
