package com.miniblas.app;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.arcadio.CosmeListener;
import com.arcadio.api.v1.service.CosmeStates;
import com.arcadio.api.v1.service.PluginClientArcadio;
import com.arcadio.common.VariablesList;
import com.arcadio.service.api.v1.listeners.OnClientStartedListener;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.miniblas.iu.controllers.ConnectionIconListener;
import com.miniblas.iu.controllers.ConnectionIconListener.ObservadorConnectionIcon;
import com.miniblas.iu.controllers.ListaVariablesListener;
import com.miniblas.iu.controllers.ListaVariablesListener.ObservadorListaVariables;
import com.miniblas.iu.controllers.ObservadorVariables;
import com.miniblas.iu.controllers.ObservadorVariables.IObservadorVariables;
import com.miniblas.model.MiniBlasCesta;
import com.miniblas.model.MiniBlasItemVariable;
import com.miniblas.model.MiniBlasPerfil;
import com.miniblas.modules.CheckIpModule;
import com.miniblas.modules.RenderAdaptersModule;
import com.miniblas.perfistence.ormlite.DBHelper;
import com.miniblas.persistence.BasketStorage;
import com.miniblas.persistence.ItemVariableStorage;
import com.miniblas.persistence.OrmLiteBasketStorage;
import com.miniblas.persistence.OrmLiteItemVariableStorage;
import com.miniblas.persistence.OrmLiteProfileStorage;
import com.miniblas.persistence.ProfileStorage;
import com.miniblas.persistence.SharedPreferencesSettingStorage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.ObjectGraph;


public class AplicacionPrincipal extends Application{
	private ObjectGraph objectGraph;
	private DBHelper mDBHelper;
	// persistencia
	private ProfileStorage profileStorage;
	private BasketStorage basketStorage;
	private ItemVariableStorage itemVariableStorage;
	private SharedPreferencesSettingStorage settingStorage;
	//pool de hilos
	private ExecutorService singleExecutor = Executors.newSingleThreadExecutor();
	private ExecutorService globalExecutor = Executors.newCachedThreadPool();
	// variables arcadio
	private ConnectionIconListener listenerIconConnection = new ConnectionIconListener();
	private ObservadorVariables listenerObservadorVariables = new ObservadorVariables();
	private ListaVariablesListener<ArrayList<MiniBlasItemVariable>> listenerListaVariables = new ListaVariablesListener<ArrayList<MiniBlasItemVariable>>();
	private PluginClientArcadio arcadio;
	private CosmeStates estado = CosmeStates.DISCONNECTED;
	private CosmeListener cosmeListener = new CosmeListener(){
		@Override
		public void onDataReceived(String _bagName, VariablesList _variableList){
			listenerObservadorVariables.Notify(_bagName, _variableList);
		}

		@Override
		public void onStateChange(CosmeStates _state){
			estado = _state;
			if(_state == CosmeStates.COMMUNICATION_OK){
				listenerIconConnection.onConnectNotify();
			}else if (_state == CosmeStates.COMMUNICATION_IMPOSSIBLE || _state == CosmeStates.CONNECTION_INTERRUPTED|| _state == CosmeStates.COMMUNICATION_TIMEOUT || _state == CosmeStates.CONNEXION_IMPOSSIBLE){
				listenerIconConnection.onDisconnectNotify();
			}
			/*
            listenerListaVariables.onReceivedNotify(listaNombres);
            System.out.println(listaNombres.toString()+"fdhdf");
            */
		}

		@Override
		public void onError(String _txtError){

		}
	};

	@Override
	public void onCreate(){
		super.onCreate();
		if(mDBHelper == null){
			mDBHelper = OpenHelperManager.getHelper(this, DBHelper.class);
		}
		initInjection();
	}

	public synchronized void addSingleTask(Runnable _task){
		singleExecutor.submit(_task);// Ejecuta una tarea
	}

	public synchronized void addGlobalTask(Runnable _task){
		globalExecutor.submit(_task);
	}

