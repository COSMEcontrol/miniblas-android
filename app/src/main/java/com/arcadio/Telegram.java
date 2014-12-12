package com.arcadio;

import com.arcadio.api.v1.service.CosmeStates;
import com.arcadio.common.ItemVariable;
import com.arcadio.common.NamesList;
import com.arcadio.common.NumericVariable;
import com.arcadio.common.TextVariable;
import com.arcadio.common.VariablesList;

import java.io.IOException;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * @author fserna, Alberto Azuara García 03/12/2014
 */

public class Telegram implements Serializable{

	private final String CONSTANTE_BLOQUEO = "sync";
	private final String CONSTANTE_NOBLOQUEO = "no_sync";
	private final String ESCRIBIR = "escribir";
	private final String ESCRIBIR_BLOQUEO = "escribir_bloqueo";
	private final String LEER = "leer";
	private final String LEER_BLOQUEO = "leer_bloqueo";
	private final String STATUS = "status";
	private final String LOG = "log";
	private final String INFO_VARIABLE = "info_nombre";
	private final String LISTA_NOMBRES = "lista_nombres";
	private final String NOMBRE_CESTA = "nombre_cesta";
	private final String CESTA = "cesta";
	private final String LISTA_NOMBRES_TIPO = "lista_nombres_tipo";

	// private final int NUM_SIN_VARIABLES = 4; //número de tokens en peticiones
	//  private final int NUM_SIN_VARIABLES_INFO = 5; //número de tokens en peticiones en info_variable

	private String telegrama; // contiene el telegrama completo
	private TelegramTypes idTelegrama = TelegramTypes.NULL;  // contiene un código asociado a cada
	//tipo de telegrama, definidos en TiposTelegrama
	private String id_cliente;
	private String id_sistema;
	private long timestamp;
	private int num_peticion;
	private boolean tlgSincrono = false; // indica si el telegrama es CON (true) o SIN (false) bloqueo
	private String comando;
	private VariablesList listaVariables = new VariablesList();
	private VariablesList listaVariablesDeTipo = new VariablesList();
	private NamesList listaTiposNombres = new NamesList();  // se usa para el tlg "is_numeric". Contiene una serie de parejas: nombre/ int|real|non
	//private ArrayList cestasLeidas = new ArrayList();
	private boolean valido = true;
	private boolean esPeticion = false; //si false, telegrama respuesta
	private String nombreCesta = "";
	private String nombreInstancia = "???";
	private String nombreClase = "???";
	private String txtStatus = "";
	private String nombreTipo = "";
	private boolean eco = false;
	private CosmeStates evento;
	private String prefijo;

	//  private boolean signo_menos;

	private ArrayList<String> mensajesError = new ArrayList(); // contiene los mensajes de error que pueda contener este telegrama


	private String tipoVariable;

	private String nombreMuestreador;
	private int numMuestrasMuestreador = 0;
	private String pathFicheroTexto = "";
	private String contenidoFicheroTexto = "";
	private int indexChunk = -1;
	private ArrayList<Double> valoresMuestreador = new ArrayList();
	private TelegramaTokenizer tt;
	//    private int indexPrimerNombre = 1;  // en el tlg "sublista_nombres", el primer
	// valor es un entero que indica que índice tiene el primer nombre recibido.

	private String MARCA_FIN_LISTA_NOMBRES = "...";  // en el telegrma "lista_nombres",
	// un nombre con este valor indica que
	// todavía faltan mas por enviar

	private boolean listaNombresCompleta = true;  // indica si ya se han recibido  todos
	// los nombres de los tlgs "lista_nombres"
	// se pondrá a false cuando se reciban
	// tlgs de ese tipo con un nombre
	// que sea: "..."

	private boolean listaTiposCompleta = true;  // indica si ya se han recibido  todos
	// los nombres de los tlgs "lista_tipos"
	// se pondrá a false cuando se reciban
	// tlgs de ese tipo con un nombre
	// que sea: "..."

	private boolean listaNombresTipoCompleta = true;  // indica si ya se han recibido  todos
	// los nombres de los tlgs "lista_nombresTipo"
	// se pondrá a false cuando se reciban
	// tlgs de ese tipo con un nombre
	// que sea: "..."


