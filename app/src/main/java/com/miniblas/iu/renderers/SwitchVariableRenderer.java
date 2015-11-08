package com.miniblas.iu.renderers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException;
import com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException;
import com.kyleduo.switchbutton.switchbutton.SwitchButton;
import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.FabActivity;
import com.miniblas.iu.utils.ThemeUtils;
import com.miniblas.model.variableWidgets.VariableSwitchWidget;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;
import com.pedrogomez.renderers.Renderer;

import butterknife.ButterKnife;
import butterknife.InjectView;
/**
 *
 * @author A. Azuara
 */
public class SwitchVariableRenderer extends Renderer<BaseVariableWidget>{


	/*
	 * Attributes
	 */
	private final Context context;
	private final AplicacionPrincipal app;
	private static ThemeUtils mThemeUtils;

	/*
	 * Constructor
     */

	public SwitchVariableRenderer(Context context){
		this.context = context;
		app = (AplicacionPrincipal) context.getApplicationContext();
		mThemeUtils = new ThemeUtils(context);
	}
    /*
     * Widgets
     */

	@InjectView(R.id.tv_nom_variable)
	TextView tv_nom_variable;
	//    @InjectView(R.id.ch_marcar_perfil)
	//    CheckBox ch_marcar_perfil;
	@InjectView(R.id.switch1)
	SwitchButton switch1;

	@InjectView(R.id.touched)
	TextView touched;


	@Override
	protected void setUpView(View rootView){
		// TODO Auto-generated method stub

	}

	@Override
	protected void hookListeners(View rootView){
		// TODO Auto-generated method stub

	}

	@Override
	protected View inflate(LayoutInflater inflater, ViewGroup parent){
		View inflatedView = inflater.inflate(R.layout.lyt_lista_variable_switch, parent, false);
		ButterKnife.inject(this, inflatedView);
		return inflatedView;
	}

	@Override
	public void render(){
		final VariableSwitchWidget variable = (VariableSwitchWidget) getContent();
		tv_nom_variable.setText(variable.getWidgetName());
		if(!Boolean.valueOf(touched.getText().toString())){
			//no notificar
			try{
				switch1.setChecked((Double.valueOf(variable.getValue()).equals(Double.valueOf(variable.getValue_on()))) == true, false);
			}catch(NumberFormatException ex){
				tv_nom_variable.setTextColor(mThemeUtils.accentColor());
				((FabActivity)context).addLineTerminal(context.getString(R.string.ErrorConvirtiendoANumber) + " " + variable.getNameElement());
			}
		}

		switch1.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event){

				return false;
			}
		});
		switch1.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				touched.setText("true");
			}
		});
		switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
				String txt_to_sed ="";
				if(isChecked){
					txt_to_sed = variable.getValue_on();
					variable.setValue(txt_to_sed);
				}else{
					txt_to_sed = variable.getValue_off();
					variable.setValue(txt_to_sed);
				}
				try{
					app.getArcadioService().writeVariable(variable.getNameElement(), txt_to_sed);
				}catch(ServiceDisconnectedArcadioException e){
					((FabActivity)context).addLineTerminal(context.getString(R.string.servicioDesconectado));
				}catch(NoConnectedArcadioException e){
					((FabActivity)context).addLineTerminal(context.getString(
							R.string.imposibleEscribirVariable)+" "+variable.getNameElement());
					((FabActivity)context).addLineTerminal(e.toString());
				}
				touched.setText("false");
			}
		});
		switch1.setFocusable(false);
	}

}
