package com.arcadio;

import android.util.Log;

import com.arcadio.api.v1.service.CosmeStates;
import com.arcadio.common.Bag;
import com.arcadio.common.ItemVariable;
import com.arcadio.common.NamesList;
import com.arcadio.common.NumericVariable;
import com.arcadio.common.TextVariable;
import com.arcadio.common.VariablesList;
import com.arcadio.ctp.ControlTelegramasPerdidos;
import com.arcadio.muestreador.Muestreador;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Thread que se encarga de escuchar el socket por el que
 * recibimos los telegramas de respuesta.
 * Debe estar siempre activo, si muere nos resultará imposible recibir
 * ningún otro telegrama, y el sistema quedará en una situación de error
 * irrecuperable.
 *
 * @author Félix Serna, Alberto Azuara García
 */
public class OyenteTelegrama extends Thread{
	// private static final Logger logger = Logger.getLogger(OyenteTelegrama.class);
	static final public String IDENTIFICADOR_DE_CLASE = "SISTEMA.CLASE";

	private BufferedReader entrada;
	private CosmeListener emcosListener;
	private Telegram telegramRecibido;
	private boolean entradaNula = false;
	private boolean estoyEnMarcha = true;
	private CosmeConnector conexion;
	private ControlTelegramasPerdidos ctp = null;
	private long t1, t2, t;
	private Semaphore pendingTlg;

	/** Creates a new instance of OyenteTelegrama */
	public OyenteTelegrama(CosmeListener _emcosListener, Semaphore _pendingTlg, CosmeConnector _conexion){
		this.emcosListener = _emcosListener;
		this.pendingTlg = _pendingTlg;
		this.conexion = _conexion;
		this.entrada = conexion.getCanalEntrada();

		this.setDaemon(true);
		this.start();
	}

	@Override
	public void destroy(){
		this.estoyEnMarcha = false; // hace que no envíe el evento CONEXION_INTERRUMPIDA
		this.entradaNula = true;
	}

	@Override
	public void run(){
		String cadena = "";

		while(!entradaNula){
			try{
				cadena = entrada.readLine();

				t1 = System.currentTimeMillis();

				if(conexion.isDebugON()){
					System.err.println(System.currentTimeMillis() + " Rx <-- " + cadena);
				}

				if(cadena == null){
					entradaNula = true;
					continue;
				}

				try{
					telegramRecibido = new Telegram(cadena);
				}catch(Exception e){
					telegramRecibido = null;
				}

				if(telegramRecibido == null){
					continue;
				}

				ctp = conexion.getCTPManager();
				if(ctp != null){
					ctp.registrarTelegramaRecibido(telegramRecibido.getNumPeticion());
				}

				if(telegramRecibido.contieneErrores()){
					int i = 1;
					for(String msg : telegramRecibido.getMensajesError()){
						emcosListener.onError("ERROR (" + i + "/" + telegramRecibido.getMensajesError().size() + "): " + msg);
						i++;
					}

					continue;
				}

				if(telegramRecibido.isValido()){
					processTelegram();

					// Notifica la llegada de un mensaje
					// IMPORTANTE para primitivas BLOQUEANTES
					pendingTlg.release();
				}else{
					//                logger.warn("Telegrama inválido: " + cadena);
				}
			}catch(IOException ex1){
				entradaNula = true;

				//             if (estoyEnMarcha)
				//                 logger.error(ex1.getMessage());
			}catch(Exception ex2){
				//             if (estoyEnMarcha)
				//                 logger.error(ex2.getMessage());
			}

			t2 = System.currentTimeMillis();
			t = t2 - t1;

			conexion.setMsTlgProcessing(t);

		}// while no entradaNula

		// Se notifica la pérdida de la conexión si no me han llamado
		// antes el método "destruir".
		if(estoyEnMarcha){
			conexion.cambiarEstado(CosmeStates.CONNECTION_INTERRUPTED);
		}

	}// run


