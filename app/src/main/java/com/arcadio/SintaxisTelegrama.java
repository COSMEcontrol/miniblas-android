package com.arcadio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * Clase SintaxisTelegramaSencilla (clase propia)
 */
public class SintaxisTelegrama {
	private int numPeticion = 0;
	private Conexion conexion;
	private int longMaxTelegrama;
	
	/**
	 * Constructor SintaxisTelegramaSencilla
	 */
	public  SintaxisTelegrama (Conexion _conexion){
        this.conexion   = _conexion;
        this.longMaxTelegrama = _conexion.getLongMaxTelegrama();
    }
	
	
	/** 
	 * M�todo que retorna un String con la "cabecera" para  
	 * realizar telegramas
	 */
	protected synchronized String getInicioTelegrama(){
        return (conexion.getIDCliente()+" "+ conexion.getIDMaquina() +" "+ (numPeticion++) +" ");
    }

	
    /** 
     * M�todo que retorna un String, para solicitar la lectura
     * de UNA sola variable (m�todo propio)  
     */
    public String getTelegrama_leer_variable (String _nombreVariable){
        String tlg = getInicioTelegrama()+"leer "+ _nombreVariable;
        
        return tlg;
    }
    
    
    public String getTelegrama_leerListaVariables (ArrayList<String> _listaVariables){
        String tlg = getInicioTelegrama()+"leer ";
        
        Iterator i = _listaVariables.iterator();
       
        while(i.hasNext()) {
        	tlg = tlg + i.next()+" ";
        }
        
        return tlg;
    }
    
    
    /** 
     * M�todo que retorna un String, para solicitar el ping de 
     * UNA sola variable (m�todo propio)   
     */
    public String getTelegrama_ping (String _nombreVariable){
        String tlg = getInicioTelegrama()+"ping "+ _nombreVariable;
        
        return tlg;
    }
    
    
    public ArrayList<String> getTelegrama_pingListaVariables (ArrayList<String> _listaVar){
    	ArrayList<String> listaTelegramas = new ArrayList<String>();  
        String txt_tlg = getInicioTelegrama()+"ping ";
        
    	for (String var: _listaVar){
        	if(txt_tlg.length() + var.length() < this.longMaxTelegrama) {
        		txt_tlg = txt_tlg + var +" "; 
        	}
        	else {
        		listaTelegramas.add(txt_tlg);
        		txt_tlg = getInicioTelegrama()+"ping ";
        		txt_tlg = txt_tlg + var +" ";
        	}
        }
    	listaTelegramas.add(txt_tlg);
        
     
        return listaTelegramas;
    }
    
    
    /** 
     * M�todo que retorna un String, para crear una cesta  
     */
    public String getTelegrama_crearCesta (String _nombreCesta, int _periodoRefresco){
        String txt_tlg = this.getInicioTelegrama()+"crear_cesta"+" "+_nombreCesta+" "+_periodoRefresco;
       
        return txt_tlg;
    }
    
    /** 
	 * M�todo que retorna un String para eliminar 
	 * una cesta
	 */
    public String getTelegrama_eliminarCesta (String _nombreCesta) {
        //enviamos el telegrama para que la pasarela baja deje de enviarnos telegramas de esta cesta
    	//return getInicioTelegrama() +"periodo_cesta "+ _nombreCesta+ " "+0;
        return getInicioTelegrama() +"eliminar_cesta "+ _nombreCesta;
    }
    
    
    /** 
	 * M�todo que retorna un String para modificar el periodo  
	 * de refresco de una cesta 
	 */   
    public String getTelegrama_periodoCesta (String _nombreCesta,int _nuevoRefrescoMs){
        return getInicioTelegrama()+"periodo_cesta "+_nombreCesta+ " "+_nuevoRefrescoMs +" ";
    }
    
    
    /** 
	 * M�todo que retorna un String para a�adir una 
	 * variable a una cesta concreta 
	 */
	public String getTelegrama_introducirNombreACesta (String _nombreCesta, String _nombreVariable){
        String txt_tlg = this.getInicioTelegrama()+"insertar_nom_cesta"+" "+_nombreCesta+" "+_nombreVariable;
        //String txt_tlg = this.getInicioTelegrama()+"bi"+" "+_nombreCesta+" "+_nombreVariable+" ";
       
        return txt_tlg;
    }
	
