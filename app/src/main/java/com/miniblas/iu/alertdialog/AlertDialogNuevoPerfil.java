package com.miniblas.iu.alertdialog;

import javax.inject.Inject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.alertdialog.interfaces.IObservadorNewAlertDialog;
import com.miniblas.model.MiniBlasPerfil;
import com.miniblas.modules.CheckIpModule;

import java.util.ArrayList;
import java.util.List;

public class AlertDialogNuevoPerfil extends DialogFragment {

    public static final String LISTA_NOMBRE_PERFILES = "LISTA_NOMBRE_PERFILES";
    public static IObservadorNewAlertDialog<MiniBlasPerfil> observador;

    public static AlertDialogNuevoPerfil newInstance(IObservadorNewAlertDialog<MiniBlasPerfil> _observador, List<MiniBlasPerfil> _perfiles){
        AlertDialogNuevoPerfil fragment = new AlertDialogNuevoPerfil();
        Bundle args = new Bundle();
        ArrayList<String> listaNombres = new ArrayList<String>();
        for(MiniBlasPerfil perfil:_perfiles){
            listaNombres.add(perfil.getNombre());
        }
        args.putStringArrayList(LISTA_NOMBRE_PERFILES,listaNombres);
        fragment.setArguments(args);
        observador=_observador;
        return fragment;
    }

	private AplicacionPrincipal application;
	private MaterialDialog.Builder alertBuilder;
	private MaterialDialog alertdialog;

    @Inject public CheckIpModule checkIpModule;

    public static final String STATE_BUTTON_SAVE = "STATE_BUTTON_SAVE";   
    public static final String STATE_NOMBRE = "STATE_NOMBRE";   
    public static final String STATE_IP = "STATE_IP";   


    private boolean bt_state = false;
    private boolean completadoNombre = false;
    private boolean completadoIp = false;
    private static View bt_guardar;
	private Activity activity;
    private ArrayList<String> lista_nombres;
	/*
	 * Get widgets
	 */
    @InjectView(R.id.et_nombre)
    EditText et_nombre;
    
    @InjectView(R.id.et_ip)
    EditText et_ip;
    
    @InjectView(R.id.et_puerto)
    EditText et_puerto;
    
    @InjectView(R.id.et_pass)
    EditText et_pass;
    

    
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	  if (savedInstanceState != null){
    		  bt_state = savedInstanceState.getBoolean(STATE_BUTTON_SAVE);
    		  completadoNombre = savedInstanceState.getBoolean(STATE_NOMBRE);
    		  completadoIp = savedInstanceState.getBoolean(STATE_IP);
    	  }
        lista_nombres = getArguments().getStringArrayList(LISTA_NOMBRE_PERFILES);
    }
    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(_activity);
        this.activity = _activity;
        application =  (AplicacionPrincipal) _activity.getApplicationContext();
        application.inject(this);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		alertBuilder =  new MaterialDialog.Builder(activity);
		alertBuilder.title(activity.getResources().getString(R.string.nuevo_perfil));

		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.lyt_nuevo_perfil,null);
		alertBuilder.customView(view);
		ButterKnife.inject(this, view);
        alertBuilder.positiveText(R.string.Guardar);
        alertBuilder.negativeText(android.R.string.cancel);
        alertBuilder.callback(new MaterialDialog.Callback() {
                                  @Override
                                  public void onPositive(MaterialDialog materialDialog) {
                                      MiniBlasPerfil perfil = new MiniBlasPerfil(et_nombre.getText().toString(),
                                              et_ip.getText().toString(),
                                              et_puerto.getText().toString(),
                                              et_pass.getText().toString());
                                      notificarOK(perfil);
                                  }

                                  @Override
                                  public void onNegative(MaterialDialog materialDialog) {
                                      onotificarCancel(null);
                                  }
                              });
		et_ip.addTextChangedListener(new TextWatcher() {


            @Override
            public void afterTextChanged(Editable s) {
                if (!checkIpModule.validate(et_ip.getText().toString())) {
                    et_ip.setError(activity.getResources().getString(R.string.requiereIp));
                    bt_guardar.setEnabled(false);
                    completadoIp = false;
                } else {
                    completadoIp = true;
                    et_ip.setError(null);
                    if (completadoIp && completadoNombre)
                        bt_guardar.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
            }
        });
		et_nombre.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(lista_nombres.contains(new MiniBlasPerfil(et_nombre.getText().toString()))){
					completadoNombre=false;
					et_nombre.setError(activity.getResources().getString(R.string.existeNombre));
					bt_guardar.setEnabled(false);
				}else if(et_nombre.getText().toString().length()==0){
					completadoNombre=false;
					et_nombre.setError(activity.getResources().getString(R.string.requiereNombre));
					bt_guardar.setEnabled(false);
				}else{
					et_nombre.setError(null);
					completadoNombre=true;
					if(completadoIp && completadoNombre)
						bt_guardar.setEnabled(true);
				}	
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub		
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});

		alertdialog = alertBuilder.build();
        return alertdialog;
    }
    
    @Override
    public void onResume() {
         super.onResume();
         bt_guardar = this.alertdialog.getActionButton(DialogAction.POSITIVE);
 		bt_guardar.setEnabled(bt_state);
 		System.out.println("onresumeeeeeeee");
    }
    public void onSaveInstanceState(Bundle saveInstanceState) {
    	  super.onSaveInstanceState(saveInstanceState);
    	  saveInstanceState.putBoolean(STATE_BUTTON_SAVE, bt_guardar.isEnabled());
    	  saveInstanceState.putBoolean(STATE_NOMBRE, completadoNombre);
    	  saveInstanceState.putBoolean(STATE_IP, completadoIp);

    }
    public void notificarOK(final MiniBlasPerfil data) {
        if(observador!=null) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    observador.OnButtonNewSave(data);
                }
            }).start();
        }

    }
    public void onotificarCancel(final MiniBlasPerfil data){
        if(observador!=null) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    observador.OnButtonNewCancel(data);

                }
            }).start();
        }
    }
}