	/** */
	public Telegram(String el_telegrama_completo){

		this.idTelegrama = TelegramTypes.ERROR; // solución neurótica preventiva

		this.telegrama = this.extraerErrores(el_telegrama_completo);

		this.telegrama = el_telegrama_completo;
		this.tt = new TelegramaTokenizer(telegrama);

		if(tt != null){
			//identificación de tokens
			tt.nextToken();
			this.id_cliente = tt.getString();
			tt.nextToken();
			this.id_sistema = tt.getString();

			tt.nextToken();
			this.num_peticion = tt.getInt();

			if(tt.nextToken() == TelegramaTokenizer.NUMBER){
				this.timestamp = tt.getInt();
				this.esPeticion = false;

				tt.nextToken();
			}else{
				this.esPeticion = true;
			}

			this.comando = tt.getString();  // este token puede ser la indicación sync/no_sync, o el comando (en versiones antiguas)

			if((this.comando.equals(CONSTANTE_BLOQUEO)) || (this.comando.equals(CONSTANTE_NOBLOQUEO))){
				if(this.comando.equals(CONSTANTE_BLOQUEO)){
					this.tlgSincrono = true;
				}else{
					this.tlgSincrono = false;
				}

				tt.nextToken();
				if(tt != null){
					this.comando = tt.getString();
				}
			}else{
				// el telegrama no lleva indicación sync||no_sync (por compatibilida pre  31-8-2010)
				this.tlgSincrono = false;
			}

        /*
		String aux = telegramaTroceado.nextToken();

       //comando o respuesta (las respuestas contienen un timestamp)
        try{
            this.timestamp = Double.valueOf(aux).longValue();
            this.comando = telegramaTroceado.nextToken();

        }catch(java.lang.NumberFormatException e){
            //es comando hacia el servidor o maquina ms-dos sin timestamp
            this.esPeticion = true;
            this.comando = aux;
        }
        */


			if(this.comando.equals(CESTA)){
				this.idTelegrama = TelegramTypes.CESTA;
				this.tratarPeticionCesta();


			}else if(this.comando.equals(ESCRIBIR)){
				this.idTelegrama = TelegramTypes.ESCRIBIR;

			}else if(this.comando.equals(ESCRIBIR_BLOQUEO)){
				this.idTelegrama = TelegramTypes.ESCRIBIR_BLOQUEO;


			}else if(this.comando.compareTo(LEER) == 0){
				this.idTelegrama = TelegramTypes.LEER;
				this.tratarPeticionLeer();

			}else if(this.comando.compareTo("ping") == 0){
				this.idTelegrama = TelegramTypes.PING;
				this.tratarPeticionLeer();
			}else if(this.comando.compareTo(LEER_BLOQUEO) == 0){
				this.idTelegrama = TelegramTypes.LEER_BLOQUEO;
				this.tratarPeticionLeerSincronizado();
			}else if(this.comando.compareTo("is_numeric") == 0){
				this.idTelegrama = TelegramTypes.IS_NUMERIC;

				while(tt.nextToken() != TelegramaTokenizer.EOF){
					String nombre = tt.getString();
					if(tt.nextToken() != TelegramaTokenizer.EOF){
						this.listaTiposNombres.add(nombre, tt.getString());
					}
				} // while
			}else if(this.comando.compareTo(LOG) == 0){
				this.idTelegrama = TelegramTypes.LOG;
				this.tratarPeticionLog();
			}else if(this.comando.compareTo(STATUS) == 0){
				this.idTelegrama = TelegramTypes.STATUS;
				this.tratarPeticionStatus();
			}else if(this.comando.compareTo(INFO_VARIABLE) == 0){
				this.idTelegrama = TelegramTypes.INFO_VARIABLE;
				//this.tratarPeticionInfoVariable();
			}else if(this.comando.equals(NOMBRE_CESTA)){
				this.idTelegrama = TelegramTypes.NOMBRE_CESTA;
				//this.tratarPeticionNombreCesta();
			}else if(this.comando.equals("periodo_cesta")){
				this.idTelegrama = TelegramTypes.PERIODO_CESTA;
			}else if(this.comando.equals(LISTA_NOMBRES)){
				this.idTelegrama = TelegramTypes.LISTA_NOMBRES;
				this.tratarPeticionListaNombres();
			}else if(this.comando.equals("lista_nombres_nivel")){
				this.idTelegrama = TelegramTypes.LISTA_NOMBRES_NIVEL;
				this.tratarPeticionListaNombresNivel();
			}else if(this.comando.equals("lista_tipos")){
				this.idTelegrama = TelegramTypes.LISTA_TIPOS;
				this.tratarPeticionTipos();
			}else if(this.comando.equals("insertar_nom_cesta")){
				//if(this.comando.equals("bi")){
				this.idTelegrama = TelegramTypes.ECHO;
			}else if(this.comando.equals("eliminar_nom_cesta")){
				//if(this.comando.equals("be")){
				this.idTelegrama = TelegramTypes.ECHO;
			}else if(this.comando.equals("crear_cesta")){
				this.idTelegrama = TelegramTypes.ECHO;
			}else if(this.comando.equals("eliminar_cesta")){
				this.idTelegrama = TelegramTypes.ECHO;
			}else


/*
		if (this.comando.equals("sublista_nombres")){
            this.idTelegrama = TelegramTypes.SUBLISTA_NOMBRES;
            this.tratarPeticionSubListaNombres();
        }else
  */
				if(this.comando.equals(LISTA_NOMBRES_TIPO)){
					this.idTelegrama = TelegramTypes.LISTA_NOMBRES_TIPO;
					this.tratarPeticionNombresTipos();
				}else
/*
		if (this.comando.equals("sublista_nombres_tipo")){
            this.idTelegrama = TelegramTypes.SUBLISTA_NOMBRES_TIPO;
            this.tratarPeticionSubListaNombresTipos();
        }else
   */


					if(this.comando.equals("lista_nombres_cesta")){
						this.idTelegrama = TelegramTypes.LISTA_NOMBRES_CESTA;
						this.tratarPeticionCestaCompleta();
					}else


						// es el eco del tlg
						if(this.comando.equals("crear_muestreador")){
							this.idTelegrama = TelegramTypes.CREAR_MUESTREADOR;

							if(tt.nextToken() != TelegramaTokenizer.EOF){
								this.nombreMuestreador = tt.getString();
							}
						}else if(this.comando.equals("muestreando")){
							this.idTelegrama = TelegramTypes.MUESTREANDO;

							if(tt.nextToken() != TelegramaTokenizer.EOF){
								this.nombreMuestreador = tt.getString();

								tt.nextToken();
								this.numMuestrasMuestreador = tt.getInt();
							}
						}else
        /*
        if (this.comando.equals("muestreo_terminado")){
            this.idTelegrama = TelegramTypes.MUESTREO_TERMINADO;

            if (tt.nextToken() != TelegramaTokenizer.EOF){
                  this.nombreMuestreador = tt.getString();
            }

            if (tt.nextToken() != TelegramaTokenizer.EOF){
                  this.numMuestrasMuestreador = tt.getInt();
            }
        }else
        */
							if(this.comando.equals("get_datos_muestreador")){
								this.idTelegrama = TelegramTypes.GET_DATOS_MUESTREADOR;
								if(tt.nextToken() != TelegramaTokenizer.EOF){
									this.nombreMuestreador = tt.getString();
								}
								if(tt.nextToken() != TelegramaTokenizer.EOF){
									this.indexChunk = tt.getInt();
								}
								while(tt.nextToken() != TelegramaTokenizer.EOF){
									double valor = tt.getDouble();
									this.valoresMuestreador.add(valor);
								}
							}else if(this.comando.equals("habilitar_muestreador")){
								this.idTelegrama = TelegramTypes.HABILITAR_MUESTREADOR;
							}else if(this.comando.equals("deshabilitar_muestreador")){
								this.idTelegrama = TelegramTypes.DESHABILITAR_MUESTREADOR;
							}else if(this.comando.equals("eliminar_muestreador")){
								this.idTelegrama = TelegramTypes.ELIMINAR_MUESTREADOR;
							}else


							/**
							 * El telegrama recibido es de tipo "tipo_nombre".
							 */
								if(this.comando.equals("tipo_nombre")){
									this.idTelegrama = TelegramTypes.TIPO_NOMBRE;
									this.nombreClase = "???";
									if(tt.nextToken() != TelegramaTokenizer.EOF){
										this.nombreInstancia = tt.getString();
										if(tt.nextToken() != TelegramaTokenizer.EOF){
											this.nombreClase = tt.getString();
										}
									}
								}else if(this.comando.equals("lista_nombres_configurables")){
									this.idTelegrama = TelegramTypes.LISTA_NOMBRES_CONFIGURABLES;
								}else


            /*if (this.comando.equals("insertar_nom_cesta")){
            this.idTelegrama = TelegramTypes.LISTA_NOMBRES_CONFIGURABLES;
        }else */



									if(this.comando.equals("lista_nombres_configurables_reservados")){
										this.idTelegrama = TelegramTypes.LISTA_NOMBRES_CONFIGURABLES_RESERVADOS;
									}else


										// SOLICITAR_FICHERO_TEXTO
										if(this.comando.equals("solicitar_fichero_texto")){
											this.idTelegrama = TelegramTypes.SOLICITAR_FICHERO_TEXTO;

											if(tt.nextToken() != TelegramaTokenizer.EOF){
												this.nombreInstancia = tt.getString();
											}
											if(tt.nextToken() != TelegramaTokenizer.EOF){
												this.pathFicheroTexto = tt.getString();
											}

											if(!this.esPeticion){
												int i = el_telegrama_completo.indexOf(this.pathFicheroTexto);

												if(i != -1){
													int longitud = this.pathFicheroTexto.length();
													int offset = i + longitud + 1;
													System.out.println("offset: " + offset + " (" + el_telegrama_completo.length() + ")");
													this.contenidoFicheroTexto = el_telegrama_completo.substring(offset);
												}
											}


											/// NOTIFICAR_EVENTO
										}
			if(this.comando.equals("notificar_evento")){
				this.idTelegrama = TelegramTypes.NOTIFICAR_EVENTO;

				String txt_evento;

				if(tt.nextToken() != TelegramaTokenizer.EOF){
					txt_evento = tt.getString();
					evento = CosmeStates.valueOf(txt_evento);
				}
			}else if(this.comando.equals("project_stop")){
				this.idTelegrama = TelegramTypes.PROJECT_STOP;
			}


		} // if tt!=null

	} // Constructor

