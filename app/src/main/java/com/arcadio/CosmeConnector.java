package com.arcadio;

import com.arcadio.api.v1.service.CosmeStates;
import com.arcadio.common.AccessLevels;
import com.arcadio.common.Bag;
import com.arcadio.common.ItemVariable;
import com.arcadio.common.NamesList;
import com.arcadio.common.TypeList;
import com.arcadio.common.VariablesList;
import com.arcadio.ctp.ControlTelegramasPerdidos;
import com.arcadio.exceptions.CosmeException;
import com.arcadio.exceptions.CosmeTimeoutException;
import com.arcadio.muestreador.Muestreador;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CosmeConnector{
	// <editor-fold defaultstate="collapsed" desc=" Vars ">

	//    private static final Logger logger = Logger.getLogger(ConexionEmcos.class);
	private String idVersion = "0.33";
	private String infoVersion = "Emcos-Arcadio. Version " + idVersion + " (19-03-2013 12:47)";
	// Convenio que seguimos:
	//      Conexión:       a nivel de socket
	//      Comunicación:   debe haber una conexión válida, y además se ha conseguido llevar a cabo
	//                      el protocolo de inicialización (recepción de ID_CLIENTE e ID_MAQUINA)
	private CosmeListener emcosListener;
	private String idCliente;
	private String idSistema;
	private CosmeStates estado = CosmeStates.DISCONNECTED;   // esta variable contiene el estado en que se encuentra la instancia ConexiÃ³n
	//###    private boolean nombresCargados = false;  // se pone a true cuando se ha rellenado nombresExistentes
	//###    private boolean tiposCargados   = false;  // se pone a true cuando se ha rellenado tiposExistentes
	private PrintWriter out;
	private BufferedReader in;

	private Socket socket;
	//private boolean sslActivado = true;
	private OyenteTelegrama oyente;
	private String host;
	private int puerto;
	private boolean hayQueUsarCelestino = false;
	private String contrasenaPasarela; // contraseña que hay que enviarle a la PasarelaAlta al conectar
	private String contrasenaAplicacion;
	private boolean reconexionActivada = true; // intentará reconectar ad nauseam
	private boolean estoyConectado = false;  // indica si estamos conectados a una pasarela (hay un socket activo)
	private int msRetardoTelegramas = 0;  // ms que debe esperarse tras el envío de un telegrama,
	// antes de enviar otro... Util para máquinas con MS-DOS
	private VariablesList nombres = new VariablesList();  // contiene los nombres que aparezcan en las cestas.
	private Map<String, Bag> cestas = new ConcurrentHashMap();
	private Watchdog watchdog;  // thread que hace una lectura puntual cada 2 segundos
	int contadorIntentos = 0;
	private SintaxisTelegrama sintaxisTelegrama;
	private Mensajero mensajero;
	private TypeList tiposExistentes = new TypeList();
	//   private TypeList clasesExistentes = new TypeList();
	private NamesList nombresExistentes = new NamesList();
	private NamesList nombresConfigurables = new NamesList();
	private NamesList nombresConfigurablesReservados = new NamesList();
	private Collection<String> nombresDeTipo = new ArrayList();
	private ControlTelegramasPerdidos ctp = null; // Control de Telegramas Perdidos  (el recuerda-paquetes, vaya)
	private Map<String, Muestreador> muestreadores = new ConcurrentHashMap();
	private boolean debug = false;
	private int MAX_LENGTH_TELEGRAMA = 1024;  // máximo número de bytes que podrán tener los telegramas que enviemos,
	// el valor lo obtenemos leyendo en el arranque: "SISTEMA.long_buffer_fifo_ent"
	private long msTlgProcessing = 0;  // contiene el número de MS usados para procesar el último telegrama recibido.
	private int numUltimoTelegramaEnviado = -1;  // contiene el "num_peticion" del último telegram que se haya enviado.
	// Se utiliza para gestionar los telegramas con bloqueo
	private int numUltimoTelegramaRecibido = -2;  // contiene el "num_peticion" del último telegrama que haya recibido OyenteTelegrama.
	// Se utiliza para gestionar los telegramas con bloqueo.
	private long pingTX;  // contiene el instante en que se envió el útimo telegrama ping
	private long pingTime = -1;  // contiene la latencia entre que se envía el tlg de ping y se recibe el eco
	private int reconnectionPeriod = 1000; // cada cuántos ms intenta establecer una conexión
	private int socketTimeout = 10000; // cuántos ms debe estar el inputStream sin recibir nada para que lance la excepción SocketTimeoutException
	protected String tipoNombre;


	/**
	 * SIN criptografia y SIN Celestino
	 *
	 * @param _emcosListener
	 * @param _contrasenaPasarela
	 * @param _host
	 * @param _puerto
	 * @throws CosmeException
	 */
	public CosmeConnector(CosmeListener _emcosListener, String _contrasenaPasarela, String _host, int _puerto) throws CosmeException{

		//sslActivado = false;
		this.emcosListener = _emcosListener;
		this.setMsRetardoTelegramas(0);


		host = _host;
		puerto = _puerto;
		contrasenaPasarela = _contrasenaPasarela;
		this.conectar(host, puerto, contrasenaPasarela);
	}

	/**
	 * SIN criptografia y SIN Celestino passing debug
	 *
	 * @param _emcosListener
	 * @param _contrasenaPasarela
	 * @param _host
	 * @param _puerto
	 * @param deb
	 * @throws CosmeException
	 */
	public CosmeConnector(CosmeListener _emcosListener, String _contrasenaPasarela, String _host, int _puerto, boolean deb) throws CosmeException{
		//cambiar
		this.debug = false;

		//sslActivado = false;
		this.emcosListener = _emcosListener;
		this.setMsRetardoTelegramas(0);

		host = _host;
		puerto = _puerto;
		contrasenaPasarela = _contrasenaPasarela;
		this.conectar(host, puerto, contrasenaPasarela);
	}

	public CosmeConnector(boolean _reconectar, CosmeListener _emcosListener, String _contrasenaPasarela, String _host, int _puerto){

		this.reconexionActivada = _reconectar;
		this.host = _host;
		this.puerto = _puerto;
		this.contrasenaPasarela = _contrasenaPasarela;

		//sslActivado = false;
		this.emcosListener = _emcosListener;
		this.setMsRetardoTelegramas(0);

	}

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc=" Conectar ">

	/**
	 * DEPRECATED
	 * Se mantiene por compatibilidad. Equivale a "conectar(true);"
	 */
	public void conectar(){
		conectar(true);
	}

	public String getHost(){
		return host;
	}
	/**
	 * >>>>>>>>>>>>>>>>>>>>>>>>>> PRUEBAS RECONEXION
	 * Este método debe ejecutarse una sola vez. Se supone que si se pierde
	 * la conexión, la recuperará por su cuenta.
	 */
	public void conectar(boolean _reconectar){
		final String hostTargetFinal = this.host;
		final int puertoTargetFinal = this.puerto;
		final String contrasenaPasarelaFinal = this.contrasenaPasarela;
		final int periodoReconexionFinal = this.reconnectionPeriod;

		// Muestra por consola la cadena de identificación de Arcadio
		System.out.println(infoVersion);

		this.reconexionActivada = _reconectar;

		if(!this.estoyConectado){

			boolean noEstoyConectado = true;

			while(noEstoyConectado && isReconexionActivada()){
				try{
					conectar(hostTargetFinal, puertoTargetFinal, contrasenaPasarelaFinal);

					System.out.println("Conectado!");

					noEstoyConectado = false;
				}catch(CosmeException ex){
					System.out.println(ex.getMessage());
					//logger.error(ex.getMessage());

					noEstoyConectado = true;

					try{
						Thread.sleep(periodoReconexionFinal);
					}catch(InterruptedException ex1){
					}
				}
			}//while


		} // if estoyConectado

	}// conectar()

	protected void restaurarCestas(){
		String txtTlg;

		for(Bag c : cestas.values()){
			// actualizo su nombre aleatorio
			c.updateRandomName();

			txtTlg = sintaxisTelegrama.getTelegrama_crearCesta(c.getRealBasketName(), c.getNameList(), c.getEventTime(), c.getInhibitTime());
			enviarTelegrama(txtTlg);

		}// for
	}

	protected void restaurarMuestreadores(){
		String txtTlg;
		List<String> muestreadoresHabilitados = new ArrayList<String>();
		List<String> muestreadoresDeshabilitados = new ArrayList<String>();

		// Recreación de los muestreadores existentes
		for(Muestreador m : muestreadores.values()){
			if(m.isModeSingle()){
				// muestreador single
				txtTlg = sintaxisTelegrama.getTlg__crear_muestreador(m.getNombre(), m.getVariableMuestreada(), "single", m.getNumTotalMuestras(), m.getVariableDisparo());
				enviarTelegrama(txtTlg);
				//   crearMuestreadorSingle (m.getVariableMuestreada(), m.getVariableDisparo(), m.getNumTotalMuestras(),m.getTiempoMuestreo());)
			}else{
				// muestreador continuo
				txtTlg = sintaxisTelegrama.getTlg__crear_muestreador(m.getNombre(), m.getVariableMuestreada(), "continuous", m.getNumTotalMuestras(), m.getVariableDisparo());
				enviarTelegrama(txtTlg);
			}

			try{
				waitUltimoTelegrama(30000);

				if(m.isHabilitado()){
					muestreadoresHabilitados.add(m.getNombre());
					//txtTlg = sintaxisTelegrama.getTlg__habilitar_muestreador(m.getNombre());
					//enviarTelegrama(txtTlg);
				}else{
					muestreadoresDeshabilitados.add(m.getNombre());
					//txtTlg = sintaxisTelegrama.getTlg__deshabilitar_muestreador(m.getNombre());
					//enviarTelegrama(txtTlg);
				}
			}catch(CosmeTimeoutException ex){
			}
		} // for muestreadores

		if(!muestreadoresHabilitados.isEmpty()){
			txtTlg = sintaxisTelegrama.getTlg__habilitar_muestreador(muestreadoresHabilitados);
			enviarTelegrama(txtTlg);
		}

		if(!muestreadoresDeshabilitados.isEmpty()){
			txtTlg = sintaxisTelegrama.getTlg__deshabilitar_muestreador(muestreadoresDeshabilitados);
			enviarTelegrama(txtTlg);
		}
	}

	/**
	 * Allows to start the communication with the specified host
	 *
	 * @param _host               IP address of the host we are going to connect to
	 * @param _puerto             Port number where the host is waiting for connections
	 * @param _contrasenaPasarela Contraseï¿œa que debe enviarse a la pasarela.
	 */
	private void conectar(String _host, int _puerto, String _contrasenaPasarela) throws CosmeException{
		String errorMsg = null;

		cambiarEstado(CosmeStates.DISCONNECTED);

		cambiarEstado(CosmeStates.TRYING_COMMUNICATION);

		try{
			socket = new Socket(_host, _puerto);
			socket.setSoTimeout(getSocketTimeout());

			setCanalDeSalida(new PrintWriter(socket.getOutputStream()));
			setCanalEntrada(new BufferedReader(new InputStreamReader(socket.getInputStream())));

			// hay socket activo, ahora buscamos la comunicación...
			PrintWriter outChannel = getCanalDeSalida();
			BufferedReader inChannel = getCanalEntrada();


			// ENVIAMOS LA CONTRASEÑA DE LA PASARELA a la que nos queremos conectar
			outChannel.println(_contrasenaPasarela);
			outChannel.flush();

			// ahora vamos a intentar la COMUNICACIÓN
			try{
				cambiarEstado(CosmeStates.TRYING_TO_ESTABLISH_COMMUNICATION);

				// Esperamos que nos envíe la pareja: "ID_MAQ ID_APP"
				String cadena = inChannel.readLine();
				printIfDebug("===> 5. Pareja ID_MAQ ID_APP recibida: " + cadena);

				StringTokenizer st = new StringTokenizer(cadena);
				if(st.countTokens() >= 2){
					this.idCliente = st.nextToken();
					this.idSistema = st.nextToken();

					printIfDebug("IU: " + this.getIDCliente() + "\tMAQ: " + this.getIDSistema() + "\n");

					//##################################################
					// Reseteo de valores ante reconexión --> OBLIGATORIA
					setNumUltimoTelegramaEnviado(-1);
					setNumUltimoTelegramaRecibido(-2);
					//##################################################

					pendingTlg = new Semaphore(0);
					oyente = new OyenteTelegrama(emcosListener, pendingTlg, this);
					sintaxisTelegrama = new SintaxisTelegrama(this);
					mensajero = new Mensajero(this);

					// Voy a obtener la longitud máxima que puede tener
					// un telegrama
					// Si no consigo leer esta variable no arrancamos,
					// así que más vale que funcioneeeeee
					printIfDebug("===> 6. Lectura de variable:  SISTEMA.long_buffer_fifo_ent");
					/**
					 * ************Modificado 03/12/2014*********
					 */
					VariablesList lv = new VariablesList();
					lv.add("SISTEMA.long_buffer_fifo_ent", 0);
					lv = leerBloqueo(lv, 5000);

					MAX_LENGTH_TELEGRAMA = (int) lv.getValue("SISTEMA.long_buffer_fifo_ent");
					printIfDebug("Long telegrama: " + this.MAX_LENGTH_TELEGRAMA);
					/**
					 * ******************************************
					 */
					// Todo parece en orden. Vamos pallá
					// lanzamos el wathdog
					if(watchdog == null){
						watchdog = new Watchdog(this);
					}
				}else{
					//setReconexionActivada(false); // si caemos aquí, no tiene sentido que reconectemos
					cambiarEstado(CosmeStates.COMMUNICATION_IMPOSSIBLE);

					errorMsg = "EXCEPCION: He recibido " + st.countTokens() + " tokens...";
				}
			}catch(CosmeTimeoutException ete){
				cambiarEstado(CosmeStates.COMMUNICATION_TIMEOUT);

				errorMsg = ete.toString() + "\nImposible leer la variable: 'SISTEMA.long_buffer_fifo_ent'.";
			}catch(Exception e){
				//this.setReconexionActivada(false); // si caemos aquí, no tiene sentido que reconectemos
				cambiarEstado(CosmeStates.COMMUNICATION_IMPOSSIBLE);

				errorMsg = "Conexión abortada. No pude finalizar el proceso de validación con '" + _host + ":" + _puerto + "': " + e.getMessage();
			}
		}catch(Exception e){
			this.cambiarEstado(CosmeStates.CONNEXION_IMPOSSIBLE);

			errorMsg = "Conexión imposible. No puedo conectarme con '" + _host + ":" + _puerto + "': " + e.getMessage();
		}finally{
			if(errorMsg != null){
				//estoyConectado = false;
				emcosListener.onError(errorMsg);

				throw new CosmeException(errorMsg);
			}else{
				// ESTOY CONECTADO!!!
				//estoyConectado = true;

				cambiarEstado(CosmeStates.COMMUNICATION_OK);

				// Recreación de las cestas existentes y los muestreadores
				restaurarCestas();
				restaurarMuestreadores();
			}
		}
	}

	/**
	 * Permite modificar el estado en que se encuentra arcadio.
	 * Se utiliza para conocer las vicisitudes de la conexión, y
	 * también para saber cuándo se han recibido algunos tipos de
	 * telegramas.
	 * Propaga este estado al emcosListener que nos esté utilizando.
	 *
	 * @param _nuevoEstado
	 */
	protected void cambiarEstado(CosmeStates _nuevoEstado){
		//modificado 06/12/2014

		// Propaga el nuevo estado al CosmeListener

		this.estado = _nuevoEstado;
		if(this.estado == CosmeStates.CONNECTION_INTERRUPTED || this.estado == CosmeStates.DISCONNECTED){
			this.estoyConectado = false;
		}else if(this.estado == CosmeStates.COMMUNICATION_OK){
			this.estoyConectado = true;
		}
		emcosListener.onStateChange(this.estado);

	}


	private void printIfDebug(String _text){
		if(debug){
			System.out.println(_text);
		}
	}

	/**
	 * Permite saber si existe el fichero que se pasa como parámetro.
	 *
	 * @param _nombreFicheroCertificado
	 * @return
	 */
	private boolean existeFicheroCertificado(String _nombreFicheroCertificado){
		boolean existe = false;

		File f = new File(_nombreFicheroCertificado);
		if(f.exists()){
			existe = true;
		}

		return existe;
	}

	/**
	 * Encola el telegrama que se proporciona en la cola de telegramas del mensajero.
	 * <p/>
	 * TODO: Es estúpido parsear todo el telegrama para terminar enviando el texto recibido
	 * sin mayores controles. Lo único que se utiliza es el "numPeticion", que podría
	 * obtenerse con un parseo mucho más ligero...
	 *
	 * @param _txtTlg String con el telegrama que deseamos enviar.
	 */
	private void enviarTelegrama(String _txtTlg){
		System.out.println(Thread.currentThread().getClass().getName());
		Telegram tlg = new Telegram(_txtTlg);

		if(tlg != null){
			if(ctp != null){
				ctp.registrarNuevoTelegrama(tlg.getNumPeticion(), tlg.getTxt_Telegrama());
			}

			setNumUltimoTelegramaEnviado(tlg.getNumPeticion());

			if(mensajero != null){
				mensajero.enviarTelegrama(tlg.getTxt_Telegrama());
			}else{
				System.err.println("-------------------------------------------------------");
				System.err.println("ERROR: NO se envió el mensaje porque no existe conexión");
				System.err.println("MSG: " + _txtTlg);
				System.err.println("-------------------------------------------------------");
			}

		}

	}

	/**
	 * Se invoca cuando se ha perdido la conexión. NO tenemos socket.
	 * Normalmente cuando se recibe el evento CONEXION_INTERRRUMPIDA.
	 */
	private void resetearConexion(){
		// 1. si se está usando el control de telegramas perdidos, lo liberamos de sus tareas
		// (el CTP algún día debería desaparecer)
		if(ctp != null){
			ctp.destruir();
			ctp = null;
		}


		// 2. si hay muestreadores, enviamos el TLG para que el host los elimine
		for(Muestreador m : this.muestreadores.values()){
			if(m != null){
				m.reset();
			}
		} // for muestreadores


		// 3. lo mismo con las cestas
  /*      for (Bag c: this.cestas.values()){
		if (c!= null){
        
        }
        }// for cestas
         */

		// 4. matamos el ping
		if(watchdog != null){
			watchdog.destroy();
			watchdog = null;
		}


		// 5. invitamos al oyenteTelegrama a que se muera,
		if(oyente != null){
			oyente.destroy();
			/*            try {
			//// nos esperamos hasta asegurarnos de que se ha muerto
            oyente.join();
            } catch (InterruptedException ex) {
            ex.printStackTrace();
            }
             */
			oyente = null;
		}


		// 6. invitamos al mensajero a que se muera, y nos esperamos hasta asegurarnos
		if(mensajero != null){
			mensajero.reset(); // si había algo en cola... ya no está
			// los tlgs que pensemos que DEBEN ejecutarse en el kernel
			// deben enviarse con bloqueo (waitUltimoTelegrama)
			mensajero.destroy();

			try{
				mensajero.join();
			}catch(InterruptedException ex){
				//logger.error(ex.getMessage());
			}

			mensajero = null;
		}

		// 7. cerramos el socket
		try{
			if(this.in != null){
				this.out.close();
				this.in.close();
				this.socket.close();
			}
		}catch(IOException ex){
		}

		//estoyConectado = false;

		// 8. notificamos al emcos listener de que ya estamos bonitamente desconectados
		cambiarEstado(CosmeStates.DISCONNECTED);

	}//resetarConexion

	protected Semaphore pendingTlg;

	/**
	 * Permite bloquear el thread desde el que se invoque hasta que se haya recibido el eco del telegrama cuyo número de petición
	 * sea el que se indica. Transcurridos _msTimeOut ms sin que se haya recibido el eco, se lanza una excepción.
	 *
	 * @param _queNumTlg Contiene el número de petición del telegrama cuya recepcion debemos esperar (bloqueándonos)
	 * @param _msTimeOut Milisegundos que estamos dispuestos a esperar. Tras ese tiempo se lanza la excepción CosmeTimeoutException
	 * @return Este método no devuelve nada, sólo se bloquea hasta que se reciba el telegrama indicado, o se sale por la excepción
	 */
	private void waitRXNumTelegrama(int _queNumTlg, long _msTimeOut) throws CosmeTimeoutException{
		// no se sale de aquí hasta que se haya recibido el telegrama con
		// los valores de las variables... o hayan transcurrido '_msTimeout' ms
		long t1 = System.currentTimeMillis();
		long t_timeout = t1 + _msTimeOut;  // en este instante caduca el bloqueo
		long time_to_timeout;
		long currentTime;

		boolean esperandoTlg = true;  // true mientras sigamos esperando, false cuando haya llegado

		while(esperandoTlg){
			if(this.getNumUltimoTelegramaRecibido() >= _queNumTlg){
				esperandoTlg = false;
			}else{
				currentTime = System.currentTimeMillis();

				if(currentTime >= t_timeout){
					esperandoTlg = false;
					throw new CosmeTimeoutException("Timeout superado en 'waitRXNumTelegrama' (" + _msTimeOut + " ms)");
				}else{
					try{
						//Thread.sleep(10);
						// la latencia de red (salvo en local) será mayor que estos 10 ms, así que
						// tampoco desperdiciamos tanto tiempo...

						time_to_timeout = t_timeout - currentTime;
						pendingTlg.tryAcquire(time_to_timeout, TimeUnit.MILLISECONDS);
					}catch(InterruptedException ex){
					}
				}//if-else
			}//if-else
		}//while
	}

	public void setReconnectionTime(int _period){
		this.reconnectionPeriod = _period;
	}

	/**
	 * Lo necesita el emcos connection manager
	 *
	 * @return
	 */
	public int getReconnectionTime(){
		return this.reconnectionPeriod;
	}

	private void setCanalDeSalida(PrintWriter _salida){
		this.out = _salida;

	}

	private void setCanalEntrada(BufferedReader _entrada){
		this.in = _entrada;
	}

	/**
	 * @param _numUltimoTlgTX
	 */
	private void setNumUltimoTelegramaEnviado(int _numUltimoTlgTX){
		this.numUltimoTelegramaEnviado = _numUltimoTlgTX;
	}

	protected CosmeListener getCosmeListener(){
		return this.emcosListener;
	}

	protected String getIDCliente(){
		return idCliente;
	}

	protected String getIDSistema(){
		return idSistema;
	}

	protected PrintWriter getCanalDeSalida(){
		return out;
	}

	protected BufferedReader getCanalEntrada(){
		return this.in;
	}

	protected ControlTelegramasPerdidos getCTPManager(){
		return this.ctp;
	}

	protected void pedirListaNombres(){

		if(sintaxisTelegrama != null){
			String txtTlg = sintaxisTelegrama.getTelegrama_pedirNombres();
			this.enviarTelegrama(txtTlg);
		}
		pedirListaTipos();
		solicitarListaNombresTipos("SISTEMA.VAR");
		getTipoNombre("ondacuadrada.seno");
		System.out.println("********"+getListaTipos().toString());
	}

	protected void pedirListaNombresTipoHabilitados(String tipo){
		String txtTlg = sintaxisTelegrama.getTelegrama_pedirNombresTipoHabilitados(tipo);
		this.enviarTelegrama(txtTlg);
	}

	protected void pedirListaNombresNivel(String _prefijo){
		String txtTlg = sintaxisTelegrama.getTlg__listaNombresNivel(_prefijo);
		this.enviarTelegrama(txtTlg);
	}

	private void pedirSubListaNombres(int _primerNombre){
		String txtTlg = sintaxisTelegrama.getTelegrama_pedirSublistaNombres(_primerNombre);

		this.enviarTelegrama(txtTlg);
	}

	protected void pedirListaTipos(){
		// No hay que resetear los tiposExistentes
		// porque debe de contener en estos instantes
		// las clases.
		String txtTlg = sintaxisTelegrama.getTelegrama_pedirTipos();

		this.enviarTelegrama(txtTlg);
	}

	private void pedirListaClases(){
		this.tiposExistentes.removeAll();
		String txtTlg = sintaxisTelegrama.getTelegrama_pedirClases();

		this.enviarTelegrama(txtTlg);
	}

	protected void pedirListaNombresDeTipo(String _queTipo){
		String txtTlg = sintaxisTelegrama.getTelegrama_pedirNombresDeTipo(_queTipo);

		this.enviarTelegrama(txtTlg);
	}

	/**
	 * Utilizado por OyenteTelegrama cuando recibe un telegrama "lista_nombres_tipo".
	 *
	 * @param _nombreDeTipo the nombresDeTipo to set
	 */
	protected void addNombreDeTipo(String _nombreDeTipo){
		this.nombresDeTipo.add(_nombreDeTipo);
	}

	/**
	 * Este método permite que cuando OyenteTelegrama recibe ciertos tipos de
	 * telegramas, añada los nombres que ha recibido directamente a esta
	 * estructura de ConexionEmcos (nombresExistentes).
	 *
	 * @param _nuevoNombre
	 */
	protected void anadirNombre(String _nuevoNombre){
		nombresExistentes.add(_nuevoNombre);
	}

	/**
	 * Este método permite que cuando OyenteTelegrama recibe ciertos tipos de
	 * telegramas, añada los tipos que ha recibido directamente a esta
	 * estructura de ConexionEmcos (tiposExistentes).
	 *
	 * @param _nuevoTipo
	 */
	protected void anadirTipo(String _nuevoTipo){
		tiposExistentes.addType(_nuevoTipo);
	}

	/**
	 * Este método permite que cuando OyenteTelegrama recibe el telegrama
	 * lista_nombres_configurables, añada los nombres que ha recibido directamente a esta
	 * estructura de ConexionEmcos (nombresConfigurables).
	 *
	 * @param _nuevoNombreConfigurable
	 */
	protected void anadirNombreConfigurable(String _nuevoNombreConfigurable){
		this.nombresConfigurables.add(_nuevoNombreConfigurable);
	}

	/**
	 * Este método permite que cuando OyenteTelegrama recibe el telegrama
	 * LISTA_NOMBRES_CONFIGURABLES_RESERVADOS, añada los nombres que ha recibido directamente a esta
	 * estructura de ConexionEmcos (nombresConfigurablesReservados).
	 *
	 * @param _nuevoNombreConfigurableReservado
	 */
	protected void anadirNombreConfigurableReservado(String _nuevoNombreConfigurableReservado){
		this.nombresConfigurablesReservados.add(_nuevoNombreConfigurableReservado);
	}

	/**
	 * Utilizado por OyenteTelegrama, que ha calculado el tiempo de procesado de
	 * cada telegrama recibido. Ese valor lo copia en esta variable para que arcadio
	 * pueda facilitarlo a sus clientes.
	 *
	 * @param msTlgProcessing the msTlgProcessing to set
	 */
	protected void setMsTlgProcessing(long msTlgProcessing){
		this.msTlgProcessing = msTlgProcessing;
	}

	/**
	 * Utilizado por OyenteTelegrama, que nos informa del número de petición del telegrama
	 * que ha recibido. Este valor se usará en "waitUltimoTelegrama()".
	 *
	 * @param _numUltimoTlgRX
	 */
	protected void setNumUltimoTelegramaRecibido(int _numUltimoTlgRX){
		this.numUltimoTelegramaRecibido = _numUltimoTlgRX;
	}

	protected VariablesList getNombres(){
		return this.nombres;
	}

	/**
	 * Invocado por OyenteTelegrama cuando reciba un telegrama PING. Actualiza
	 * el tiempo de latencia de el último telegrama ping.
	 */
	protected void setPingRXTime(){
		this.pingTime = System.nanoTime() - this.pingTX;
	}

	//
	//   A P I    P Ú B L I C A
	//
	//   Métodos a utilizar desde quienes implementen CosmeListener, según sus
	//   necesidades...
	//

	/**
	 * EXPLICITAMENTE indico que quiero terminar la conexión. No tiene sentido
	 * que haya reconexión.
	 */
	public void desconectar(){
		//logger.info("Desconexión");

		// 1. si se está usando el control de telegramas perdidos, lo liberamos de sus tareas
		// (el CTP algún día debería desaparecer)
		if(ctp != null){
			ctp.destruir();
			ctp = null;
		}


		// 2. si hay muestreadores, enviamos el TLG para que el host los elimine
		for(Muestreador m : this.muestreadores.values()){
			if(m != null){
				if(this.estoyConectado){
					String txtTlg = sintaxisTelegrama.getTlg__eliminarMuestreador(m.getNombre());
					this.enviarTelegrama(txtTlg);
				}
				m.reset();
			}
		} // for muestreadores


		// 3. lo mismo con las cestas
		for(Bag c : this.cestas.values()){
			if(c != null){
				if(this.estoyConectado){
					String nombreCesta = c.getRealBasketName();
					if(nombreCesta != null){
						String txtTlg = sintaxisTelegrama.getTelegrama_eliminarCesta(nombreCesta);
						this.enviarTelegrama(txtTlg);
					}
				}
			}
		}// for cestas


		// 4. matamos el ping
		if(watchdog != null){
			watchdog.destroy();
			watchdog = null;
		}


		// 5. invitamos al oyenteTelegrama a que se muera,
		if(oyente != null){
			oyente.destroy();
            /*            try {
            //// nos esperamos hasta asegurarnos de que se ha muerto
            oyente.join();
            } catch (InterruptedException ex) {
            ex.printStackTrace();
            }
             */
			oyente = null;
		}


		// 6. invitamos al mensajero a que se muera, y nos esperamos hasta asegurarnos
		if(mensajero != null){
			mensajero.reset(); // si había algo en cola... ya no está
			// los tlgs que pensemos que DEBEN ejecutarse en el kernel
			// deben enviarse con bloqueo (waitUltimoTelegrama)
			mensajero.destroy();
			try{
				mensajero.join();
			}catch(InterruptedException ex){
				//logger.error(ex.getMessage());
			}
			mensajero = null;
		}

		// 7. cerramos el socket
		try{
			if(this.in != null){
				this.out.close();
				this.in.close();
				this.socket.close();
			}
		}catch(IOException ex){
		}

		//this.estoyConectado = false;
		this.reconexionActivada = false;

		// 8. notificamos al emcos listener de que ya estamos bonitamente desconectados
		cambiarEstado(CosmeStates.DISCONNECTED);

	}

	/**
	 * Permite a un cliente obtener la lista de nombres existentes (públicos)
	 * en el sistema. Puede ser un proceso largo que necesite del intercambio
	 * de varios telegramas, por lo que se recomienda lanzarlo en una conexión
	 * dedicada sólo a este propósito y ejecutada en un thread independiente
	 * para no interferir con el resto de la aplicación.
	 */
	public void solicitarListaNombres(){
		this.nombresExistentes.removeAll(); // si se invoca por segunda vez, hay
		// que eliminar lo que hubiéramos recibido.
		this.pedirListaNombres();
	}

	/**
	 * Se envía un telegrama que solicita la lista de nombres que posean el prefijo
	 * que se indica.
	 * Cuando la lista de nombres se muestra en forma de árbol, permite obtener los
	 * nombres que deben de colgar de una rama dada (el prefijo).
	 * Cuando llegue el telegram de respuesta se disparará el evento RECIBIDA_LISTA_NOMBRES.
	 *
	 * @param _prefijo
	 */
	public void solicitarListaNombresNivel(String _prefijo){
		this.nombresExistentes.removeAll(); // si se invoca por segunda vez, hay
		// que eliminar lo que hubiéramos recibido.
		this.pedirListaNombresNivel(_prefijo);
	}

	/**
	 * Este método permite obtener la lista de nombres públicos definidiso en
	 * el sistema, pero debe invocarse después de:
	 * 1. haber invocado anteriormetne el método "solicitarNombres()"
	 * 2. haber llegado al estado CosmeStates.LISTA_RECIBIDA
	 * Esto es así porque la lista de nombres puede ser MUY larga, y necesitar
	 * un número indeterminado de telegramas para su recepción (y también un número
	 * indeterminado de segundos según las condiciones de la comunicación).
	 *
	 * @return Devuelve la lista de los nombres conocidos por el sistema. Si no se
	 * ha llamado previamente al método "solicitarNombres()" o no se ha esperado
	 * al evento CosmeStates.LISTA_RECIBIDA, la lista recibida podrá estar incompleta.
	 */
	public NamesList getListaNombres(){
		//   return nombresExistentes.getNameList();
		return nombresExistentes;
	}

	/**
	 * Devuelve la lista de nombres cuyo tipo coincida con el solicitado.
	 * IMPORTANTE: es un método síncrono. No sigue el modelo de programación habitual en
	 * ConexionEmcos (asíncrono).
	 *
	 * @param _tipo
	 * @param _msTimeout
	 * @return Lista de nombres cuyo tipo es el que se pasa como parámetro.
	 */
	public Collection<String> solicitarListaNombresDeTipoBloqueo(String _tipo, int _msTimeout){
		this.nombresDeTipo.clear();

		this.pedirListaNombresDeTipo(_tipo);

		try{
			this.waitUltimoTelegrama(_msTimeout);
		}catch(CosmeTimeoutException ex){
			//logger.error(ex.getMessage());
		}

		return this.nombresDeTipo;
	}

	public Collection<String> solicitarListaNombresTipoHabilitadosDeTipoBloqueo(String tipo, int _msTimeout){
		this.nombresDeTipo.clear();

		this.pedirListaNombresTipoHabilitados(tipo);

		try{
			this.waitUltimoTelegrama(_msTimeout);
		}catch(CosmeTimeoutException ex){
			//logger.error(ex.getMessage());
		}

		return this.nombresDeTipo;
	}

	/**
	 * @param _idPrimerNombre
	 */
	public void solicitarSubListaNombres(int _idPrimerNombre){
		if(_idPrimerNombre > 0){
			this.pedirSubListaNombres(_idPrimerNombre);
		}
	}

	/**
	 *
	 */
	public void solicitarListaTipos(){
		this.cambiarEstado(CosmeStates.RECEIVING_TYPE_LIST);
		this.pedirListaTipos();
	}

	/**
	 * @return
	 */
	public Collection<String> getListaTipos(){
		return tiposExistentes.getTypeList();
	}

	/**
	 *
	 */
	public void solicitarListaClases(){
		this.cambiarEstado(CosmeStates.RECEIVING_CLASS_LIST);
		this.pedirListaClases();
	}

	/**
	 *
	 */
	public void solicitarListaNombresConfigurables(){
		this.cambiarEstado(CosmeStates.ASKING_LIST);
		String txtTlg = sintaxisTelegrama.getTlg__listaNombresConfigurables();

		this.enviarTelegrama(txtTlg);
	}

	/**
	 * @return
	 */
	public NamesList getListaNombresConfigurables(){
		return this.nombresConfigurables;
	}

	/**
	 *
	 */
	public void solicitarListaNombresConfigurablesReservados(){
		this.cambiarEstado(CosmeStates.ASKING_LIST);
		String txtTlg = sintaxisTelegrama.getTlg__listaNombresConfigurablesReservados();

		this.enviarTelegrama(txtTlg);

	}

	/**
	 * @return
	 */
	public NamesList getListaNombresConfigurablesReservados(){
		return this.nombresConfigurablesReservados;
	}

	/**
	 * @param _nombreTipo
	 */
	public void solicitarListaNombresTipos(String _nombreTipo){
		this.cambiarEstado(CosmeStates.RECEIVING_TYPE_NAME_LIST);

		this.pedirListaNombresDeTipo(_nombreTipo);

	}

	/**
	 * @return
	 */
	public SortedMap<String, String> getListaNombresTipos(){
		return nombresExistentes.getNameTypeList();
	}

	/**
	 * @param _listaVariables
	 */
	public void lecturaPuntual(VariablesList _listaVariables){
		if(sintaxisTelegrama != null){
			String txtTlg = sintaxisTelegrama.getTelegrama_leer(_listaVariables);
			this.enviarTelegrama(txtTlg);
		}
	}

	/**
	 * Igualico que lecturaPuntual, pero manda el telegrama ping, y registra
	 * el instante en que se envió.
	 *
	 * @param _listaVariables
	 */
	public void ping(VariablesList _listaVariables){
		if(sintaxisTelegrama != null){
			String txtTlg = sintaxisTelegrama.getTelegrama_ping(_listaVariables);
			pingTX = System.nanoTime(); // guardo la hora que es al enviarlo
			this.enviarTelegrama(txtTlg);
		}
	}

	/**
	 * @param _listaVariables
	 * @param _msTimeout
	 * @return
	 * @throws CosmeTimeoutException
	 */
	public synchronized VariablesList leerBloqueo(VariablesList _listaVariables, int _msTimeout) throws CosmeTimeoutException{
		VariablesList lv = null;

		if(sintaxisTelegrama != null){
			long tA = System.nanoTime();

			GestorAccesoSincronizado gas = GestorAccesoSincronizado.getInstance();

			String txtTlg = sintaxisTelegrama.getTelegrama_leer_bloqueo(_listaVariables);

			gas.bloquear();

			this.enviarTelegrama(txtTlg);

			waitUltimoTelegrama(_msTimeout);

            /*
            // no se sale de aquí hasta que se haya recibido el telegrama con
            // los valores de las variables... o hayan transcurrido '_msTimeout' ms
            long t1 = System.currentTimeMillis();
            long t_timeout = t1 + _msTimeout;  // en este instante caduca el bloqueo
            
            while (gas.estaBloqueado())
            {
            if (System.currentTimeMillis() >= t_timeout)
            {
            throw new CosmeTimeoutException("Timeout superado en 'leer_bloqueo' ("+_msTimeout+" ms)");
            }
            else
            {
            try 
            {
            Thread.sleep(10);
            } 
            catch (InterruptedException ex) 
            {
            logger.error(ex.getMessage());
            }
            }
            }
             */

			lv = gas.getListaVariables();

			long tB = System.nanoTime();

			long tt = (tB - tA) / 1000000;

			//          logger.info(lv.getNombres() + " [time = " + tt + " ms]");
		}

		return lv;
	}

	/**
	 * @param _prefijoCesta No debe existir ninguna otra cesta con el mismo prefijo. Tampoco debe
	 *                      contener ningún carácter Cesta.BASKET_NAME_SEPARATOR. En cualquiera de estos dos casos lanza una CosmeException.
	 * @param event_time
	 * @throws CosmeException
	 */
	public void crearCesta(String _prefijoCesta, int event_time, int inhibit_time) throws CosmeException{
		this.crearCesta(_prefijoCesta, null, event_time, inhibit_time);
	}


	/**
	 * Mantiene compatibilidad con versiones anteriores de Pasarelas y arcadios, antes
	 * de que se introdujera el parámetro "inhibit_time"
	 */
	public void crearCesta(String _prefijoCesta, int event_time) throws CosmeException{
		this.crearCesta(_prefijoCesta, null, event_time, 0);
	}

	/**
	 * @param _prefijoCesta No debe existir ninguna otra cesta con el mismo prefijo. Tampoco debe
	 *                      contener ningún carácter Bag.BASKET_NAME_SEPARATOR . En cuaquiera de estos dos casos lanza una CosmeException.
	 * @param _nombres
	 * @param event_time
	 * @throws CosmeException
	 */
	public void crearCesta(String _prefijoCesta, Collection<String> _nombres, int event_time, int inhibit_time) throws CosmeException{
		// busco si ya existe una cesta con ese prefijo... que es malo
		if(cestas.containsKey(_prefijoCesta)){
			new CosmeException("ERROR: Cart name already exists.");
		}
		// miro si en el prefijo se usa el caracter Cesta.BASKET_NAME_SEPARATOR
		else if(_prefijoCesta.contains(Bag.BASKET_NAME_SEPARATOR)){
			new CosmeException("ERROR: Cart name must not contain '" + Bag.BASKET_NAME_SEPARATOR + "' character.");
		}else{
			Bag c = new Bag(_prefijoCesta, event_time, inhibit_time);
			cestas.put(_prefijoCesta, c);

			if(_nombres != null){
				c.setNameList(_nombres);

				for(String n : _nombres){
					if(!this.nombres.exist(n)){
						this.nombres.add(n); // añade este nombre a la hash general de nombres
					}
				}
			}

			if(this.estoyConectado){
				String txtTlg = sintaxisTelegrama.getTelegrama_crearCesta(c.getRealBasketName(), _nombres, event_time, inhibit_time);
				this.enviarTelegrama(txtTlg);
			}

		} // if-else-else
	}

	public Collection<String> getCestas(){
		return this.cestas.keySet();
	}

	/**
	 * Permite añadir un único nombre a la cesta indicada.
	 * Puede usarse "insertarNombres" si se desea insertar un conjunto de ellos.
	 *
	 * @param _prefijoCesta Nombre de la cesta a la que van a añadirse los nombres
	 * @param _nombre       Nombre que va a insertarse en la cesta
	 */
	public void insertarNombre(String _prefijoCesta, String _nombre){
		Bag c = cestas.get(_prefijoCesta);

		if(c != null){
			if(estoyConectado){
				String nombreCesta = c.getRealBasketName();
				String txtTlg = sintaxisTelegrama.getTelegrama_anadirNombreACesta(nombreCesta, _nombre);

				enviarTelegrama(txtTlg);
			}

			if(!c.existsName(_nombre)){
				c.addName(_nombre);
			}

			if(!nombres.exist(_nombre)){
				nombres.add(_nombre); // añade este nombre a las hash general de nombres
			}
		}
	}

	/**
	 * Permite añadir una lista de nombres a la cesta que se indica como primer parámetro.
	 *
	 * @param _prefijoCesta Nombre de la cesta a la que van a añadirse los nombres
	 * @param _nombres      Colección de nombres que van a insertarse en la cesta
	 */
	public void insertarNombres(String _prefijoCesta, Collection<String> _nombres){
		Bag c = cestas.get(_prefijoCesta);
		if(c != null){
			if(this.estoyConectado){
				String nombreCesta = cestas.get(_prefijoCesta).getRealBasketName();
				String txtTlg = sintaxisTelegrama.getTelegrama_anadirNombresACesta(nombreCesta, _nombres);
				this.enviarTelegrama(txtTlg);
			}

			for(String n : _nombres){
				c.addName(n);
				if(!this.nombres.exist(n)){
					this.nombres.add(n); // añade este nombre a las hash general de nombres
				}

			}
		}

	}

	/**
	 * Permite eliminar un nombre de la cesta que se indica como primer parámetro.
	 *
	 * @param _prefijoCesta Nombre de la cesta de la que va a eliminarse el nombre.
	 * @param _nombre       Nombre que va a eliminarse de la cesta
	 */
	public void eliminarNombre(String _prefijoCesta, String _nombre){
		Bag c = cestas.get(_prefijoCesta);
		if(c != null){
			if(this.estoyConectado){
				String nombreCesta = cestas.get(_prefijoCesta).getRealBasketName();
				String txtTlg = sintaxisTelegrama.getTelegrama_eliminarNombreDeCesta(nombreCesta, _nombre);
				this.enviarTelegrama(txtTlg);
			}

			c.deleteName(_nombre);
			//           this.nombres.eliminar(_nombre); // quita este nombre de la hash general de nombres
		}
	}

	/**
	 * Permite eliminar una lista de nombres de la cesta que se indica como primer parámetro.
	 *
	 * @param _prefijoCesta Nombre de la cesta a la que van a añadirse los nombres
	 * @param _nombres      Colección de nombres que van a eliminarse de la cesta
	 */
	public void eliminarNombres(String _prefijoCesta, Collection<String> _nombres){
		Bag c = cestas.get(_prefijoCesta);
		if(c != null){
			if(this.estoyConectado){
				String nombreCesta = cestas.get(_prefijoCesta).getRealBasketName();
				String txtTlg = sintaxisTelegrama.getTelegrama_eliminarNombresDeCesta(nombreCesta, _nombres);
				this.enviarTelegrama(txtTlg);
			}

			for(String n : _nombres){
				c.deleteName(n);
				//                this.nombres.eliminar(n);
			}
		}
	}

	/**
	 * Permite modificar el periodo de refresco de la cesta indicada.
	 *
	 * @param _prefijoCesta         Nombre de la cesta a la que va a modificarse su periodo de refresco.
	 * @param _nuevoPeriodoRefresco Nuevo periodo en ms.
	 */
	public void setPeriodoCesta(String _prefijoCesta, int _nuevoPeriodoRefresco){
		Bag c = cestas.get(_prefijoCesta);
		if(c != null){
			if(this.estoyConectado){
				String nombreCesta = cestas.get(_prefijoCesta).getRealBasketName();
				String txtTlg = sintaxisTelegrama.getTelegrama_periodoCesta(nombreCesta, _nuevoPeriodoRefresco);
				this.enviarTelegrama(txtTlg);
			}
			c.setEventTime(_nuevoPeriodoRefresco);
		}
	}

	public int getPeriodoCesta(String _prefijoCesta){
		Bag c = cestas.get(_prefijoCesta);
		if(c != null){
			return c.getEventTime();
		}

		return -1;
	}

	/**
	 * >>>> DEPRECATED. Ha sido sustituido por <b>eliminarCesta(_nc)</b>. Todos los otros métodos similares se llaman eliminarXXXX, y
	 * este no tiene por qué ser una excepción.
	 * Cualquier día de estos este método desaparecerá de la API.
	 *
	 * @param _prefijoCesta Nombre de la cesta que deseamos eliminar.
	 */
	public void borrarCesta(String _prefijoCesta){
		Bag c = cestas.get(_prefijoCesta);
		if(c != null){
			if(this.estoyConectado){
				String nombreCesta = cestas.get(_prefijoCesta).getRealBasketName();
				String txtTlg = sintaxisTelegrama.getTelegrama_eliminarCesta(nombreCesta);
				this.enviarTelegrama(txtTlg);
			}

			// hay que eliminar de las hash general de nombres todos los nombres que tuviera esta cesta
/*
            for (String n: c.getNameList()){
            this.nombres.eliminar(n);
            }*/
		}
		cestas.remove(_prefijoCesta);
	}

	/**
	 * Permite destruir una cesta existente.
	 *
	 * @param _prefijoCesta Nombre de la cesta que deseamos eliminar.
	 */
	public void eliminarCesta(String _prefijoCesta){
		Bag c = cestas.get(_prefijoCesta);
		if(c != null){
			if(this.estoyConectado){
				String nombreCesta = c.getRealBasketName();
				if(nombreCesta != null){
					String txtTlg = sintaxisTelegrama.getTelegrama_eliminarCesta(nombreCesta);
					this.enviarTelegrama(txtTlg);
				}
			}

			// hay que eliminar de las hash general de nombres todos los nombres que tuviera esta cesta
			// OJO: si un nombre está en varias cestas... entonces hacemos una escabechina
			// Es preferible NO quitar el nombre de la lista
            /*for (String n: c.getNameList()){
            this.nombres.eliminar(n);
            }*/
		}
		cestas.remove(_prefijoCesta);
	}

	public void setRetardoTelegramas(int _nuevoRetardoTelegramas){
		this.setMsRetardoTelegramas(_nuevoRetardoTelegramas);
	}

	public boolean existeNombre(String _prefijoCesta, String _nombre){
		return this.nombres.exist(_nombre);
	}

	/**
	 * Devuelve el ItemVariable asociado al nombre que se pasa como parametro.
	 * Contendra el último valor recibido en una cesta o en un telegrama leer, leer_bloqueo, ping.
	 *
	 * @param _nombre
	 * @return
	 */
	public ItemVariable getVariable(String _nombre){
		return this.nombres.getVariable(_nombre);
	}

	public List<ItemVariable> getVariables(List<String> _nombres){
		ItemVariable iv;
		List<ItemVariable> variables = new ArrayList<ItemVariable>();

		for(String nombre : _nombres){
			iv = this.nombres.getVariable(nombre);
			variables.add(iv);
		}

		return variables;
	}

	/**
	 * @param _nombreVariable
	 * @param _nuevoValor
	 */
	public void modificarVariable(String _nombreVariable, Double _nuevoValor){
		if(this.estoyConectado){
			String txtTlg = sintaxisTelegrama.getTelegrama_escribir(_nombreVariable, _nuevoValor);
			this.enviarTelegrama(txtTlg);
		}

	}

	/**
	 * @param _nombreVariable
	 * @param _nuevoValor
	 */
	public void modificarVariable(String _nombreVariable, String _nuevoValor){
		if(this.estoyConectado){
			String txtTlg = sintaxisTelegrama.getTelegrama_escribir(_nombreVariable, _nuevoValor);
			this.enviarTelegrama(txtTlg);
		}
	}

	/**
	 * @param _lv
	 */
	public void modificarVariable(VariablesList _lv){

		Collection<StringBuffer> telegramas = sintaxisTelegrama.getTelegrama_escribir(_lv);
		if(this.estoyConectado){
			for(StringBuffer txtTlg : telegramas){
				this.enviarTelegrama(txtTlg.toString());
			}
		}

	}

	/**
	 * >>>>############   ????
	 *
	 * @param _lv
	 * @param first
	 * @param dim
	 */
	public void modificarVariable(ArrayList<ItemVariable> _lv, int first, int dim){
		String txtTlg = sintaxisTelegrama.obtTelegramaEscribir(_lv, first, dim);

		this.enviarTelegrama(txtTlg);

	}

	/**
	 * >>>> ####COMPROBAR QUE FUNCIONAAAA. Ahora usa waitUltimoTelegrama
	 *
	 * @param _nombreVariable
	 * @param _nuevoValor
	 * @param _msTimeout
	 * @throws CosmeTimeoutException
	 */
	public synchronized void modificarVariableBloqueo(String _nombreVariable, Double _nuevoValor, int _msTimeout) throws CosmeTimeoutException{
		String txtTlg = sintaxisTelegrama.getTelegrama_escribir_bloqueo(_nombreVariable, _nuevoValor);
		this.enviarTelegrama(txtTlg);
		this.waitUltimoTelegrama(_msTimeout);
	}

	/**
	 * >>>>> ####COMPROBAR QUE FUNCIONAAAA. Ahora usa waitUltimoTelegrama
	 *
	 * @param _nombreVariable
	 * @param _nuevoValor
	 * @param _msTimeout
	 * @throws CosmeTimeoutException
	 */
	public synchronized void modificarVariableBloqueo(String _nombreVariable, String _nuevoValor, int _msTimeout) throws CosmeTimeoutException{

		String txtTlg = sintaxisTelegrama.getTelegrama_escribir_bloqueo(_nombreVariable, _nuevoValor);
		this.enviarTelegrama(txtTlg);
		this.waitUltimoTelegrama(_msTimeout);
	}

	/**
	 * >>>>> ####COMPROBAR QUE FUNCIONAAAA. Ahora usa waitUltimoTelegrama
	 *
	 * @param _lv
	 * @param _msTimeout
	 * @throws CosmeTimeoutException
	 */
	public synchronized void modificarVariableBloqueo(VariablesList _lv, int _msTimeout) throws CosmeTimeoutException{
		Collection<StringBuffer> telegramas = sintaxisTelegrama.getTelegrama_escribir_bloqueo(_lv);

		for(StringBuffer txtTlg : telegramas){
			this.enviarTelegrama(txtTlg.toString());
		}

		this.waitUltimoTelegrama(_msTimeout);
	}


    /* TODO ÁNGEL: Para qué??? 
    public void modificarVariableBloqueo(ArrayList<ItemVariable> _lv, int quantum,int elStruct,
    int _msTimeout,String downloaded)
    throws CosmeTimeoutException {
    
    int i=0;
    quantum=quantum*elStruct;
    modificarVariableBloqueo(downloaded,0.0,500);
    for (i = 0; i < (_lv.size() / quantum); i++) {
    modificarVariableBloqueo(_lv, i * quantum, quantum,_msTimeout);
    modificarVariableBloqueo(downloaded,(double)((i+1)*quantum/elStruct),500);
    }
    if ((_lv.size() % quantum) != 0) {
    modificarVariableBloqueo(_lv, i * quantum, (_lv.size() % quantum),_msTimeout);
    modificarVariableBloqueo(downloaded,(double)_lv.size()/elStruct,500);
    }
    }
    
    public void modificarVariableBloqueo(ArrayList<ItemVariable> _lv, int init,int quantum,
    int _msTimeout)
    throws CosmeTimeoutException {
    
    GestorAccesoSincronizado gas = GestorAccesoSincronizado.getInstance();
    gas.bloquear();
    
    modificarVariable(_lv, init, quantum);
    
    // no se sale de aquï¿œ hasta que se haya recibido el telegrama con
    // los valores de las variables... o hayan transcurrido 10"
    long t1 = System.currentTimeMillis();
    long t_timeout = t1 + _msTimeout;  // en este instante caduca el bloqueo
    
    //GestorAccesoSincronizado gas = GestorAccesoSincronizado.getInstance();
    //gas.bloquear();
    
    while (gas.estaBloqueado()) {
    if (t_timeout <= System.currentTimeMillis()) {
    throw new CosmeTimeoutException("Timeout superado en 'modificarVariable_sincronizado'");
    } else {
    try {
    //Thread.sleep(100);
    } catch (Exception e) {
    }
    }
    }
    }
    */

	/**
	 * Permite conocer el último evento de estado recibido.
	 *
	 * @return
	 */
	public CosmeStates getEstado(){
		return this.estado;
	}

	/**
	 * Permite crear un nuevo muestreador.
	 *
	 * @param _nombreVariableAMuestrear
	 * @param _nombreVariableDisparo
	 * @param _numMuestras
	 * @param _msMuestreo
	 * @return
	 */
	public String crearMuestreadorSingle(String _nombreVariableAMuestrear, String _nombreVariableDisparo, int _numMuestras, int _msMuestreo){

		Muestreador m = new Muestreador(emcosListener, this, _nombreVariableAMuestrear, _nombreVariableDisparo, _numMuestras, _msMuestreo);
		String nombreMuestreadorAleatorio;

		if(m != null){
			nombreMuestreadorAleatorio = m.getNombre();
			muestreadores.put(nombreMuestreadorAleatorio, m);

			if(this.estoyConectado){
				String txtTlg = sintaxisTelegrama.getTlg__crear_muestreador(nombreMuestreadorAleatorio, _nombreVariableAMuestrear, "single", _numMuestras, _nombreVariableDisparo);
				this.enviarTelegrama(txtTlg);
			}
			//    String v_muestrasATomar = m.getNombreVar_num_muestras_tomar();
			String v_tiempo = m.getNombreVar_tiempo();
			//     this.modificarVariable(v_muestrasATomar, (double)_numMuestras);
			this.modificarVariable(v_tiempo, (double) _msMuestreo);


		}else{
			nombreMuestreadorAleatorio = null;
		}

		return nombreMuestreadorAleatorio;
	}

	//     public String crearMuestreadorContinuous (String _nombreVariableAMuestrear, String _nombreVariableDisparo, int _numMuestras){
	public String crearMuestreadorContinuous(String _nombreVariableAMuestrear, int _numMuestras){

		//        Muestreador m = new Muestreador (emcosListener, _nombreVariableAMuestrear, _nombreVariableDisparo, _numMuestras);
		Muestreador m = new Muestreador(emcosListener, _nombreVariableAMuestrear, _numMuestras);
		String nombreMuestreador = null;

		if(m != null){
			nombreMuestreador = m.getNombre();
			muestreadores.put(nombreMuestreador, m);

			if(this.estoyConectado){
				String txtTlg = sintaxisTelegrama.getTlg__crear_muestreador_continuous(nombreMuestreador, _nombreVariableAMuestrear, _numMuestras);
				this.enviarTelegrama(txtTlg);
			}

		}else{
			nombreMuestreador = null;
		}

		return nombreMuestreador;
	}

	/**
	 * @param _nombreMuestreador
	 */
	public void eliminarMuestreador(String _nombreMuestreador){
		Muestreador m = this.getMuestreador(_nombreMuestreador);

		if(m != null){
			if(this.estoyConectado){
				String txtTlg = sintaxisTelegrama.getTlg__eliminarMuestreador(_nombreMuestreador);
				this.enviarTelegrama(txtTlg);
			}
			muestreadores.remove(_nombreMuestreador);
		}
	}

	/**
	 * @param _nombreMuestreador
	 */
	public void habilitarMuestreador(String _nombreMuestreador){
		List<String> _nombreMuestreadores = new ArrayList<String>();
		_nombreMuestreadores.add(_nombreMuestreador);

		habilitarMuestreadores(_nombreMuestreadores);
	}

	public void habilitarMuestreadores(List<String> _nombreMuestreadores){
		Muestreador m;

		for(String _nombreMuestreador : _nombreMuestreadores){
			m = muestreadores.get(_nombreMuestreador);

			if(m != null){
				m.setHabilitado(true);
			}
		}

		if(this.estoyConectado){
			String txtTlg = sintaxisTelegrama.getTlg__habilitar_muestreador(_nombreMuestreadores);
			this.enviarTelegrama(txtTlg);
		}
	}

	/**
	 * @param _nombreMuestreador
	 */
	public void deshabilitarMuestreador(String _nombreMuestreador){
		List<String> _nombreMuestreadores = new ArrayList<String>();
		_nombreMuestreadores.add(_nombreMuestreador);

		deshabilitarMuestreadores(_nombreMuestreadores);
	}

	public void deshabilitarMuestreadores(List<String> _nombreMuestreadores){
		Muestreador m;

		for(String _nombreMuestreador : _nombreMuestreadores){
			m = muestreadores.get(_nombreMuestreador);

			if(m != null){
				m.setHabilitado(false);
			}
		}

		if(this.estoyConectado){
			String txtTlg = sintaxisTelegrama.getTlg__deshabilitar_muestreador(_nombreMuestreadores);
			this.enviarTelegrama(txtTlg);
		}
	}

	/**
	 * @return
	 */
	//    public Map<String,Muestreador> getListaMuestreadores(){
	public Collection<String> getListaMuestreadores(){
		return muestreadores.keySet();
	}

	/**
	 * @param _nombre
	 * @return
	 */
	public Muestreador getMuestreador(String _nombre){
		return muestreadores.get(_nombre);
	}

	/**
	 * >> Arcadio Plus
	 * El parámetro "_pathFichero" contiene la ruta relativa al fichero solicitado,
	 * a partir del directorio del proyecto (por defecto: /usr/emcos/projects/xxx"
	 *
	 * @param _pathFichero
	 */
	public void solicitarFicheroTexto(String _nombreInstancia, String _pathFichero){
		String txtTlg = sintaxisTelegrama.getTlg__solicitarFicheroTexto(_nombreInstancia, _pathFichero);
		this.enviarTelegrama(txtTlg);
	}

	/**
	 * >>> Arcadio Plus
	 * El parámetro "_contenidoFicheroEMC" contiene el contenido del nuevo
	 * fichero ".emc" que ha modificado un gestor de configuración.
	 *
	 * @param _contenidoFicheroEMC
	 */
	public void enviarActualizacionFicheroEMC(String _contenidoFicheroEMC){
		String txtTlg = sintaxisTelegrama.getTlg__enviarFicheroEMC(_contenidoFicheroEMC);
		this.enviarTelegrama(txtTlg);
	}

	/**
	 * Envía el telegrama que permite conocer a qué clase pertenece la instancia cuyo nombre se
	 * pasa como parámetro.
	 */
	public String getTipoNombreBloqueo(String _nombre){
		String txtTlg = sintaxisTelegrama.getTlg__tipoNombre(_nombre);
		this.enviarTelegrama(txtTlg);

		try{
			waitUltimoTelegrama(500);
		}catch(CosmeTimeoutException ex){
			//logger.error(ex.getMessage());
		}

		return this.tipoNombre;
	}

	public void getTipoNombre(String _nombre){
		String txtTlg = sintaxisTelegrama.getTlg__tipoNombre(_nombre);
		this.enviarTelegrama(txtTlg);
	}

	/**
	 * @return
	 */
	public NamesList getNombresExistentes(){
		return this.nombresExistentes;
	}

	public boolean isDebugON(){
		return debug;
	}

	public void setDebug(boolean debug){
		this.debug = debug;
	}

	public int getMsRetardoTelegramas(){
		return msRetardoTelegramas;
	}

	public void setMsRetardoTelegramas(int msRetardoTelegramas){
		this.msRetardoTelegramas = msRetardoTelegramas;
	}

	/**
	 * Si está activado el CTP (Control de Telegramas Perdidos), devuelve el nÃºmero de telegramas
	 * que han sido enviados, pero de los que aún no hemos recibido respuesta (son por lo tanto
	 * candidatos a haberse perdido).
	 */
	public int getNumTelegramasPendientesConfirmacion(){
		int numTelegramasPendientes = 0;

		if(ctp != null){
			numTelegramasPendientes = ctp.getNumTelegramasPendientes();
		}

		return numTelegramasPendientes;
	}

	/**
	 * Activa el CTP. La librería sigue la pista de los telegramas enviados, para saber
	 * si han sido contestados o si se han perdido por el camino (en cuyo caso los
	 * reenvía automáticamente).
	 * En muchas configuraciones el uso de CTP no será necesario.
	 */
	public void activarCTP(){
		if(ctp == null){
			ctp = new ControlTelegramasPerdidos(this);
		}
	}

	public void desactivarCTP(){
		if(ctp != null){
			ctp.destruir();
			ctp = null;
		}
	}

	/**
	 * El funcionamiento de CTP se basa en un thread que se despierta cada x milisegundos,
	 * y comprueba si ha hecho timeout alguno de los telegramas marcados como
	 * pendientes.
	 * Este mÃ©todo permite fijar esa "x". Cada cuÃ¡nto tiempo CTP va a buscar telegramas
	 * perdidos.
	 */
	public void setPeriodoActivacionCTP(long _ms){
		if(ctp != null){
			ctp.setPeriodoActivacionCTP(_ms);
		}
	}

	/**
	 *
	 */
	public void setTimeoutCTP(long _ms){
		if(ctp != null){
			ctp.setTimeOut(_ms);
		}
	}

	/**
	 * >> Arcadio Plus
	 * ########### Replantear PLS
	 *
	 * @param _nuevoNivelAcceso
	 * @param _password
	 */
	public void cambiarNivelAcceso(AccessLevels _nuevoNivelAcceso, String _password){
		String txtTlg = sintaxisTelegrama.getTlg__modificarNivelAcceso(_nuevoNivelAcceso, _password);
		this.enviarTelegrama(txtTlg);
	}

	/**
	 * Devuelve la longitud máxima (en caracteres) que podrá tener un telegrama.
	 * Si enviamos un telegrama cuya longitud supere este valor, nos exponemos a
	 * que se trunque y no sea correctamente procesado por el kernel.
	 * Mensajero no enviará telegramas de tamaño superior a este valor.
	 *
	 * @return
	 */
	public int getMaxLengthTelegram(){
		return this.MAX_LENGTH_TELEGRAMA;
	}

	/**
	 * Devuelve el número de milisegundos empleados para procesar el último telegrama
	 * recibido. Mide el tiempo usado por el método run de la clase "OyenteTelegrama",
	 * desde que se sale del readLine, hasta que vuelve el bucle while para bloquearse
	 * en la espera del siguiente telegrama.
	 * Este tiempo incluye el utilizado por los métodos "notificarRefrescoVariables"
	 * (y también los otros "notificarXXX" del cliente CosmeListener.
	 *
	 * @return the msTlgProcessing
	 */
	public long getMsTlgProcessing(){
		return msTlgProcessing;
	}

	/**
	 * @return Devuelve la cadena con la versión de arcadio.
	 */
	public String getTxtVersion(){
		return this.infoVersion;
	}

	/**
	 * @return Devuelve sólamente el código de versión.
	 */
	//
	public String getVersion(){
		return this.idVersion;
	}

	/**
	 * Envía un telegrama que nos permitirá saber si los nombres que se pasan
	 * como parametro son "int", "real" o "nonum".
	 * El telegrama de respuesta disparará el evento "RECIBIDO_ISNUMERIC".
	 *
	 * @param _listaNombres
	 */
	public void isNumeric(NamesList _listaNombres){
		String nombres = "";
		for(String n : _listaNombres.getNameList()){
			nombres = nombres + n + " ";
		}
		String txtTlg = this.sintaxisTelegrama.getTlg__isNumeric(nombres);
		this.enviarTelegrama(txtTlg);
	}

	/**
	 * //########  D  A  N  G  E  R  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * <p/>
	 * A fecha de hoy (1.17g 22-5-2008) este método no lo llama nadie.
	 * Da mucho MIEDO!!!! Abre la puerta a inyectar cualquier veneno con total impunidad.
	 *
	 * @param _peticion
	 */
	public void enviarTelegramaLibre(String _peticion){
		if(this.sintaxisTelegrama != null){
			String tlg = this.sintaxisTelegrama.getInicioTelegrama() + " " + _peticion;
			this.enviarTelegrama(tlg);
		}else{
			System.err.println("ERROR enviarTelegramaLibre. Petición: " + _peticion);
		}
	}

	/**
	 * Sólo se utiliza en el CTP. Candidato a desaparecer  //###########
	 *
	 * @param _tlgAReenviar
	 */
	public void reenviarTelegrama(String _tlgAReenviar){
		this.enviarTelegrama(_tlgAReenviar);
	}

	/**
	 * @return Devuelve el "num_peticion" del último telegrama que se haya enviado.
	 * Útil para gestionar los telegramas con bloqueo
	 */
	public int getNumUltimoTelegramaEnviado(){
		return this.numUltimoTelegramaEnviado;
	}

	/**
	 * @return Devuelve el "num_peticion" del último telegrama que haya recibido el OyenteTelegrama.
	 * Útil para gestionar los telegramas con bloqueo
	 */
	public int getNumUltimoTelegramaRecibido(){
		return this.numUltimoTelegramaRecibido;
	}

	/**
	 * Permite que el thread desde el que invoquemos este método se bloquee hasta que hayamos
	 * recibido el eco del último telegrama que se haya enviado.
	 * Si esto no sucede en "_msTimeOut" ms, entonces se lanza una excepción.
	 *
	 * @param _msTimeOut
	 * @throws CosmeTimeoutException
	 */
	public void waitUltimoTelegrama(long _msTimeOut) throws CosmeTimeoutException{
		//this.printIfDebug(">>> Waiting for telegram num: "+getNumUltimoTelegramaEnviado());
		this.waitRXNumTelegrama(getNumUltimoTelegramaEnviado(), _msTimeOut);
		//this.printIfDebug(">>> Received telegram num: "+this.numUltimoTelegramaRecibido);
	}

	/**
	 * ***Félix dice***: en mi opinión los usuarios no deberían tener control sobre estructuras
	 * tan internas. Hay que ofrecerles una abstracción más digerible y universal.
	 */
    /*   public void cleanTlgs()
    {
    mensajero.reset();
    
    long initialTemp = System.currentTimeMillis();
    do {
    } while ((System.currentTimeMillis() - initialTemp) < 100); // <<== OJO: espera ocupada
    if(ctp!=null)ctp.inicializar();
    initialTemp = System.currentTimeMillis();
    do {
    } while ((System.currentTimeMillis() - initialTemp) < 100);// <<== OJO: espera ocupada
    }*/

	/**
	 * @return el número de nanosegundos transcurridos entre el envío y la recepción
	 * del último telegrama de ping.
	 */
	public long getPingTime(){
		return pingTime;
	}

	/**
	 * Permite modificar el periodo de envío de los telegramas "ping", siempre que
	 * el watchdog esté habilitado (ver el método "setWatchdogEnabled()").
	 * Si el periodo es 0 (o negativo), equivale a deshabilitar el watchdog.
	 *
	 * @param _ms Valor en milisegundos.
	 */
	public void setWatchdowgPeriod(int _ms){
		int ms = _ms;
		if(ms < 0){
			ms = 0;
		}
		if(this.watchdog != null){
			this.watchdog.setPeriod(ms);
			//System.err.println("Watchdog period: "+ms);
		}
	}

	/**
	 * Permite habilitar/deshabilitar el watchdog. Este es un thread que envía periódicamente
	 * un telegrama "ping" para generar tráfico entre arcadio y la pasarela, evitando que sus
	 * sockets hagan timeout por falta de actividad.
	 * El periodo de envío de esos telegramas "ping" puede modificarse con el método
	 * "setWatchdgPeriod()", que por defecto es de 2 segundos.
	 *
	 * @param _enabled
	 */
	public void setWatchdogEnabled(boolean _enabled){
		int watchdogPeriod = 2000;
		if(this.watchdog != null){
			watchdogPeriod = this.watchdog.getPeriod();
		}


		if(_enabled == true && this.watchdog == null){
			// Threads cannot be reused, so we create a new one.
			// Period has been recovered if already existed
			this.watchdog = new Watchdog(this, watchdogPeriod);
		}else{
			this.watchdog.setPeriod(0);
			this.watchdog.destroy();
		}

	}

	/**
	 * Permite conocer el número de ms que deben transcurrir sin que el socket reciba
	 * nada, para que salte el timeout.
	 *
	 * @return the socketTimeout
	 */
	public int getSocketTimeout(){
		return socketTimeout;
	}

	/**
	 * Permite modificar el número de ms que deben transcurrir para que salte el timeout
	 * del socket cuando no se haya recibido nada en ese tiempo.
	 *
	 * @param _socketTimeout the socketTimeout to set
	 */
	public void setSocketTimeout(int _socketTimeout){
		this.socketTimeout = _socketTimeout;
	}

	public boolean isConnected(){
		return this.estoyConectado;
	}

	/**
	 * @return the reconexionActivada
	 */
	public boolean isReconexionActivada(){
		return reconexionActivada;
	}

	/**
	 * @param _reconexionActivada the reconexionActivada to set
	 */
	public void setReconexionActivada(boolean _reconexionActivada){
		this.reconexionActivada = _reconexionActivada;
	}

	public void setTipoNombre(String tipoNombre){
		this.tipoNombre = tipoNombre;
	}

	public void setConnection(String input, String output){
		String txtTlg = sintaxisTelegrama.getTlg__set_connection(input, output);

		this.enviarTelegrama(txtTlg);
	}
}