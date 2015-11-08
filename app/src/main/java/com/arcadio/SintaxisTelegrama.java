package com.arcadio;

import com.arcadio.common.AccessLevels;
import com.arcadio.common.ItemVariable;
import com.arcadio.common.NumericVariable;
import com.arcadio.common.TextVariable;
import com.arcadio.common.VariablesList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author fserna, A. Azuara 03/12/2014
 */
public class SintaxisTelegrama{

	private final int LONG_MAX_TELEGRAMA = 8000;
	private int numPeticion = 0;
	private CosmeConnector conexion;

	public SintaxisTelegrama(CosmeConnector _conexion){
		this.conexion = _conexion;
	}

	/*
	 * D  A  N  G  E  R
	 *
	 * Esto fue privado una vez...  pero mientras exista "ConexionEmcos.enviarTelegramaLibre(xx)"
	 * hay que hacerlo protegido.
	 * El método "enviarTelegramaLibre()" debería desaparecer, pero es sólo una opinión...
	 *
	 **/
	protected synchronized String getInicioTelegrama(){
		return (conexion.getIDCliente() + " " + conexion.getIDSistema() + " " + (numPeticion++) + " ");
	}

	protected synchronized StringBuffer getInicioTelegramaSB(){
		StringBuffer sb = new StringBuffer(LONG_MAX_TELEGRAMA + 500);
		sb.append(conexion.getIDCliente() + " " + conexion.getIDSistema() + " " + (numPeticion++) + " ");
		return (sb);
	}

	// insertar_nom_cesta
	public String getTelegrama_anadirNombreACesta(String _nombreCesta, String _nombreVariable){
		String txt_tlg = this.getInicioTelegrama() + "insertar_nom_cesta" + " " + _nombreCesta + " " + _nombreVariable + " ";
		//String txt_tlg = this.getInicioTelegrama()+"bi"+" "+_nombreCesta+" "+_nombreVariable+" ";

		return txt_tlg;
	}

	// insertar_nom_cesta
	public String getTelegrama_anadirNombresACesta(String _nombreCesta, Collection<String> _nombres){
		String txt_tlg = this.getInicioTelegrama() + "insertar_nom_cesta" + " " + _nombreCesta + " ";
		for(String n : _nombres){
			txt_tlg = txt_tlg + n + " ";
		}

		return txt_tlg;
	}

	// eliminar_nom_cesta
	public String getTelegrama_eliminarNombreDeCesta(String _nombreCesta, String _nombreVariable){
		String txt_tlg = this.getInicioTelegrama() + "eliminar_nom_cesta" + " " + _nombreCesta + " " + _nombreVariable + " ";
		//String txt_tlg = this.getInicioTelegrama()+"be"+" "+_nombreCesta+" "+_nombreVariable+" ";

		return txt_tlg;
	}
	// eliminar_nom_cesta

	public String getTelegrama_eliminarNombresDeCesta(String _nombreCesta, Collection<String> _nombres){
		String txt_tlg = this.getInicioTelegrama() + "eliminar_nom_cesta" + " " + _nombreCesta + " ";
		for(String n : _nombres){
			txt_tlg = txt_tlg + n + " ";
		}
		return txt_tlg;
	}

	//  crear_cesta
	public String getTelegrama_crearCesta(String _nombreCesta, Collection<String> _nombres, int _event_time, int inhibit_time){
		String txt_tlg = this.getInicioTelegrama() + "crear_cesta" + " " + _nombreCesta + " " + _event_time + " " + inhibit_time;

		if(_nombres != null){
			txt_tlg = txt_tlg + " ";

			for(String n : _nombres){
				txt_tlg = txt_tlg + n + " ";
			}
		}

		return txt_tlg;
	}

	public String getTelegrama_escribir(String nombreVariable, Double valor){
		return this.getInicioTelegrama() + "escribir " + nombreVariable + " " + valor + " ";
	}

