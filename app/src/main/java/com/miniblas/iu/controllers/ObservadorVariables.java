package com.miniblas.iu.controllers;

import android.util.Log;

import com.arcadio.common.VariablesList;
import com.miniblas.app.AplicacionPrincipal;

import java.util.ArrayList;

public class ObservadorVariables{
	private ArrayList<IObservadorVariables> observadores = new ArrayList<ObservadorVariables.IObservadorVariables>();

	public interface IObservadorVariables{
		/**
		 * Notificar que se ha cambiando el estado a conectado.
		 * Este evento se ejecuta en un hilo independiente a la IU
		 */
		public void onNotifyVariables(String _nombreCesta, VariablesList _variableList);

	}

	public void setObservador(IObservadorVariables _observador){
		observadores.add(_observador);
		Log.v("Añadir observador", "lista observadores: " + observadores.size());
	}

	public void removeObservador(IObservadorVariables _observador){
		observadores.remove(_observador);
		Log.v("Eliminar observador", "lista observadores: " + observadores.size());
	}

	public void deleteAllObservers(){
		observadores.clear();
	}

	public void Notify(final String _basketName, final VariablesList _variableList){
		if(!observadores.isEmpty()){
			AplicacionPrincipal.getInstance().addGlobalTask(new Runnable(){
				@Override
				public void run(){
					for(IObservadorVariables _observador : observadores){
						_observador.onNotifyVariables(_basketName, _variableList);
					}
				}
			});
		}

	}

}
