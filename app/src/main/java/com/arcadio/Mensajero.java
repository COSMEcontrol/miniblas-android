package com.arcadio;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
/**
 *
 * @author fserna
 */
public class Mensajero extends Thread{
	//  private static final Logger logger = Logger.getLogger(Mensajero.class);

	private CosmeConnector connection;
	private BlockingQueue<String> tlgQueue;

	private PrintWriter out;
	private boolean estoyVivo = true;

	/** Creates a new instance of Mensajero */
	public Mensajero(CosmeConnector _emcos){
		this.connection = _emcos;
		this.out = _emcos.getCanalDeSalida();
		this.estoyVivo = true;

		this.tlgQueue = new LinkedBlockingQueue<String>();

		this.setDaemon(true);
		this.start();
	}

	@Override
	public void destroy(){
		estoyVivo = false;

		interrupt();
	}

	@Override
	public void run(){
		while(this.estoyVivo){
			try{
				String tlg = tlgQueue.take();
				//poll();  // NO BLOQUEANTE

				if(tlg != null){
					enviar(tlg);
				}
			}catch(Exception ex){
				//  if (this.estoyVivo)
				//      logger.error(ex.getMessage());
			}
		} // while estoyVivo
	}


	/**
	 * Añade el telegrama que se pasa como parámetro a una cola, y esta clase
	 * se encargará de enviarlo cuando sea posible.
	 * El envío se produce en un thread independiente del resto de la aplicación.
	 *
	 * @param _tlg Telegrama a enviar.
	 */
	public void enviarTelegrama(String _tlg){
		if(_tlg.length() > connection.getMaxLengthTelegram()){
			connection.getCosmeListener().onError("ERROR: Se está intentando enviar un telegrama de " + _tlg.length() + " bytes.");
		}else{
			try{
				tlgQueue.put(_tlg);
			}catch(Exception ex){
				//logger.error(ex.getMessage());
			}
		}
	}

	private void enviar(String _tlg){
		if(connection.getMsRetardoTelegramas() != 0){
			try{
				sleep(connection.getMsRetardoTelegramas());
			}catch(InterruptedException ex){
				//ex.printStackTrace();
			}
		}

		if(_tlg != null){
			out.write(_tlg);
			out.print("\n");
			out.flush();

			if(connection.isDebugON()){
				System.err.println(System.currentTimeMillis() + " Tx ==> " + _tlg);
			}
		}
	}

	/**
	 * Elimina todos los elementos que pudiera tener la cola "tlgQueue". Si algún telegrama es
	 * importante y pensamos que DEBE ejecutarlo el kernel, debería haberse enviado con bloqueo
	 * (usando "waitUltimoTelegrama()", p.e.)
	 */
	public void reset(){
		tlgQueue.clear();
	}
}