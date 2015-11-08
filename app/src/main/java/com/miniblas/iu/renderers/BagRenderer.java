package com.miniblas.iu.renderers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miniblas.app.R;
import com.miniblas.iu.base.ThemableActivity;
import com.miniblas.model.MiniBlasBag;
import com.miniblas.views.RoundedLetterView;
import com.pedrogomez.renderers.Renderer;

import butterknife.ButterKnife;
import butterknife.InjectView;
/**
 *
 * @author A. Azuara
 */
public class BagRenderer extends Renderer<MiniBlasBag>{


	/*
	 * Attributes
	 */
	private final Context context;
	//    private OnPerfilClicked listener;

	/*
     * Constructor
     */

	public BagRenderer(Context context){
		this.context = context;
	}
    /*
     * Widgets
     */

	@InjectView(R.id.tv_nom_cesta)
	TextView tv_nom_cesta;
	//    @InjectView(R.id.ch_marcar_perfil)
	//    CheckBox ch_marcar_perfil;


	@InjectView(R.id.mRoundedLetterView)
	RoundedLetterView mRoundedLetterView;

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
		View inflatedView = inflater.inflate(R.layout.lyt_cesta, parent, false);
		ButterKnife.inject(this, inflatedView);
		return inflatedView;
	}

	@Override
	public void render(){
		MiniBlasBag bag = getContent();
		tv_nom_cesta.setText(bag.getNameElement());
		mRoundedLetterView.setBackgroundColor(((ThemableActivity) context).getThemeUtils().accentColor());
		mRoundedLetterView.setTitleText(bag.getNameElement().substring(0, 1).toUpperCase());
	}

}
