package com.miniblas.iu.dialog.alert;

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
import com.miniblas.app.R;
import com.miniblas.iu.controllers.VariablesController;
import com.miniblas.iu.dialog.interfaces.IObservadorEditBasket;
import com.miniblas.model.MiniBlasBag;

import butterknife.ButterKnife;
import butterknife.InjectView;
/**
 *
 * @author A. Azuara
 */
public class DialogEditRefreshBag extends DialogFragment{

	private MaterialDialog.Builder alertBuilder;
	private MaterialDialog alertdialog;
	public static IObservadorEditBasket observador;


	public static MiniBlasBag basketSelected;


	private static View bt_guardar;

	private boolean completadoPeriodoRefresco = true;
	private boolean bt_state = false;

	private Activity activity;


	public static final String STATE_BUTTON_SAVE = "STATE_BUTTON_SAVE";
	public static final String STATE_REFRESH = "STATE_REFRESH";


	public static DialogEditRefreshBag newInstance(IObservadorEditBasket _observador){
		DialogEditRefreshBag fragment = new DialogEditRefreshBag();
		Bundle args = new Bundle();
		observador = _observador;
		basketSelected = ((VariablesController) _observador).getBasket();
		return fragment;
	}

	/*
	 * Get widgets
	 */

	@InjectView(R.id.et_periodo_refresco)
	EditText et_periodo_refresco;


	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			bt_state = savedInstanceState.getBoolean(STATE_BUTTON_SAVE);
			completadoPeriodoRefresco = savedInstanceState.getBoolean(STATE_REFRESH);
		}
	}

	@Override
	public void onAttach(Activity _activity){
		super.onAttach(activity);
		super.onAttach(_activity);
		this.activity = _activity;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){

		alertBuilder = new MaterialDialog.Builder(activity);

		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.lyt_editar_periodo_cesta, null);
		alertBuilder.customView(view, true);
		ButterKnife.inject(this, view);

		et_periodo_refresco.setText(String.valueOf(basketSelected.getRefreshPeriod()));


		alertBuilder.title(activity.getResources().getString(R.string.editar_cesta) + " " + basketSelected.getNameElement());
		alertBuilder.positiveText(R.string.Guardar);
		alertBuilder.negativeText(android.R.string.cancel);
		alertBuilder.callback(new MaterialDialog.ButtonCallback(){
			@Override
			public void onPositive(MaterialDialog materialDialog){
				if(!et_periodo_refresco.getText().toString().isEmpty()){
					basketSelected.setRefreshPeriod(Integer.valueOf(et_periodo_refresco.getText().toString()));
				}
				notificarOK(basketSelected);
			}

			@Override
			public void onNegative(MaterialDialog materialDialog){
				notificarCancel(null);
			}
		});

		alertdialog = alertBuilder.build();

		bt_guardar = this.alertdialog.getActionButton(DialogAction.POSITIVE);

		et_periodo_refresco.addTextChangedListener(new TextWatcher(){


			@Override
			public void afterTextChanged(Editable s){

				try{
					int periodoRefresco = Integer.valueOf(et_periodo_refresco.getText().toString());

					if(periodoRefresco < 0){
						throw new NumberFormatException();
					}else{
						completadoPeriodoRefresco = true;
						et_periodo_refresco.setError(null);
						if(completadoPeriodoRefresco){
							bt_guardar.setEnabled(true);
						}
					}

				}catch(NumberFormatException e){
					et_periodo_refresco.setError(activity.getResources().getString(R.string.periodoPositivo));
					bt_guardar.setEnabled(false);
					completadoPeriodoRefresco = false;
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
		saveInstanceState.putBoolean(STATE_REFRESH, completadoPeriodoRefresco);

	}

	public void notificarOK(MiniBlasBag data){
		if(observador != null){
			observador.OnButtonEditSave(data);
		}

	}

	public void notificarCancel(MiniBlasBag data){
		if(observador != null){
			observador.OnButtonEditCancel(data);
		}
	}

}
