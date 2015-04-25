package com.miniblas.iu.dialog.interfaces;

import com.miniblas.model.base.BaseElement;
import com.miniblas.model.base.BaseElementList;

/**
 * Created by alberto on 14/11/14.
 */
public interface IObservadorEditDialog <T extends BaseElement>{
	/**
	 * Notificar que se ha pulsado el boton de guardar y recepción de data.
	 * Este evento se ejecuta en un hilo independiente a la IU
	 */
	public void OnButtonEditSave(BaseElementList<T> data);

	/**
	 * Notificar que se ha pulsado el botón de cancelar y recepción de data.
	 * Este evento se ejecuta en un hilo independiente a la IU
	 */
	public void OnButtonEditCancel(BaseElementList<T> data);
}
