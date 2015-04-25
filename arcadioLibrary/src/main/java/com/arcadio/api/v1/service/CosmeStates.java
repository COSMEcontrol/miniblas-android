/*
* EstadosComunicacion.java
*
* Created on 19 de enero de 2007, 16:19
*
* To change this template, choose Tools | Template Manager
* and open the template in the editor.
*/
 
package com.arcadio.api.v1.service;
 
/**
*
* @author fserna,
*/
public enum CosmeStates {


    // Convenio que seguimos:
    //      Conexión:       a nivel de socket
    //      Comunicación:   debe haber una conexión válida, y además se ha
    //                      conseguido llevar a cabo
    //                      el protocolo de inicialización (recepción de
    //                      ID_CLIENTE e ID_MAQUINA)



    DISCONNECTED,

    // Nos disponemos a establecer la conexión
    TRYING_COMMUNICATION,

    // se ha intentado pero no ha podido establecerse
    CONNEXION_IMPOSSIBLE,

    // había una conexión OK, pero se ha interrumpido
    CONNECTION_INTERRUPTED,

    // Ya hay conexión, pero estamos esperando a recibir los 2 parámetros ID_IU
    // y ID_MAQ de la pasarela alta
    TRYING_TO_ESTABLISH_COMMUNICATION,

    // NO estamos en situación de intercambiar telegramas
    COMMUNICATION_IMPOSSIBLE,

    // Se ha superado el tiempo de espera prefijado para establecer la
    // COMUNICACION
    COMMUNICATION_TIMEOUT,

    // No puede accederse a Celestino
    CELESTINO_UNREACHABLE,

    //Se establece cuando se solicita las listas de: nombres/tipos/nombres-tipo
    ASKING_LIST,

    // podemos inicializar la máquina (crear sus cestas, etc...) y A
    // FUNCIONAR!!!
    COMMUNICATION_OK,

    //Se están recibiendo los "n" telegramas con la lista de nombres
    RECEIVING_NAMES_LIST,

    // Se ha recibido el último telegrama
    NAMES_LIST_RECEIVED,

    // Recibida la lista de clases
    RECEIVING_CLASS_LIST,
    RECEIVED_CLASS_LIST,

    //Lista de tipos recibida
    RECEIVING_TYPE_LIST,
    RECEIVED_TYPE_LIST,

    //Lista de nombres-Tipos recibida
    RECEIVING_TYPE_NAME_LIST,
    RECEIVED_TYPE_NAME_LIST,

    BAG_RECEIVED,   // ha llegado una cesta
    READ_RECEIVED,   // se ha recibido una lectura puntual
    WRITE_CONFIRMED, // se ha recibido el eco de una escritura

    ISNUMERIC_RECEIVED,  // se ha recibido un telegrama "is_numeric"

    MUESTREADOR_CREADO_OK,  // significa que tenemos constancia de que el muestreador ya ha sido creado en el runtime
    RECIBIDO_TELEGRAMA_MUESTREANDO, // se ha activado un muestreador
    RECIBIDO_DATOS_MUESTREADOR, // ha llegao un telegrama con un chunk de datos de un muestreador
    MUESTREADOR_FIN,  //arcadio ya ha recibido todas las muestras del muestreador

    RECEIVED_NAME_TYPE,
    RECEIVED_NAME_LIST_CONFIGURABLES,
    RECEIVED_NAME_LIST_CONFIGURABLES_RESERVED,

    RECEIVED_TELEGRAM_TEXT_FILE_REQUEST,

    RECEIVED_UNKNOWN_TELEGRAM,
    RECEIVED_INSTANCES_LIST_CLASS,

    RECEIVED_GATEWAY_EVENT,




    // -----EVENTOS QUE VIENEN DE LA PASARELA --------------------------------------------------
    UNKNOWN_ERROR,
    FICHERO_EMC_RECEIVED,
    FICHERO_EMC_RECEIVED_ERROR,
    ADMINISTRATOR_PASSWORD_ERROR,
    ADMINISTRATOR_OK,
    USER_OK,
    MAINTENANCE_INFO
}
