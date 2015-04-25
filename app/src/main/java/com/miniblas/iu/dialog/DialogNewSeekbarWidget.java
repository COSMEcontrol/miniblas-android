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
import com.miniblas.iu.dialog.interfaces.IObservadorNewDialog;
import com.miniblas.model.base.BaseElementList;
import com.miniblas.model.variableWidgets.VariableSeekWidget;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DialogNewSeekbarWidget extends DialogFragment{


	public static IObservadorNewDialog<VariableSeekWidget> observador;
	public static final int RESULT_OK = 1;
	private static ArrayList<BaseVariableWidget> lista_variables;


	public static DialogNewSeekbarWidget newInstance(IObservadorNewDialog _observador, ArrayList<BaseVariableWidget> _lista_variables){
		DialogNewSeekbarWidget fragment = new DialogNewSeekbarWidget();
		observador = _observador;
		//controller= (VariablesController) _observador;
		lista_variables=_lista_variables;
		return fragment;
	}

	private AplicacionPrincipal application;
	private MaterialDialog.Builder alertBuilder;
	private MaterialDialog alertdialog;


	public static final String STATE_BUTTON_SAVE = "STATE_BUTTON_SAVE";
	public static final String STATE_VALUE_MAX= "STATE_VALUE_MAX";
	public static final String STATE_VALUE_MIN = "STATE_VALUE_MIN";
	public static final String STATE_SALT = "STATE_SALT";

	private boolean bt_save_state = false;
	private boolean completeValueMax = false;
	private boolean completeValueMin = false;
	private boolean completeSalt = false;
	private static View bt_save;
	private Activity activity;

	/*
	 * Get widgets
	*/
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
		View view = inflater.inflate(R.layout.lyt_new_seekbar_widget, null);
		alertBuilder.customView(view,true);
		ButterKnife.inject(this, view);
		alertBuilder.positiveText(R.string.Guardar);
		alertBuilder.negativeText(android.R.string.cancel);
		alertBuilder.callback(new MaterialDialog.ButtonCallback(){
			@Override
			public void onPositive(MaterialDialog materialDialog){
				BaseElementList<VariableSeekWidget> seekWidgetVariable = new BaseElementList<VariableSeekWidget>();
				int value_max_seekbar = Integer.valueOf(et_valor_max_seekbar.getText().toString());
				int value_min_seekbar = Integer.valueOf(et_valor_min_seekbar.getText().toString());
				int value_sal_seekbar = Integer.valueOf(et_salto_seekbar.getText().toString());
				for(BaseVariableWidget variable:lista_variables){
					VariableSeekWidget var = new VariableSeekWidget(variable.getNameElement(),value_max_seekbar,value_min_seekbar,value_sal_seekbar);
					var.setBag(variable.getBag());
					seekWidgetVariable.add(var);
				}

				notificarOK(seekWidgetVariable);
			}

			@Override
			public void onNegative(MaterialDialog materialDialog){
				notificarCancel(null);
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

					if(value_max_seekbar > value_min_seekbar){
						completeValueMax = true;
						et_valor_max_seekbar.setError(null);
						if(completeValueMax && completeValueMin && completeSalt){
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
					value_max_seekbar = 0;
				}
				try{
					value_min_seekbar = Integer.valueOf(et_valor_min_seekbar.getText().toString());

					if(value_min_seekbar<value_max_seekbar){
						completeValueMin = true;
						et_valor_min_seekbar.setError(null);
						if(completeValueMax && completeValueMin && completeSalt){
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
					value_sal_seekbar = Integer.valueOf(et_salto_seekbar.getText().toString());
					value_max_seekbar = Integer.valueOf(et_valor_max_seekbar.getText().toString());
					value_min_seekbar = Integer.valueOf(et_valor_min_seekbar.getText().toString());


					if((value_max_seekbar-value_min_seekbar)>value_sal_seekbar&&value_sal_seekbar >= 0){
						completeSalt = true;
						et_salto_seekbar.setError(null);
						if(completeValueMax && completeValueMin && completeSalt){
							bt_save.setEnabled(true);
						}
					}else{
						throw new NumberFormatException();
					}

				}catch(NumberFormatException e){
					et_salto_seekbar.setError(activity.getResources().getString(R.string.requiereIp));
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

	}

	public void notificarOK(final BaseElementList<VariableSeekWidget> data){
		if(observador != null){
			observador.OnButtonNewSave(data);
		}

	}

	public void notificarCancel(final BaseElementList<VariableSeekWidget> data){
		if(observador != null){
			observador.OnButtonNewCancel(data);
		}
	}

}
