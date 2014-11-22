package com.arcadio.api.v1.service;

import java.util.ArrayList;

import android.os.RemoteException;
import android.util.Log;

import com.arcadio.CosmeListener;
import com.arcadio.CosmeError;
import com.arcadio.EstadosCosme;
import com.arcadio.modelo.Cesta;
import com.arcadio.modelo.ItemVariable;

public class AdapterCosmeListener implements CosmeListener{
	
	private ICosmeListener cosmeListener;
	
	public AdapterCosmeListener(ICosmeListener cosmeListener) {
		this.cosmeListener = cosmeListener;
	}

	@Override
	public void notificarRefrescoVariables(String _nombreCesta,
			ArrayList<ItemVariable> _listaVariables) {
		try {
			cosmeListener.notificarRefrescoVariables(_nombreCesta, _listaVariables);
		} catch (RemoteException e) {
			Log.v("AdapterCosme Error: --->", " fail to send information(notificarRefrescoVariables) to client arcadio");
		}
		
	}

	@Override
	public void notificarEstadoConexion(EstadosCosme _estado) {
		try {
			cosmeListener.notificarEstadoConexion(new ParceableEstadosCosme(_estado));
		} catch (RemoteException e) {
			Log.v("AdapterCosme Error: --->", " fail to send information(notificarEstadoConexion) to client arcadio");
		}
		
	}

	@Override
	public void notificarError(CosmeError _error) {
		try {
			cosmeListener.notificarError(new ParceableCosmeError(_error));
		} catch (RemoteException e) {
			Log.v("AdapterCosme Error: --->", " fail to send information(notificarError) to client arcadio");
		}
		
	}

	@Override
	public void notificarListaNombres(ArrayList<ItemVariable> listaNombres) {
		try {
			cosmeListener.notificarListaNombres(listaNombres);
		} catch (RemoteException e) {
			Log.v("AdapterCosme Error: --->", " fail to send information(notificarListaNombres) to client arcadio");
		}		
	}

	@Override
	public void notificarIsNumeric(ItemVariable variable) {
		try {
			cosmeListener.notificarIsNumeric(variable);
		} catch (RemoteException e) {
			Log.v("AdapterCosme Error: --->", " fail to send information to client arcadio");
		}
		
	}

	@Override
	public void notificarCestaCreada(Cesta cesta) {
		try {
			cosmeListener.notificarCestaCreada(cesta.getNombre());
		} catch (RemoteException e) {
			Log.v("AdapterCosme Error: --->", " fail to send information to client arcadio");
		}
		
	}

	@Override
	public void notificarNomACesta(Cesta cesta, ItemVariable variable) {
		try {
			cosmeListener.notificarNomACesta(cesta.getNombre(), variable.getNombre());
		} catch (RemoteException e) {
			Log.v("AdapterCosme Error: --->", " fail to send information to client arcadio");
		}	
	}


}
