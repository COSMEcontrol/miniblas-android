package com.miniblas.persistence;

/**
 * Created by alberto on 14/11/14.
 */
public class BdException extends Exception{

	private static final long serialVersionUID = 1L;

	public BdException(){
		super();
	}

	public BdException(String message){
		super(message);
	}
}

