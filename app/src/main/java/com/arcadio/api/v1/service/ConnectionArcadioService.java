package com.arcadio.api.v1.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.arcadio.ConexionEmcos;
import com.arcadio.common.ItemVariable;
import com.arcadio.common.NamesList;
import com.arcadio.common.VariablesList;
import com.arcadio.exceptions.CosmeException;
import com.arcadio.exceptions.CosmeTimeoutException;
import com.miniblas.app.R;
import com.miniblas.iu.FabActivity;
import com.miniblas.model.MiniBlasPerfil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionArcadioService extends Service{

	//(sessionId, sessionKey);
	private HashMap<Integer, Session> sessions = new HashMap<Integer, Session>();
	private Context context = this;
	private Toast error_sessionId;
	private Toast error_sessionKey;
	public static String SESSION_ID = "sessionId";
	public static String SESSION_KEY = "sessionKey";
	private ExecutorService globalExecutor = Executors.newCachedThreadPool();
	private ICosmeListener iCosmeListener;
	private final IPluginServiceArcadio.Stub binder = new IPluginServiceArcadio.Stub(){
		@Override
		public void addNameToBag(final int sessionId, final String sessionKey, final String _bagName, final String _name) throws RemoteException{
			addGlobalTask(new Runnable(){

				@Override
				public void run(){
					try{
						ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
						conexionEmcos.insertarNombre(_bagName, _name);
					}catch(SessionNotFound e){
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();

					}catch(IncorrectSessionKey e){
						e.printStackTrace();
						error_sessionKey.show();
					}

				}
			});
		}

		@Override
		public void addNamesToBag(final int sessionId, final String sessionKey, final String _bagName, final List<String> _names) throws RemoteException{
			addGlobalTask(new Runnable(){

				@Override
				public void run(){
					try{
						ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
						conexionEmcos.insertarNombres(_bagName, _names);
					}catch(SessionNotFound e){
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();

					}catch(IncorrectSessionKey e){
						e.printStackTrace();
						error_sessionKey.show();
					}

				}
			});
		}

		@Override
		public void blockingRead(final int sessionId, final String sessionKey, String _name, int _timeout) throws RemoteException{
			try{
				ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
				VariablesList variablesList = new VariablesList();
				variablesList.add(_name);
				conexionEmcos.leerBloqueo(variablesList, _timeout);
			}catch(SessionNotFound e){
				e.printStackTrace();
				//generar un error en el listener indicando que con encuentra la session
				error_sessionId.show();

			}catch(IncorrectSessionKey e){
				e.printStackTrace();
				error_sessionKey.show();
			}catch(CosmeTimeoutException e){
				iCosmeListener.onError(e.toString());
				e.printStackTrace();
			}
		}


		@Override
		public void blockingWrite(final int sessionId, final String sessionKey, final String _name, final double _value, final int _timeout) throws RemoteException{

			try{
				ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
				conexionEmcos.modificarVariableBloqueo(_name, _value, _timeout);
			}catch(SessionNotFound e){
				e.printStackTrace();
				//generar un error en el listener indicando que con encuentra la session
				error_sessionId.show();

			}catch(IncorrectSessionKey e){
				e.printStackTrace();
				error_sessionKey.show();
			}catch(CosmeTimeoutException e){
				try{
					iCosmeListener.onError(e.toString());
				}catch(RemoteException e1){
					e1.printStackTrace();
				}
				e.printStackTrace();
			}

		}

		@Override
		public void connect1(final int sessionId, final ISessionStartedListener iSessionStartedListener, final ICosmeListener iCosmeListener) throws RemoteException{
			new Thread(new Runnable(){

				@Override
				public void run(){
					try{
						MiniBlasPerfil perfil = Tools.getProfileById(context, sessionId);
						ConexionEmcos conexion = new ConexionEmcos(new AdapterCosmeListener(iCosmeListener), perfil.getPassword(), perfil.getIp(), perfil.getPuerto(), true);
						Session session = new Session(iSessionStartedListener, conexion);
						sessions.put(session.getSessionId(), session);
						//notificar al cliente con su identificacion
						iSessionStartedListener.onSessionStarted(session.getSessionId(), session.getSessionKeyString());
						//conexion.conectar(true);
						if(conexion.getEstado() == CosmeStates.COMMUNICATION_OK){
							int sdk = android.os.Build.VERSION.SDK_INT;
							if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN){
								notify_deprecated(getString(com.miniblas.app.R.string.contectadoConCosme), getString(com.miniblas.app.R.string.ip_notification)+ perfil.getIp(), session.getSessionId());
							}else{
								notify_api16(getString(com.miniblas.app.R.string.contectadoConCosme), getString(com.miniblas.app.R.string.ip_notification)+ perfil.getIp(), session.getSessionId(), session.getSessionKeyString());
							}
						}
					}catch(SQLException e1){
						try{
							iSessionStartedListener.onSessionError("ConnectionArcadioService ----> Error intentando " + "abrir base de datos");
						}catch(RemoteException e){
							e.printStackTrace();
							error_sessionKey.show();
						}
						e1.printStackTrace();
					}catch(ErrorProfile e1){
						try{
							iSessionStartedListener.onSessionError(e1.getMessage());
						}catch(RemoteException e){
							e.printStackTrace();
							error_sessionKey.show();
						}
						e1.printStackTrace();
					}catch(RemoteException e){
						e.printStackTrace();
						error_sessionKey.show();
					}catch(CosmeException e){
						try{
							iCosmeListener.onError(e.toString());
						}catch(RemoteException e1){
							e1.printStackTrace();
						}
						e.printStackTrace();
					}
				}
			}).start();

		}

		@Override
		public void connect2(final ISessionStartedListener iSessionStartedListener, final ICosmeListener iCosmeListener, final String _password, final String _host, final int _port) throws RemoteException{
			new Thread(new Runnable(){

				@Override
				public void run(){
					try{
						ConexionEmcos conexion = new ConexionEmcos(new AdapterCosmeListener(iCosmeListener), _password, _host, _port, true);
						Session session = new Session(iSessionStartedListener, conexion);
						sessions.put(session.getSessionId(), session);
						//notificar al cliente con su identificacion
						iSessionStartedListener.onSessionStarted(session.getSessionId(), session.getSessionKeyString());
						//conexion.conectar(true);
						if(conexion.getEstado() == CosmeStates.COMMUNICATION_OK){
							//							Log.v("",String.valueOf(session.getSessionId()));
							int sdk = android.os.Build.VERSION.SDK_INT;
							if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN){
								notify_deprecated("Conectado a servidor COSME", "IP: " + _host, session.getSessionId());
							}else{
								notify_api16("Conectado a servidor COSME", "IP: " + _host, session.getSessionId(), session.getSessionKeyString());
							}
						}
					}catch(RemoteException e){
						e.printStackTrace();
						error_sessionKey.show();
					}catch(CosmeException e){
						e.printStackTrace();
					}
				}
			}).start();
		}

		@Override
		public void createBag(final int sessionId, final String sessionKey, final String _bagName) throws RemoteException{
			addGlobalTask(new Runnable(){

				@Override
				public void run(){
					try{
						ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
						conexionEmcos.crearCesta(_bagName, 0);
					}catch(SessionNotFound e){
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();

					}catch(IncorrectSessionKey e){
						e.printStackTrace();
						error_sessionKey.show();
					}catch(CosmeException e){
						try{
							iCosmeListener.onError(e.toString());
						}catch(RemoteException e1){
							e1.printStackTrace();
						}
						e.printStackTrace();
					}

				}
			});
		}

		@Override
		public void deleteBag(final int sessionId, final String sessionKey, final String _bagName) throws RemoteException{
			addGlobalTask(new Runnable(){

				@Override
				public void run(){
					try{
						ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
						conexionEmcos.borrarCesta(_bagName);
					}catch(SessionNotFound e){
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();

					}catch(IncorrectSessionKey e){
						e.printStackTrace();
						error_sessionKey.show();
					}

				}
			});
		}

		@Override
		public void disconnect(final int sessionId, final String sessionKey) throws RemoteException{
			addGlobalTask(new Runnable(){

				@Override
				public void run(){
					try{
						ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
						conexionEmcos.desconectar();
						NotificationManager mNotifyMgr =
								(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
						mNotifyMgr.cancel(sessionId);
					}catch(SessionNotFound e){
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();

					}catch(IncorrectSessionKey e){
						e.printStackTrace();
						error_sessionKey.show();
					}

				}
			});
		}

		@Override
		public int getBagPeriod(final int sessionId, final String sessionKey, final String _bagName) throws RemoteException{
			try{
				ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
				return conexionEmcos.getPeriodoCesta(_bagName);
			}catch(SessionNotFound e){
				e.printStackTrace();
				//generar un error en el listener indicando que con encuentra la session
				error_sessionId.show();

			}catch(IncorrectSessionKey e){
				e.printStackTrace();
				error_sessionKey.show();
			}


			return 0;
		}

		@Override
		public List<String> getBags(int sessionId, String sessionKey) throws RemoteException{
			try{
				ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
				return new ArrayList<String>(conexionEmcos.getCestas());
			}catch(SessionNotFound e){
				e.printStackTrace();
				//generar un error en el listener indicando que con encuentra la session
				error_sessionId.show();

			}catch(IncorrectSessionKey e){
				e.printStackTrace();
				error_sessionKey.show();
			}
			return null;
		}

		@Override
		public long getPingLatencyMs(int sessionId, String sessionKey) throws RemoteException{
			try{
				ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
				return conexionEmcos.getPingTime();
			}catch(SessionNotFound e){
				e.printStackTrace();
				//generar un error en el listener indicando que con encuentra la session
				error_sessionId.show();

			}catch(IncorrectSessionKey e){
				e.printStackTrace();
				error_sessionKey.show();
			}
			return 0;
		}

		@Override
		public ItemVariable getVariable(int sessionId, String sessionKey, String _name) throws RemoteException{
			try{
				ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
				return conexionEmcos.getVariable(_name);
			}catch(SessionNotFound e){
				e.printStackTrace();
				//generar un error en el listener indicando que con encuentra la session
				error_sessionId.show();

			}catch(IncorrectSessionKey e){
				e.printStackTrace();
				error_sessionKey.show();
			}
			return null;
		}

		@Override
		public List<ItemVariable> getVariables(int sessionId, String sessionKey, List<String> _names) throws RemoteException{
			try{
				ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
				return conexionEmcos.getVariables(_names);
			}catch(SessionNotFound e){
				e.printStackTrace();
				//generar un error en el listener indicando que con encuentra la session
				error_sessionId.show();

			}catch(IncorrectSessionKey e){
				e.printStackTrace();
				error_sessionKey.show();
			}
			return null;
		}

		@Override
		public String getVersion(int sessionId, String sessionKey) throws RemoteException{
			try{
				ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
				return conexionEmcos.getVersion();
			}catch(SessionNotFound e){
				e.printStackTrace();
				//generar un error en el listener indicando que con encuentra la session
				error_sessionId.show();

			}catch(IncorrectSessionKey e){
				e.printStackTrace();
				error_sessionKey.show();
			}
			return null;
		}

		@Override
		public boolean isConnected(int sessionId, String sessionKey) throws RemoteException{
			try{
				ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
				return conexionEmcos.isConnected();
			}catch(SessionNotFound e){
				e.printStackTrace();
				//generar un error en el listener indicando que con encuentra la session
				error_sessionId.show();

			}catch(IncorrectSessionKey e){
				e.printStackTrace();
				error_sessionKey.show();
			}
			return false;
		}

		@Override
		public void removeNameFromBag(final int sessionId, final String sessionKey, final String _bagName, final String _name) throws RemoteException{
			addGlobalTask(new Runnable(){

				@Override
				public void run(){
					try{
						ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
						conexionEmcos.eliminarNombre(_bagName, _name);
					}catch(SessionNotFound e){
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();

					}catch(IncorrectSessionKey e){
						e.printStackTrace();
						error_sessionKey.show();
					}

				}
			});
		}

		@Override
		public void setBagPeriod(final int sessionId, final String sessionKey, final String _bagName, final int _ms) throws RemoteException{
			addGlobalTask(new Runnable(){

				@Override
				public void run(){
					try{
						ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
						conexionEmcos.setPeriodoCesta(_bagName, _ms);
					}catch(SessionNotFound e){
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();

					}catch(IncorrectSessionKey e){
						e.printStackTrace();
						error_sessionKey.show();
					}

				}
			});
		}

		@Override
		public void setPingPeriod(final int sessionId, final String sessionKey, int _ms) throws RemoteException{
			addGlobalTask(new Runnable(){

				@Override
				public void run(){
					try{
						ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
						// conexionEmcos.pin
					}catch(SessionNotFound e){
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();

					}catch(IncorrectSessionKey e){
						e.printStackTrace();
						error_sessionKey.show();
					}

				}
			});
		}

		@Override
		public void singleRead(final int sessionId, final String sessionKey, final VariablesList variablesList) throws RemoteException{
			addGlobalTask(new Runnable(){

				@Override
				public void run(){
					try{
						ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
						conexionEmcos.lecturaPuntual(variablesList);
					}catch(SessionNotFound e){
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();

					}catch(IncorrectSessionKey e){
						e.printStackTrace();
						error_sessionKey.show();
					}

				}
			});
		}

		@Override
		public void waitForLastTelegram(final int sessionId, final String sessionKey, final int _msTimeout) throws RemoteException{
			addGlobalTask(new Runnable(){

				@Override
				public void run(){
					try{
						ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
						conexionEmcos.waitUltimoTelegrama(Long.valueOf(_msTimeout));
					}catch(SessionNotFound e){
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();

					}catch(IncorrectSessionKey e){
						e.printStackTrace();
						error_sessionKey.show();
					}catch(CosmeTimeoutException e){
						try{
							iCosmeListener.onError(e.toString());
						}catch(RemoteException e1){
							e1.printStackTrace();
						}
					}

				}
			});
		}

		@Override
		public void writeVariable1(final int sessionId, final String sessionKey, final String _name, final double _value) throws RemoteException{
			addGlobalTask(new Runnable(){

				@Override
				public void run(){
					try{
						ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
						conexionEmcos.modificarVariable(_name, _value);
					}catch(SessionNotFound e){
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();

					}catch(IncorrectSessionKey e){
						e.printStackTrace();
						error_sessionKey.show();
					}

				}
			});
		}

		@Override
		public void writeVariable2(final int sessionId, final String sessionKey, final String _name, final String _value) throws RemoteException{
			addGlobalTask(new Runnable(){

				@Override
				public void run(){
					try{
						ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
						conexionEmcos.modificarVariable(_name, _value);
					}catch(SessionNotFound e){
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();

					}catch(IncorrectSessionKey e){
						e.printStackTrace();
						error_sessionKey.show();
					}

				}
			});
		}

		@Override
		public void writeVariables3(final int sessionId, final String sessionKey, final VariablesList _names) throws RemoteException{
			addGlobalTask(new Runnable(){

				@Override
				public void run(){
					try{
						ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
						conexionEmcos.modificarVariable(_names);
					}catch(SessionNotFound e){
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();

					}catch(IncorrectSessionKey e){
						e.printStackTrace();
						error_sessionKey.show();
					}

				}
			});
		}

		@Override
		public NamesList getNamesList(int sessionId, String sessionKey) throws RemoteException{
			try{
				ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
				return conexionEmcos.getNombresExistentes();
			}catch(SessionNotFound e){
				e.printStackTrace();
				//generar un error en el listener indicando que con encuentra la session
				error_sessionId.show();

			}catch(IncorrectSessionKey e){
				e.printStackTrace();
				error_sessionKey.show();
			}
			return null;
		}

		@Override
		public void requestNamesList(final int sessionId, final String sessionKey) throws RemoteException{
			addGlobalTask(new Runnable(){

				@Override
				public void run(){
					try{
						ConexionEmcos conexionEmcos = Tools.getConexion(sessionId, sessionKey, sessions);
						conexionEmcos.solicitarListaNombres();
					}catch(SessionNotFound e){
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();

					}catch(IncorrectSessionKey e){
						e.printStackTrace();
						error_sessionKey.show();
					}

				}
			});
		}
	};

	@Override
	public IBinder onBind(Intent intent){
		return this.binder;
	}

	public synchronized void addGlobalTask(Runnable _task){
		globalExecutor.submit(_task);
	}

	@Override
	public void onCreate(){
		error_sessionId = Toast.makeText(context, "ConnectionArcadioService -> Error entrada sessionId no encontrada", Toast.LENGTH_SHORT);
		error_sessionKey = Toast.makeText(context, "ConnectionArcadioService -> Error entrada sessionKey incorrecta", Toast.LENGTH_SHORT);
	}

	@Override
	public void onStart(Intent intent, int startId){
		super.onStart(intent, startId);
	}

	private void notify_deprecated(String notificationTitle, String notificationMessage, int mNotificationId){

		PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
		Notification notification = new NotificationCompat.Builder(this).setAutoCancel(true).setContentTitle(notificationTitle).setContentText(notificationMessage).setContentIntent(pi).setSmallIcon(R.drawable.ic_launcher).setWhen(System.currentTimeMillis())
				//                                        .setTicker(tickerText)
				.build();
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(mNotificationId, notification);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void notify_api16(String notificationTitle, String notificationMessage, int mNotificationId, String sessionKeyString){

		// Create the style object with BigPictureStyle subclass.
		NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
		notiStyle.setBigContentTitle(notificationTitle);
		notiStyle.setSummaryText(notificationMessage);

		Bitmap remote_picture;

		Drawable d = this.getResources().getDrawable(R.drawable.ic_launcher);
		remote_picture = ((BitmapDrawable) d).getBitmap();


		//
		//		 // Add the big picture to the style.
		notiStyle.bigPicture(remote_picture);

		// Creates an explicit intent for an ResultActivity to receive.
		Intent resultIntent = new Intent(this, ConnectionArcadioService.class);

		// This ensures that the back button follows the recommended
		// convention for the back key.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

		// Adds the back stack for the Intent (but not the Intent itself).
		stackBuilder.addParentStack(FabActivity.class);

		// Adds the Intent that starts the Activity to the top of the stack.
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setLargeIcon(remote_picture)
				//		 			.setAutoCancel(true)
				.setSmallIcon(R.drawable.ic_launcher).setContentTitle(notificationTitle).setStyle(notiStyle).setOngoing(true).setContentText(notificationMessage);
		//botones

		Intent intent = new Intent(this, StopConnection.class);
		intent.putExtra(SESSION_KEY, sessionKeyString);
		intent.putExtra(SESSION_ID, mNotificationId);
		PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder.setContentIntent(pending);
		mBuilder.addAction(android.R.drawable.ic_delete, getString(R.string.cancel_conexion), pending); // 4.1 +


		mBuilder.setContentIntent(resultPendingIntent);

		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(mNotificationId, mBuilder.build());

	}

}
