package com.arcadio.api.v1.service;

import com.arcadio.CosmeConnector;

import java.util.UUID;

public class Session{

	private CosmeConnector cosmeCosmeConnector;
	private ISessionStartedListener sessionStartedListener;

	private int sessionId;
	private String sessionKey;
	//	private static Session session;


	public Session(ISessionStartedListener sessionStartedListener, CosmeConnector cosmeCosmeConnector){
		this.sessionStartedListener = sessionStartedListener;
		this.cosmeCosmeConnector = cosmeCosmeConnector;
		this.sessionId = UUID.randomUUID().hashCode();
		this.sessionKey = UUID.randomUUID().toString();
	}
	//	public static Session getInstance(){
	//		return session;
	//	}
	//	public static Session getInstance(ISessionStartedListener sessionStartedListener, Conexion cosmeConexion){
	//		if(session==null){
	//			session = new Session(sessionStartedListener, cosmeConexion);
	//		}
	//		return session;
	//	}


	public CosmeConnector getCosmeConexion(String _sessionKey) throws IncorrectSessionKey{
		if(sessionKey.equals(_sessionKey)){
			return cosmeCosmeConnector;
		}else{
			throw new IncorrectSessionKey("Session --> Incorrect UUID sessionKey in getCosmeConexion");
		}
	}


	public ISessionStartedListener getSessionStartedListener(String _sessionKey) throws IncorrectSessionKey{
		if(sessionKey.equals(_sessionKey)){
			return sessionStartedListener;
		}else{
			throw new IncorrectSessionKey("Session --> Incorrect UUID sessionKey in getSessionStartedListener");
		}

	}


	public int getSessionId(){
		return sessionId;
	}


	public String getSessionKeyString(){
		return sessionKey.toString();
	}

}

class IncorrectSessionKey extends Exception{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public IncorrectSessionKey(){
		super();
	}

	public IncorrectSessionKey(String message){
		super(message);
	}
}