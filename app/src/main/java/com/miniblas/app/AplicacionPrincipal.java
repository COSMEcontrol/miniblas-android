package com.miniblas.app;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.arcadio.CosmeListener;
import com.arcadio.api.v1.service.CosmeStates;
import com.arcadio.api.v1.service.PluginClientArcadio;
import com.arcadio.common.ItemVariable;
import com.arcadio.common.VariablesList;
import com.arcadio.service.api.v1.listeners.OnClientStartedListener;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.miniblas.iu.FabActivity;
import com.miniblas.iu.controllers.ConnectionIconListener;
import com.miniblas.iu.controllers.ConnectionIconListener.ObservadorConnectionIcon;
import com.miniblas.iu.controllers.ConnectionListener;
import com.miniblas.iu.controllers.ListVariablesListener;
import com.miniblas.iu.controllers.ListVariablesListener.ObservadorListaVariables;
import com.miniblas.iu.controllers.ObservadorError;
import com.miniblas.iu.controllers.ObservadorState;
import com.miniblas.iu.controllers.ObservadorVariables;
import com.miniblas.iu.controllers.ObservadorVariables.IObservadorVariables;
import com.miniblas.iu.utils.ThemeUtils;
import com.miniblas.model.MiniBlasBag;
import com.miniblas.model.MiniBlasProfile;
import com.miniblas.model.variableWidgets.VariableSeekWidget;
import com.miniblas.model.variableWidgets.VariableSwitchWidget;
import com.miniblas.model.variableWidgets.VariableValueWidget;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;
import com.miniblas.modules.CheckIpModule;
import com.miniblas.persistence.ormlite.DBHelper;
import com.miniblas.persistence.BasketStorage;
import com.miniblas.persistence.OrmLiteBasketStorage;
import com.miniblas.persistence.OrmLiteProfileStorage;
import com.miniblas.persistence.OrmLiteVariableWidgetsStorage;
import com.miniblas.persistence.ProfileStorage;
import com.miniblas.persistence.SharedPreferencesSettingStorage;
import com.miniblas.persistence.VariableWidgetsStorage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.ObjectGraph;
/**
 *
 * @author A. Azuara
 */
public class AplicacionPrincipal extends Application{

	private static AplicacionPrincipal instance;
	private ThemeUtils mThemeUtils;
	private ObjectGraph objectGraph;
	private DBHelper mDBHelper;
	// persistencia
	private ProfileStorage profileStorage;
	private BasketStorage basketStorage;
	private VariableWidgetsStorage variableWidgetsStorage;
	private SharedPreferencesSettingStorage settingStorage;
	//pool de hilos
	private ExecutorService singleExecutor = Executors.newSingleThreadExecutor();
	private ExecutorService globalExecutor = Executors.newCachedThreadPool();
	// variables arcadio
	private ConnectionIconListener listenerIconConnection = new ConnectionIconListener();
	private ObservadorState listenerState = new ObservadorState();
	private ObservadorVariables listenerObservadorVariables = new ObservadorVariables();
	private ObservadorError listenerObservadorError = new ObservadorError();
	private ListVariablesListener<ArrayList<BaseVariableWidget>> listenerListaVariables
			= new ListVariablesListener<ArrayList<BaseVariableWidget>>();
	private ConnectionListener listenerConnection = new ConnectionListener();
	private PluginClientArcadio arcadio;
	private CosmeListener cosmeListener = new CosmeListener(){
		@Override
		public void onDataReceived(String _bagName, VariablesList _variableList){
			listenerObservadorVariables.Notify(_bagName, _variableList);
		}

		@Override
		public void onStateChange(CosmeStates _state){
			if(_state == CosmeStates.COMMUNICATION_OK){
				listenerIconConnection.onConnectNotify();
			}else if (_state == CosmeStates.COMMUNICATION_IMPOSSIBLE ||
					_state == CosmeStates.CONNECTION_INTERRUPTED||
					_state == CosmeStates.COMMUNICATION_TIMEOUT ||
					_state == CosmeStates.DISCONNECTED ||
					_state == CosmeStates.CONNEXION_IMPOSSIBLE){
				listenerIconConnection.onDisconnectNotify();
			}
			listenerState.notify(_state);
		}

		@Override
		public void onError(String _txtError){
			listenerObservadorError.notify(_txtError);
		}
	};

