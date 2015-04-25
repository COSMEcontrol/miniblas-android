/*
 * EmcosException.java
 *
 * Created on 22 de enero de 2007, 12:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arcadio.exceptions;

/**
 *
 * @author fserna
 */
public class CelestinoException extends Exception {
    
    private String mensaje;
    
    /** Creates a new instance of CelestinoException */
    public CelestinoException(String _msg) {
        this.mensaje = _msg;
    }
    
    
    public String getMessage(){
        return this.mensaje;
    }
    
    public String toString(){
        return this.mensaje;
    }
    
}
