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
import com.miniblas.iu.dialog.interfaces.IObservadorNewDialog;
import com.miniblas.model.MiniBlasBag;
import com.miniblas.model.MiniBlasProfile;
import com.miniblas.model.base.BaseElementList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AlertDialogNewBag extends DialogFragment{

	private MaterialDialog.Builder alertBuilder;
	private MaterialDialog alertdialog;
	public static IObservadorNewDialog<MiniBlasBag> observador;


	private static View bt_guardar;
	private boolean completadoNombre = false;
	private boolean completadoPeriodoRefresco = false;
	private boolean bt_state = false;

	private Activity activity;
	private static Collection<String> lista_nombres;
	private static MiniBlasProfile profile;

	public static final String STATE_BUTTON_SAVE = "STATE_BUTTON_SAVE";
	public static final String STATE_NOMBRE = "STATE_NOMBRE";
	public static final String STATE_REFRESH = "STATE_REFRESH";


	public static AlertDialogNewBag newInstance(IObservadorNewDialog<MiniBlasBag> _observador, BaseElementList<MiniBlasBag> _cestas, MiniBlasProfile _profile){
		AlertDialogNewBag fragment = new AlertDialogNewBag();
		Bundle args = new Bundle();
		lista_nombres = _cestas.getNameList();
		/*
        args.putStringArrayList(LISTA_NOMBRE_PERFILES,listaNombres);
        fragment.setArguments(args);
        */
		observador = _observador;
		profile = _profile;
		return fragment;
	}


	/*
	 * Get widgets
	 */
	@InjectView(R.id.et_nombre_cesta)
	EditText et_nombre_cesta;

	@InjectView(R.id.et_periodo_refresco)
	EditText et_periodo_refresco;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			bt_state = savedInstanceState.getBoolean(STATE_BUTTON_SAVE);
			completadoNombre = savedInstanceState.getBoolean(STATE_NOMBRE);
			completadoPeriodoRefresco = savedInstanceState.getBoolean(STATE_REFRESH);
		}
	}

	@Override
	public void onAttach(Activity _activity){
		super.onAttach(_activity);
		this.activity = _activity;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		alertBuilder = new MaterialDialog.Builder(activity);
		alertBuilder.title(activity.getResources().getString(R.string.nueva_cesta));


		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.lyt_nueva_cesta, null);
		alertBuilder.customView(view,true);
		ButterKnife.inject(this, view);
		alertBuilder.title(activity.getResources().getString(R.string.nueva_cesta));

		alertBuilder.positiveText(R.string.Guardar);
		alertBuilder.negativeText(android.R.string.cancel);
		alertBuilder.callback(new MaterialDialog.ButtonCallback(){
			@Override
			public void onPositive(MaterialDialog materialDialog){
				String nombreBag = et_nombre_cesta.getText().toString();
				try{
					int port = Integer.valueOf(et_periodo_refresco.getText().toString());
					MiniBlasBag basket = new MiniBlasBag(nombreBag, port);
					basket.setProfile(profile);
					BaseElementList<MiniBlasBag> listBags = new BaseElementList<MiniBlasBag>();
					listBags.add(basket);
					notificarOK(listBags);
				}catch(NumberFormatException exception){
					notificarCancel(null);
				}


			}

			@Override
			public void onNegative(MaterialDialog materialDialog){
				notificarCancel(null);
			}
		});

		alertdialog = alertBuilder.build();


		bt_guardar = this.alertdialog.getActionButton(DialogAction.POSITIVE);
		//validacion de campos
		et_periodo_refresco.addTextChangedListener(new TextWatcher(){


			@Override
			public void afterTextChanged(Editable s){
				try{
					int periodoRefresco = Integer.valueOf(et_periodo_refresco.getText().toString());
					if(periodoRefresco >= 0){
						completadoPeriodoRefresco = true;
						et_periodo_refresco.setError(null);
						if(completadoPeriodoRefresco && completadoNombre){
							bt_guardar.setEnabled(true);
						}
					}else{
						throw new NumberFormatException();
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
		et_nombre_cesta.addTextChangedListener(new TextWatcher(){

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
				if(lista_nombres.contains(et_nombre_cesta.getText().toString())){
					completadoNombre = false;
					et_nombre_cesta.setError(activity.getResources().getString(R.string.existeNombre));
					bt_guardar.setEnabled(false);
				}else if(et_nombre_cesta.getText().toString().length() == 0){
					completadoNombre = false;
					et_nombre_cesta.setError(activity.getResources().getString(R.string.requiereNombre));
					bt_guardar.setEnabled(false);
				}else{
					et_nombre_cesta.setError(null);
					completadoNombre = true;
					if(completadoPeriodoRefresco && completadoNombre){
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
		saveInstanceState.putBoolean(STATE_NOMBRE, completadoNombre);
		saveInstanceState.putBoolean(STATE_REFRESH, completadoPeriodoRefresco);

	}

	public void notificarOK(final BaseElementList<MiniBlasBag> data){
		if(observador != null){
			observador.OnButtonNewSave(data);
		}

	}

	public void notificarCancel(final BaseElementList<MiniBlasBag> data){
		if(observador != null){
			observador.OnButtonNewCancel(data);
		}
	}

}
