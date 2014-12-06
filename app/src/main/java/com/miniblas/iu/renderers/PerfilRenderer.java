package com.miniblas.iu.renderers;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miniblas.app.R;
import com.miniblas.model.MiniBlasPerfil;
import com.pedrogomez.renderers.Renderer;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PerfilRenderer extends Renderer<MiniBlasPerfil>{

	/*
	 * Attributes
	 */
	private final Context context;
	//    private OnPerfilClicked listener;

	/*
     * Constructor
     */

	public PerfilRenderer(Context context){
		this.context = context;
	}
    /*
     * Widgets
     */

	@InjectView(R.id.tv_nom_perfil)
	TextView tv_nom_perfil;
	//    @InjectView(R.id.ch_marcar_perfil)
	//    CheckBox ch_marcar_perfil;


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
		View inflatedView = inflater.inflate(R.layout.lyt_perfil, parent, false);
		ButterKnife.inject(this, inflatedView);
		return inflatedView;
	}


	//	public interface OnPerfilClicked {
	//		void onPerfilClicked(final Perfil perfil);
	//	}
	@Override
	public void render(){
		MiniBlasPerfil perfil = getContent();
		tv_nom_perfil.setText(perfil.getNombre());

	}

}
