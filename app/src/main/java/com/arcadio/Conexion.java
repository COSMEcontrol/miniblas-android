package com.arcadio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.arcadio.exceptions.TelegramTypesException;
import com.arcadio.modelo.Cesta;
import com.arcadio.modelo.ItemVariable;

public class Conexion{
	private String servidorIp; // = "155.210.68.102"; //por defecto
	private int puerto; // = 15150; //por defecto
	private String password; // = "GTA70\n"; //por defecto
	
	private Socket skCliente;
	private OutputStreamWriter flujoSalida = null;
	private BufferedReader flujoEntrada = null;
	//pool threads
	private ExecutorService globalExecutor = Executors.newCachedThreadPool();


	private String IDCliente;
	private String IDMaquina;
	private SintaxisTelegrama sintaxisTelegrama;
	private Mensajero mensajero;
	private Oyente oyente;
	private Ping ping;
	private int longMaxTelegrama;
	private final static String COMANDO_LEER_VARIABLE = "leer";
	private final static String VARIABLE_MAX_TAM_BUFFER_COSME = "SISTEMA.long_buffer_fifo_ent"; 
	private EstadosCosme estado;
	private String lineaLeida = null;
	
	private ArrayList<CosmeListener> observadores;
	
	

	public Conexion(CosmeListener _observador) {
		observadores = new ArrayList<CosmeListener>();
		observadores.add(_observador);
	}

	public Conexion(String _servidor, int _puerto, String _password, CosmeListener _observador) {
		observadores = new ArrayList<CosmeListener>();
		servidorIp = _servidor;
		puerto = _puerto;
		password =_password;
		observadores.add(_observador);
	}
    public void addTask(Runnable _task) {
		globalExecutor.submit(_task);
	}
	public void conectarSocket(){
        this.cambiarEstado(EstadosCosme.INTENTANDO_ESTABLECER_COMUNICACION);
		try {
			Log.v(servidorIp, String.valueOf(puerto));
			skCliente = null;
			skCliente = new Socket(servidorIp, puerto);
			if(skCliente.isConnected()){
				try {
					flujoSalida = new OutputStreamWriter(skCliente.getOutputStream());
					flujoEntrada = new BufferedReader((new InputStreamReader(skCliente.getInputStream())));
					
					flujoSalida.write(password.toString()+"\n");
					flujoSalida.flush();
					lineaLeida = flujoEntrada.readLine();
//					Log.v("Arcadio","linealeida "+lineaLeida);
					
					if (lineaLeida != null) {
						StringTokenizer tokens = new StringTokenizer(lineaLeida);
						this.IDCliente = tokens.nextToken();
						this.IDMaquina = tokens.nextToken();
//						Log.v("Respuesta de Cosme IDCliente",IDCliente);
//						Log.v("Respuesta de Cosme IDSistema",IDMaquina);
						
						if (IDCliente != null && IDMaquina != null) {
							obtenerMaxTamBufferCosme();
//							Log.v("hola","hola");
							sintaxisTelegrama = new SintaxisTelegrama(this);
//							Log.v("Arcadio->", sintaxisTelegrama.toString());
							oyente = new Oyente(this);
							oyente.start();
							
							mensajero = new Mensajero(this);
							mensajero.start();
							//probar con esta variable ping que no existe
							//String variablePing = "SISTEMA.REF.DOUBLE";
							String variablePing = "SISTEMA.tiempo_ciclo.rt";
							int periodoEnvio = 2000;
							ping = new Ping(this, sintaxisTelegrama, variablePing, periodoEnvio);
							ping.start();
							this.cambiarEstado(EstadosCosme.COMUNICACION_OK);
						}
					} else {
						this.cambiarEstado(EstadosCosme.COMUNICACION_IMPOSIBLE);
					}
				} catch (IOException e) {
					this.cambiarEstado(EstadosCosme.COMUNICACION_IMPOSIBLE);
					e.printStackTrace();
				}
	        } else {
	        	this.cambiarEstado(EstadosCosme.COMUNICACION_IMPOSIBLE);
	        }
			
		} catch (UnknownHostException e) {
			this.cambiarEstado(EstadosCosme.COMUNICACION_IMPOSIBLE);
			e.printStackTrace();
		} catch (IOException e) {
			this.cambiarEstado(EstadosCosme.COMUNICACION_IMPOSIBLE);
			e.printStackTrace();
		}
	}
	
	public void actualizarObservador(CosmeListener _observador){
		observadores.clear();
		observadores.add(_observador);
	}
	////Variables
	public void leerVariable(String _nombreVariable){
		String tlg = sintaxisTelegrama.getTelegrama_leer_variable(_nombreVariable);
		mensajero.enviarTelegrama(tlg);
	}
	
	public void leerListaVariables(ArrayList<String> _listaVariables){
		String tlg = sintaxisTelegrama.getTelegrama_leerListaVariables(_listaVariables);
		mensajero.enviarTelegrama(tlg);
	}
	
