package com.arcadio.api.v1.service;

import android.os.IBinder;
import android.os.RemoteException;

import com.arcadio.CosmeListener;
import com.arcadio.common.VariablesList;

public class AdapterICosmeListener extends ICosmeListener.Stub {
	
	CosmeListener cosmeListener;
	
	public AdapterICosmeListener(CosmeListener cosmeListener){
		this.cosmeListener = cosmeListener;
	}



	@Override
	public void onDataReceived(String _nombreCesta, VariablesList _listaVariables) throws RemoteException {
		cosmeListener.onDataReceived(_nombreCesta, _listaVariables);
	}

	@Override
	public void onStateChange(ParceableCosmeStates _estado)
			throws RemoteException {
		cosmeListener.onStateChange(_estado.getState());
	}

	@Override
	public void onError(String _txtError) throws RemoteException {
		cosmeListener.onError(_txtError);
		
	}

	@Override
	public IBinder asBinder() {
		return this;
	}

}
