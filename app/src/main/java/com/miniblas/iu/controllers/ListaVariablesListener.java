package com.miniblas.iu.controllers;


public class ListaVariablesListener <T>{
	private ObservadorListaVariables<T> observador;
	//	private T data;

	public interface ObservadorListaVariables <T>{
		/**
		 * Notificar que se ha recibido la lista de variables.
		 * Este evento se ejecuta en un hilo independiente a la IU
		 */
		public void OnReceivedVariablesList(T _variables);

	}

	//	public ConnectionIconListener(ObservadorConnectionIcon observador){
	//		this.observador=observador;
	//	}
	public void setObservador(ObservadorListaVariables<T> _observador){
		this.observador = _observador;
	}

	public void onReceivedNotify(final T _variables){
		if(observador != null){
			new Thread(new Runnable(){

				@Override
				public void run(){
					observador.OnReceivedVariablesList(_variables);
				}
			}).start();
		}

	}
}