	public void ping(String _nombreVariable){
		String tlg = sintaxisTelegrama.getTelegrama_leer_variable(_nombreVariable);
		mensajero.enviarTelegrama(tlg);
	}
	
	public void pingListaVariables(ArrayList<String> _listaVariables){
		ArrayList<String> listaTelegramas;
		listaTelegramas = sintaxisTelegrama.getTelegrama_pingListaVariables(_listaVariables);
		for (String telegrama: listaTelegramas){
			mensajero.enviarTelegrama(telegrama);
		}
	}
	
	public void crearCesta(String _nombreCesta, int _periodoRefresco){
		String tlg = sintaxisTelegrama.getTelegrama_crearCesta(_nombreCesta, _periodoRefresco);
		mensajero.enviarTelegrama(tlg);
	}
	
	public void eliminarCesta(String _nombreCesta){
		String tlg = sintaxisTelegrama.getTelegrama_eliminarCesta(_nombreCesta);
		mensajero.enviarTelegrama(tlg);
	}
	
	public void modificarPeriodoCesta(String _nombreCesta,int nuevoRefrescoMs){
		String tlg = sintaxisTelegrama.getTelegrama_periodoCesta(_nombreCesta, nuevoRefrescoMs);
		mensajero.enviarTelegrama(tlg);
	}
	

	public void introducirVariableACesta(String _nombreCesta, String _nombreVariable){
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		String tlg = sintaxisTelegrama.getTelegrama_introducirNombreACesta(_nombreCesta, _nombreVariable);
		mensajero.enviarTelegrama(tlg);
	}
	
	public void introducirVariablesACesta(String _nombreCesta, Collection<String> _listaVariables){
		ArrayList<String> listaTelegramas;  
		listaTelegramas = sintaxisTelegrama.getTelegrama_introducirNombresACesta(_nombreCesta, _listaVariables);
		for (String telegrama: listaTelegramas){
			mensajero.enviarTelegrama(telegrama);
		}
	}
	
	public void eliminarVariableDeCesta(String _nombreCesta,String _nombreVariable){
		String tlg = sintaxisTelegrama.getTelegrama_eliminarNombreDeCesta(_nombreCesta, _nombreVariable);
		mensajero.enviarTelegrama(tlg);
	}
	
	public void eliminarVariablesDeCesta(String _nombreCesta,Collection<String> _listaVariables){
		//introducir for 
		/*String tlg = sintaxisTelegrama.getTelegrama_eliminarNombresDeCesta(_nombreCesta, _listaVariables);
		mensajero.enviarTelegrama(tlg);*/
	}
	
	public void solicitarListaVariablesCesta(String _nombreCesta){
		String tlg = sintaxisTelegrama.getTelegrama_listaNombresDeCesta(_nombreCesta);
		mensajero.enviarTelegrama(tlg);
	}
	
	public void solicitarListaCestas(){
		String tlg = sintaxisTelegrama.getTelegrama_listaCestas();
		mensajero.enviarTelegrama(tlg);
	}
	
	////Cosme
	public void solicitarVariables(){
		String tlg = sintaxisTelegrama.getTelegrama_pedirNombres();
		mensajero.enviarTelegrama(tlg);
	}
	/**
	 * Modificar el valor de una variable 
	 */
	public void modificarValorVariable(String _nombreVariable, Double _valor){
		String tlg = sintaxisTelegrama.getTelegrama_escribir(_nombreVariable, _valor);
		mensajero.enviarTelegrama(tlg);
	}
	/**
	 * 
	 * @param _nombresVariables string con una serie de variables separadas por espacios 
	 */
	public void isNumeric(String _nombresVariables){
		String tlg = sintaxisTelegrama.getTelegrama_isNumeric(_nombresVariables);
		mensajero.enviarTelegrama(tlg);
	}
	
	public void tipoNombre(String _nombreVariable){
		String tlg = sintaxisTelegrama.getTelegrama_tipoNombre(_nombreVariable);
		mensajero.enviarTelegrama(tlg);
	}
	
	public void pedirTipos(){
		String tlg = sintaxisTelegrama.getTelegrama_pedirTipos();
		mensajero.enviarTelegrama(tlg);
	}
	
	public void stopMensajero(){
		mensajero.destroy();
	}
	
	public void stopOyente(){
		oyente.destroy();
	}	
	