	public String getTelegrama_escribir(String nombreVariable, String valor){
		return this.getInicioTelegrama() + "escribir " + nombreVariable + " " + String.valueOf('"') + valor + String.valueOf('"') + " ";
	}

	/**
	 * Permite hacer una escritura multivariable sin bloqueo
	 */
	public Collection<StringBuffer> getTelegrama_escribir(VariablesList _lv){
		Collection<StringBuffer> telegramas = new ArrayList();
		StringBuffer tlg = this.getInicioTelegramaSB();
		tlg.append("escribir ");


		for(ItemVariable iv : _lv.getList()){
			if(tlg.length() > LONG_MAX_TELEGRAMA){
				telegramas.add(tlg);
				tlg = this.getInicioTelegramaSB();
				tlg.append("escribir ");
			}
			tlg.append(iv.getName() + " ");
			if(iv instanceof TextVariable){
				//                tlg=tlg+ String.valueOf('"') +iv.getValor_txt()+String.valueOf('"')+" ";
				tlg.append(String.valueOf('"') + ((TextVariable) iv).getValue() + String.valueOf('"') + " ");
			}
			if(iv instanceof NumericVariable){
				//                tlg=tlg+iv.getValue()+" ";
				tlg.append(((NumericVariable) iv).getValue() + " ");
			}
		}// for

		if(tlg.length() <= LONG_MAX_TELEGRAMA){
			telegramas.add(tlg);
		}
		return telegramas;

        /*

        String tlg = this.getInicioTelegrama()+"escribir ";

        for (ItemVariable iv: _lv.getList()){
        tlg = tlg + iv.getNameElement()+" ";
        if (iv.isText()){
        tlg=tlg+ String.valueOf('"') +iv.getValor_txt()+String.valueOf('"')+" ";
        }
        if (iv.isNumeric()){
        tlg=tlg+iv.getValue()+" ";
        }
        }// for
        return tlg;
         */
	}

	public String obtTelegramaEscribir(VariablesList _lv){
		StringBuffer stb = new StringBuffer(this.getInicioTelegrama() + "escribir ");

		for(ItemVariable iv : _lv.getList()){
			stb.append(iv.getName() + " ");
			if(iv instanceof TextVariable){
				stb.append('"' + ((TextVariable) iv).getValue() + '"' + " ");
			}
			if(iv instanceof NumericVariable){
				stb.append(((NumericVariable) iv).getValue() + " ");
			}
		}// for
		return stb.toString();
	}

	public String obtTelegramaEscribir(ArrayList<ItemVariable> _lv, int first, int dim){
		StringBuffer stb = new StringBuffer(this.getInicioTelegrama() + "escribir ");

		ItemVariable iv;
		if(first + dim > _lv.size()){
			dim = _lv.size() - first;
		}
		for(int i = first; i < (first + dim); i++){
			iv = _lv.get(i);

			stb.append(iv.getName() + " ");
			if(iv instanceof TextVariable){
				stb.append('"' + ((TextVariable) iv).getValue() + '"' + " ");
			}
			if(iv instanceof NumericVariable){
				stb.append(((NumericVariable) iv).getValue() + " ");
			}
		}// for
		return stb.toString();
	}

	public String getTelegrama_escribir_bloqueo(String nombreVariable, Double valor){
		return this.getInicioTelegrama() + "escribir_bloqueo " + nombreVariable + " " + valor + " ";
	}

	public String getTelegrama_escribir_bloqueo(String nombreVariable, String valor){
		return this.getInicioTelegrama() + "escribir_bloqueo " + nombreVariable + " " + String.valueOf('"') + valor + String.valueOf('"') + " ";
	}

