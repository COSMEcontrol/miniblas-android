/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.arcadio;


import com.arcadio.common.ItemVariable;
import com.arcadio.common.VariablesList;

/**
 * @author fserna
 */
public class Watchdog extends Thread{

	private ConexionEmcos emcos;
	private boolean estoyVivo = true;
	private int periodo = 2000; // 2" por defecto


	public Watchdog(ConexionEmcos _emcos){
		this.emcos = _emcos;
		this.estoyVivo = true;
		this.start();
	}

	/**
	 * @param _emcos
	 * @param _periodoMs Periodo con el que se env�an telegramas de ping.
	 */
	public Watchdog(ConexionEmcos _emcos, int _periodoMs){
		this.emcos = _emcos;
		setPeriod(_periodoMs);
		this.estoyVivo = true;
		this.start();
	}

	public void run(){
		VariablesList lv = new VariablesList();
		ItemVariable iv = new ItemVariable("SISTEMA.tiempo_ciclo.rt");
		lv.add(iv);

		while(this.estoyVivo){
			if(emcos != null){
				if(emcos.isConnected()){
					emcos.ping(lv);
				}else if(emcos.isReconexionActivada()){
					System.out.println("Probando a reconectar...");

					emcos.conectar(true);

					System.out.println("Reconectado!");
				}
			}

			try{
				Thread.sleep(getPeriod());
			}catch(InterruptedException e){
				//hilo interrumpido antes de morir.
			}

		}

	}


	public void destroy(){
		this.estoyVivo = false;
		this.interrupt();
	}

	/**
	 * Permite modificar el periodo con el que se env�an telegrams de ping.
	 *
	 * @param _periodoMs En milisegundos. Debe ser necesariamente <b>MAYOR que 0</b>. De no ser as� este
	 *                   m�todo no tiene ning�n efecto.
	 */
	public synchronized void setPeriod(int _periodoMs){
		if(_periodoMs > 0){
			this.periodo = _periodoMs;
		}else{
			this.estoyVivo = false;
			this.periodo = 0;
		}
	}

	public synchronized int getPeriod(){
		return this.periodo;
	}

	public synchronized void setEnabled(boolean _enabled){
		if(_enabled){
			if(!this.estoyVivo){
				this.estoyVivo = true;
				start();
			}
		}else{
			this.estoyVivo = false;
			this.interrupt();
		}
	}

}
