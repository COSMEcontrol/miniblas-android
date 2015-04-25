package com.arcadio.api.v1.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.arcadio.CosmeListener;
import com.arcadio.common.ItemVariable;
import com.arcadio.common.NamesList;
import com.arcadio.common.VariablesList;
import com.arcadio.service.api.v1.listeners.OnClientStartedListener;
import com.arcadio.api.v1.service.exceptions.NoConnectedArcadioException;
import com.arcadio.api.v1.service.exceptions.ServiceDisconnectedArcadioException;
import java.util.List;

/**
 * Created by Alberto Azuara García on 05/12/14.
 */

public class PluginClientArcadio {
	
	private static IPluginServiceArcadio remoteArcadio;
	private OnClientStartedListener onclientstartedlistener;
    public static final String MINIBLAS_PACKAGE_NAME = "com.miniblas.app";
	public static final String MINIBLAS_SERVICE_ARCADIO = "com.arcadio.api.v1.service.ConnectionArcadioService";
	//variables de session
	private int sessionId = 0;
	private String sessionKey = "";
	private Context context;
	private boolean isServiceConnected = false;
	
	
	public PluginClientArcadio(Context context){
		this.context=context;
	}


	public void setServiceConnected(boolean isConnected){
		this.isServiceConnected = isConnected;
	}
	
