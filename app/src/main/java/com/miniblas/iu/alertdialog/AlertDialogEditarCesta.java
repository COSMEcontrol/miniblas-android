package com.miniblas.iu.alertdialog;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.miniblas.app.R;
import com.miniblas.iu.alertdialog.interfaces.IObservadorEditAlertDialog;
import com.miniblas.model.MiniBlasCesta;

public class AlertDialogEditarCesta extends DialogFragment {

    private MaterialDialog.Builder alertBuilder;
    private MaterialDialog alertdialog;
    public static IObservadorEditAlertDialog<MiniBlasCesta> observador;


    public static MiniBlasCesta basketSelected;
    public static ArrayList<String> nameList = new ArrayList<String>();


    private static View bt_guardar;

    private boolean completadoNombre = true;
    private boolean completadoPeriodoRefresco = true;
    private boolean bt_state = false;

    private Activity activity;


    public static final String STATE_BUTTON_SAVE = "STATE_BUTTON_SAVE";
    public static final String STATE_NOMBRE = "STATE_NOMBRE";
    public static final String STATE_REFRESH = "STATE_REFRESH";



    public static AlertDialogEditarCesta newInstance(IObservadorEditAlertDialog<MiniBlasCesta> _observador, MiniBlasCesta _basketSelected, List<MiniBlasCesta> _baskets){
        AlertDialogEditarCesta fragment = new AlertDialogEditarCesta();
        Bundle args = new Bundle();
        // ArrayList<String> listaNombres = new ArrayList<String>();
        nameList.clear();
        for(MiniBlasCesta basket:_baskets){
            nameList.add(basket.getNombre());
        }
        //args.putStringArrayList(LISTA_NOMBRE_PERFILES,listaNombres);
        //fragment.setArguments(args);
        observador=_observador;
        basketSelected =_basketSelected;
        return fragment;
    }
    
	/*
	 * Get widgets
	 */
    @InjectView(R.id.et_nombre_cesta)
    EditText et_nombre_cesta;
    
    @InjectView(R.id.et_periodo_refresco)
    EditText et_periodo_refresco;




    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            bt_state = savedInstanceState.getBoolean(STATE_BUTTON_SAVE);
            completadoNombre = savedInstanceState.getBoolean(STATE_NOMBRE);
            completadoPeriodoRefresco = savedInstanceState.getBoolean(STATE_REFRESH);
        }
    }
    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(activity);
        super.onAttach(_activity);
        this.activity = _activity;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        alertBuilder =  new MaterialDialog.Builder(activity);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.lyt_nueva_cesta,null);
        alertBuilder.customView(view);
        ButterKnife.inject(this, view);


        alertBuilder.title(activity.getResources().getString(R.string.nueva_cesta));
        et_nombre_cesta.setText(basketSelected.getNombre());
        et_periodo_refresco.setText(String.valueOf(basketSelected.getPeriodoRefresco()));


        alertBuilder.title(activity.getResources().getString(R.string.editar_cesta)+" " + basketSelected.getNombre());
        alertBuilder.positiveText(R.string.Guardar);
        alertBuilder.negativeText(android.R.string.cancel);
        alertBuilder.callback(new MaterialDialog.Callback() {
            @Override
            public void onPositive(MaterialDialog materialDialog) {
                if(!et_nombre_cesta.getText().toString().isEmpty())
                    basketSelected.setNombre(et_nombre_cesta.getText().toString());
                if(!et_periodo_refresco.getText().toString().isEmpty())
                    basketSelected.setPeriodoRefresco(Integer.valueOf(et_periodo_refresco.getText().toString()));
                notificarOK(basketSelected);
            }

            @Override
            public void onNegative(MaterialDialog materialDialog) {
                notificarCancel(null);
            }
        });

        et_periodo_refresco.addTextChangedListener(new TextWatcher() {


            @Override
            public void afterTextChanged(Editable s) {
                int periodoRefresco =0;
                if(!et_periodo_refresco.getText().toString().equals("")){
                    periodoRefresco = Integer.valueOf(et_periodo_refresco.getText().toString());
                }
                if(periodoRefresco<0){
                    et_periodo_refresco.setError(activity.getResources().getString(R.string.requiereIp));
                    bt_guardar.setEnabled(false);
                    completadoPeriodoRefresco = false;
                }else{
                    completadoPeriodoRefresco = true;
                    et_periodo_refresco.setError(null);
                    if(completadoPeriodoRefresco && completadoNombre)
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
        et_nombre_cesta.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(nameList.contains(new MiniBlasCesta(et_nombre_cesta.getText().toString()))
                        && !basketSelected.getNombre().equals(et_nombre_cesta.getText().toString())){
                    completadoNombre=false;
                    et_nombre_cesta.setError(activity.getResources().getString(R.string.existeNombre));
                    bt_guardar.setEnabled(false);
                }else if(et_nombre_cesta.getText().toString().length()==0){
                    completadoNombre=false;
                    et_nombre_cesta.setError(activity.getResources().getString(R.string.requiereNombre));
                    bt_guardar.setEnabled(false);
                }else{
                    et_nombre_cesta.setError(null);
                    completadoNombre=true;
                    if(completadoPeriodoRefresco && completadoNombre)
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
    }
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putBoolean(STATE_BUTTON_SAVE, bt_guardar.isEnabled());
        saveInstanceState.putBoolean(STATE_NOMBRE, completadoNombre);
        saveInstanceState.putBoolean(STATE_REFRESH, completadoPeriodoRefresco);

    }
    public void notificarOK(final MiniBlasCesta data) {
        if(observador!=null) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    observador.OnButtonEditSave(data);
                }
            }).start();
        }

    }
    public void notificarCancel(final MiniBlasCesta data){
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
