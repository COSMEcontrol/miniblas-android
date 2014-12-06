package com.miniblas.iu;

import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.base.ThemableActivity;
import com.miniblas.iu.utils.ThemeUtils;
import com.miniblas.model.MiniBlasPerfil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class Preferences extends ThemableActivity{


	public static class SettingsFragment extends PreferenceFragment{
		private CheckBoxPreference basic_modeCheckBoxPreference;
		private CheckBoxPreference other_modeCheckBoxPreference;

		@Override
		public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferencias);
			basic_modeCheckBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("basic_mode");
			other_modeCheckBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("other_mode");

			basic_modeCheckBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
				@Override
				public boolean onPreferenceChange(Preference preference, Object o){
					if(other_modeCheckBoxPreference.isChecked()){
						other_modeCheckBoxPreference.setChecked(false);
					}
					getActivity().recreate();
					return true;
				}
			});
			other_modeCheckBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
				@Override
				public boolean onPreferenceChange(Preference preference, Object o){
					if(basic_modeCheckBoxPreference.isChecked()){
						basic_modeCheckBoxPreference.setChecked(false);
					}
					getActivity().recreate();
					return true;
				}
			});

		}
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		MiniBlasPerfil.CONTRASEÑA_POR_DEFECTO = ((AplicacionPrincipal) getApplication()).getSettingStorage().getPrefDefaultPassword();
		MiniBlasPerfil.PUERTO_POR_DEFECTO = ((AplicacionPrincipal) getApplication()).getSettingStorage().getPrefDefaultPort();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preference_activity_custom);
		ThemeUtils mThemeUtils = new ThemeUtils(this);
		if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT && mThemeUtils.getCurrent() == R.style.MiniblasMaterialDesing){
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
			findViewById(android.R.id.content).setPadding(config.getPixelInsetRight(), config.getPixelInsetTop(true), config.getPixelInsetRight(), 0);
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(item.getItemId() == android.R.id.home){
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

