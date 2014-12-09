package com.arcadio;

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
	private Telegrama telegramaRecibido;
	private boolean entradaNula = false;
	private boolean estoyEnMarcha = true;
	private ConexionEmcos conexion;
	private ControlTelegramasPerdidos ctp = null;
	private long t1, t2, t;
	private Semaphore pendingTlg;

	/** Creates a new instance of OyenteTelegrama */
	public OyenteTelegrama(CosmeListener _emcosListener, Semaphore _pendingTlg, ConexionEmcos _conexion){
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
					telegramaRecibido = new Telegrama(cadena);
				}catch(Exception e){
					telegramaRecibido = null;
				}

				if(telegramaRecibido == null){
					continue;
				}

				ctp = conexion.getCTPManager();
				if(ctp != null){
					ctp.registrarTelegramaRecibido(telegramaRecibido.getNumPeticion());
				}

				if(telegramaRecibido.contieneErrores()){
					int i = 1;
					for(String msg : telegramaRecibido.getMensajesError()){
						emcosListener.onError("ERROR (" + i + "/" + telegramaRecibido.getMensajesError().size() + "): " + msg);
						i++;
					}

					continue;
				}

				if(telegramaRecibido.isValido()){
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

		if(telegramaRecibido == null){
			return;
		}

		// permitirá a ConexionEmcos gestionar los telegramas con bloqueo
		// el telegrama lista_nombres_tipo es un poco especial!!
		if(telegramaRecibido.getIdTelegrama() != TelegramTypes.LISTA_NOMBRES_TIPO && telegramaRecibido.getIdTelegrama() != TelegramTypes.LISTA_NOMBRES_TIPO_HABILITADOS){
			if(telegramaRecibido.getNumPeticion() > conexion.getNumUltimoTelegramaRecibido()){
				conexion.setNumUltimoTelegramaRecibido(telegramaRecibido.getNumPeticion());
			}
		}
		switch(telegramaRecibido.getIdTelegrama()){
			// <editor-fold defaultstate="collapsed" desc=" LEER ">
			case LEER:
				lv = conexion.getNombres();
				for(ItemVariable iv : telegramaRecibido.getListaVariables().getList()){
					ItemVariable var = lv.getVariable(telegramaRecibido.getNombreInstancia());
					if(var != null){
						if(var instanceof NumericVariable){
							((NumericVariable) var).setValor(((NumericVariable) iv).getValue());
						}else if(var instanceof TextVariable){
							((TextVariable) var).setValue(((TextVariable) iv).getValue());
						}
					}
				}

				emcosListener.onDataReceived(telegramaRecibido.getNombreCesta(), telegramaRecibido.getListaVariables());
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
				for(ItemVariable iv : telegramaRecibido.getListaVariables().getList()){
					ItemVariable var = lv.getVariable(telegramaRecibido.getNombreInstancia());
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
				for(ItemVariable iv : telegramaRecibido.getListaVariables().getList()){
					ItemVariable var = lv.getVariable(iv.getName());
					if(var != null){
						if(var instanceof NumericVariable){
							((NumericVariable) var).setValor(((NumericVariable) iv).getValue());
						}else if(var instanceof TextVariable){
							((TextVariable) var).setValue(((TextVariable) iv).getValue());
						}
					}else{
						// si no existe la añadimos
						lv.add(iv);
					}
				}
				if(!telegramaRecibido.esEco()){
					String prefijoCesta = telegramaRecibido.getNombreCesta();
					int index = telegramaRecibido.getNombreCesta().indexOf(Bag.BASKET_NAME_SEPARATOR);

					if(index != -1){
						prefijoCesta = prefijoCesta.substring(0, index);
					}
					emcosListener.onDataReceived(prefijoCesta, telegramaRecibido.getListaVariables());
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
				conexion.setTipoNombre(telegramaRecibido.getNombreClase());
				emcosListener.onStateChange(CosmeStates.RECEIVED_NAME_TYPE);
				break;
			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" LEER_BLOQUEO ">
			case LEER_BLOQUEO:
				lv = conexion.getNombres();
				for(ItemVariable iv : telegramaRecibido.getListaVariables().getList()){
					ItemVariable var = lv.getVariable(telegramaRecibido.getNombreInstancia());
					if(var != null){
						if(var instanceof NumericVariable){
							((NumericVariable) var).setValor(((NumericVariable) iv).getValue());
						}else if(var instanceof TextVariable){
							((TextVariable) var).setValue(((TextVariable) iv).getValue());
						}
					}
				}

				gas = GestorAccesoSincronizado.getInstance();
				gas.desbloquear(telegramaRecibido.getListaVariables());

				break;
			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" ESCRIBIR_BLOQUEO ">
			case ESCRIBIR_BLOQUEO:

				break;
			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" LISTA_NOMBRES ">
			case LISTA_NOMBRES:
				for(ItemVariable v : telegramaRecibido.getListaVariables().getList()){
					conexion.anadirNombre(v.getName());
				}
				//System.out.println ("En el TLG hay: "+telegramaRecibido.getListaVariables().getLista().size()+"\t y en el saco: "+conexion.getListaNombres().getNumNombres());

				if(!telegramaRecibido.isListaNombresCompleta()){
					conexion.cambiarEstado(CosmeStates.RECEIVING_NAMES_LIST);
					conexion.pedirListaNombres(); //
				}else{
					conexion.cambiarEstado(CosmeStates.NAMES_LIST_RECEIVED);
				}
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" LISTA_NOMBRES_TIPO_HABILITADOS ">
			case LISTA_NOMBRES_TIPO_HABILITADOS:
				tipo = telegramaRecibido.getTipoVariable();
				for(ItemVariable v : telegramaRecibido.getListaVariablesDeTipo().getList()){
					conexion.addNombreDeTipo(v.getName());
				}

				if(!telegramaRecibido.isListaNombresCompleta()){
					conexion.cambiarEstado(CosmeStates.RECEIVING_NAMES_LIST);
					conexion.pedirListaNombresTipoHabilitados(tipo);
				}else{
					conexion.cambiarEstado(CosmeStates.NAMES_LIST_RECEIVED);
					conexion.setNumUltimoTelegramaRecibido(telegramaRecibido.getNumPeticion());
				}
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" LISTA_NOMBRES_NIVEL ">
			case LISTA_NOMBRES_NIVEL:
				String prefijo = telegramaRecibido.getPrefijo();

				if(prefijo != null){
					for(ItemVariable v : telegramaRecibido.getListaVariables().getList()){
						conexion.anadirNombre(v.getName());
					}
				}
				//System.out.println ("En el TLG hay: "+telegramaRecibido.getListaVariables().getLista().size()+"\t y en el saco: "+conexion.getListaNombres().getNumNombres());

				if(!telegramaRecibido.isListaNombresCompleta()){
					conexion.cambiarEstado(CosmeStates.RECEIVING_NAMES_LIST);
					conexion.pedirListaNombresNivel(prefijo); //
				}else{
					conexion.cambiarEstado(CosmeStates.NAMES_LIST_RECEIVED);
				}
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" LISTA_TIPOS ">
			case LISTA_TIPOS:
				for(ItemVariable v : telegramaRecibido.getListaVariables().getList()){
					conexion.anadirTipo(v.getName());
				}

				if(!telegramaRecibido.isListaTiposCompleta()){
					conexion.cambiarEstado(CosmeStates.RECEIVING_TYPE_LIST);
					conexion.pedirListaNombres(); //
				}else{
					conexion.cambiarEstado(CosmeStates.RECEIVED_TYPE_LIST);
				}

				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" LISTA_NOMBRES_TIPO ">
			case LISTA_NOMBRES_TIPO:
				tipo = telegramaRecibido.getTipoVariable();
				NamesList ln = conexion.getNombresExistentes();

				for(ItemVariable iv : telegramaRecibido.getListaVariablesDeTipo().getList()){
					conexion.addNombreDeTipo(iv.getName());
				}


				// CUANDO EL TLG TRAE LA LISTA DE CLASES
				if(telegramaRecibido.getTipoVariable().equals(IDENTIFICADOR_DE_CLASE)){ //"SISTEMA.CLASE"
					for(ItemVariable v : telegramaRecibido.getListaVariablesDeTipo().getList()){
						//  conexion.anadirTipo(v.getName());
						ln.setType(v.getName(), tipo);
					}// for

					// Si no ha llegado la marca de fin, pide otro telegrama con el siguiente cacho
					if(!telegramaRecibido.isListaNombresTipoCompleta()){
						conexion.cambiarEstado(CosmeStates.RECEIVING_CLASS_LIST);
						conexion.pedirListaNombresDeTipo(tipo);
					}else{
						conexion.cambiarEstado(CosmeStates.RECEIVED_CLASS_LIST);
						conexion.setNumUltimoTelegramaRecibido(telegramaRecibido.getNumPeticion());
					}

					// CUANDO EL TLG TRAE NOMBRES DE UN TIPO CUALQUIERA, distinto de "SISTEMA.CLASE"
				}else{
					for(ItemVariable v : telegramaRecibido.getListaVariablesDeTipo().getList()){
						ln.setType(v.getName(), tipo);
						//           numeroTelegramasDeTipoRecibidos++;
					}//for

					// Si el tlg ha llegado con la marca "...", pide otro telegrama con el siguiente cacho
					if(!telegramaRecibido.isListaNombresTipoCompleta()){
						conexion.pedirListaNombresDeTipo(tipo);
						conexion.cambiarEstado(CosmeStates.RECEIVING_TYPE_NAME_LIST);
					}else{
						conexion.cambiarEstado(CosmeStates.RECEIVED_TYPE_NAME_LIST);
						conexion.setNumUltimoTelegramaRecibido(telegramaRecibido.getNumPeticion());
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
				for(ItemVariable v : telegramaRecibido.getListaVariables().getList()){
					conexion.anadirNombreConfigurable(v.getName());
				}
				emcosListener.onStateChange(CosmeStates.RECEIVED_NAME_LIST_CONFIGURABLES);
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" LISTA_NOMBRES_CONFIGURABLES_RESERVADOS ">
			case LISTA_NOMBRES_CONFIGURABLES_RESERVADOS:
				for(ItemVariable v : telegramaRecibido.getListaVariables().getList()){
					conexion.anadirNombreConfigurableReservado(v.getName());
				}
				emcosListener.onStateChange(CosmeStates.RECEIVED_NAME_LIST_CONFIGURABLES_RESERVED);
				break;

			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc=" CREAR_MUESTREADOR ">
			case CREAR_MUESTREADOR:
				if(!telegramaRecibido.contieneErrores()){
					nombreMuestreador = telegramaRecibido.getNombreMuestreador();
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
				nombreMuestreador = telegramaRecibido.getNombreMuestreador();
				numMuestras = telegramaRecibido.getNumMuestrasMuestreador();
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
				nombreMuestreador = telegramaRecibido.getNombreMuestreador();
				int index = telegramaRecibido.getIndexChunk();
				ArrayList<Double> valoresMuestreador = telegramaRecibido.getValoresMuestreador();
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
				emcosListener.onStateChange(telegramaRecibido.getEvento());
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
				System.out.println(">>>>> " + telegramaRecibido.toString() + telegramaRecibido.getIdTelegrama());
				break;
			// </editor-fold>

		} // switch
	}// processTelegram
}