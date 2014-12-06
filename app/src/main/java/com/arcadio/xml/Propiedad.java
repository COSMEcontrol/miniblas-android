/*
 * Propiedad.java
 *
 * Created on 13 de febrero de 2007, 17:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arcadio.xml;

/**
 * @author fserna
 */
public class Propiedad{

	private String nombrePropiedad;
	private String valor;

	/** Creates a new instance of Propiedad */
	public Propiedad(){
	}

	public String getNombrePropiedad(){
		return nombrePropiedad;
	}

	public void setNombrePropiedad(String nombrePropiedad){
		this.nombrePropiedad = nombrePropiedad;
	}

	public String getValor(){
		return valor;
	}

	public void setValor(String valor){
		this.valor = valor;
	}

}