	/**
	 *
	 */
	public String getTxt_Telegrama(){
		return telegrama;
	}

	/**
	 *
	 */
	public VariablesList getListaVariables(){
		// your code here
		return listaVariables;
	}

	public NamesList getListaTiposNombres(){
		return this.listaTiposNombres;
	}


	/**
	 *
	 */
	public String getIDCliente(){
		return id_cliente;
	}

	/**
	 *
	 */
	public String getIDSistema(){
		return id_sistema;
	}

	/**
	 *
	 */
	public int getNumPeticion(){
		return num_peticion;
	}

	/**
	 *
	 */
	public String getPeticion(){
		return comando;
	}

	public String getNombreCesta(){
		return this.nombreCesta;
	}

	/**
	 *
	 */
	public String toString(){
		return this.telegrama;
	}

	/**
	 *
	 */
	public String leerTodo(){
		String s = id_cliente + "/" + id_sistema + "/" + num_peticion + "/" + comando + "//";
		for(ItemVariable v : listaVariables.getList()){
			s = s + v.toString() + "//";
		}
		return s;
	}


	/**
	 * Devuvleve la variable cuyo nombre se indica
	 */
	public ItemVariable getVariable(String _variableABuscar){
		ItemVariable v = (ItemVariable) this.listaVariables.getVariable(_variableABuscar);

		return v;
	}

