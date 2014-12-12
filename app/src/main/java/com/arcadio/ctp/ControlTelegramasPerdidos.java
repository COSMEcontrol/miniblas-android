/*
 * ControlTelegramasPerdidos.java
 *
 * Created on 29 de mayo de 2006, 19:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arcadio.ctp;


import com.arcadio.CosmeConnector;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author fserna
 */
public class ControlTelegramasPerdidos extends Thread{

	private long MS_TIMEOUT = 10000;  //10"
	private long periodoActivacionLimpieza = 6000;   // 6"

	private HashMap<Integer, ItemTlgPerdido> telegramasPendientes = new HashMap();

	private CosmeConnector emcos;

	private boolean ctpEnMarcha = true;


	/**
	 * Creates a new instance of ControlTelegramasPerdidos
	 */
	public ControlTelegramasPerdidos(CosmeConnector _emcos){
		this.emcos = _emcos;


		start();
	}


	public void setTimeOut(long _msTimeout){
		this.MS_TIMEOUT = _msTimeout;
	}

	public void setPeriodoActivacionCTP(long _periodoActivacionLimpieza){
		this.periodoActivacionLimpieza = _periodoActivacionLimpieza;
	}


	// Debe invocarse cuando se envía un telegrama por primera vez
	public synchronized void registrarNuevoTelegrama(int _numPeticion, String _tlg){

		ItemTlgPerdido t = new ItemTlgPerdido(_numPeticion, _tlg);
		telegramasPendientes.put(_numPeticion, t);
		//System.out.println("CTP. Nuevo: "+_numPeticion+"\t"+_tlg);
	}


	// Debe invocarse cuando se ha recibido un telegrama
	public synchronized void registrarTelegramaRecibido(int _numPeticion){

		telegramasPendientes.remove(_numPeticion);
		//System.out.println("CTP. Recibido: "+_numPeticion+"\tHay: "+this.getNumTelegramasPendientes());

	}


	public int getNumTelegramasPendientes(){
		return telegramasPendientes.size();
	}


	// Borra la tabla hash, y nos permite empezar de cero.
	// Normalmente se invoca cuando ha habido una desconexión... porque en ese caso
	// no nos sirve de nada el seguir llevando la cuenta de los telegramas que faltan...
	public void inicializar(){
		telegramasPendientes.clear();
	}


	private synchronized void detectarPendientes(){

		Iterator it = telegramasPendientes.values().iterator();
		for(ItemTlgPerdido t : telegramasPendientes.values()){

			if(t.getMsTranscurridos() > this.MS_TIMEOUT){
				t.actualizarMsTranscurridos();
				this.reenviarTelegrama(t.getTelegrama());
				//                System.out.println("TLG: "+t.getNumPeticion()+" reenviado. ("+t.getTelegrama()+")");
			}
			//            System.out.println("TLG: "+t.getNumPeticion()+" / "+t.getMsTranscurridos());

		} // for

	}


	public void run(){
		while(ctpEnMarcha){
			try{
				Thread.sleep(this.periodoActivacionLimpieza);
				if(ctpEnMarcha){
					this.detectarPendientes();
				}

			}catch(Exception e){
			}


		}// true

		this.inicializar();
	}


	public void destruir(){
		ctpEnMarcha = false;
		this.inicializar();
		this.interrupt();

	}

	private void reenviarTelegrama(String _tlg){
		System.out.println("REENVIADO: " + _tlg);
		emcos.reenviarTelegrama(_tlg);
	}


}