	public Collection<StringBuffer> getTelegrama_escribir_bloqueo(VariablesList _lv){
		Collection<StringBuffer> telegramas = new ArrayList();
		StringBuffer tlg = this.getInicioTelegramaSB();
		tlg.append("escribir_bloqueo ");


		for(ItemVariable iv : _lv.getList()){
			if(tlg.length() > LONG_MAX_TELEGRAMA){
				telegramas.add(tlg);
				tlg = this.getInicioTelegramaSB();
				tlg.append("escribir_bloqueo ");
			}
			tlg.append(iv.getName() + " ");
			if(iv instanceof NumericVariable){
				//                tlg=tlg+iv.getValue()+" ";
				tlg.append(((NumericVariable) iv).getValue() + " ");
			}else if(iv instanceof TextVariable){
				tlg.append(String.valueOf('"') + ((TextVariable) iv).getValue() + String.valueOf('"') + " ");
			}
		}// for

		if(tlg.length() <= LONG_MAX_TELEGRAMA){
			telegramas.add(tlg);
		}

		return telegramas;
	}

	public String getTelegrama_listaCestas(){
		return getInicioTelegrama() + "lista_nombres_tipo CESTA ";
	}

	private String getTelegrama_listaNombresDeCesta(String _nombreCesta){
		//lista_nombres_cesta <nombre>
		return getInicioTelegrama() + "lista_nombres_cesta " + _nombreCesta;
	}

	public String getTelegrama_leerCesta(String _nombreCesta){
		return getInicioTelegrama() + "leer_cesta" + " " + _nombreCesta + " ";

	}

	public String getTelegrama_periodoCesta(String nombreCesta, int nuevoRefresco){
		return getInicioTelegrama() + "periodo_cesta " + nombreCesta + " " + nuevoRefresco;
	}

	public String getTelegrama_eliminarCesta(String _nombreCesta){
		//enviamos el telegrama para que la pasarela baja deje de enviarnos telegramas de esta cesta
		//        return getInicioTelegrama() +"periodo_cesta "+ _nombreCesta+ " "+0;
		return getInicioTelegrama() + "eliminar_cesta " + _nombreCesta;
	}

	public String getTelegrama_pedirNombres(){

		return getInicioTelegrama() + "lista_nombres ";
	}

	public String getTelegrama_pedirNombresTipoHabilitados(String tipo){
		return getInicioTelegrama() + "lista_nombres_tipo_habilitados" + " " + tipo + " ";
	}

	public String getTelegrama_pedirSublistaNombres(int _primerNombre){
		return getInicioTelegrama() + "sublista_nombres " + _primerNombre + " ";
	}

	public String getTelegrama_pedirTipos(){

		return getInicioTelegrama() + "lista_tipos ";
	}

	public String getTelegrama_pedirClases(){

		return getTelegrama_pedirNombresDeTipo(OyenteTelegrama.IDENTIFICADOR_DE_CLASE);
	}

	public String getTelegrama_pedirNombresDeTipo(String _queTipo){

		return getInicioTelegrama() + "lista_nombres_tipo " + _queTipo + " ";
	}

	/*
	public String getTelegrama_pedirSubListaNombresDeTipo (String _queTipo, int _primerNombre) {

	return getInicioTelegrama() +"sublista_nombres_tipo "+ _queTipo + " "+ _primerNombre +" ";
	}

	 */
	public String getTelegrama_cambiarNombre(String _nombreCesta, String _nuevoNombre){

		return getInicioTelegrama() + "nombre_cesta " + _nombreCesta + " " + _nuevoNombre + " ";
	}

	public String getTelegrama_leer(VariablesList _listaVariables){
		String tlg = getInicioTelegrama() + "leer ";
		for(ItemVariable v : _listaVariables.getList()){
			tlg = tlg + v.getName() + " ";
		}

		return tlg;
	}

	public String getTelegrama_ping(VariablesList _listaVariables){
		String tlg = getInicioTelegrama() + "ping ";
		for(ItemVariable v : _listaVariables.getList()){
			tlg = tlg + v.getName() + " ";
		}

		return tlg;
	}

