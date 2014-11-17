package com.miniblas.iu.controllers;

import java.util.List;


public class OnClickButtonDeleteListener<T>{
	
	private ObservadorDelete observador;
	
	public interface ObservadorDelete<T>{
		/**
		 * Notificar que se ha pulsado el boton de eliminar.
		 * Este evento se ejecuta en un hilo independiente a la IU
		 */
		public void OnButtonDelete(List<T> _elementos);

	}
	
	public OnClickButtonDeleteListener(ObservadorDelete _observador){
		this.observador=_observador;
	}
	
	public void onClickDelete(final List<T> _data) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				observador.OnButtonDelete(_data);	
			}
		}).start();
		
	}
}
