package com.arcadio.exceptions;

public class TelegramTypesException extends Exception{
	
	public String idTelegrama;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TelegramTypesException(String _idTelegrama){
		
	}
	public String getIdTelegrama(){
		return idTelegrama;
	}
}
