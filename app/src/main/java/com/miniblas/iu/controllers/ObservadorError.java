package com.miniblas.iu.controllers;

import com.arcadio.api.v1.service.CosmeStates;
import com.miniblas.app.AplicacionPrincipal;

public class ObservadorError{
	private IObservadorError observadores;

	public interface IObservadorError{
		/**
		 * Notificar que se ha cambiando el estado.
		 * Este evento se ejecuta en un hilo independiente a la IU
		 */
		public void onNotifyError(String _error);

	}

	public void setObservador(IObservadorError _observador){
		observadores = _observador;
	}


	public void deleteAllObservers(){
		observadores=null;
	}

	public void notify(final String _txtError){
		if(observadores != null){
			AplicacionPrincipal.getInstance().addGlobalTask(new Runnable(){
				@Override
				public void run(){
					//for(IObservadorState _observador : observadores){
						observadores.onNotifyError(_txtError);
					//}
				}
			});
		}

	}

}
