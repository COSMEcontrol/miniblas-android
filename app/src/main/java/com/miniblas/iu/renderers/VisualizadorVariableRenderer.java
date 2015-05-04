package com.miniblas.iu.renderers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException;
import com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException;
import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.FabActivity;
import com.miniblas.iu.utils.ThemeUtils;
import com.miniblas.model.variableWidgets.VariableValueWidget;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;
import com.pedrogomez.renderers.Renderer;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class VisualizadorVariableRenderer extends Renderer<BaseVariableWidget>{


	/*
	 * Attributes
	 */
	private final Context context;
	private final AplicacionPrincipal app;
	private static ThemeUtils mThemeUtils;

	/*
     * Constructor
     */

	public VisualizadorVariableRenderer(Context context){
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
	@InjectView(R.id.tv_variable)
	TextView tv_variable;


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
		View inflatedView = inflater.inflate(R.layout.lyt_lista_variable_visualizador, parent, false);
		ButterKnife.inject(this, inflatedView);
		return inflatedView;
	}

	@Override
	public void render(){
		final VariableValueWidget variable = (VariableValueWidget) getContent();
		tv_nom_variable.setText(variable.getWidgetName());
		tv_variable.setText(variable.getValue());
		// no siempre serán variables numericas
		try{
			double valueDouble = Double.valueOf(variable.getValue());
			if(valueDouble>variable.getMax_value()
					|| valueDouble< variable.getMin_value()){

				tv_variable.setTextColor(mThemeUtils.accentColor());
			}else{
				tv_variable.setTextColor(context.getResources().getColor(R.color.black));
			}
		}catch(NumberFormatException ex){
			tv_variable.setTextColor(context.getResources().getColor(R.color.black));
		}

		tv_variable.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				new MaterialDialog.Builder(context).title(variable.getWidgetName()).content(variable.getNameElement()).input(R.string.value, 0, new MaterialDialog.InputCallback(){
					@Override
					public void onInput(MaterialDialog dialog, CharSequence input){
						variable.setValue(input.toString());
						try{
							app.getArcadioService().writeVariable(variable.getNameElement(), variable.getValue());
						}catch(ServiceDisconnectedArcadioException e){
							((FabActivity) context).addLineTerminal(context.getString(R.string.servicioDesconectado));
						}catch(NoConnectedArcadioException e){
							((FabActivity) context).addLineTerminal(context.getString(R.string.imposibleEscribirVariable) + " " + variable.getNameElement());
							((FabActivity) context).addLineTerminal(e.toString());
						}

					}
				}).show();
			}
		});

	}

}