	private ServiceConnection conexion = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.v("PluginClientArcadioLibrary-->", "Disconnected to service");
			remoteArcadio = null;
			onclientstartedlistener.onClientStopped();
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder _remoteArcadio) {
			Log.v("PluginClientArcadioLibrary-->", "Connected to service");
			remoteArcadio =  IPluginServiceArcadio.Stub.asInterface(_remoteArcadio);
			//generar llamada a onClientStarted
			onclientstartedlistener.onClientStarted();
			
		}
	};
	/**
	 * Conexion con servicio android Arcadio.
	 * @param onclientstartedlistener ( OnClientStartedListener ) 
	 */
	public void startService(OnClientStartedListener onclientstartedlistener){
		//iniciar la unicion-conexion stub con el servicio de arcadio
		this.onclientstartedlistener=onclientstartedlistener;
		Intent msgIntent = new Intent();
		msgIntent.setClassName(MINIBLAS_PACKAGE_NAME, MINIBLAS_SERVICE_ARCADIO);
		if(context.startService(msgIntent)==null)
			Log.v("PluginClientArcadioLibrary-->", "Failed to start service");
		else
			Log.v("PluginClientArcadioLibrary-->", "Iniciate service");
        Intent intent = new Intent();
        intent.setClassName(MINIBLAS_PACKAGE_NAME, MINIBLAS_SERVICE_ARCADIO);
		if(!context.bindService(intent, conexion, Context.BIND_AUTO_CREATE))
			Log.v("PluginClientArcadioLibrary-->", "ERROR Connecting to an application Arcadio service");
		else{
			Log.v("PluginClientArcadioLibrary-->", "Connecting to an application Arcadio service");
		}	
	}
	/**
	 * Conexion con servidor COSME remoto
	 */
	//requestId es el codigo que acabará devolviendo gotActivityResult
	public void connect(int connectionId, CosmeListener cosmeListener){
		if(remoteArcadio== null){
            Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
        }
		try {
			remoteArcadio.connect1(connectionId, new ISessionStartedListener.Stub() {
                @Override
                public IBinder asBinder() {
                    return this;
                }

                @Override
                public void onSessionStarted(int _sessionId, String _sessionKey)
                        throws RemoteException {
                    sessionId = _sessionId;
                    sessionKey = _sessionKey;
					setServiceConnected(true);
                    Log.v("PluginClientArcadioLibrary-->", "Client identifier received API");
                }

                @Override
                public void onSessionError(String error) throws RemoteException {
					setServiceConnected(false);
                    Log.v("PluginClientArcadioLibrary-->", "Session Error: " + error);

                }
            }, new AdapterICosmeListener(cosmeListener));
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
    /**
     * EXPLICITAMENTE indico que quiero terminar la conexión. No tiene sentido
     * que haya reconexión.
     */

	public void disconnect() throws NoConnectedArcadioException, ServiceDisconnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					remoteArcadio.disconnect(sessionId, sessionKey);
					setServiceConnected(false);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();	
		}

	}
	public void stopService(){
		//desactiva la union-conexion con el stub del servicio de arcadio
		context.unbindService(conexion);
	}
	
	//variables

    //
    //   A P I    P Ú B L I C A
    //
    //   Métodos a utilizar desde quienes implementen EmcosListener, según sus
    //   necesidades...
    //


    /**
     * Permite a un cliente obtener la lista de nombres existentes (públicos)
     * en el sistema. Puede ser un proceso largo que necesite del intercambio
     * de varios telegramas, por lo que se recomienda lanzarlo en una conexión
     * dedicada sólo a este propósito y ejecutada en un thread independiente
     * para no interferir con el resto de la aplicación.
     */
    public void addNameToBag(String _bagName, String _name) throws ServiceDisconnectedArcadioException, NoConnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					remoteArcadio.addNameToBag(sessionId, sessionKey, _bagName, _name);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
    }

    public void addNamesToBag(String _bagName, List<String> _variablesList) throws ServiceDisconnectedArcadioException, NoConnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					remoteArcadio.addNamesToBag(sessionId, sessionKey, _bagName, _variablesList);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
    }

    public void blockingRead(String _name, int _timeout) throws ServiceDisconnectedArcadioException, NoConnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					remoteArcadio.blockingRead(sessionId, sessionKey, _name, _timeout);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
    }
    public void blockingWrite(String _name, double _value, int _timeout) throws ServiceDisconnectedArcadioException, NoConnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					remoteArcadio.blockingWrite(sessionId, sessionKey, _name, _value, _timeout);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
    }

    public void connect(ICosmeListener _iCosmeListener, String _password, String _host, int _port){
        try {
            if(remoteArcadio== null) {
                Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
            }else {
                remoteArcadio.connect2(new ISessionStartedListener.Stub() {
					@Override
					public IBinder asBinder() {
						return this;
					}

					@Override
					public void onSessionStarted(int _sessionId, String _sessionKey)
							throws RemoteException {
						sessionId = _sessionId;
						sessionKey = _sessionKey;
						setServiceConnected(true);
						Log.v("PluginClientArcadioLibrary-->", "Client identifier received API");
					}

					@Override
					public void onSessionError(String error) throws RemoteException {
						setServiceConnected(false);
						Log.v("PluginClientArcadioLibrary-->", "Session Error: " + error);

					}
				}, _iCosmeListener, _password, _host, _port);
				setServiceConnected(true);
            }
        } catch (RemoteException e) {
            onclientstartedlistener.onClientStopped();
        }

    }
    public void createBag(String _bagName) throws ServiceDisconnectedArcadioException, NoConnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					remoteArcadio.createBag(sessionId, sessionKey, _bagName);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
    }
    public void deleteBag(String _bagName) throws ServiceDisconnectedArcadioException, NoConnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					remoteArcadio.deleteBag(sessionId, sessionKey, _bagName);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
    }
    public int getBagPeriod(String _bagName) throws NoConnectedArcadioException, ServiceDisconnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					return remoteArcadio.getBagPeriod(sessionId, sessionKey, _bagName);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
        return 0;
    }
    public List<String> getBags() throws NoConnectedArcadioException, ServiceDisconnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					return remoteArcadio.getBags(sessionId, sessionKey);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
        return null;
    }
    public long getPingLatencyMs() throws NoConnectedArcadioException, ServiceDisconnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					return remoteArcadio.getPingLatencyMs(sessionId, sessionKey);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
        return 0;
    }
    public ItemVariable getVariable(String _name) throws ServiceDisconnectedArcadioException, NoConnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					return remoteArcadio.getVariable(sessionId, sessionKey, _name);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
        return null;
    }
    public List<ItemVariable> getVariables(List<String> _names) throws NoConnectedArcadioException, ServiceDisconnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					remoteArcadio.getVariables(sessionId, sessionKey, _names);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
        return null;
    }
    public String getVersion() throws ServiceDisconnectedArcadioException, NoConnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					return remoteArcadio.getVersion(sessionId, sessionKey);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
        return null;
    }
    public boolean isConnected(){
		try {
			if(remoteArcadio == null){
				return false;
			}else{
				if(isServiceConnected){
					return remoteArcadio.isConnected(sessionId, sessionKey);
				}else{
					return false;
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
        return false;
    }
    public void removeNameFromBag(String _bagName, String _name) throws ServiceDisconnectedArcadioException, NoConnectedArcadioException{
		try {
			if(remoteArcadio == null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					remoteArcadio.removeNameFromBag(sessionId, sessionKey, _bagName, _name);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
    }
    public void setBagPeriod(String _bagName, int _ms) throws ServiceDisconnectedArcadioException, NoConnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					remoteArcadio.setBagPeriod(sessionId, sessionKey, _bagName, _ms);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}

    }
    public void setPingPeriod(int _ms) throws ServiceDisconnectedArcadioException, NoConnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					remoteArcadio.setPingPeriod(sessionId, sessionKey, _ms);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
    }
    public void singleRead(VariablesList _vars) throws ServiceDisconnectedArcadioException, NoConnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					remoteArcadio.singleRead(sessionId, sessionKey, _vars);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
    }
    public void waitForLastTelegram(int _msTimeout) throws ServiceDisconnectedArcadioException, NoConnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					remoteArcadio.waitForLastTelegram(sessionId, sessionKey, _msTimeout);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
    }
    public void writeVariable(String _name, double _value) throws ServiceDisconnectedArcadioException, NoConnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					remoteArcadio.writeVariable1(sessionId, sessionKey,_name, _value);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
    }
    public void writeVariable(String _name, String _value) throws ServiceDisconnectedArcadioException, NoConnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					remoteArcadio.writeVariable2(sessionId, sessionKey, _name, _value);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
    }
    public void writeVariables(VariablesList _names) throws ServiceDisconnectedArcadioException, NoConnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					remoteArcadio.writeVariables3(sessionId, sessionKey, _names);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
    }
    //CosmeconnectorPlus
    public NamesList getNamesList() throws ServiceDisconnectedArcadioException, NoConnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					return remoteArcadio.getNamesList(sessionId, sessionKey);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
        return null;
    }
    public void requestNamesList() throws ServiceDisconnectedArcadioException, NoConnectedArcadioException{
		try {
			if(remoteArcadio== null){
				Log.v("PluginClientArcadioLibrary-->", "Not connected with Arcadio Service");
				throw new ServiceDisconnectedArcadioException();
			}else{
				if(isServiceConnected){
					remoteArcadio.requestNamesList(sessionId, sessionKey);
				}else{
					throw new NoConnectedArcadioException();
				}
			}
		} catch (RemoteException e) {
			onclientstartedlistener.onClientStopped();
		}
    }

}