	/**
	 *
	 */
	public boolean isValido(){
		return valido;
	}

	/**
	 * Procesa los telegramas LEER
	 *
	 * @return Rellena la variable "listaVariables" con los nombres y valores recibidos en el telegrama.
	 */
	public void tratarPeticionLeer(){

		while(tt.nextToken() != TelegramaTokenizer.EOF){
			String nombre = tt.getString();
			ItemVariable iv = null;

			if(tt.nextToken() != TelegramaTokenizer.EOF){
				if(tt.getType() == TelegramaTokenizer.NUMBER){
					double v = tt.getDouble();
					iv = new NumericVariable(nombre, v);
				}else{

					iv = new TextVariable(nombre, tt.getString());
				}
				listaVariables.add(iv);


			} // if 2º token de la pareja
		} // while


	} // tratarPeticionLeer


	/**
	 * Procesa los telegramas LEER_SINCRONIZADO
	 *
	 * @return Rellena la variable "listaVariables" con los nombres y valores recibidos en el telegrama.
	 */
	public void tratarPeticionLeerSincronizado(){

		this.tratarPeticionLeer();

		// Desbloquear XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

	} // tratarPeticionLeerSincronziado

	/**
	 *
	 */
	public void tratarPeticionEscribir(){

    /*    if(this.esPeticion()){
            //tratamos petici?n

//            while(telegramaTroceado.hasMoreTokens()){
//                String var = telegramaTroceado.nextToken();
//                String valor = telegramaTroceado.nextToken();
//
//                if(valor.substring(0,1).compareTo("_")==0){
//                    //hexadecimal
//                    lista_parametros.anadir(var,((Double)(Double.longBitsToDouble
//                            (Long.valueOf(valor.substring(4),16)))).doubleValue());
//
//                }else{
//                    //decimal
//                    try{
//                        double valorDouble = Double.valueOf(valor).doubleValue();
//                        lista_parametros.anadir
//                                (var,valorDouble);
//                    }catch(java.lang.NumberFormatException e){
//                        lista_parametros.anadir(var,valor);
//                    }
//                }
//            }

        }else{
            //tratamos respuesta
            listaVariables.anadir
                    (new ItemVariable
                    ("escribir",this.reconstruirMensaje(telegramaTroceado)));
        }*/
	}

