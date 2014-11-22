package com.arcadio;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

import com.arcadio.exceptions.TelegramTypesException;
import com.arcadio.modelo.Cesta;
import com.arcadio.modelo.ItemVariable;


public class Oyente extends Thread{
	private Conexion conexion;
	private BufferedReader flujoEntrada = null;
	private boolean entradaNula = false;
    private boolean estoyEnMarcha = true;
    private Telegrama telegramaRecibido;
    private String idTelegrama;
	
	public Oyente(Conexion _conexion) {
		this.conexion = _conexion;
		this.flujoEntrada = _conexion.getFlujoEntrada();
		this.setDaemon(true);
	}
	
	public void destroy (){
        this.entradaNula = true;
        this.estoyEnMarcha = false;
    }
	
	public void run() {
		String cadena = "";
		TelegramTypes tipoTelegrama;
		
		while( ! entradaNula){
			try {          
				cadena = flujoEntrada.readLine();
				if(cadena == null){
                    entradaNula = true;
                }else{
//                    try{
                        try {
							telegramaRecibido = new Telegrama(cadena);
						} catch (TelegramTypesException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        
//                    }catch (Exception e){
//                        telegramaRecibido = null;
//                        e.printStackTrace();
//                    }
                }
				
				if(telegramaRecibido == null) continue;
				
				idTelegrama = telegramaRecibido.getIdTelegrama();
				try{
					tipoTelegrama = TelegramTypes.valueOf(idTelegrama.toUpperCase());
					switch (tipoTelegrama){
					
					case LEER: 				
						new ItemVariable(telegramaRecibido.getValorVariable(), telegramaRecibido.getNombreVariable());
						break;
						
					case PING: 				
						new ItemVariable(telegramaRecibido.getValorVariable(), telegramaRecibido.getNombreVariable());
						break;
					
					case CREAR_CESTA:
						//notificar cesta creada o error 
						if (!telegramaRecibido.getListaErrores().isEmpty()){
							Iterator<CosmeError> iterador =  telegramaRecibido.getListaErrores().iterator();
							while(iterador.hasNext()){
								CosmeError mensajeError = iterador.next();
								conexion.notificarError(mensajeError);
							}
						}
	//					else
	//						conexion.notificarEvento(EstadosCosme.RECIBIDA_CESTA, telegramaRecibido);
						break;
					case CESTA:
						//notificar refresco variables de una cesta o error
						if (!telegramaRecibido.getListaErrores().isEmpty()){
							Iterator<CosmeError> iterador =  telegramaRecibido.getListaErrores().iterator();
							while(iterador.hasNext()){
								CosmeError mensajeError = iterador.next();
								conexion.notificarError(mensajeError);
							}
						}
						if(!telegramaRecibido.getListaVariables().isEmpty()){
							conexion.notificarRefrescoVariables(telegramaRecibido.getNombreCesta(), telegramaRecibido.getListaVariables());
						}					
						break;
					case LISTA_NOMBRES:
						conexion.notificarListaVariablesCosme(telegramaRecibido.getListaVariables());
						break;
					case IS_NUMERIC:
						conexion.notificarIsNumeric(telegramaRecibido.getVariable());
						break;
						
					case ESCRIBIR:
						if (!telegramaRecibido.getListaErrores().isEmpty()){
							Iterator<CosmeError> iterador =  telegramaRecibido.getListaErrores().iterator();
							while(iterador.hasNext()){
								CosmeError mensajeError = iterador.next();
								conexion.notificarError(mensajeError);
							}
						}
						break;
					
					case INSERTAR_NOM_CESTA:
						conexion.notificarNomCesta(new Cesta(telegramaRecibido.getNombreCesta()), new ItemVariable(telegramaRecibido.getNombreVariable()));
						break;
					default:
						break;
					}
				}catch(IllegalArgumentException e){
					e.printStackTrace();
				}
			}catch (IOException ex) {
				entradaNula = true;
				conexion.cambiarEstado(EstadosCosme.CONEXION_INTERRUMPIDA);
			}
		}
		
		if (estoyEnMarcha){
            conexion.cambiarEstado(EstadosCosme.CONEXION_INTERRUMPIDA);
        }
	}
}

