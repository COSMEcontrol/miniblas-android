/*
 * ItemTlgPerdido.java
 *
 * Created on 29 de mayo de 2006, 19:04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arcadio.ctp;

/**
 * @author fserna
 */
public class ItemTlgPerdido{

	private String telegrama;
	private int numPeticion;
	private long ms_emision;
	private int contadorReintentos = 0;


	/**
	 * Creates a new instance of ItemTlgPerdido
	 */
	public ItemTlgPerdido(int _numPeticion, String _telegrama){
		this.numPeticion = _numPeticion;
		this.telegrama = _telegrama;
		this.ms_emision = System.currentTimeMillis();
	}


	public int getNumPeticion(){
		return this.numPeticion;
	}

	public String getTelegrama(){
		return this.telegrama;
	}

	public long getMsTranscurridos(){
		return (System.currentTimeMillis() - this.ms_emision);
	}

	public void actualizarMsTranscurridos(){
		this.ms_emision = System.currentTimeMillis();
		this.contadorReintentos++;
	}


	public int getNumReintentos(){
		return this.contadorReintentos;
	}


}
