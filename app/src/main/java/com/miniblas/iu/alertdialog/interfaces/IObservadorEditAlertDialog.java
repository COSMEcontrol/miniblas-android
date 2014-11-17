package com.miniblas.iu.alertdialog.interfaces;

/**
 * Created by alberto on 14/11/14.
 */
public interface IObservadorEditAlertDialog<T> {
        /**
         * Notificar que se ha pulsado el boton de guardar y recepción de data.
         * Este evento se ejecuta en un hilo independiente a la IU
         */
        public void OnButtonEditSave(T data);
        /**
         * Notificar que se ha pulsado el botón de cancelar y recepción de data.
         * Este evento se ejecuta en un hilo independiente a la IU
         */
        public void OnButtonEditCancel(T data);
}
