package com.miniblas.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.miniblas.model.MiniBlasPerfil;

public class SharedPreferencesSettingStorage implements SettingStorage{
	
    //editor de preferencias
	private SharedPreferences preferencias;
	private SharedPreferences.Editor editor;
	
	public static final String PREF_AUTOCONEXION = "PREF_AUTOCONEXION";
	public static final String PREF_AUTOCONEXION_ID_PROFILE = "PREF_AUTOCONEXION_ID_PROFILE";
	
	public static final String PREF_DEFAULT_PORT = "porPref";
	public static final String PREF_DEFAULT_PASS = "passPref";
	
	public SharedPreferencesSettingStorage(Context ctx) {
		preferencias = 
    			PreferenceManager.getDefaultSharedPreferences(ctx);
		editor = preferencias.edit();
	}

	@Override
	public boolean getPrefAutoConexion() {
		return preferencias.getBoolean(PREF_AUTOCONEXION, false);
	}

	@Override
	public int getPrefAutoConexionIdProfile() {
		return preferencias.getInt(PREF_AUTOCONEXION_ID_PROFILE, 0);
	}

	@Override
	public void setPrefAutoConexion(boolean _autoConexion) {
		editor.putBoolean(PREF_AUTOCONEXION, _autoConexion);
        editor.commit();
		
	}

	@Override
	public void setPrefAutoConexionIdProfile(int _ip) {
        editor.putInt(PREF_AUTOCONEXION_ID_PROFILE, _ip);
        editor.commit();
	}

//	@Override
//	public void setPrefDefaultPort(int _port) {
//		 editor.putInt(PREF_DEFAULT_PORT,_port);
//	     editor.commit();
//	}
//
//	@Override
//	public void setPrefDefaultIp(String _ip) {
//		editor.putString(PREF_DEFAULT_IP, _ip);
//	    editor.commit();
//		
//	}

	@Override
	public int getPrefDefaultPort() {
		return Integer.valueOf(preferencias.getString(PREF_DEFAULT_PORT, String.valueOf(MiniBlasPerfil.PUERTO_POR_DEFECTO)));
	}

	@Override
	public String getPrefDefaultPassword() {
		return preferencias.getString(PREF_DEFAULT_PASS, MiniBlasPerfil.CONTRASEÑA_POR_DEFECTO);
	}

}
