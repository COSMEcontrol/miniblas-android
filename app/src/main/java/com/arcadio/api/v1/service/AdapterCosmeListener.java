package com.arcadio.api.v1.service;

import android.os.RemoteException;
import android.util.Log;

import com.arcadio.CosmeListener;
import com.arcadio.common.VariablesList;
/**
 *
 * @author A. Azuara
 */
public class AdapterCosmeListener implements CosmeListener{

	private ICosmeListener cosmeListener;

	public AdapterCosmeListener(ICosmeListener cosmeListener){
		this.cosmeListener = cosmeListener;
	}

	@Override
	public void onDataReceived(String _bagName, VariablesList variablesList){
		try{
			cosmeListener.onDataReceived(_bagName, variablesList);
		}catch(RemoteException e){
			Log.v("AdapterCosmeListener Error: --->", " fail to send information(notificarRefrescoVariables) to client arcadio");
		}
	}

	@Override
	public void onStateChange(CosmeStates cosmeState){
		try{
			cosmeListener.onStateChange(new ParceableCosmeStates(cosmeState));
		}catch(RemoteException e){
			Log.v("AdapterCosmeListener Error: --->", " fail to send information(onStateChange) to client arcadio");
		}
	}

	@Override
	public void onError(String _msgError){
		try{
			cosmeListener.onError(_msgError);
		}catch(RemoteException e){
			Log.v("AdapterCosmeListener Error: --->", " fail to send information(onError) to client arcadio");
		}
	}
}