	/**
	 *
	 */
	public void tratarPeticionLog(){
	}

	/**
	 *
	 */
	public void tratarPeticionStatus(){
		if(!this.esPeticion){
			if(tt.nextToken() != TelegramaTokenizer.TEXT){
				this.txtStatus = tt.getString();

			}

		}
	}

	/**
	 *
	 */
	public void tratarPeticionListaVariables(){
		if(!this.esPeticion()){
			//tratamos respuesta
			while(tt.nextToken() != TelegramaTokenizer.EOF){
				String s = tt.getString();
				listaVariables.add(s);
			}
		}
	}


	/**
	 *
	 */
	public void tratarPeticionListaNombres(){
		if(!this.esPeticion()){
			//tratamos respuesta

			while(tt.nextToken() != TelegramaTokenizer.EOF){
				String s = tt.getString();

				if(s.equals(this.MARCA_FIN_LISTA_NOMBRES)){
					this.listaNombresCompleta = false;
				}else{
					listaVariables.add(s);
				}
			}

		}
	}


	/**
	 *
	 */
	public void tratarPeticionListaNombresNivel(){
		if(!this.esPeticion()){
			//tratamos respuesta
			tt.nextToken();
			this.prefijo = tt.getString();

			while(tt.nextToken() != TelegramaTokenizer.EOF){
				String s = tt.getString();

				if(s.equals(this.MARCA_FIN_LISTA_NOMBRES)){
					this.listaNombresCompleta = false;
				}else{
					if(!prefijo.equals(".")){
						s = prefijo + s;
					}
					listaVariables.add(s);
				}
			}

		}
	}

/*
    public void tratarPeticionSubListaNombres(){
        //if(!this.esPeticion()){
            //tratamos respuesta
            if (tt.nextToken() != TelegramaTokenizer.EOF){
                this.indexPrimerNombre = Integer.valueOf(tt.getString()).intValue();
            }

            while (tt.nextToken() != TelegramaTokenizer.EOF){
                String s = tt.getString();
                listaVariables.anadir(s);
            }
        //}
    }
 * */


