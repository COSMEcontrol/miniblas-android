package com.arcadio;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.arcadio.exceptions.TelegramTypesException;
import com.arcadio.modelo.ItemVariable;

public class Telegrama {
	private String idCliente;
	private String idMaquina;
	private int numPeticion;
	private BigInteger timeStamp;
	private String idTelegrama; //idTelegrama equivale al comando del telegrama (segun el)
	private String nombreCesta;
	private String nombreVariable;
	private String valorVariable;
	private String periodoCesta;
	private String telegramaCompleto;
	private String esNumerica;
	//private String mensajeError;
//	private boolean hayError;
//	private String variableAEliminar;
	private ArrayList<ItemVariable> listaVariables = new ArrayList<ItemVariable>();
	private ArrayList<CosmeError> listaErrores;
	private ItemVariable variable;
	
	private String idHexa1 = "__Ox";
	private String idHexa2 = "0x"; 
	private Double valorNumerico;
	
	
	public Telegrama(String _cadena) throws TelegramTypesException{
		this.telegramaCompleto = _cadena;
//		hayError=false;
		listaErrores = new ArrayList<CosmeError>();
		cargarTelegrama(telegramaCompleto);
	}
	
	
	private void cargarTelegrama(String _telegrama) throws TelegramTypesException{
		StringTokenizer st = new StringTokenizer(_telegrama);
		cargarInicioTelegrama(st);
		cargarRestoTelegrama(st, idTelegrama);
	}
	

	private void cargarInicioTelegrama(StringTokenizer _st) {
		this.idCliente = _st.nextToken();
		this.idMaquina = _st.nextToken();
		this.numPeticion = Integer.valueOf(_st.nextToken());
		this.timeStamp = new BigInteger(_st.nextToken());
		this.idTelegrama = _st.nextToken();
	}
	
	private void cargarRestoTelegrama(StringTokenizer _st, String _idTelegrama) throws TelegramTypesException{
		try{
			TelegramTypes tipoTelegrama = TelegramTypes.valueOf(_idTelegrama.toUpperCase());
			listaVariables.clear();
			switch(tipoTelegrama) {
			
			case LEER: //leer varias variables de un arrayList
				while (_st.hasMoreTokens()){
					nombreVariable = _st.nextToken();
					valorVariable = _st.nextToken();
					listaVariables.add(new ItemVariable(nombreVariable, valorVariable));
				}
				break;
			
	//		case LEER_UNA_VAR: //leer una sola variable (propio)
	//			
	//				nombreVariable = _st.nextToken();
	//				valorVariable = _st.nextToken();
	//				
	//			break;
				
			case PING:
				
				nombreVariable = _st.nextToken();
				valorVariable = _st.nextToken();
				
				break;
		 
			case CREAR_CESTA:
				nombreCesta = _st.nextToken();
				periodoCesta = _st.nextToken();
				//unicamente se notifica el error
				if(_st.nextToken("\"").contains("___ERROR___")){
					listaErrores.add(new CosmeError(nombreCesta, _st.nextToken()));
				}
				break;		
	//		case ELIMINAR_CESTA:
	//			
	//			nombreCesta = _st.nextToken();	
			case CESTA:
				try{
				nombreCesta = _st.nextToken();
				while (_st.hasMoreTokens()){
					nombreVariable = _st.nextToken();
					valorVariable = _st.nextToken();
					if (!nombreVariable.equals("___ERROR___")){
						if (valorVariable.startsWith(idHexa2)){
							valorNumerico = obtenerValorNumericoIdHexa2(valorVariable);
							listaVariables.add(ItemVariable.createInstance(nombreVariable, String.valueOf(valorNumerico)));
						}
						else if(valorVariable.startsWith(idHexa1)){
							valorNumerico = obtenerValorNumericoIdHexa1(valorVariable);
							listaVariables.add(ItemVariable.createInstance(nombreVariable, String.valueOf(valorNumerico)));
						}
						else{
							listaVariables.add(ItemVariable.createInstance(nombreVariable, valorVariable));
						}
					}
					else if(valorVariable.startsWith("\"") && !valorVariable.endsWith("\"")){
						while (_st.hasMoreTokens()) {
							valorVariable = valorVariable + " " + _st.nextToken();					
							if (valorVariable.endsWith("\"")){
								break;
							}
						}
						String [] parser = valorVariable.split("'");			
						listaErrores.add(new CosmeError(parser[1], "\""+parser[2]));			
					}
				}
				}catch (Exception e) {
					System.out.println(nombreVariable);
					System.out.println(valorVariable);
					_st.nextToken();
					
				}
			break;
			
			case INSERTAR_NOM_CESTA: //a�adir una variable a una cesta concreta	
				nombreCesta = _st.nextToken();
				nombreVariable = _st.nextToken();
			break;
	//			
	//		case INSERTAR_NOMS_CESTA: //a�adir varias variables a una cesta concreta
	//			
	//			nombreCesta = _st.nextToken();
	//			
	//			while (_st.hasMoreTokens()){
	//				nombreVariable = _st.nextToken();
	//				listaVariables.add(new ItemVariable(nombreVariable));
	//			}
	//			
	//			break;
				
			//eliminar_nom_cesta
	//		
	//		case ELIMINAR_NOM_CESTA: //a�adir varias variables a una cesta concreta
	//			
	//			nombreCesta = _st.nextToken();
	//			nombreVariable = _st.nextToken();
	//			
	//			break;
				
			case PERIODO_CESTA: //modificar periodo de una cesta
				nombreCesta = _st.nextToken();
				periodoCesta = _st.nextToken();
				while (_st.hasMoreTokens()){
					nombreVariable = _st.nextToken();
					listaVariables.add(new ItemVariable(nombreVariable));
				}
				periodoCesta = _st.nextToken();
				break;
			
			case LISTA_NOMBRES_CESTA: //variables que contiene una cesta
				while (_st.hasMoreTokens()){
					listaVariables.add(new ItemVariable(_st.nextToken()));
				}
				break;
				
			case LISTA_NOMBRES:
				listaVariables.clear();
				while (_st.hasMoreTokens()){
					String variable = _st.nextToken();
					if (!variable.equals("...")) {
						listaVariables.add(new ItemVariable(variable));
					}
				}
				break;
			case IS_NUMERIC:
				nombreVariable = _st.nextToken();
				esNumerica = _st.nextToken();
				variable = new ItemVariable(nombreVariable,valorVariable);
				break;
			
			case ESCRIBIR:
				nombreVariable = _st.nextToken();
				valorVariable = _st.nextToken();
				if(_st.nextToken("\"").contains("___ERROR___")){
					listaErrores.add(new CosmeError(nombreVariable, _st.nextToken()));
				}
				break;
			default:
				throw new TelegramTypesException(_idTelegrama);
			}
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}
	}
	
	
	private Double obtenerValorNumericoIdHexa1(String valorVariable){
		
		double valorVariableNumerica;
		
		if (valorVariable.indexOf("p") != -1){ //si la "p" esta dentro de la cadena, retorna -1 si no está dentro de la cadena
			valorVariableNumerica = Double.longBitsToDouble(Long.valueOf( //longBitsToDouble --> pasar de hexadec. a double 
					valorVariable.substring(idHexa1.length() + 1), 16)); //Long.valueOf --> retorna una instancia de Long
											//ej:  __0x010330304040440 substring coge "010330304040440"
			return valorVariableNumerica;								//convierte este nº --> 010330304040440 a double
		
		}else{
				valorVariableNumerica = -1 //si no contiene la p (entonces es negativo) y multiplica el nº por -1
				* Double.longBitsToDouble(Long.valueOf(
						valorVariable.substring(idHexa1.length() + 1),16));
				
				return valorVariableNumerica;
		}	
	}
	