	public void startService(){
		if(arcadio == null){
			arcadio = new PluginClientArcadio(this);
			arcadio.startService(new OnClientStartedListener(){

				@Override
				public void onClientStopped(){
					Toast.makeText(getApplicationContext(), "Desconectado del servicio Arcadio", Toast.LENGTH_SHORT).show();
					Log.v("Aplicacion ejemplo:-->", "Desconectado del servicio Arcadio");
				}

				@Override
				public void onClientStarted(){
					Toast.makeText(getApplicationContext(), "Conectado del servicio Arcadio", Toast.LENGTH_SHORT).show();
					Log.v("Aplicacion ejemplo:-->", "Conectado al servicio Arcadio");
				}
			});
		}
	}

	public PluginClientArcadio getArcadioService(){
		return arcadio;
	}

	public void connect(int _connectionId){
		if(!arcadio.isConnected()){
			System.out.println("Intentando establecer comunicacion con cosme con perfil: "+_connectionId);
			arcadio.connect(_connectionId, cosmeListener);
		}
	}

	public void setIconObserver(ObservadorConnectionIcon _observer){
		if(listenerIconConnection != null){
			this.listenerIconConnection.setObservador(_observer);
			// notificar el ultimo estado a la nueva vista
			cosmeListener.onStateChange(estado);
		}

	}

	public void setVariablesObserver(IObservadorVariables _observer){
		listenerObservadorVariables.setObservador(_observer);
	}

	public void deleteVariablesObserver(IObservadorVariables _observer){
		listenerObservadorVariables.removeObservador(_observer);
	}

	public void deleteAllVariablesObservers(){
		listenerObservadorVariables.deleteAllObservers();
	}

	public void setListaNombresObserver(ObservadorListaVariables<ArrayList<MiniBlasItemVariable>> _observador){
		if(listenerListaVariables != null){
			this.listenerListaVariables.setObservador(_observador);
		}
	}

	public void inject(Object object){
		objectGraph.inject(object);
	}

	private List<Object> getModules(){
		return Arrays.asList(new CheckIpModule(), new RenderAdaptersModule(getBaseContext()));
	}

	private void initInjection(){
		if(objectGraph == null){
			Object[] modules = getModules().toArray();
			objectGraph = ObjectGraph.create(modules);
		}
		objectGraph.inject(this);
	}

	/** ********* Clases de la base de datos ********** */

	private Dao<MiniBlasPerfil, Integer> getPerfilDao() throws SQLException{
		return mDBHelper.getPerfilDao();
	}

	private Dao<MiniBlasCesta, Integer> getCestaDao() throws SQLException{
		return mDBHelper.getCestaDao();
	}

	private Dao<MiniBlasItemVariable, Integer> getVariableDao() throws SQLException{
		return mDBHelper.getVariableDao();
	}

	/** ******* Seleccion de base de datos ******* */

	public ProfileStorage getProfileStorage(){
		if(profileStorage == null){
			try{
				profileStorage = OrmLiteProfileStorage.getInstance(getPerfilDao());
			}catch(SQLException e){
				e.printStackTrace();
				Toast.makeText(this, getResources().getString(R.string.errorAccessBd), Toast.LENGTH_SHORT).show();
			}
		}
		return profileStorage;
	}

	public BasketStorage getBasketStorage(){
		if(basketStorage == null){
			try{
				basketStorage = OrmLiteBasketStorage.getInstance(getCestaDao());
			}catch(SQLException e){
				e.printStackTrace();
				Toast.makeText(this, getResources().getString(R.string.errorAccessBd), Toast.LENGTH_SHORT).show();
			}
		}
		return basketStorage;
	}

	public ItemVariableStorage getVariableStorage(){
		if(itemVariableStorage == null){
			try{
				itemVariableStorage = OrmLiteItemVariableStorage.getInstance(getVariableDao());
			}catch(SQLException e){
				e.printStackTrace();
				Toast.makeText(this, getResources().getString(R.string.errorAccessBd), Toast.LENGTH_SHORT).show();
			}
		}
		return itemVariableStorage;
	}

	public SharedPreferencesSettingStorage getSettingStorage(){
		if(settingStorage == null){
			settingStorage = new SharedPreferencesSettingStorage(this);
		}
		return settingStorage;
	}

}
