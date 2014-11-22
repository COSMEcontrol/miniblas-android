package com.arcadio.api.v1.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import android.util.Log;
import android.widget.Toast;

import com.arcadio.Conexion;
import com.arcadio.EstadosCosme;
import com.miniblas.app.R;
import com.miniblas.iu.FabActivity;
import com.miniblas.model.MiniBlasPerfil;

public class ConnectionArcadioService extends Service{
	
	//(sessionId, sessionKey);
	private HashMap<Integer,Session> sessions ;
	private Context context = this;
	private Toast error_sessionId;
	private Toast error_sessionKey;
	public static String SESSION_ID = "sessionId";
	public static String SESSION_KEY = "sessionKey";
	private ExecutorService globalExecutor = Executors.newCachedThreadPool();
	private final IPluginServiceArcadio.Stub binder = new IPluginServiceArcadio.Stub() {
		@Override
		public void tipoNombre(final int sessionId, final String sessionKey,
				final String _nombreVariable) throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						 conexion.tipoNombre(_nombreVariable);
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					}
					
				}
			});
			
			
		}
		
		@Override
		public void solicitarVariables(final int sessionId, final String sessionKey)
				throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						 conexion.solicitarVariables();
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					}
					
				}
			});
			
			
		}
		
		@Override
		public void solicitarListaVariablesCesta(final int sessionId, final String sessionKey,
				final String _nombreCesta) throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						 conexion.solicitarListaVariablesCesta(_nombreCesta);
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
//						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
//						error_sessionKey.show();
					}
					
				}
			});
			
			
		}
		
		@Override
		public void solicitarListaCestas(final int sessionId, final String sessionKey)
				throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						 conexion.solicitarListaCestas();
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					}
				}
			});

			
		}
		
		@Override
		public void pingListaVariables(final int sessionId, final String sessionKey,
				final List<String> _listaVariables) throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						 ArrayList<String> listaVariables = new ArrayList<String>(_listaVariables.size());
						 listaVariables.addAll(_listaVariables);
						 conexion.pingListaVariables(listaVariables);
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					}
				}
			});

			
		}
		
		@Override
		public void ping(final int sessionId, final String sessionKey, final String _nombreVariable)
				throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						 conexion.ping(_nombreVariable);
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					}
				}
			});
		}
		
		@Override
		public void pedirTipos(final int sessionId, final String sessionKey)
				throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						 conexion.pedirTipos();
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					}
				}
			});
		}
		
		@Override
		public void obtenerMaxTamBufferCosme(final int sessionId, final String sessionKey)
				throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						 conexion.getLongMaxTelegrama();
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					}
				}
			});
		}
		
		@Override
		public void modificarValorVarible(final int sessionId, final String sessionKey,
				final String _nombreVariable, final double _valor) throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						 conexion.modificarValorVariable(_nombreVariable, _valor);
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					}
				}
			});		
		}
		
		@Override
		public void modificarPeriodoCesta(final int sessionId, final String sessionKey,
				final String _nombreCesta, final int nuevoRefrescoMs) throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						 conexion.modificarPeriodoCesta(_nombreCesta, nuevoRefrescoMs);
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					}
				}
			});
		}
		
		@Override
		public void leerVariable(final int sessionId, final String sessionKey,
				final String _nombreVariable) throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						 conexion.leerVariable(_nombreVariable);
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					}
				}
			});
		}
		
		@Override
		public void leerListaVariables(final int sessionId, final String sessionKey,
				final List<String> _listaVariables) throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						 ArrayList<String> listaVariables = new ArrayList<String>(_listaVariables.size());
						 listaVariables.addAll(_listaVariables);
						 conexion.leerListaVariables(listaVariables);
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					}
					
				}
			});
		}
		
		@Override
		public void isNumeric(final int sessionId, final String sessionKey,
				final String _nombresVariables) throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						 conexion.isNumeric(_nombresVariables);
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					}
				}
			});

			
		}
		
		@Override
		public void introducirVariablesACesta(final int sessionId, final String sessionKey,
				final String _nombreCesta, final List<String> _listaVariables)
				throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						 conexion.introducirVariablesACesta(_nombreCesta, _listaVariables);
						 System.out.println(_listaVariables);
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					}
				}
			});

			
		}
		
		@Override
		public void introducirVariableACesta(final int sessionId, final String sessionKey,
				final String _nombreCesta, final String _nombreVariable) throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						 conexion.introducirVariableACesta(_nombreCesta, _nombreVariable);
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					}
					
				}
			});

		}
		
		@Override
		public void eliminarVariablesDeCesta(final int sessionId, final String sessionKey,
				final String _nombreCesta, final List<String> _listaVariables)
				throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						 conexion.eliminarVariablesDeCesta(_nombreCesta, _listaVariables);
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					}
				}
			});

			
		}
		
		@Override
		public void eliminarVariableDeCesta(final int sessionId, final String sessionKey,
				final String _nombreCesta, final String _nombreVariable) throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						 conexion.eliminarVariableDeCesta(_nombreCesta, _nombreVariable);
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					}	
				}
			});

			
		}
		
		@Override
		public void eliminarCesta(final int sessionId, final String sessionKey,
				final String _nombreCesta) throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						 conexion.eliminarCesta(_nombreCesta);
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					}
					
				}
			});

		}
		
		@Override
		public void disconnect(final int sessionId, final String sessionKey)
				throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
						 Log.v(sessions.toString(),"ñkl");
						 Conexion conexion = Tools.getConexion(sessionId, sessionKey, sessions);
						
						 conexion.cerrar();
						 NotificationManager mNotifyMgr = 
							        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
						 mNotifyMgr.cancel(sessionId);
							
					} catch (SessionNotFound e) {
						e.printStackTrace();
						//generar un error en el listener indicando que con encuentra la session
						error_sessionId.show();
						
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					}
				
				}
			});
	
		}
		
		@Override
		public void crearCesta(final int sessionId, final String _sessionKey,
				final String _nombreCesta, final int _periodoRefresco) throws RemoteException {
			addGlobalTask(new Runnable() {
				
				@Override
				public void run() {
					try {
							Conexion conexion = Tools.getConexion(sessionId, _sessionKey, sessions);
							conexion.crearCesta(_nombreCesta, _periodoRefresco);
					} catch (IncorrectSessionKey e) {
						e.printStackTrace();
						error_sessionKey.show();
					} catch (SessionNotFound e) {
						error_sessionId.show();
						e.printStackTrace();
					}
				}
			});		
		}
		
		@Override
		public void connect(final int id, final ISessionStartedListener sessionListener, final ICosmeListener cosmeListener )
				throws RemoteException {
			
			addGlobalTask(new Runnable() {
				@Override
				public void run() {
					try {
						MiniBlasPerfil perfil = Tools.getProfileById(context, id);
						Conexion conexion = new Conexion(perfil.getIp(), perfil.getPuerto(),
							perfil.getPassword(),new AdapterCosmeListener(cosmeListener));
						Session session = new Session(sessionListener, conexion);
						sessions.put(session.getSessionId(), session);
						//notificar al cliente con su identificacion
						sessionListener.onSessionStarted(session.getSessionId(), session.getSessionKeyString());
						conexion.conectarSocket();		
						if(conexion.getEstado()==EstadosCosme.COMUNICACION_OK){
//							Log.v("",String.valueOf(session.getSessionId()));
							int sdk = android.os.Build.VERSION.SDK_INT;
							if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
								notify_deprecated("Conectado a servidor COSME",
									      "IP: "+conexion.getServidor(),session.getSessionId());
							} else {
								notify_api16("Conectado a servidor COSME",
									      "IP: "+conexion.getServidor(),session.getSessionId(), session.getSessionKeyString());
							}
						}
					} catch (SQLException e1) {
						try {
							sessionListener.onSessionError("ConnectionArcadioService ----> Error intentando "
									+ "abrir base de datos");
						} catch (RemoteException e) {
							e.printStackTrace();
							error_sessionKey.show();
						}
						e1.printStackTrace();
					} catch (ErrorProfile e1) {
						try {
							sessionListener.onSessionError(e1.getMessage());
						} catch (RemoteException e) {
							e.printStackTrace();
							error_sessionKey.show();
						}
						e1.printStackTrace();
					} catch (RemoteException e) {
						e.printStackTrace();
						error_sessionKey.show();
					}
		}
		});
			
		}
		
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return this.binder;
	}
	public synchronized void addGlobalTask(Runnable _task) {
		globalExecutor.submit(_task);
	}

	 @Override
	  public void onCreate() {
		 sessions=  new HashMap<Integer, Session>();
		 error_sessionId = Toast.makeText(context,
				 "ArcadioService -> Error entrada sessionId no encontrada", Toast.LENGTH_SHORT);
		 error_sessionKey = Toast.makeText(context,
				 "ArcadioService -> Error entrada sessionKey incorrecta", Toast.LENGTH_SHORT);
	  }
	  @Override
	   public void onStart(Intent intent, int startId) {
	      super.onStart(intent, startId);
	  }

	private void notify_deprecated(String notificationTitle, String notificationMessage, int mNotificationId) {
		 
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification =  new NotificationCompat.Builder(this).setAutoCancel(true)
                                        .setContentTitle(notificationTitle)
                                        .setContentText(notificationMessage)
                                        .setContentIntent(pi)
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setWhen(System.currentTimeMillis())
//                                        .setTicker(tickerText)
                                        .build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = 
		        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(mNotificationId, notification);
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	 private void notify_api16(String notificationTitle, String notificationMessage, int mNotificationId, String sessionKeyString ) {
		
		 // Create the style object with BigPictureStyle subclass.
		 NotificationCompat.BigPictureStyle notiStyle = new 
		         NotificationCompat.BigPictureStyle();
		 notiStyle.setBigContentTitle(notificationTitle);
		 notiStyle.setSummaryText(notificationMessage);

		Bitmap remote_picture;
		
		Drawable d  = this.getResources().getDrawable(R.drawable.ic_launcher);
		remote_picture = ((BitmapDrawable)d).getBitmap();
		

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
		 PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
		         0, PendingIntent.FLAG_UPDATE_CURRENT);
		 NotificationCompat.Builder mBuilder =
				    new NotificationCompat.Builder(this)
		 			.setLargeIcon(remote_picture)
//		 			.setAutoCancel(true)
				    .setSmallIcon(R.drawable.ic_launcher)
				    .setContentTitle(notificationTitle)
				    .setStyle(notiStyle)
				    .setOngoing(true)
				    .setContentText(notificationMessage);
		 //botones
		
		 Intent intent = new Intent(this, StopConnection.class);
		 intent.putExtra(SESSION_KEY, sessionKeyString);
		 intent.putExtra(SESSION_ID, mNotificationId);
		 PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		 mBuilder.setContentIntent(pending);
		 mBuilder.addAction(android.R.drawable.ic_delete, "Cancelar conexión", pending); // 4.1 +
		 
		 
		 mBuilder.setContentIntent(resultPendingIntent);

		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = 
		        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(mNotificationId, mBuilder.build());
		
	 }
		
}
