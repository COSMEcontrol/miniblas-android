package com.arcadio.api.v1.service;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class StopConnection extends Activity{
	private static IPluginServiceArcadio remoteArcadio;
	private int sessionId;
	private String sessionKey;

	private ServiceConnection conexion = new ServiceConnection(){
		@Override
		public void onServiceDisconnected(ComponentName name){
			Log.v("onServiceDisconnected", "onServiceDisconnected");
			remoteArcadio = null;

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder _remoteArcadio){
			Log.v("onserviceconected", "onserviceconected");
			remoteArcadio = IPluginServiceArcadio.Stub.asInterface(_remoteArcadio);

			try{
				Log.v("enviando al remoto", String.valueOf(sessionId));
				remoteArcadio.disconnect(sessionId, sessionKey);
				unbindService(conexion);
				finish();
			}catch(NumberFormatException e){
				e.printStackTrace();
			}catch(RemoteException e){
				e.printStackTrace();
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();

		Log.v("session id", String.valueOf(b.getInt(ConnectionArcadioService.SESSION_ID)));
		Log.v("session key", b.getString(ConnectionArcadioService.SESSION_KEY));
		sessionId = b.getInt(ConnectionArcadioService.SESSION_ID);
		sessionKey = b.getString(ConnectionArcadioService.SESSION_KEY);
		startService();
	}

	public void startService(){

		Intent msgIntent = new Intent();
		msgIntent.setClassName("com.miniblas.app", "com.arcadio.api.v1.service.ConnectionArcadioService");
		if(startService(msgIntent) == null){
			Log.v("no start con el servicio", "no start con el servicio");
		}else{
			Log.v("servicio iniciado", "servicio iniciado");
		}
		Intent intent = new Intent();
		intent.setClassName("com.miniblas.app", "com.arcadio.api.v1.service.ConnectionArcadioService");
		if(!bindService(intent, conexion, Context.BIND_AUTO_CREATE)){
			Log.v("no bind con el servicio", "no bind con el servicio");
		}else{
			Log.v("iniciado bind con el servicio", "iniciado bind con el servicio");
		}
	}
}
