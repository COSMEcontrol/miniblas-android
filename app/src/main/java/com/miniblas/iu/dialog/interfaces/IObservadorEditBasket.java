package com.miniblas.iu.dialog.interfaces;

import com.miniblas.model.MiniBlasBag;

/**
 * Created by alberto on 14/11/14.
 */
public interface IObservadorEditBasket{
	/**
	 * Notificar que se ha pulsado el boton de guardar y recepción de data.
	 * Este evento se ejecuta en un hilo independiente a la IU
	 */
	public void OnButtonEditSave(MiniBlasBag data);

	/**
	 * Notificar que se ha pulsado el botón de cancelar y recepción de data.
	 * Este evento se ejecuta en un hilo independiente a la IU
	 */
	public void OnButtonEditCancel(MiniBlasBag data);
}