	/**
	 *
	 */
	public void tratarPeticionInfoVariable(){
	}

	/**
	 *  Reconstruye un mensaje que fue troceado en Tokens. La reconstrucci?n es
	 *  hasta el final del StringTokenizer.
	 */
/*    public String reconstruirMensaje(StringTokenizer st){
        String mensaje = "";
        while (telegramaTroceado.hasMoreTokens()) {
            //Reconstru?mos el mensaje de log
            mensaje = mensaje + " " + telegramaTroceado.nextToken();
        }

        return mensaje;
    }*/

	/**
	 *
	 */
	public boolean esPeticion(){
		return this.esPeticion;
	}

	/**
	 * elimina del telegrama informacion de error para aprovechar
	 * información útil
	 *//*
    private String depurarErrores(String s) {
        String cadenaDepurada = "";
        String cadena ="";
        StringTokenizer st = new StringTokenizer(s);
        while(st.hasMoreTokens()){
            cadena=st.nextToken();
            if(cadena.compareToIgnoreCase("___ERROR___")==0){
                while(st.hasMoreTokens()){
                    st.nextToken();
                }
            }else{
                cadenaDepurada = cadenaDepurada + " " + cadena;
            }
        }
        return cadenaDepurada.substring(1);
    }*/

  /*  private void tratarPeticionNombreCesta() {

    }
    */


    /*
    private void rellenarListaVariables (StringTokenizer st){

        while(st.hasMoreTokens()){
            String nombre = st.nextToken();
            String valor = "";
            if (st.hasMoreTokens()){
                valor = st.nextToken();


                //analizar tipo de valor, hexadecimal, decimal o texto
                if(valor.startsWith("__Ox")){
                    //respuesta hexadecimal

                    if(valor.indexOf("n") != -1)
                        signo_menos = true;
                    else signo_menos = false;

                    String ss = valor.substring(5);
                    try {
                        double valorFiltrado =Double.longBitsToDouble(Long.valueOf(ss,16));
                        if (signo_menos)
                            valorFiltrado = valorFiltrado*(-1);
                        //c.addVariable(new ItemVariable(variable,valorFiltrado),false);
                        listaVariables.anadir(new ItemVariable (nombre, valorFiltrado));

                    } catch (NumberFormatException e) {
                        valor = "Formato double incorrecto";
                    }

                }else if(valor.startsWith(String.valueOf('"'))){
                    //respuesta texto, una o más palabras
                    String cadena = "";
                    while(!valor.endsWith(String.valueOf('"'))){
                        cadena = cadena+" "+valor;
                        valor = st.nextToken();
                    }
                    cadena = cadena+" "+valor;
                    StringTokenizer stValor = new StringTokenizer(cadena,String.valueOf('"'));
                    stValor.nextToken();
                    if(stValor.hasMoreTokens()){
                        valor = stValor.nextToken();
                        if(valor==null){
                            //c.addVariable(new ItemVariable(variable,new String("-")),false);
                            listaVariables.anadir(new ItemVariable (nombre, "-"));

                        }else{
                            //c.addVariable(new ItemVariable(variable,valor),false);
                            listaVariables.anadir(new ItemVariable (nombre, valor));

                        }

                    }

                }else if(valor.codePointAt(0)>=65&valor.codePointAt(0)<=90){
                    //respuesta texto sin comillas
                    //this.lista_parametros.anadir(variable,valor);
                    //c.addVariable(new ItemVariable(variable,valor),false);
                    listaVariables.anadir(new ItemVariable (nombre, valor));


                }else{
                    //respuesta decimal
                    try{
                        //this.lista_parametros.anadir(variable,Double.valueOf(valor).doubleValue());
                        //c.addVariable(new ItemVariable(variable,Double.valueOf(valor).doubleValue()),false);
                        listaVariables.anadir(new ItemVariable (nombre, Double.valueOf(valor).doubleValue()));

                    }catch(java.lang.NumberFormatException e){
                        //c.addVariable(new ItemVariable(variable,Double.valueOf(0.0).doubleValue()),false);
                        listaVariables.anadir(new ItemVariable (nombre, Double.valueOf(0.0).doubleValue()));
                    }
                }
            } // if (valor != "")
        } // while
    } // rellenarListaVariables
    */

