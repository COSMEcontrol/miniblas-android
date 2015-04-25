/*
 * EmcosException.java
 *
 * 
 */

package com.arcadio.exceptions;

/**
 *
 * @author fserna
 */
public class CosmeTimeoutException extends Exception {
    
    private String mensaje;
    
    /** Creates a new instance of EmcosTimeoutException */
    public CosmeTimeoutException(String _msg) {
        this.mensaje = _msg;
    }
    
    
    public String getMessage(){
        return this.mensaje;
    }
    
    public String toString(){
        return this.mensaje;
    }
    
}
