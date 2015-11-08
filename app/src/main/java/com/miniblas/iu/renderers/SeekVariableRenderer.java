package com.miniblas.iu.renderers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException;
import com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException;
import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.FabActivity;
import com.miniblas.iu.utils.ThemeUtils;
import com.miniblas.model.variableWidgets.VariableSeekWidget;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;
import com.pedrogomez.renderers.Renderer;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.ButterKnife;
import butterknife.InjectView;
/**
 *
 * @author A. Azuara
 */
public class SeekVariableRenderer extends Renderer<BaseVariableWidget>{

	/*
	 * Attributes
	 */
	private final Context context;
	private final AplicacionPrincipal app;
	private static ThemeUtils mThemeUtils;

	/*
	 * Constructor
     */

	public SeekVariableRenderer(Context context){
		this.context = context;
		app = (AplicacionPrincipal) context.getApplicationContext();
		mThemeUtils = new ThemeUtils(context);
	}
    /*
     * Widgets
     */

	@InjectView(R.id.tv_nom_variable)
	TextView tv_nom_variable;
	@InjectView(R.id.seekBar)
	DiscreteSeekBar sv_seekBar;
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
		View inflatedView = inflater.inflate(R.layout.lyt_lista_variable_seekbar, parent, false);
		ButterKnife.inject(this, inflatedView);
		return inflatedView;
	}

	@Override
	public void render(){
		final VariableSeekWidget variable = (VariableSeekWidget) getContent();

		sv_seekBar.setScrubberColor(mThemeUtils.accentColor());
		sv_seekBar.setThumbColor(mThemeUtils.accentColor(), mThemeUtils.accentColor());
		tv_nom_variable.setText(variable.getWidgetName());
		sv_seekBar.setMax((((variable.getValue_max() - variable.getValue_min()) / variable.getValue_salt())));
		sv_seekBar.setMin(0);
		Double value =  Double.valueOf(variable.getValue());
		sv_seekBar.setProgress((value.intValue() * variable.getValue_salt())+variable.getValue_min());

		sv_seekBar.setNumericTransformer(new DiscreteSeekBar.NumericTransformer(){
			@Override
			public int transform(int value){
				return (value * variable.getValue_salt())+variable.getValue_min();
			}
		});

		sv_seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener(){
			@Override
			public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser){

				int real_value = value * variable.getValue_salt();
				try{
					app.getArcadioService().writeVariable(variable.getNameElement(), Double.valueOf(real_value));
				}catch(ServiceDisconnectedArcadioException e){
					((FabActivity)context).addLineTerminal(context.getString(
							R.string.servicioDesconectado));
				}catch(NoConnectedArcadioException e){
					((FabActivity)context).addLineTerminal(context.getString(
							R.string.imposibleEscribirVariable)+" "+variable.getNameElement());
					((FabActivity)context).addLineTerminal(e.toString());
				}
			}

			@Override
			public void onStartTrackingTouch(DiscreteSeekBar seekBar){
				touched.setText("true");
			}

			@Override
			public void onStopTrackingTouch(DiscreteSeekBar seekBar){
				touched.setText("false");
			}
		});

		sv_seekBar.setFocusable(false);

	}

}