	public static AplicacionPrincipal getInstance(){
		return instance;
	}
	protected boolean hasNavDrawer() {
		return false;
	}
	@Override
	public void onCreate(){
		super.onCreate();
		mThemeUtils = new ThemeUtils(getBaseContext());
		setTheme(mThemeUtils.getCurrent(hasNavDrawer()));
		instance = this;
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
					Toast.makeText(getApplicationContext(), getString(R.string.disconnectToArcadio), Toast.LENGTH_SHORT).show();
					listenerConnection.onDisconnectNotify();
				}

				@Override
				public void onClientStarted(){
					Toast.makeText(getApplicationContext(),getString(R.string.connectToArcadio), Toast.LENGTH_SHORT).show();
					listenerConnection.onConnectNotify();
				}
			});
		}
	}
	public void setConnectionObserver(ConnectionListener.IObservadorConnection _observer){
		if(listenerConnection != null){
			this.listenerConnection.setObservador(_observer);
			// notificar el ultimo estado a la nueva vista
		}
	}
	public void resetConnectionObserver(){
		if(listenerConnection != null){
			this.listenerConnection.setObservador(null);
			// notificar el ultimo estado a la nueva vista
		}
	}
	public PluginClientArcadio getArcadioService(){
		return arcadio;
	}

	public void connect(int _connectionId){
		if(!arcadio.isConnected()){
			arcadio.connect(_connectionId, cosmeListener);
		}
	}

	public void setIconObserver(ObservadorConnectionIcon _observer){
		if(listenerIconConnection != null){
			this.listenerIconConnection.setObservador(_observer);
			if(arcadio.isConnected()){
				listenerIconConnection.onConnectNotify();
			}
			// notificar el ultimo estado a la nueva vista
			//cosmeListener.onStateChange(estado);
		}

	}
	public void setStateObserver(ObservadorState.IObservadorState _observer){
		if(listenerState != null){
			this.listenerState.setObservador(_observer);
			// notificar el ultimo estado a la nueva vista
		}
	}
	public void deleteAllStateObservers(){
		if(listenerState != null){
			listenerState.deleteAllObservers();
		}
	}
	public void setVariablesObserver(IObservadorVariables _observer){
		listenerObservadorVariables.setObservador(_observer);
	}

	public void deleteVariablesObserver(){
		listenerObservadorVariables.deleteAllObservers();
	}

	public void setListaNombresObserver(ObservadorListaVariables<ArrayList<BaseVariableWidget>> _observador){
		if(listenerListaVariables != null){
			this.listenerListaVariables.setObservador(_observador);
		}
	}

	public void inject(Object object){
		objectGraph.inject(object);
	}
/*
	private List<Object> getModules(){
		return Arrays.asList(new CheckIpModule(), new RenderAdaptersModule(getBaseContext()));
	}
*/
	private void initInjection(){
		if(objectGraph == null){
			//getModules().toArray();
			Object[] modules = {new CheckIpModule()};
			objectGraph = ObjectGraph.create(modules);
		}
		//objectGraph.inject(this);
	}

	/** ********* Clases de la base de datos ********** */

	private Dao<MiniBlasProfile, Integer> getPerfilDao() throws SQLException{
		return mDBHelper.getPerfilDao();
	}

	private Dao<MiniBlasBag, Integer> getCestaDao() throws SQLException{
		return mDBHelper.getCestaDao();
	}

	private Dao<VariableSeekWidget, Integer> getVariableSeekDao() throws SQLException{
		return mDBHelper.getVariableSeekWidgetDao();
	}

	private Dao<VariableSwitchWidget, Integer> getVariableSwitchDao() throws SQLException{
		return mDBHelper.getVariableSwitchWidgetDao();
	}
	private Dao<VariableValueWidget, Integer> getVariableValueDao() throws SQLException{
		return mDBHelper.getVariableValueWidgetDao();
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

	public VariableWidgetsStorage getVariableWidgetsStorage(){
		if(variableWidgetsStorage == null){
			try{
				variableWidgetsStorage = OrmLiteVariableWidgetsStorage.getInstance(getVariableSeekDao(), getVariableSwitchDao(),getVariableValueDao());
			}catch(SQLException e){
				e.printStackTrace();
				Toast.makeText(this, getResources().getString(R.string.errorAccessBd), Toast.LENGTH_SHORT).show();
			}
		}
		return variableWidgetsStorage;
	}




	public SharedPreferencesSettingStorage getSettingStorage(){
		if(settingStorage == null){
			settingStorage = new SharedPreferencesSettingStorage(this);
		}
		return settingStorage;
	}
	public void setErrorObserver(FabActivity _errorObserver){
		this.listenerObservadorError.setObservador(_errorObserver);
	}
}
