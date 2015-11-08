package com.miniblas.iu.controllers;

import com.arcadio.api.v1.service.CosmeStates;
import com.miniblas.app.AplicacionPrincipal;
/**
 *
 * @author A. Azuara
 */
public class ObservadorState{
	private IObservadorState observadores;

	public interface IObservadorState{
		/**
		 * Notificar que se ha cambiando el estado.
		 * Este evento se ejecuta en un hilo independiente a la IU
		 */
		public void onNotifyNewState(CosmeStates _state);

	}

	public void setObservador(IObservadorState _observador){
		observadores = _observador;
	}


	public void deleteAllObservers(){
		observadores=null;
	}

	public void notify(final CosmeStates _state){
		if(observadores != null){
			AplicacionPrincipal.getInstance().addGlobalTask(new Runnable(){
				@Override
				public void run(){
					//for(IObservadorState _observador : observadores){
						observadores.onNotifyNewState(_state);
					//}
				}
			});
		}

	}

}
