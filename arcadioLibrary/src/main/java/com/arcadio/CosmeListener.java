/*
* CosmeListener.java
*
* Created on 27 de junio de 2006, 18:23
*
* To change this template, choose Tools | Template Manager
* and open the template in the editor.
*/
 
package com.arcadio;

import com.arcadio.api.v1.service.CosmeStates;
import com.arcadio.common.VariablesList;


/**
*   Cosme Listener
* @author Alberto Azuara García
*/
public interface CosmeListener{
    /*
        *Receives as parameters the name of the basket received
        * and the list of variables that it contains.
     */
   
    public void onDataReceived(String _bagName, VariablesList _variableList);

   /*
        *Receives as a parameter the new state that has transited,
         * (CosmeStates).
    */
    public void onStateChange(CosmeStates _state);

    /*
        *Receives as a parameter an error code,
        *  Invoked when Arcadio detects some kind of error.
     */
    public void onError(String _txtError);


}