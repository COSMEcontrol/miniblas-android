package com.miniblas.iu.controllers;

import android.util.Log;

import com.arcadio.common.VariablesList;
import com.miniblas.app.AplicacionPrincipal;

import java.util.ArrayList;

public class ObservadorVariables{
	private IObservadorVariables observador;

	public interface IObservadorVariables{
		/**
		 * Notificar que se ha cambiando el estado a conectado.
		 * Este evento se ejecuta en un hilo independiente a la IU
		 */
		public void onNotifyVariables(String _nombreCesta, VariablesList _variableList);

	}

	public void setObservador(IObservadorVariables _observador){
		observador=_observador;
		Log.v("Añadir observador", "Añadir observador");
	}

	public void deleteAllObservers(){
		observador=null;
	}

	public void Notify(final String _basketName, final VariablesList _variableList){
		if(observador!=null){
			AplicacionPrincipal.getInstance().addGlobalTask(new Runnable(){
				@Override
				public void run(){
					//for(IObservadorVariables _observador : observadores){
						observador.onNotifyVariables(_basketName, _variableList);
					}
				//}
			});
		}

	}

}
