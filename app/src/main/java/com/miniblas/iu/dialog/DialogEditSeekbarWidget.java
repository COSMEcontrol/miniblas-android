package com.miniblas.iu.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.dialog.interfaces.IObservadorEditDialog;
import com.miniblas.model.base.BaseElementList;
import com.miniblas.model.variableWidgets.VariableSeekWidget;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
/**
 *
 * @author A. Azuara
 */
public class DialogEditSeekbarWidget extends DialogFragment{


	public static IObservadorEditDialog<VariableSeekWidget> observador;
	public static final int RESULT_OK = 1;
	//private static VariablesController controller;
	private static VariableSeekWidget variable;
	public static ArrayList<String> nameList;


	public static DialogEditSeekbarWidget newInstance(IObservadorEditDialog _observador, VariableSeekWidget _variable, ArrayList<String> _variableList){
		DialogEditSeekbarWidget fragment = new DialogEditSeekbarWidget();
		observador = _observador;
		nameList=_variableList;
		variable=_variable;
		return fragment;
	}

	private AplicacionPrincipal application;
	private MaterialDialog.Builder alertBuilder;
	private MaterialDialog alertdialog;


	public static final String STATE_BUTTON_SAVE = "STATE_BUTTON_SAVE";
	public static final String STATE_VALUE_NAME = "STATE_VALUE_NAME";
	public static final String STATE_VALUE_MAX= "STATE_VALUE_MAX";
	public static final String STATE_VALUE_MIN = "STATE_VALUE_MIN";
	public static final String STATE_SALT = "STATE_SALT";
	public static final String VARIABLE_LIST = "VARIABLE_LIST";

	private boolean bt_save_state = false;
	private boolean completeName = true;
	private boolean completeValueMax = true;
	private boolean completeValueMin = true;
	private boolean completeSalt = true;
	private static View bt_save;
	private Activity activity;

	/*
	 * Get widgets
	*/
	@InjectView(R.id.et_name)
	EditText et_name;

	@InjectView(R.id.et_valor_max_seekbar)
	EditText et_valor_max_seekbar;

	@InjectView(R.id.et_valor_min_seekbar)
	EditText et_valor_min_seekbar;

