package com.arcadio.api.v1.service;
import com.arcadio.api.v1.service.ParceableCosmeStates;
import com.arcadio.common.VariablesList;

interface ICosmeListener {

        /*
            *Receives as parameters the name of the basket received
            * and the list of variables that it contains.
         */

        void onDataReceived(String _bagName,inout VariablesList _variableList);

       /*
            *Receives as a parameter the new state that has transited,
             * (CosmeStates).
        */
        void onStateChange(inout ParceableCosmeStates _state);

        /*
            *Receives as a parameter an error code,
            *  Invoked when Arcadio detects some kind of error.
         */
        void onError(String _txtError);

}