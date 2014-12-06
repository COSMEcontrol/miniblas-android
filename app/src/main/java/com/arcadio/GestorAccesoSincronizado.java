/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.arcadio;


import com.arcadio.common.VariablesList;

/**
 * @author fserna
 */
public class GestorAccesoSincronizado{

	static private GestorAccesoSincronizado gas;
	private boolean bloqueado = false;
	private VariablesList lv = null;


	private GestorAccesoSincronizado(){

	}


	static public GestorAccesoSincronizado getInstance(){
		if(gas == null){
			gas = new GestorAccesoSincronizado();
		}

		return gas;
	}


	public synchronized boolean estaBloqueado(){
		return this.bloqueado;
	}


	synchronized public void bloquear(){
		this.bloqueado = true;
		this.lv = null;
	}


	synchronized public void desbloquear(VariablesList _lv){
		this.lv = _lv;
		this.bloqueado = false;
	}

	/*
	 * // se usaba para el tlg: escribir_bloqueo.
	 * // ahora usa el waitUltimoTelegrama() que es m�s elegante, por lo que
	 * // este m�todo ya no es necesario
	synchronized public void desbloquear(){
		this.bloqueado = false;
	}
	*/
	public synchronized VariablesList getListaVariables(){
		return this.lv;
	}
}