	private Double obtenerValorNumericoIdHexa2(String valorVariable){
		
		double valorVariableNumerica;
		
		valorVariable= valorVariable.substring(idHexa2.length());
		int signo = 1;

		if (valorVariable.charAt(0) > '7') { //charAt --> si el primer caracter de sval es > 7
			switch (valorVariable.toLowerCase().charAt(0)) {
			case '8':
				valorVariable = "0" + valorVariable.substring(1);
				break;
			case '9':
				valorVariable = "1" + valorVariable.substring(1);
				break;
			case 'a':
				valorVariable = "2" + valorVariable.substring(1);
				break;
			case 'b':
				valorVariable = "3" + valorVariable.substring(1);
				break;
			case 'c':
				valorVariable = "4" + valorVariable.substring(1);
				break;
			case 'd':
				valorVariable = "5" + valorVariable.substring(1);
				break;
			case 'e':
				valorVariable = "6" + valorVariable.substring(1);
				break;
			case 'f':
				valorVariable = "7" + valorVariable.substring(1);
				break;
			}
			signo = -1;
		}
		
		valorVariableNumerica = signo
				* Double.longBitsToDouble(Long.valueOf(valorVariable, 16));
		
		return valorVariableNumerica;		
	}
	
	public String getIdCliente() {
		return idCliente;
	}

	
	public void setIdCliente(String _idCliente) {
		this.idCliente = _idCliente;
	}

	
	public String getIdMaquina() {
		return idMaquina;
	}

	
	public void setIdMaquina(String _idMaquina) {
		this.idMaquina = _idMaquina;
	}

	
	public int getNumPeticion() {
		return numPeticion;
	}


	public String getIdTelegrama(){
		return idTelegrama;
	}

	
	public String getNombreCesta() {
		return nombreCesta;
	}

	
	public void setNombreCesta(String _nombreCesta) {
		this.nombreCesta = _nombreCesta;
	}

	
	public String getNombreVariable() {
		return nombreVariable;
	}

	
	public void setNombreVariable(String _nombreVar) {
		this.nombreVariable = _nombreVar;
	}

	
	public String getTelegramaCompleto() {
		return telegramaCompleto;
	}

	
	public void setTelegramaCompleto(String _telegramaCompleto) {
		this.telegramaCompleto = _telegramaCompleto;
	}
	
	public BigInteger getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(BigInteger _timeStamp) {
		this.timeStamp = _timeStamp;
	}

	public String getValorVariable() {
		return valorVariable;
	}

	public void setValorVariable(String _valorVariable) {
		this.valorVariable = _valorVariable;
	}

	public void setIdTelegrama(String idTelegrama) {
		this.idTelegrama = idTelegrama;
	}
	
	
	public ArrayList<ItemVariable> getListaVariables() {
		return listaVariables;
	}
	
//	public boolean isNumeric(){
//		return !this.esNumerica.equals("non");
//	}
//	public String getEsNumerica(){
//		return this.esNumerica;
//	}


	public ArrayList<CosmeError> getListaErrores() {
		return listaErrores;
	}


	public ItemVariable getVariable() {
		return variable;
	}


//
//	public boolean getHayError() {
//		return hayError;
//	}

	
	
}