	private void obtenerMaxTamBufferCosme() {
		try {
			flujoSalida.write(IDCliente + " " + IDMaquina + " " + 0 + " " + COMANDO_LEER_VARIABLE + " "+VARIABLE_MAX_TAM_BUFFER_COSME + "\n");
			flujoSalida.flush();
			lineaLeida = flujoEntrada.readLine();
			Log.v("MAxTamBufferCosme",lineaLeida);
			Telegrama tlg = null;
			try {
				tlg = new Telegrama(lineaLeida);
			} catch (TelegramTypesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ItemVariable variable = tlg.getListaVariables().get(0);
			this.longMaxTelegrama = Integer.valueOf(variable.getValor());
			Log.v("obtenermaxtambuffercosme-->", "Establecido valor "+longMaxTelegrama);
		} catch (IOException e) {
			this.cambiarEstado(EstadosCosme.COMUNICACION_IMPOSIBLE);
			e.printStackTrace();
		}
		
	}
	
	public void notificarRefrescoVariables(final String _nombreCesta, final ArrayList<ItemVariable> _listaVariables){
		addTask(new Runnable() {
			@Override
			public void run() {
				if(observadores != null){
					for (CosmeListener o:observadores) {
				         o.notificarRefrescoVariables(_nombreCesta, _listaVariables);
				    }
				}
				ItemVariable.clearInstanceList();
			}
		});
	}
//	public void notificarEvento(EstadosCosme _nuevoEstado, Telegrama _tlg){
//		for (CosmeListener o:observadores) {
//	         o.notificarEvento(_nuevoEstado, _tlg);
//	    }
//	}
	
	public void cambiarEstado(final EstadosCosme _nuevoEstado) {
		this.estado = _nuevoEstado;
		addTask(new Runnable() {
			
			@Override
			public void run() {
				Log.v("Estado",_nuevoEstado.toString());
				for (CosmeListener o:observadores) {
			         o.notificarEstadoConexion(_nuevoEstado);
			    }			
			}
		});
	}
    
	public void enviar(String _datos) throws IOException{
		flujoSalida.write(_datos);
		flujoSalida.flush();
	}
	
	/*public static boolean verificaConexion(final Context ctx) {
	    boolean bConectado = false;
	    ConnectivityManager connec = (ConnectivityManager) ctx
	            .getSystemService(Context.CONNECTIVITY_SERVICE);
	    // No s�lo wifi, tambi�n GPRS
	    NetworkInfo[] redes = connec.getAllNetworkInfo();
	    // este bucle deber�a no ser tan �apa
	    for (int i = 0; i < 2; i++) {
	        // �Tenemos conexi�n? ponemos a true
	        if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
	            bConectado = true;
	        }
	    }
	    return bConectado;
	}*/
	
	public void cerrar() {
		try {
			if(skCliente.isConnected()){
				skCliente.close();
			}
			if(flujoSalida!=null)
				flujoSalida.close();
			if(flujoEntrada!=null)
				flujoEntrada.close();
			if(ping!=null)
				ping.destroy();
			this.cambiarEstado(EstadosCosme.DESCONECTADO);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getServidor() {
		return servidorIp;
	}

	public int getPuerto() {
		return puerto;
	}

	public Socket getSkCliente() {
		return skCliente;
	}

	public OutputStreamWriter getFlujoSalida() {
		return flujoSalida;
	}

	public BufferedReader getFlujoEntrada() {
		return flujoEntrada;
	}
	
	public String getIDCliente() {
		return IDCliente;
	}

	public String getIDMaquina() {
		return IDMaquina;
	}

	public EstadosCosme getEstado() {
		return estado;
	}
	
	public int getLongMaxTelegrama() {
		return longMaxTelegrama;
	}

	public void notificarListaVariablesCosme(final ArrayList<ItemVariable> _listaNombres) {
		addTask(new Runnable() {
			
			@Override
			public void run() {
				if(observadores != null){
					for (CosmeListener o:observadores) {
				         //o.notificarEvento(EstadosCosme.RECIBIDA_LISTA_NOMBRES, telegramaRecibido);
				         o.notificarListaNombres(_listaNombres);
				    }
				}
			}
		});
	}
	public void notificarCestaCreada(final Cesta cesta){
		addTask(new Runnable() {
			
			@Override
			public void run() {
				if(observadores != null)
					for(CosmeListener o:observadores){
						o.notificarCestaCreada(cesta);
					}
			}
		});
	}
	public void notificarNomCesta(final Cesta cesta, final ItemVariable variable){
		addTask(new Runnable() {
			
			@Override
			public void run() {
				if(observadores != null)
					for(CosmeListener o:observadores){
						o.notificarNomACesta(cesta, variable);
					}
			}
		});
	}
	public void notificarIsNumeric(final ItemVariable _variable){
		addTask(new Runnable() {
			
			@Override
			public void run() {
				if(observadores != null)
					for (CosmeListener o:observadores) {
				         o.notificarIsNumeric(_variable);
				    }
			}
		});
	}

	public void notificarError(final CosmeError _error) {
		addTask(new Runnable() {
			
			@Override
			public void run() {
				if(observadores != null)
					for (CosmeListener o:observadores) {
				         o.notificarError(_error);
				    }
			}
		});
		
		
		
	}
}
