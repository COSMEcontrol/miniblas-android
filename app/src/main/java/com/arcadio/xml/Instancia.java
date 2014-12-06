/*
 * Instancia.java
 *
 * Created on 13 de febrero de 2007, 17:18
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arcadio.xml;

/**
 * @author fserna
 */
public class Instancia{
	private String nombreInstancia;
	private String clase;
	private int orden;
	private boolean habilitado;

	/** Creates a new instance of Instancia */
	public Instancia(){
	}

	public String getNombreInstancia(){
		return nombreInstancia;
	}

	public void setNombreInstancia(String nombreInstancia){
		this.nombreInstancia = nombreInstancia;
	}

	public String getClase(){
		return clase;
	}

	public void setClase(String clase){
		this.clase = clase;
	}

	public int getOrden(){
		return orden;
	}

	public void setOrden(int orden){
		this.orden = orden;
	}

	public boolean isHabilitado(){
		return habilitado;
	}

	public void setHabilitado(boolean habilitado){
		this.habilitado = habilitado;
	}

}