	public String getTelegrama_leer_bloqueo(VariablesList _listaVariables){
		String tlg = getInicioTelegrama() + "leer_bloqueo ";
		for(ItemVariable v : _listaVariables.getList()){
			tlg = tlg + v.getName() + " ";
		}

		return tlg;
	}

	public String getTelegrama_crearInstancia(String _nombreClase, String _nombreInstancia, int _ordenEjecucion, boolean _habilitacion){
		int habilitacionAlCargar = 0;
		if(_habilitacion){
			habilitacionAlCargar = 1;
		}
		return getInicioTelegrama() + "crear_instancia " + _nombreClase + " " + _nombreInstancia + " " + _ordenEjecucion + " " + habilitacionAlCargar + " ";
	}

	/**
	 * Permite crear un nuevo muestreado
	 *
	 * @param _nombreMuestreador        Es el nombre aleatorio de no más de 6 caracteres. Único.
	 * @param _nombreVariableMuestreada
	 * @param _modo                     Puede contener 2 posibles valores: "single" o "continuous".
	 * @param _nombreVariableDisparo
	 * @return
	 */
	public String getTlg__crear_muestreador(String _nombreMuestreador, String _nombreVariableMuestreada, String _modo, int _numMuestrasATomar, String _nombreVariableDisparo){
		if(_nombreMuestreador.length() > 6){
			_nombreMuestreador = _nombreMuestreador.substring(0, 6);
		}
		if((!_modo.equals("single") && (!_modo.equals("continuous")))){
			_modo = "single";
		}
		String tlg = getInicioTelegrama() + "crear_muestreador " + _nombreMuestreador + " " + _modo + " " + _numMuestrasATomar + " " + _nombreVariableMuestreada + " " + _nombreVariableDisparo;

		return tlg;
	}

	public String getTlg__crear_muestreador_continuous(String _nombreMuestreador, String _nombreVariableMuestreada, int _numMuestrasATomar){
		if(_nombreMuestreador.length() > 6){
			_nombreMuestreador = _nombreMuestreador.substring(0, 6);
		}

		String tlg = getInicioTelegrama() + "crear_muestreador " + _nombreMuestreador + " continuous " + _numMuestrasATomar + " " + _nombreVariableMuestreada;

		return tlg;
	}

	public String getTlg__crear_muestreador_single(String _nombreMuestreador, String _nombreVariableMuestreada, int _numMuestrasATomar, String _nombreVariableDisparo){
		if(_nombreMuestreador.length() > 6){
			_nombreMuestreador = _nombreMuestreador.substring(0, 6);
		}

		String tlg = getInicioTelegrama() + "crear_muestreador " + _nombreMuestreador + " single " + _numMuestrasATomar + " " + _nombreVariableMuestreada + " " + _nombreVariableDisparo;

		return tlg;
	}

	public String getTlg__habilitar_muestreador(String _nombreMuestreador){
		List<String> _nombreMuestreadores = new ArrayList<String>();
		_nombreMuestreadores.add(_nombreMuestreador);

		return getTlg__habilitar_muestreador(_nombreMuestreadores);
	}

	public String getTlg__habilitar_muestreador(List<String> _nombreMuestreadores){
		StringBuilder sb = new StringBuilder();
		sb.append(getInicioTelegrama());

		sb.append("habilitar_muestreador ");

		for(int i = 0; i < _nombreMuestreadores.size(); i++){
			sb.append(_nombreMuestreadores.get(i));

			if((i + 1) < _nombreMuestreadores.size()){
				sb.append(" ");
			}
		}

		return sb.toString();
	}

	public String getTlg__deshabilitar_muestreador(String _nombreMuestreador){
		List<String> _nombreMuestreadores = new ArrayList<String>();
		_nombreMuestreadores.add(_nombreMuestreador);

		return getTlg__deshabilitar_muestreador(_nombreMuestreadores);
	}