	private void processTelegram(){
		String nombreMuestreador;
		Muestreador m;
		int numMuestras;
		GestorAccesoSincronizado gas;
		VariablesList lv;
		String tipo;

		if(telegramRecibido == null){
			return;
		}

		// permitirá a ConexionEmcos gestionar los telegramas con bloqueo
		// el telegrama lista_nombres_tipo es un poco especial!!
		if(telegramRecibido.getIdTelegrama() != TelegramTypes.LISTA_NOMBRES_TIPO && telegramRecibido.getIdTelegrama() != TelegramTypes.LISTA_NOMBRES_TIPO_HABILITADOS){
			if(telegramRecibido.getNumPeticion() > conexion.getNumUltimoTelegramaRecibido()){
				conexion.setNumUltimoTelegramaRecibido(telegramRecibido.getNumPeticion());
			}
		}
		switch(telegramRecibido.getIdTelegrama()){
			// <editor-fold defaultstate="collapsed" desc=" LEER ">
			case LEER:
				lv = conexion.getNombres();
				for(ItemVariable iv : telegramRecibido.getListaVariables().getList()){
					ItemVariable var = lv.getVariable(telegramRecibido.getNombreInstancia());
					if(var != null){
						if(var instanceof NumericVariable){
							((NumericVariable) var).setValor(((NumericVariable) iv).getValue());
						}else if(var instanceof TextVariable){
							((TextVariable) var).setValue(((TextVariable) iv).getValue());
						}
					}
				}

				emcosListener.onDataReceived(telegramRecibido.getNombreCesta(), telegramRecibido.getListaVariables());
				break;
			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" ESCRIBIR ">
			case ESCRIBIR:
				emcosListener.onStateChange(CosmeStates.WRITE_CONFIRMED);
				break;
			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" PING ">
			case PING:
				conexion.setPingRXTime();
				lv = conexion.getNombres();
				for(ItemVariable iv : telegramRecibido.getListaVariables().getList()){
					ItemVariable var = lv.getVariable(telegramRecibido.getNombreInstancia());
					if(var != null){
						if(var instanceof NumericVariable){
							((NumericVariable) var).setValor(((NumericVariable) iv).getValue());
						}else if(var instanceof TextVariable){
							((TextVariable) var).setValue(((TextVariable) iv).getValue());
						}
					}
				}
				break;
			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" CESTA ">
			case CESTA:
				lv = conexion.getNombres();
				for(ItemVariable iv : telegramRecibido.getListaVariables().getList()){
						if(iv instanceof NumericVariable){
							ItemVariable var = lv.getVariable(iv.getName());
							if(var != null){
								if(var instanceof NumericVariable){
									((NumericVariable) var).setValor(((NumericVariable) iv).getValue());
								}else{
									lv.add(iv.getName(),((NumericVariable) iv).getValue());
								}
							}else{
								lv.add((NumericVariable) iv);
							}

						}else if(iv instanceof TextVariable){
							ItemVariable var = lv.getVariable(iv.getName());
							if(var != null){
								if(var instanceof TextVariable){
									((TextVariable) var).setValue(((TextVariable) iv).getValue());
								}else{
									lv.add(iv.getName(),((TextVariable) iv).getValue());
								}
							}else{
								lv.add((TextVariable) iv);
							}
						}
				}
				if(!telegramRecibido.esEco()){
					String prefijoCesta = telegramRecibido.getNombreCesta();
					int index = telegramRecibido.getNombreCesta().indexOf(Bag.BASKET_NAME_SEPARATOR);

					if(index != -1){
						prefijoCesta = prefijoCesta.substring(0, index);
					}
					emcosListener.onDataReceived(prefijoCesta, telegramRecibido.getListaVariables());
				}

				break;
			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" IS_NUMERIC ">
			case IS_NUMERIC:
				emcosListener.onStateChange(CosmeStates.ISNUMERIC_RECEIVED);
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" TIPO_NOMBRE ">
			case TIPO_NOMBRE:
				conexion.setTipoNombre(telegramRecibido.getNombreClase());
				emcosListener.onStateChange(CosmeStates.RECEIVED_NAME_TYPE);
				break;
			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" LEER_BLOQUEO ">
			case LEER_BLOQUEO:
				lv = conexion.getNombres();
				for(ItemVariable iv : telegramRecibido.getListaVariables().getList()){
					ItemVariable var = lv.getVariable(telegramRecibido.getNombreInstancia());
					if(var != null){
						if(var instanceof NumericVariable){
							((NumericVariable) var).setValor(((NumericVariable) iv).getValue());
						}else if(var instanceof TextVariable){
							((TextVariable) var).setValue(((TextVariable) iv).getValue());
						}
					}
				}

				gas = GestorAccesoSincronizado.getInstance();
				gas.desbloquear(telegramRecibido.getListaVariables());

				break;
			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" ESCRIBIR_BLOQUEO ">
			case ESCRIBIR_BLOQUEO:

				break;
			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" LISTA_NOMBRES ">
			case LISTA_NOMBRES:
				for(ItemVariable v : telegramRecibido.getListaVariables().getList()){
					conexion.anadirNombre(v.getName());
				}
				//System.out.println ("En el TLG hay: "+telegramaRecibido.getListaVariables().getLista().size()+"\t y en el saco: "+conexion.getListaNombres().getNumNombres());

				if(!telegramRecibido.isListaNombresCompleta()){
					conexion.cambiarEstado(CosmeStates.RECEIVING_NAMES_LIST);
					conexion.pedirListaNombres(); //
				}else{
					conexion.cambiarEstado(CosmeStates.NAMES_LIST_RECEIVED);
				}
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" LISTA_NOMBRES_TIPO_HABILITADOS ">
			case LISTA_NOMBRES_TIPO_HABILITADOS:
				tipo = telegramRecibido.getTipoVariable();
				for(ItemVariable v : telegramRecibido.getListaVariablesDeTipo().getList()){
					conexion.addNombreDeTipo(v.getName());
				}

				if(!telegramRecibido.isListaNombresCompleta()){
					conexion.cambiarEstado(CosmeStates.RECEIVING_NAMES_LIST);
					conexion.pedirListaNombresTipoHabilitados(tipo);
				}else{
					conexion.cambiarEstado(CosmeStates.NAMES_LIST_RECEIVED);
					conexion.setNumUltimoTelegramaRecibido(telegramRecibido.getNumPeticion());
				}
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" LISTA_NOMBRES_NIVEL ">
			case LISTA_NOMBRES_NIVEL:
				String prefijo = telegramRecibido.getPrefijo();

				if(prefijo != null){
					for(ItemVariable v : telegramRecibido.getListaVariables().getList()){
						conexion.anadirNombre(v.getName());
					}
				}
				//System.out.println ("En el TLG hay: "+telegramaRecibido.getListaVariables().getLista().size()+"\t y en el saco: "+conexion.getListaNombres().getNumNombres());

				if(!telegramRecibido.isListaNombresCompleta()){
					conexion.cambiarEstado(CosmeStates.RECEIVING_NAMES_LIST);
					conexion.pedirListaNombresNivel(prefijo); //
				}else{
					conexion.cambiarEstado(CosmeStates.NAMES_LIST_RECEIVED);
				}
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" LISTA_TIPOS ">
			case LISTA_TIPOS:
				for(ItemVariable v : telegramRecibido.getListaVariables().getList()){
					conexion.anadirTipo(v.getName());
				}

				if(!telegramRecibido.isListaTiposCompleta()){
					conexion.cambiarEstado(CosmeStates.RECEIVING_TYPE_LIST);
					conexion.pedirListaNombres(); //
				}else{
					conexion.cambiarEstado(CosmeStates.RECEIVED_TYPE_LIST);
				}

				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" LISTA_NOMBRES_TIPO ">
			case LISTA_NOMBRES_TIPO:
				tipo = telegramRecibido.getTipoVariable();
				NamesList ln = conexion.getNombresExistentes();

				for(ItemVariable iv : telegramRecibido.getListaVariablesDeTipo().getList()){
					conexion.addNombreDeTipo(iv.getName());
				}


				// CUANDO EL TLG TRAE LA LISTA DE CLASES
				if(telegramRecibido.getTipoVariable().equals(IDENTIFICADOR_DE_CLASE)){ //"SISTEMA.CLASE"
					for(ItemVariable v : telegramRecibido.getListaVariablesDeTipo().getList()){
						//  conexion.anadirTipo(v.getNameElement());
						ln.setType(v.getName(), tipo);
					}// for

					// Si no ha llegado la marca de fin, pide otro telegrama con el siguiente cacho
					if(!telegramRecibido.isListaNombresTipoCompleta()){
						conexion.cambiarEstado(CosmeStates.RECEIVING_CLASS_LIST);
						conexion.pedirListaNombresDeTipo(tipo);
					}else{
						conexion.cambiarEstado(CosmeStates.RECEIVED_CLASS_LIST);
						conexion.setNumUltimoTelegramaRecibido(telegramRecibido.getNumPeticion());
					}

					// CUANDO EL TLG TRAE NOMBRES DE UN TIPO CUALQUIERA, distinto de "SISTEMA.CLASE"
				}else{
					for(ItemVariable v : telegramRecibido.getListaVariablesDeTipo().getList()){
						ln.setType(v.getName(), tipo);
						//           numeroTelegramasDeTipoRecibidos++;
					}//for

					// Si el tlg ha llegado con la marca "...", pide otro telegrama con el siguiente cacho
					if(!telegramRecibido.isListaNombresTipoCompleta()){
						conexion.pedirListaNombresDeTipo(tipo);
						conexion.cambiarEstado(CosmeStates.RECEIVING_TYPE_NAME_LIST);
					}else{
						conexion.cambiarEstado(CosmeStates.RECEIVED_TYPE_NAME_LIST);
						conexion.setNumUltimoTelegramaRecibido(telegramRecibido.getNumPeticion());
					}
				}

				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" LISTA_NOMBRES_CESTA ">
			case LISTA_NOMBRES_CESTA:
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" LISTA_NOMBRES_CONFIGURABLES ">
			case LISTA_NOMBRES_CONFIGURABLES:
				for(ItemVariable v : telegramRecibido.getListaVariables().getList()){
					conexion.anadirNombreConfigurable(v.getName());
				}
				emcosListener.onStateChange(CosmeStates.RECEIVED_NAME_LIST_CONFIGURABLES);
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" LISTA_NOMBRES_CONFIGURABLES_RESERVADOS ">
			case LISTA_NOMBRES_CONFIGURABLES_RESERVADOS:
				for(ItemVariable v : telegramRecibido.getListaVariables().getList()){
					conexion.anadirNombreConfigurableReservado(v.getName());
				}
				emcosListener.onStateChange(CosmeStates.RECEIVED_NAME_LIST_CONFIGURABLES_RESERVED);
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" CREAR_MUESTREADOR ">
			case CREAR_MUESTREADOR:
				if(!telegramRecibido.contieneErrores()){
					nombreMuestreador = telegramRecibido.getNombreMuestreador();
					m = conexion.getMuestreador(nombreMuestreador);
					if(m != null){
						m.setCreado(true);
						emcosListener.onStateChange(CosmeStates.MUESTREADOR_CREADO_OK);
					}
				}
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" MUESTREANDO ">
			case MUESTREANDO:
				nombreMuestreador = telegramRecibido.getNombreMuestreador();
				numMuestras = telegramRecibido.getNumMuestrasMuestreador();
				m = conexion.getMuestreador(nombreMuestreador);
				if(m != null){
					m.reset();
					m.setNumTotalMuestras(numMuestras);
					emcosListener.onStateChange(CosmeStates.RECIBIDO_TELEGRAMA_MUESTREANDO);
				}
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" GET_DATOS_MUESTREADOR ">
			case GET_DATOS_MUESTREADOR:
				nombreMuestreador = telegramRecibido.getNombreMuestreador();
				int index = telegramRecibido.getIndexChunk();
				ArrayList<Double> valoresMuestreador = telegramRecibido.getValoresMuestreador();
				m = conexion.getMuestreador(nombreMuestreador);
				if(m != null){
					numMuestras = valoresMuestreador.size();
					for(int i = 0; i < numMuestras; i++){
						m.setValor(index + i, valoresMuestreador.get(i));
					}
					emcosListener.onStateChange(CosmeStates.RECIBIDO_DATOS_MUESTREADOR);

				}
				break;


			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" HABILITAR_MUESTREADOR ">
			case HABILITAR_MUESTREADOR:
				break;
			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" DESHABILITAR_MUESTREADOR ">
			case DESHABILITAR_MUESTREADOR:
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" ELIMINAR_MUESTREADOR ">
			case ELIMINAR_MUESTREADOR:
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" SOLICITAR_FICHERO_TEXTO ">
			case SOLICITAR_FICHERO_TEXTO:

				emcosListener.onStateChange(CosmeStates.RECEIVED_TELEGRAM_TEXT_FILE_REQUEST);
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" NOTIFICAR_EVENTO ">
			case NOTIFICAR_EVENTO:
				emcosListener.onStateChange(telegramRecibido.getEvento());
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" ECHO ">
			case ECHO:
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" PERIODO_CESTA ">
			case PERIODO_CESTA:
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" PROJECT_STOP ">
			case PROJECT_STOP:
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" MAINTENANCE_INFO ">
			case MAINTENANCE_INFO:
				emcosListener.onStateChange(CosmeStates.MAINTENANCE_INFO);
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" SET CONNECTION ">
			case SET_CONNECTION:
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" DEFAULT ">
			default:
				// peticion desconocida
				emcosListener.onStateChange(CosmeStates.RECEIVED_UNKNOWN_TELEGRAM);
				System.out.println(">>>>> " + telegramRecibido.toString() + telegramRecibido.getIdTelegrama());
				break;
			// </editor-fold>

		} // switch
	}// processTelegram
}