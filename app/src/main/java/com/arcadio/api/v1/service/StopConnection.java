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
	public static final String MINIBLAS_PACKAGE_NAME = "com.miniblas.app";
	public static final String MINIBLAS_SERVICE_ARCADIO = "com.arcadio.api.v1.service.ConnectionArcadioService";

	private ServiceConnection conexion = new ServiceConnection(){
		@Override
		public void onServiceDisconnected(ComponentName name){
			Log.v("StopConnection-->", "Disconnected to service");
			remoteArcadio = null;

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder _remoteArcadio){
			Log.v("StopConnection-->", "Connected to service");
			remoteArcadio = IPluginServiceArcadio.Stub.asInterface(_remoteArcadio);

			try{
				if(remoteArcadio.isConnected(sessionId, sessionKey)){
					remoteArcadio.disconnect(sessionId, sessionKey);
				}
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
	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		sessionId = b.getInt(ConnectionArcadioService.SESSION_ID);
		sessionKey = b.getString(ConnectionArcadioService.SESSION_KEY);
		startService();
	}

	public void startService(){

		Intent msgIntent = new Intent();
		msgIntent.setClassName(MINIBLAS_PACKAGE_NAME, MINIBLAS_SERVICE_ARCADIO);
		if(startService(msgIntent) == null){
			Log.v("StopConnection-->", "Failed to start service");
		}else{
			Log.v("StopConnection-->", "Iniciate service");
		}
		Intent intent = new Intent();
		intent.setClassName(MINIBLAS_PACKAGE_NAME, MINIBLAS_SERVICE_ARCADIO);
		if(!bindService(intent, conexion, Context.BIND_AUTO_CREATE)){
			Log.v("PluginClientArcadioLibrary-->", "ERROR Connecting to an application Arcadio service");
		}else{
			Log.v("PluginClientArcadioLibrary-->", "Connecting to an application Arcadio service");
		}
	}
}
