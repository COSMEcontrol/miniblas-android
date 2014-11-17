package com.miniblas.iu.controllers;

import java.util.ArrayList;

import android.util.Log;

import com.miniblas.model.MiniBlasItemVariable;

public class ObservadorVariables {
	private ArrayList<IObservadorVariables> observadores = new ArrayList<ObservadorVariables.IObservadorVariables>();
	
	public interface IObservadorVariables{
		/**
		 * Notificar que se ha cambiando el estado a conectado.
		 * Este evento se ejecuta en un hilo independiente a la IU
		 */
		public void onNotifyVariables(String _nombreCesta,
				ArrayList<MiniBlasItemVariable> _listaVariables);

	}
	
	public void setObservador(IObservadorVariables _observador){
		observadores.add(_observador);
		Log.v("Añadir observador", "lista observadores: "+ observadores.size());
	}
	public void removeObservador(IObservadorVariables _observador){
		observadores.remove(_observador);
		Log.v("Eliminar observador", "lista observadores: "+ observadores.size());
	}
	public void deleteAllObservers(){
		observadores.clear();
	}
	
	public void Notify(final String _basketName,
			final ArrayList<MiniBlasItemVariable> _variablesList) {
		if(!observadores.isEmpty())
			new Thread(new Runnable() {
				@Override
				public void run() {
					for(IObservadorVariables _observador: observadores){
						_observador.onNotifyVariables(_basketName, _variablesList);	
					}
				}
			}).start();
		
	}

}