   /*
    public ArrayList getCestasLeidas(){
        return cestasLeidas;
    }
    */
	private void tratarPeticionCesta(){
		if(!this.esPeticion){
			//<cliente> <sistema> <num_peticion> <tiempo_sistema> cesta <nombre_cesta> <nombre_variable> <valor> ...


			this.idTelegrama = TelegramTypes.CESTA;

			tt.nextToken();
			this.nombreCesta = tt.getString();

			while(tt.nextToken() != TelegramaTokenizer.EOF){
				String nombre = tt.getString();
				ItemVariable iv = null;

				if(tt.nextToken() != TelegramaTokenizer.EOF){
					if(tt.getType() == TelegramaTokenizer.NUMBER){
						double v = tt.getDouble();
						iv = new NumericVariable(nombre, v);
					}else{

						iv = new TextVariable(nombre, tt.getString());
					}
					listaVariables.add(iv);


				} // if 2º token de la pareja
			} // while


		} // if esPeticion
	} // cesta

	public boolean esEco(){
		return eco;
	}


	private void tratarPeticionTipos(){
		if(!this.esPeticion()){
			while(tt.nextToken() != TelegramaTokenizer.EOF){
				String s = tt.getString();

				if(s.equals(this.MARCA_FIN_LISTA_NOMBRES)){
					this.listaTiposCompleta = false;
				}else{
					listaVariables.add(s);
				}
			}
		}

	}

	private void tratarPeticionNombresTipos(){


		if(!this.esPeticion()){
			//Guardamos el tipo

			if(tt.nextToken() != TelegramaTokenizer.EOF){
				this.tipoVariable = tt.getString();
			}

			while(tt.nextToken() != TelegramaTokenizer.EOF){
				String s = tt.getString();
				if(s.equals(this.MARCA_FIN_LISTA_NOMBRES)){
					this.listaNombresTipoCompleta = false;
				}else{
					this.listaVariablesDeTipo.add(s);
				}
			}
		}
	}

/*
    private void tratarPeticionSubListaNombresTipos() {


            if (tt.nextToken() != TelegramaTokenizer.EOF){
                this.tipoVariable = tt.getString();
            }

            if (tt.nextToken() != TelegramaTokenizer.EOF){
                this.indexPrimerNombre = Integer.valueOf(tt.getString()).intValue();
            }

             while (tt.nextToken() != TelegramaTokenizer.EOF){
                String s = tt.getString();
                this.listaVariablesDeTipo.anadir(s);
            }
    }*/

	public String getTipoVariable(){
		return tipoVariable;
	}

	private void tratarPeticionCestaCompleta(){

		while(tt.nextToken() != TelegramaTokenizer.EOF){
			this.listaVariables.add(tt.getString());
		}
	}


	private String extraerErrores(String _tlgOriginal){
		if(_tlgOriginal.indexOf("___ERROR___") != -1){
			String tlgSinErrores = "";
			StreamTokenizer st = new StreamTokenizer(new StringReader(_tlgOriginal));
			st.ordinaryChars('0', '9');
			st.wordChars('0', '9');
			st.wordChars('_', '_');
			try{
				while(st.nextToken() != st.TT_EOF){
					if(st.ttype == st.TT_WORD){
						if(st.sval.equals("___ERROR___")){
							st.nextToken();
							mensajesError.add(st.sval);
						}else{
							tlgSinErrores += st.sval + " ";
						}
					}
				} // while
			}catch(IOException ex){
				ex.printStackTrace();
			}
			return tlgSinErrores;
		}else{
			return _tlgOriginal;
		}

	}