	/** 
	 * M�todo que retorna un String para a�adir varias 
	 * variables a una cesta concreta 
	 */
	public ArrayList<String> getTelegrama_introducirNombresACesta(String _nombreCesta, Collection<String> _listaVar) {
    	ArrayList<String> listaTelegramas = new ArrayList<String>();  
    	String txt_tlg = this.getInicioTelegrama()+"insertar_nom_cesta"+" "+_nombreCesta+" ";
        
    	for (String var: _listaVar){
        	if(txt_tlg.length() + var.length() < this.longMaxTelegrama) {
        		txt_tlg = txt_tlg + var +" "; //concatena txt_tlg con cada elemento(variable) de la colecci
        	}
        	else {
        		listaTelegramas.add(txt_tlg);
        		txt_tlg = this.getInicioTelegrama()+"insertar_nom_cesta"+" "+_nombreCesta+" ";
        		txt_tlg = txt_tlg + var +" ";
        	}
        }
    	listaTelegramas.add(txt_tlg);
 
        return listaTelegramas;
    }
	
	
	/** 
	 * M�todo que retorna un String para eliminar una 
	 * variable de una cesta concreta 
	 */
    public String getTelegrama_eliminarNombreDeCesta (String _nombreCesta, String _nombreVariable){
        String txt_tlg = this.getInicioTelegrama()+"eliminar_nom_cesta"+" "+_nombreCesta+" "+_nombreVariable+" ";
        //String txt_tlg = this.getInicioTelegrama()+"be"+" "+_nombreCesta+" "+_nombreVariable+" ";
 
        return txt_tlg;
    }
    
    /** 
	 * M�todo que retorna un String para mostrar las 
	 * variables de una cesta concreta 
	 */         
    public String getTelegrama_listaNombresDeCesta (String _nombreCesta) {
        //lista_nombres_cesta <nombre>
       return getInicioTelegrama()+"lista_nombres_cesta "+_nombreCesta;
    }
    
    /** 
	 * M�todo que retorna un String para solicitar 
	 * el nombre de todas las cestas
	 */
    public String getTelegrama_listaCestas(){
    	return getInicioTelegrama()+"lista_nombres_tipo CESTA ";
    }
    
    
    /** 
	 * M�todo que retorna un String para solicitar 
	 * a Cosme el nombre de todas las variables
	 */
    public String getTelegrama_pedirNombres () {
        return getInicioTelegrama() +"lista_nombres ";
    }
    
    
    /** 
	 * M�todo que retorna el tipo de una variable (NO LO SE SEGURO)????
	 */
    public String getTelegrama_tipoNombre (String _nombreVariable){
        String tlg = getInicioTelegrama()+"tipo_nombre "+_nombreVariable;
       
        return tlg;
    }
    
    /** 
	 * Metodo para escribir el valor de una variable
	 */
    public String getTelegrama_escribir (String nombreVariable, Double valor) {
    	return this.getInicioTelegrama()+"escribir "+nombreVariable+" " +valor+" ";
    }
    /**
     * Telegrama que permite saber si una serie de nombres son de tipo entero, real, o no numericos
     * @param _nombresVar Debe contener un string con la lista de nombres separados por espacios.
     * Si le pasas una variable de tipo:
     * int retorna int
     * double retorna real
     * String retorna non
     */
     public String getTelegrama_isNumeric (String _nombresVariables){
  
         String tlg = getInicioTelegrama()+"is_numeric "+_nombresVariables;
  
         return tlg;
     }
    
     /** 
 	 * M�todo que retorna todos los tipos de las variables
 	 * de Cosme
 	 */
     public String getTelegrama_pedirTipos () {
    	 
         return getInicioTelegrama() +"lista_tipos ";
     }
}
