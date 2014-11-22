package com.arcadio;

import java.io.IOException;
import java.io.OutputStreamWriter;

import android.util.Log;

public class Ping extends Thread{
	private long periodoEnvio;
	private boolean vivo = true;
	private Conexion conexion;
	private OutputStreamWriter flujoSalida;
	private SintaxisTelegrama sintaxisTelegrama;
	private String nomVariable;
	private String tlg;
	
	
	public Ping(Conexion _conexion, SintaxisTelegrama _sintaxisTelegrama, String _nomVariable, long _periodoEnvio) {
		this.conexion = _conexion;
		this.flujoSalida = _conexion.getFlujoSalida();
		this.sintaxisTelegrama = _sintaxisTelegrama;
		this.nomVariable = _nomVariable;
		this.periodoEnvio=_periodoEnvio;
	}

	public void run() {
		int i = 0;
		
		if (conexion.getEstado() == EstadosCosme.COMUNICACION_OK) {
			while(vivo) {
				i++;
				try {
					tlg = sintaxisTelegrama.getTelegrama_ping(nomVariable);
					flujoSalida.write(tlg + "\n"); 
					flujoSalida.flush();
					try {
						Thread.sleep(periodoEnvio);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
//					Log.v("Ping","Enviado ping "+i+" "+tlg);
				} catch (IOException e) {
					e.printStackTrace();
//					Log.v("Ping","Enviado ping fallido "+i);
					conexion.cambiarEstado(EstadosCosme.CONEXION_INTERRUMPIDA);
				}
			}
		}
	}

	public boolean isVivo() {
		return vivo;
	}

	public void destroy() {
		this.vivo = false;
	}
	
}
