/*
 * Muestreador.java
 *
 * Created on 1 de junio de 2007, 11:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arcadio.muestreador;

import com.arcadio.CosmeConnector;
import com.arcadio.CosmeListener;
import com.arcadio.api.v1.service.CosmeStates;

import java.util.Random;
/**
 *
 * @author fserna
 */
public class Muestreador{


	private CosmeListener app;
	private CosmeConnector emcos;


	private String nombre = "M"; // el prefijo del nombre del muestreador es
	// "M" porque lo digo yo.
	// El identificador de la zona de memoria compartida
	// debe ser de no más de 6 caracteres. Y se usa
	// el nombre del muestreador como ident. de la
	// zona de memoria compartida asociada.
	private String variableMuestreada;
	private String variableDisparo;

	private int indexUltimaMuestraRecibida = 0;
	private int numTotalMuestras = 0;
	private int msMuestreo = 0;
	private double[] valores;
	private boolean modeSingle = true;  // el modo puede ser "single" o "continuous"
	private boolean habilitado = false;
	private boolean creado = false;  // tenemos constancia de que el muetreador asociado ya ha sido creado en el runtime ???

	/**
	 * Creates a new instance of Muestreador
	 */
	public Muestreador(CosmeListener _app, CosmeConnector _emcos, String _nombreVariableAMuestrear, String _nombreVariableDisparo, int _numMuestras, int _msMuestreo){
		this.app = _app;
		this.emcos = _emcos;
		this.variableMuestreada = _nombreVariableAMuestrear;
		this.variableDisparo = _nombreVariableDisparo;
		this.nombre = getNombreAleatorio(nombre);
		this.modeSingle = true;

		this.setNumTotalMuestras(_numMuestras);
		this.msMuestreo = _msMuestreo;
	}


	// Modo Continuous
	public Muestreador(CosmeListener _app, String _nombreVariableAMuestrear, int _numMuestras){
		this.app = _app;
		//  this.emcos =        _emcos;
		this.variableMuestreada = _nombreVariableAMuestrear;
		//this.variableDisparo    = _nombreVariableDisparo;
		this.nombre = getNombreAleatorio(nombre);
		this.modeSingle = false; // modo continuous
		this.setNumTotalMuestras(_numMuestras);
	}


	// devuelve el nombre asociado al muestreador, que coincide con el identificador
	// de la zona de memoria compartida asociada.
	public String getNombre(){
		return nombre;
	}


	public String getVariableMuestreada(){
		return variableMuestreada;
	}

	public void setVariableMuestreada(String val){
		this.variableMuestreada = val;
	}

	public String getVariableDisparo(){
		return variableDisparo;
	}

	public void setVariableDisparo(String variableDisparo){
		this.variableDisparo = variableDisparo;
	}

	public double getValor(int _index){
		double valor = Double.NaN;
		if((_index >= 0) && (_index < this.numTotalMuestras)){
			valor = valores[_index];
		}

		return valor;
	}

	public void setValor(int _index, double _valor){
		if((_index >= 0) && (_index < this.numTotalMuestras)){
			//System.out.println(_index+"\t"+_valor+"\t"+this.getNumMuestrasRecibidas());
			valores[_index] = _valor;
			this.indexUltimaMuestraRecibida = _index;

			if(this.indexUltimaMuestraRecibida + 1 == this.numTotalMuestras){
				// si no estamos en modo "single" no tiene sentido que me avisen de que han llegado todos los datos
				if(this.modeSingle){
					//  emcos.deshabilitarMuestreador(this.nombre);
					app.onStateChange(CosmeStates.MUESTREADOR_FIN);
				}
			}
		}
	}

	public int getTiempoMuestreo(){
		return this.msMuestreo;
	}

	public int getNumMuestrasRecibidas(){
		return this.indexUltimaMuestraRecibida;
	}


	/**
	 * @return Devuelve el número de muestras que se almacenan de este muestreadpr.
	 */
	public int getNumTotalMuestras(){
		return this.numTotalMuestras;
	}


	/**
	 * Permite modificar el número de muestras que van a almacenarse para este muestreador.
	 *
	 * @param _numMuestras
	 */
	public void setNumTotalMuestras(int _numMuestras){
		this.numTotalMuestras = _numMuestras;

		// inicializamos el vector a valores nulos
		valores = new double[this.numTotalMuestras];
		for(int i = 0; i < this.numTotalMuestras; i++){
			valores[i] = Double.NaN;
		}
	}


	public int getPorcentajeFinalizacion(){
		int porcentaje = 0;
		if(this.numTotalMuestras != 0){
			porcentaje = (int) (100.0 * (double) ((double) (this.indexUltimaMuestraRecibida + 1) / (double) this.numTotalMuestras));
		}

		return porcentaje;
	}


	/**
	 * Olvidamos los datos que hayamos podido recibir, y empezaremos de cero.
	 */
	public void reset(){
		//this.numTotalMuestras = 0;
		this.indexUltimaMuestraRecibida = 0;
	}

	public String getNombreVar_num_muestras_tomar(){
		return nombre + ".num_muestras_tomar";
	}

	public String getNombreVar_num_muestras_tomadas(){
		return nombre + ".num_muestras_tomadas";
	}


	public String getNombreVar_tiempo(){
		return nombre + ".tiempo";
	}

	public String getNombreVar_tiempo_transcurrido(){
		return nombre + ".tiempo_transcurrido";
	}


	private String getNombreAleatorio(String _prefijo){

		// int numDigitos = 8;
		Random r = new Random();
		String key = _prefijo + "_" + Long.toString(Math.abs(r.nextLong()), 36);

		return key.substring(0, 6);

	}

	/**
	 * @return the habilitado
	 */
	public boolean isHabilitado(){
		return this.habilitado;
	}

	/**
	 * @param habilitado the habilitado to set
	 */
	public void setHabilitado(boolean habilitado){
		this.habilitado = habilitado;
	}

	/**
	 * @return the modeSingle
	 */
	public boolean isModeSingle(){
		return modeSingle;
	}

	/**
	 * @param modeSingle the modeSingle to set
	 */
	public void setModeSingle(boolean modeSingle){
		this.modeSingle = modeSingle;
	}

	/**
	 * @return the creado
	 */
	public boolean isCreado(){
		return creado;
	}

	/**
	 * @param creado the creado to set
	 */
	public void setCreado(boolean creado){
		this.creado = creado;
	}


}