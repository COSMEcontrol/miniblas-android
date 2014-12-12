package com.miniblas.iu.controllers;


import com.miniblas.app.AplicacionPrincipal;

public class ConnectionListener{

	private IObservadorConnection observador;
	//	private T data;

	public interface IObservadorConnection{
		/**
		 * Notificar que se ha cambiando el estado a conectado.
		 * Este evento se ejecuta en un hilo independiente a la IU
		 */
		public void OnConnect();

		/**
		 * Notificar que se ha cambiando el estado a desconectado.
		 * Este evento se ejecuta en un hilo independiente a la IU
		 */
		public void OnDisconnect();
	}

	//	public ConnectionIconListener(ObservadorConnectionIcon observador){
	//		this.observador=observador;
	//	}
	public void setObservador(IObservadorConnection _observador){
		this.observador = _observador;
	}

	public void onConnectNotify(){
		if(observador != null){
			AplicacionPrincipal.getInstance().addGlobalTask(new Runnable(){

				@Override
				public void run(){
					observador.OnConnect();
				}
			});
		}

	}

	public void onDisconnectNotify(){
		if(observador != null){
			AplicacionPrincipal.getInstance().addGlobalTask(new Runnable(){

				@Override
				public void run(){
					observador.OnDisconnect();

				}
			});
		}
	}
}