    /*
    private String extraerErrores (String _tlgOriginal){
        String tlgSinErrores ="";
        int indexError = 0;
        int indexInicioBusqueda = 0;
        int indexPrimeraComilla = -1;
        int indexSegundaComilla = -1;

        do{
            if (indexInicioBusqueda < _tlgOriginal.length()){
                indexError = _tlgOriginal.indexOf("___ERROR___",indexInicioBusqueda);
            }else{
                indexError = -1;
            }

            if (indexError != -1){
                tlgSinErrores = tlgSinErrores+_tlgOriginal.substring(indexInicioBusqueda,indexError-1);
                indexPrimeraComilla = indexError +13;
                for (int i=indexPrimeraComilla; i<_tlgOriginal.length();i++){
                    if (_tlgOriginal.charAt(i)=='\"'){
                        indexSegundaComilla = i;
                        break;
                    }
                }

                String msgError = _tlgOriginal.substring(indexPrimeraComilla, indexSegundaComilla);
                this.mensajesError.add(msgError); // añade el mensaje al arraylist

            }else{
                tlgSinErrores = tlgSinErrores+_tlgOriginal.substring(indexInicioBusqueda,_tlgOriginal.length());
            }


            indexInicioBusqueda = indexSegundaComilla+1;
        }while(indexError != -1);

        return tlgSinErrores;
    }
     */

	public boolean contieneErrores(){
		boolean contieneErrores = true;

		if(mensajesError.size() > 0){
			contieneErrores = true;
		}else{
			contieneErrores = false;
		}
		return contieneErrores;
	}

	public ArrayList<String> getMensajesError(){
		return mensajesError;
	}

	public String getNombreMuestreador(){
		return nombreMuestreador;
	}

	public int getNumMuestrasMuestreador(){
		return numMuestrasMuestreador;
	}

	public int getIndexChunk(){
		return indexChunk;
	}

	public void setIndexChunk(int indexChunk){
		this.indexChunk = indexChunk;
	}

	public ArrayList<Double> getValoresMuestreador(){
		return this.valoresMuestreador;
	}

    /*
    private double convertirADouble (String _txt){
        boolean signo_menos = false;
        double valor = Double.NaN;

        if(_txt.startsWith("__Ox")){
            if(_txt.indexOf("n") != -1){
                signo_menos = true;
            } else {
                signo_menos = false;
            }

            String ss = _txt.substring(5);
            try {
                valor =Double.longBitsToDouble(Long.valueOf(ss,16));
                if (signo_menos){
                    valor = valor*(-1);
                }

            } catch (NumberFormatException e) {
                valor = Double.NaN;
            }

        }

        return valor;
    }
*/


	public String getNombreClase(){
		return nombreClase;
	}

	public String getNombreInstancia(){
		return nombreInstancia;
	}

	public String getStatus(){
		return txtStatus;
	}

	public String getNombreTipo(){
		return nombreTipo;
	}

	public VariablesList getListaVariablesDeTipo(){
		return listaVariablesDeTipo;
	}

	public TelegramTypes getIdTelegrama(){
		return this.idTelegrama;
	}

	public String getPathFicheroTexto(){
		return pathFicheroTexto;
	}

	public String getContenidoFicheroTexto(){
		return contenidoFicheroTexto;
	}

	public CosmeStates getEvento(){
		return this.evento;
	}

  /*  public int getIndexPrimerNombre (){
        return this.indexPrimerNombre;
    }
*/


	/*
		Permite saber si ya se han recibido todos los nombres
		mientras sea falso deberá de enviarse otro tlg "lista_nombres"
	 */
	public boolean isListaNombresCompleta(){
		return this.listaNombresCompleta;
	}

	/*
		Permite saber si ya se han recibido todos los nombres de tipos
		mientras sea falso deberá de enviarse otro tlg "lista_tipo"
	 */
	public boolean isListaTiposCompleta(){
		return this.listaTiposCompleta;
	}

	/*
		Permite saber si ya se han recibido todos los nombres de tipos
		mientras sea falso deberá de enviarse otro tlg "lista_tipo"
	 */
	public boolean isListaNombresTipoCompleta(){
		return this.listaNombresTipoCompleta;
	}

	public String getPrefijo(){
		return this.prefijo;
	}

	protected boolean isTlgSincrono(){
		return this.tlgSincrono;
	}
}