	public String getTlg__deshabilitar_muestreador(List<String> _nombreMuestreadores){
		StringBuilder sb = new StringBuilder();
		sb.append(getInicioTelegrama());

		sb.append("deshabilitar_muestreador ");

		for(int i = 0; i < _nombreMuestreadores.size(); i++){
			sb.append(_nombreMuestreadores.get(i));

			if((i + 1) < _nombreMuestreadores.size()){
				sb.append(" ");
			}
		}

		return sb.toString();
	}

	/*
	public String getTlg__getDatosMuestreador (String _nombreMuestreador, int _numMuestrasPorChunk){
	String tlg = getInicioTelegrama()+"get_datos_muestreador "+_nombreMuestreador+" "+_numMuestrasPorChunk;

	return tlg;
	}
	 */
	public String getTlg__eliminarMuestreador(String _nombreMuestreador){
		String tlg = getInicioTelegrama() + "eliminar_muestreador " + _nombreMuestreador;

		return tlg;
	}

	/**
	 * Telegrama que permite obtener el nombre de la clase a la que pertenece la instancia que se indique.
	 */
	public String getTlg__tipoNombre(String _nombre){
		String tlg = getInicioTelegrama() + "tipo_nombre " + _nombre;

		return tlg;
	}

	/**
	 * Telegrama que permite solicitar la lista de nombres configurables.
	 */
	public String getTlg__listaNombresConfigurables(){
		String tlg = getInicioTelegrama() + "lista_nombres_configurables ";

		return tlg;
	}

	/**
	 * Telegrama que permite solicitar la lista de nombres configurables.
	 */
	public String getTlg__listaNombresConfigurablesReservados(){
		String tlg = getInicioTelegrama() + "lista_nombres_configurables_reservados ";

		return tlg;
	}

	public String getTlg__solicitarFicheroTexto(String _nombreInstancia, String _pathFichero){
		String tlg = getInicioTelegrama() + "solicitar_fichero_texto " + _nombreInstancia + " " + _pathFichero;

		return tlg;
	}

	public String getTlg__enviarFicheroEMC(String _contenidoFicheroEMC){
		String tlg = getInicioTelegrama() + "enviar_fichero_EMC " + _contenidoFicheroEMC;

		return tlg;
	}

	public String getTlg__modificarNivelAcceso(AccessLevels _nuevoNivelAcceso, String _passwd){
		String tlg = getInicioTelegrama() + "modificar_nivel_acceso " + _nuevoNivelAcceso.toString() + " " + _passwd;

		return tlg;
	}

    /*

    public String getTelegrama_iniciarPropiedad (String _nombrePropiedad, String _valor) {
    return getInicioTelegrama() +"iniciar_propiedad "+_nombrePropiedad+" "+_valor+" ";
    }
    public String getTelegrama_iniciarPropiedad (String _nombrePropiedad, double _valor) {
    return getInicioTelegrama() +"iniciar_propiedad "+_nombrePropiedad+" "+_valor+" ";
    }
    public String getTelegrama_iniciarPropiedad (String _nombrePropiedad, int _valor) {
    return getInicioTelegrama() +"iniciar_propiedad "+_nombrePropiedad+" "+_valor+" ";
    }
     */

	/**
	 *
	 */
	public String getTlg__listaNombresNivel(String _prefijo){
		if(!_prefijo.endsWith(".")){
			_prefijo = _prefijo + ".";
		}
		String tlg = getInicioTelegrama() + "lista_nombres_nivel " + _prefijo;

		return tlg;
	}

	/**
	 * Telegrama que permite saber si una serie de nombres son de tipo entero, real, o no numérico...
	 *
	 * @param _nombres Debe contener un string con la lista de nombres separados por espacios.
	 */
	public String getTlg__isNumeric(String _nombres){

		String tlg = getInicioTelegrama() + "is_numeric " + _nombres;

		return tlg;
	}


	public String getTlg__set_connection(String input, String output){

		String tlg = getInicioTelegrama() + "set_connection" + " " + input + " " + output;

		return tlg;
	}
}