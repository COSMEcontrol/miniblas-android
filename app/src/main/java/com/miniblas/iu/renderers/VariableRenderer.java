package com.miniblas.iu.renderers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miniblas.app.R;
import com.miniblas.model.MiniBlasItemVariable;
import com.pedrogomez.renderers.Renderer;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class VariableRenderer extends Renderer<MiniBlasItemVariable>{


	/*
	 * Attributes
	 */
	private final Context context;
	//    private OnPerfilClicked listener;

	/*
     * Constructor
     */

	public VariableRenderer(Context context){
		this.context = context;
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
		View inflatedView = inflater.inflate(R.layout.lyt_lista_variable, parent, false);
		ButterKnife.inject(this, inflatedView);
		return inflatedView;
	}

	@Override
	public void render(){
		MiniBlasItemVariable variable = getContent();
		tv_nom_variable.setText(variable.getNombre());
		tv_variable.setText(variable.getValor());

	}

}
