package com.miniblas.iu.alertdialog;

import javax.inject.Inject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
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
import com.miniblas.iu.alertdialog.interfaces.IObservadorEditAlertDialog;
import com.miniblas.iu.controllers.ProfilesController;
import com.miniblas.model.MiniBlasPerfil;
import com.miniblas.modules.CheckIpModule;

import java.util.ArrayList;
import java.util.List;

public class AlertDialogEditarPerfil extends DialogFragment{

    public static final String LISTA_NOMBRE_PERFILES = "LISTA_NOMBRE_PERFILES";
    public static IObservadorEditAlertDialog<MiniBlasPerfil> observador;
    public static MiniBlasPerfil profileSelected;
    public static ArrayList<String> nameList = new ArrayList<String>();

    public static AlertDialogEditarPerfil newInstance(IObservadorEditAlertDialog<MiniBlasPerfil> _observador, MiniBlasPerfil _profileSelected, List<MiniBlasPerfil> _profiles){
        AlertDialogEditarPerfil fragment = new AlertDialogEditarPerfil();
        Bundle args = new Bundle();
       // ArrayList<String> listaNombres = new ArrayList<String>();
        nameList.clear();
        for(MiniBlasPerfil perfil:_profiles){
            nameList.add(perfil.getNombre());
        }
        //args.putStringArrayList(LISTA_NOMBRE_PERFILES,listaNombres);
        //fragment.setArguments(args);
        observador=_observador;
        profileSelected=_profileSelected;
        return fragment;
    }


    private static View bt_guardar;
    @Inject public CheckIpModule checkIpModule;

    
    private boolean completadoNombre = true;
    private boolean completadoIp = true;
    private boolean completadoPass= true;
    private boolean completadoPort = true;
 
	private Activity activity;
	private AplicacionPrincipal application;
    private MaterialDialog.Builder alertBuilder;
    private MaterialDialog alertdialog;
	
    public static final String STATE_BUTTON_SAVE = "STATE_BUTTON_SAVE";   
    public static final String STATE_NOMBRE = "STATE_NOMBRE";   
    public static final String STATE_IP = "STATE_IP";   


    private boolean bt_state = false;
	
	
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
    }
    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(activity);
        this.activity = _activity;
        application =  (AplicacionPrincipal) _activity.getApplicationContext();
        application.inject(this);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        alertBuilder =  new MaterialDialog.Builder(activity);
    	
		application =  (AplicacionPrincipal) activity.getApplicationContext();
		application.inject(this);

		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.lyt_editar_perfil,null);
        alertBuilder.customView(view);
		ButterKnife.inject(this, view);
        alertBuilder.positiveText(R.string.Guardar);
        alertBuilder.negativeText(android.R.string.cancel);
        /*
		if(savedInstanceState == null){
			for(int i =0; i<listActivity.getListView().getCheckedItemPositions().size();i++){
				if (listActivity.getListView().getCheckedItemPositions().valueAt(i)) {
					perfil = mAdapter.getItem(listActivity.getListView().getCheckedItemPositions().keyAt(i));
					break;
				}
			}
		}
		*/
		et_nombre.setText(profileSelected.getNombre());
		et_ip.setText(profileSelected.getIp());
		et_puerto.setText(String.valueOf(profileSelected.getPuerto()));
		alertBuilder.title(activity.getResources().getString(R.string.editar_perfil)+" " + profileSelected.getNombre());
        alertBuilder.callback(new MaterialDialog.Callback() {
            @Override
            public void onPositive(MaterialDialog materialDialog) {
                profileSelected.setIp(et_ip.getText().toString());
                if(!et_pass.getText().toString().isEmpty())
                    profileSelected.setPassword(et_pass.getText().toString());
                profileSelected.setNombre(et_nombre.getText().toString());
                if(!et_puerto.getText().toString().isEmpty())
                    profileSelected.setPuerto(Integer.valueOf(et_puerto.getText().toString()));
                notificarOk(profileSelected);
            }

            @Override
            public void onNegative(MaterialDialog materialDialog) {
                notificarCancel(null);
            }
        });

		et_ip.addTextChangedListener(new TextWatcher() {

			
			@Override
			public void afterTextChanged(Editable s) {
				if(!checkIpModule.validate(et_ip.getText().toString())){
					et_ip.setError(activity.getResources().getString(R.string.requiereIp));
					bt_guardar.setEnabled(false);
					completadoIp = false;
				}else{
					completadoIp = true;
					et_ip.setError(null);
					if(completadoIp && completadoNombre && completadoPort && completadoPass)
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
				if(nameList.contains(new MiniBlasPerfil(et_nombre.getText().toString()))
						&& !profileSelected.getNombre().equals(et_nombre.getText().toString())){
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
					if(completadoIp && completadoNombre && completadoPort && completadoPass)
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
		et_puerto.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(et_puerto.getText().toString().length()==0){
					completadoPort=false;
					et_puerto.setError(activity.getResources().getString(R.string.requierePuerto));
					bt_guardar.setEnabled(false);
				}else{
					et_puerto.setError(null);
					completadoPort=true;
					if(completadoIp && completadoNombre && completadoPort && completadoPass)
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
		et_pass.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
					completadoPass=true;
					if(completadoIp && completadoNombre && completadoPort && completadoPass)
						bt_guardar.setEnabled(true);

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
        bt_guardar.setEnabled(bt_state);;
    }
    public void onSaveInstanceState(Bundle saveInstanceState) {
    	  super.onSaveInstanceState(saveInstanceState);
    	  saveInstanceState.putBoolean(STATE_BUTTON_SAVE, bt_guardar.isEnabled());
    	  saveInstanceState.putBoolean(STATE_NOMBRE, completadoNombre);
    	  saveInstanceState.putBoolean(STATE_IP, completadoIp);
    }


    public void notificarOk(final MiniBlasPerfil data) {
        if(observador!=null) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    observador.OnButtonEditSave(data);
                }
            }).start();
        }

    }
    public void notificarCancel(final MiniBlasPerfil data){
        if(observador!=null) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    observador.OnButtonEditCancel(data);

                }
            }).start();
        }
    }
}
