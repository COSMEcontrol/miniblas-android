package com.arcadio.api.v1.service;

import android.content.Context;
import android.util.Log;

import com.arcadio.CosmeConnector;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.miniblas.model.MiniBlasPerfil;
import com.miniblas.perfistence.ormlite.Constantes;
import com.miniblas.perfistence.ormlite.DBHelper;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class Tools{
	public static CosmeConnector getConexion(int sessionId, String sessionKey, HashMap<Integer, Session> sessions) throws SessionNotFound, IncorrectSessionKey{
		Session session = sessions.get(sessionId);
		if(session == null){
			throw new SessionNotFound("ConnectionArcadioService --> sessionId " + sessionId + " not found.");
		}else{
			return session.getCosmeConexion(sessionKey);
		}
	}

	public static MiniBlasPerfil getProfileById(Context context, int id) throws SQLException, ErrorProfile{
		//			AplicacionPrincipal application = ((AplicacionPrincipal) context.getApplicationContext());
		//			application.
		Log.v("Abriendo base de datos", "Abriendo base de datos");
		Dao<MiniBlasPerfil, Integer> perfilDao = OpenHelperManager.getHelper(context, DBHelper.class).getPerfilDao();
		Log.v("rescatado perfildao", "rescatado perfildao");
		QueryBuilder<MiniBlasPerfil, Integer> queryBuilder = perfilDao.queryBuilder();
		Log.v("instanciando consulta", "instanciando consulta");
		//seleccion de la tabla
		queryBuilder.where().eq(Constantes.PROFILE_ID, id);
		Log.v("generada consulta", "generada consulta");
		// prepare our sql statement
		PreparedQuery<MiniBlasPerfil> preparedQuery = queryBuilder.prepare();
		Log.v("preparado query", "preparado query");
		// query for all profiles that have  String id as a id.
		List<MiniBlasPerfil> profileList = perfilDao.query(preparedQuery);
		Log.v("Tengo listas", "tengo listas");
		if(profileList.size() > 1){
			throw new ErrorProfile("ToolsConnectionArcadioService ----> Error, encontrado " + "mas de un perfil con ID pedido " + String.valueOf(id));

		}else{
			Log.v("Tools->>Rescatado datos de la base de datos", profileList.toString());
			if(profileList==null){
				throw new ErrorProfile("ToolsConnectionArcadioService ----> Error, profile list null");
			}else if(profileList.isEmpty()){
				throw new ErrorProfile("ToolsConnectionArcadioService ----> Error, profile list empty");
			}else{
				return profileList.get(0);
			}
		}
	}

}

class SessionNotFound extends Exception{
	private static final long serialVersionUID = 1L;

	public SessionNotFound(){
		super();
	}

	public SessionNotFound(String message){
		super(message);
	}
}

class ErrorProfile extends Exception{

	private static final long serialVersionUID = 1L;

	public ErrorProfile(){
		super();
	}

	public ErrorProfile(String message){
		super(message);
	}
}