	@InjectView(R.id.et_salto_seekbar)
	EditText et_salto_seekbar;


	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			bt_save_state = savedInstanceState.getBoolean(STATE_BUTTON_SAVE);
			completeSalt = savedInstanceState.getBoolean(STATE_SALT);
			completeValueMax = savedInstanceState.getBoolean(STATE_VALUE_MAX);
			completeValueMin = savedInstanceState.getBoolean(STATE_VALUE_MIN);
			completeName= savedInstanceState.getBoolean(STATE_VALUE_NAME);
			nameList= savedInstanceState.getStringArrayList(VARIABLE_LIST);
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
		alertBuilder.title(activity.getResources().getString(R.string.new_seekbar_widget));

		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.lyt_edit_seekbar_widget, null);
		alertBuilder.customView(view,true);
		ButterKnife.inject(this, view);
		alertBuilder.positiveText(R.string.Guardar);
		alertBuilder.negativeText(android.R.string.cancel);
		et_name.setText(variable.getWidgetName());
		et_valor_max_seekbar.setText(String.valueOf(variable.getValue_max()));
		et_valor_min_seekbar.setText(String.valueOf(variable.getValue_min()));
		et_salto_seekbar.setText(String.valueOf(variable.getValue_salt()));

		alertBuilder.callback(new MaterialDialog.ButtonCallback(){
			@Override
			public void onPositive(MaterialDialog materialDialog){
				BaseElementList<VariableSeekWidget> list_variables = new BaseElementList<VariableSeekWidget>();
				String nom_widget = et_name.getText().toString();
				int value_max_seekbar = Integer.valueOf(et_valor_max_seekbar.getText().toString());
				int value_min_seekbar = Integer.valueOf(et_valor_min_seekbar.getText().toString());
				int value_sal_seekbar = Integer.valueOf(et_salto_seekbar.getText().toString());
				variable.setWidgetName(nom_widget);
				variable.setValue_max(value_max_seekbar);
				variable.setValue_min(value_min_seekbar);
				variable.setValue_salt(value_sal_seekbar);
				list_variables.add(variable);
				notificarOK(list_variables);
			}

			@Override
			public void onNegative(MaterialDialog materialDialog){
				notificarCancel(null);
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
					if(completeValueMax && completeValueMin && completeSalt && completeName){
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
		et_valor_max_seekbar.addTextChangedListener(new TextWatcher(){


			@Override
			public void afterTextChanged(Editable s){
				int value_max_seekbar = 0;
				int value_min_seekbar = 0;
				try{
					value_min_seekbar = Integer.valueOf(et_valor_min_seekbar.getText().toString());
				}catch(NumberFormatException e){
					value_min_seekbar = 0;
				}
				try{
					value_max_seekbar = Integer.valueOf(et_valor_max_seekbar.getText().toString());

					if(value_max_seekbar > value_min_seekbar && value_max_seekbar != value_min_seekbar){
						completeValueMax = true;
						et_valor_max_seekbar.setError(null);
						if(completeValueMax && completeValueMin && completeSalt && completeName){
							bt_save.setEnabled(true);
						}
					}else{
						throw new NumberFormatException();
					}

				}catch(NumberFormatException e){
					et_valor_max_seekbar.setError(activity.getResources().getString(R.string.datoIncorrecto));
					bt_save.setEnabled(false);
					completeValueMax = false;
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
		et_valor_min_seekbar.addTextChangedListener(new TextWatcher(){


			@Override
			public void afterTextChanged(Editable s){
				int value_max_seekbar=0;
				int value_min_seekbar=0;
				try{
					value_max_seekbar = Integer.valueOf(et_valor_max_seekbar.getText().toString());
				}catch(NumberFormatException e){
					value_max_seekbar=0;
				}
				try{

					value_min_seekbar = Integer.valueOf(et_valor_min_seekbar.getText().toString());

					if(value_min_seekbar<value_max_seekbar && value_max_seekbar != value_min_seekbar){
						completeValueMin = true;
						et_valor_min_seekbar.setError(null);
						if(completeValueMax && completeValueMin && completeSalt && completeName){
							bt_save.setEnabled(true);
						}
					}else{
						throw new NumberFormatException();
					}

				}catch(NumberFormatException e){
					et_valor_min_seekbar.setError(activity.getResources().getString(R.string.datoIncorrecto));
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
		et_salto_seekbar.addTextChangedListener(new TextWatcher(){

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
				int value_sal_seekbar=0;
				int value_max_seekbar=0;
				int value_min_seekbar=0;
				try{
					value_max_seekbar = Integer.valueOf(et_valor_max_seekbar.getText().toString());
				}catch(NumberFormatException e){
					value_max_seekbar=0;
				}
				try{
					value_min_seekbar = Integer.valueOf(et_valor_min_seekbar.getText().toString());
				}catch(NumberFormatException e){
					value_min_seekbar = 0;
				}
				try{
					value_sal_seekbar = Integer.valueOf(et_salto_seekbar.getText().toString());

					if((value_max_seekbar-value_min_seekbar)>value_sal_seekbar&&value_sal_seekbar > 0){
						completeSalt = true;
						et_salto_seekbar.setError(null);
						if(completeValueMax && completeValueMin && completeSalt && completeName){
							bt_save.setEnabled(true);
						}
					}else{
						throw new NumberFormatException();
					}

				}catch(Exception e){
					et_salto_seekbar.setError(activity.getResources().getString(R.string.datoIncorrecto));
					bt_save.setEnabled(false);
					completeSalt = false;
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
		bt_save.setEnabled(bt_save_state);
	}

	public void onSaveInstanceState(Bundle saveInstanceState){
		super.onSaveInstanceState(saveInstanceState);
		saveInstanceState.putBoolean(STATE_BUTTON_SAVE, bt_save.isEnabled());
		saveInstanceState.putBoolean(STATE_VALUE_MAX, completeValueMax);
		saveInstanceState.putBoolean(STATE_VALUE_MIN, completeValueMin);
		saveInstanceState.putBoolean(STATE_SALT, completeSalt);
		saveInstanceState.putBoolean(STATE_VALUE_NAME, completeName);
		saveInstanceState.putStringArrayList(VARIABLE_LIST,nameList);
	}

	public void notificarOK(final BaseElementList<VariableSeekWidget> data){
		if(observador != null){
			observador.OnButtonEditSave(data);
		}

	}

	public void notificarCancel(final BaseElementList<VariableSeekWidget> data){
		if(observador != null){
			observador.OnButtonEditCancel(data);
		}
	}

}
