package com.miniblas.iu;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.prefs.MaterialListPreference;
import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.base.ThemableActivity;
import com.miniblas.iu.dialog.ColorChooserDialog;
import com.miniblas.iu.utils.ThemeUtils;
import com.miniblas.model.MiniBlasProfile;
import com.miniblas.model.base.BaseElementList;
import com.miniblas.persistence.BdException;
import com.miniblas.utils.Utils;
import com.miniblas.views.MiniblasPreference;

public class Preferences extends ThemableActivity implements ColorChooserDialog.ColorCallback{


	@Override
	public void onColorSelection(int title, int color) {
		if (title == R.string.primaryColor)
			getThemeUtils().setPrimaryColor(color);
		else
			getThemeUtils().accentColor(color);
		recreate();
	}

	public static class SettingsFragment extends PreferenceFragment {
		//private CheckBoxPreference basic_modeCheckBoxPreference;
		//private CheckBoxPreference other_modeCheckBoxPreference;
		private MaterialListPreference autoBag;
		private MaterialListPreference themePreference;
		private SharedPreferences preferencias;
		private SharedPreferences.Editor editor;
		BaseElementList<MiniBlasProfile> list = null;


		@Override
		public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferencias);
			getActivity().setTitle(R.string.ajustesMiniBlas);
			preferencias = getPreferenceManager().getDefaultSharedPreferences(getActivity());
			editor = preferencias.edit();
			//basic_modeCheckBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("basic_mode");
			//other_modeCheckBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("other_mode");
			autoBag = (MaterialListPreference) getPreferenceScreen().findPreference(getString(R.string.profilePref));
			try{
				list = ((AplicacionPrincipal)getActivity().getApplication()).getProfileStorage().getProfilesOrdered();
				MiniBlasProfile profileDefault = new MiniBlasProfile(getString(R.string.ninguno));
				profileDefault.setId(0);
				list.add(profileDefault);
				autoBag.setEntries(list.getNameList().toArray(new CharSequence[list.size()]));
				autoBag.setEntryValues(list.getIdsStringList().toArray(new CharSequence[list.size()]));
			}catch(BdException e){
				Toast.makeText(getActivity(),R.string.errorAccessBd, Toast.LENGTH_SHORT);
				e.printStackTrace();
				getActivity().finish();
			}

			autoBag.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue){
					if(((String) newValue).equals("0")){
						((AplicacionPrincipal) getActivity().getApplication()).getSettingStorage().setPrefAutoConexion(false);
					}else{
						((AplicacionPrincipal) getActivity().getApplication()).getSettingStorage().setPrefAutoConexion(true);
					}
					return true;
				}
			});
			/*
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
			*/

			findPreference(getString(R.string.themePref)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {

					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
					int preselect = 0;
					if (prefs.getBoolean("dark_mode", false)) {
						preselect = 1;
					} /*else if (prefs.getBoolean("dark_mode", false)) {
						preselect = 1;
					}*/

					new MaterialDialog.Builder(getActivity())
							.title(R.string.themeTitle)
							.items(R.array.themeMode)
							.itemsCallbackSingleChoice(preselect, new MaterialDialog.ListCallbackSingleChoice(){
								@Override
								public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence){
									SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
									switch (i) {
										case 0:
											prefs.putBoolean("basic_mode",true)
													.remove("dark_mode");
											break;
										case 1:
											prefs.remove("basic_mode")
													.putBoolean("dark_mode", true);
											break;
									}
									prefs.commit();
									getActivity().recreate();
									return false;
								}
							} ).show();
					return false;
				}
			});
			/*
			themePreference = (MaterialListPreference) getPreferenceScreen().findPreference(getString(R.string.themePref));
			themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue){
					String nameThemes[] = getResources().getStringArray(R.array.themeMode);
					if(((String) newValue).equals(nameThemes[0])){
						editor.putBoolean("basic_mode", true);
						editor.putBoolean("dark_mode", false);
						editor.commit();
						getActivity().recreate();
					}else if(((String) newValue).equals(nameThemes[1])){
						editor.putBoolean("basic_mode", false);
						editor.putBoolean("dark_mode", true);
						editor.commit();
						getActivity().recreate();
					}
					return true;
				}
			});
			*/
			Preference coloredNav = findPreference("colored_navbar");
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				coloredNav.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						if (getActivity() != null)
							getActivity().recreate();
						return true;
					}
				});
			} else {
				coloredNav.setEnabled(false);
				coloredNav.setSummary(R.string.only_available_lollipop);
			}

			ThemeUtils themeUtils = ((ThemableActivity) getActivity()).getThemeUtils();
			MiniblasPreference primaryColor = (MiniblasPreference) findPreference("primary_color");
			primaryColor.setColor(themeUtils.primaryColor(), Utils.resolveColor(getActivity(), R.attr.colorAccent));
			primaryColor.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					ThemeUtils themeUtils = ((ThemableActivity) getActivity()).getThemeUtils();
					new ColorChooserDialog().show(getActivity(), preference.getTitleRes(),
							themeUtils.primaryColor());
					return true;
				}
			});


			MiniblasPreference accentColor = (MiniblasPreference) findPreference("accent_color");
			accentColor.setColor(themeUtils.accentColor(), Utils.resolveColor(getActivity(), R.attr.colorAccent));
			accentColor.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
				@Override
				public boolean onPreferenceClick(Preference preference){
					ThemeUtils themeUtils = ((ThemableActivity) getActivity()).getThemeUtils();
					new ColorChooserDialog().show(getActivity(), preference.getTitleRes(), themeUtils.accentColor());
					return true;
				}
			});
		}

	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		MiniBlasProfile.CONTRASEÑA_POR_DEFECTO = ((AplicacionPrincipal) getApplication()).getSettingStorage().getPrefDefaultPassword();
		MiniBlasProfile.PUERTO_POR_DEFECTO = ((AplicacionPrincipal) getApplication()).getSettingStorage().getPrefDefaultPort();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preference_activity_custom);

		Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		mToolbar.setBackgroundColor(getThemeUtils().primaryColor());
		setSupportActionBar(mToolbar);
		mToolbar.setNavigationIcon(R.drawable.ic_arrow_left_white_24dp);

		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getFragmentManager().beginTransaction().replace(R.id.settings_content, new SettingsFragment()).commit();
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

