package com.miniblas.iu;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.arcadio.api.v1.service.CosmeStates;
import com.melnykov.fab.FloatingActionButton;
import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.base.ThemableActivity;
import com.miniblas.iu.cab.base.BaseCab;
import com.miniblas.iu.cab.base.BaseOrdenableElementsCab;
import com.miniblas.iu.controllers.ObservadorError;
import com.miniblas.iu.controllers.ObservadorState;
import com.miniblas.iu.fragments.ProfilesElementsFragmentCab;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
/**
 *
 * @author A. Azuara
 */
public class FabActivity extends ThemableActivity implements ObservadorError.IObservadorError{

	private FloatingActionButton fab; // the floating blue add/paste button
	private boolean fabDisabled = false; // flag indicating whether fab should stay hidden while scrolling
	private FabListener fabListener;
	//private RecyclerView rv ;
	private BaseOrdenableElementsCab mCab; // the current contextual action bar, saves state throughout fragments
	private Toolbar mToolbar;

	private SlidingUpPanelLayout lytTerminal;
	private TextView textTerminal;
	public static final String TERMINALPANELHEIGHT ="TERMINALPANELHEIGHT";
	public static final String TERMINALTEXT ="TERMINALTEXT";


	@Override
	public void onNotifyError(String _error){
		addLineTerminal(_error);
	}


	public interface FabListener{
		public abstract void onFabPressed();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putInt(TERMINALPANELHEIGHT, lytTerminal.getPanelHeight());
		outState.putString(TERMINALTEXT, textTerminal.getText().toString());
	}
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer);

		mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		mToolbar.setBackgroundColor(getThemeUtils().primaryColor());
		mToolbar.setLogo(null);
		setSupportActionBar(mToolbar);
		//mToolbar.setNavigationIcon(R.drawable.ic_arrow_left_white_36dp);
		/*
			if (savedInstanceState != null) {
				if (savedInstanceState.containsKey("cab")) {
					mCab = (BaseOrdenableElementsCab) savedInstanceState.getSerializable("cab");
					mCab.setContext(this).start();
				}
			}
			*/
		fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setColorNormal(getThemeUtils().accentColor());
		fab.setColorPressed(getThemeUtils().accentColorDark());
		fab.setColorRipple(getThemeUtils().accentColorLight());
		fab.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				fabListener.onFabPressed();
			}
		});
		((AplicacionPrincipal)getApplication()).setErrorObserver(this);

		lytTerminal = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
		lytTerminal.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
		lytTerminal.setPanelHeight(0);
		lytTerminal.setAnchorPoint(0.4f);
		textTerminal = (TextView) findViewById(R.id.contentTerminal);
		textTerminal.setLines(500);

		// necesario para que no se vuelva a generar otra vez el fragment
		if(savedInstanceState != null){
			lytTerminal.setPanelHeight(savedInstanceState.getInt(TERMINALPANELHEIGHT));
			textTerminal.setText(savedInstanceState.getString(TERMINALTEXT));
			return;
		}
		getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		// trans.setCustomAnimations(R.anim.frag_enter, R.anim.frag_exit);
		trans.replace(R.id.container, new ProfilesElementsFragmentCab());
		try{
			trans.commit();
		}catch(Exception e){
			e.printStackTrace();
		}
		fab.show(true);
		defaultTerminalView();
	}

	public void addLineTerminal(final String _txt){
		Runnable h = new Runnable(){
			@Override
			public void run(){
				if(textTerminal!=null)
					textTerminal.setText(_txt+"\n"+textTerminal.getText());
			}
		};
		runOnUiThread(h);
	}

	public BaseCab getCab(){
		return mCab;
	}

	public void hideCab(){
		mCab.finish();
	}

	public void setCab(BaseOrdenableElementsCab cab){
		mCab = cab;
	}

	public void hideFab(){
		fab.hide(true);
	}
	public void showFab(){
		fab.show(true);
	}

	public void invalidateToolbarMenu(boolean cabShown) {
		for (int i = 0; i < mToolbar.getMenu().size(); i++) {
			mToolbar.getMenu().getItem(i).setVisible(!cabShown);
		}
	}

	public void setImageFab(int resource){
		ImageButton button1 = (ImageButton) findViewById(R.id.fab);
		button1.setImageResource(resource);
	}



	@Override
	public void onBackPressed(){
		if (lytTerminal != null &&
				(lytTerminal.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
						lytTerminal.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
			lytTerminal.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
		} else {
			if(mCab != null && mCab.isActive()){
				hideCab();
			}else{
				FragmentManager fm = getSupportFragmentManager();
				if(fm.getBackStackEntryCount() == 0){
					Log.i("MainActivity", "nothing on backstack, calling super");
					super.onBackPressed();
				}else{
					backStackFragment();
				}
			}
		}
	}

	public void backStackFragment(){

		FragmentManager fm = getSupportFragmentManager();
		fm.popBackStack();
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()){
			case R.id.Acercade:
				PackageInfo pInfo = null;
				try{
					pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				}catch(PackageManager.NameNotFoundException e){
					e.printStackTrace();
				}
				new MaterialDialog.Builder(this)
						.title(R.string.acercaDe)
						.positiveText(R.string.ok)
						.content(Html.fromHtml(getString(R.string.about_body)+
								"Version: " + pInfo.versionName))
						.contentLineSpacing(1.6f)
						.build()
						.show();
				return true;
			case R.id.terminal:
				if(lytTerminal.getPanelHeight()==0){
					DisplayMetrics metrics = getResources().getDisplayMetrics();
					float dp = 25f;
					float fpixels = metrics.density * dp;
					int pixels = (int) (fpixels + 0.5f);
					lytTerminal.setPanelHeight(pixels);
					lytTerminal.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
					((AplicacionPrincipal)getApplication()).getSettingStorage().setPrefTerminal(true);
				}else{
					if(lytTerminal.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN){
						lytTerminal.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
						((AplicacionPrincipal)getApplication()).getSettingStorage().setPrefTerminal(false);
					}else{
						lytTerminal.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
						((AplicacionPrincipal)getApplication()).getSettingStorage().setPrefTerminal(false);
					}
				}
				return true;

			case R.id.menu_ajustes:
				Intent intent = new Intent(this, Preferences.class);
				startActivity(intent);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}


	public void setFabListener(FabListener _fabListener){
		this.fabListener = _fabListener;
	}

	public void attachFabToListView(ListView _lv){
		this.fab.attachToListView(_lv);
	}

	private void defaultTerminalView(){
		if(((AplicacionPrincipal)getApplication()).getSettingStorage().getPrefTerminal()){
			DisplayMetrics metrics = getResources().getDisplayMetrics();
			float dp = 25f;
			float fpixels = metrics.density * dp;
			int pixels = (int) (fpixels + 0.5f);
			lytTerminal.setPanelHeight(pixels);
			lytTerminal.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
		}else{
			if(lytTerminal.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN){
				lytTerminal.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
			}else{
				lytTerminal.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
			}
		}
	}

}