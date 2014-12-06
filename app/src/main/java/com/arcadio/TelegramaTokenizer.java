/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.arcadio;

/**
 *
 * @author fserna
 */

import java.util.StringTokenizer;

public class TelegramaTokenizer{

	private StringTokenizer st;
	public static final int NUMBER = -1;
	public static final int TEXT = -2;
	public static final int EOF = -3;
	private int ttype;
	private String sval;
	private double nval;
	private String idHexa1 = "__Ox";
	private String idHexa2 = "0x";

	TelegramaTokenizer(String telegrama){
		st = new StringTokenizer(telegrama);
	}

	public int nextToken(){
		if(st.hasMoreTokens()){
			sval = st.nextToken();

			// texto de varias palabras entre comillas
			if(sval.startsWith("\"") && !sval.endsWith("\"")){
				while(st.hasMoreTokens()){
					sval = sval + " " + st.nextToken();
					if(sval.endsWith("\"")){
						break;
					}
				}
				ttype = TEXT;
				return TEXT;
			}

			// texto de una palabra entre comillas
			else if(sval.startsWith("\"") && sval.endsWith("\"")){
				ttype = TEXT;
				return TEXT;
			}

			// n�mero hexadecimal __Ox
			else if(sval.startsWith(idHexa1)){
				ttype = NUMBER;
				try{
					if(sval.indexOf("p") != -1){
						nval = Double.longBitsToDouble(Long.valueOf(sval.substring(idHexa1.length() + 1), 16));
					}else{
						nval = -1 * Double.longBitsToDouble(Long.valueOf(sval.substring(idHexa1.length() + 1), 16));
					}
					return NUMBER;
				}catch(NumberFormatException e){
					nval = Double.NaN;
					return NUMBER;
				}
			}

			// n�mero hexadecimal 0x
			else if(sval.startsWith(idHexa2)){
				try{
					ttype = NUMBER;
					sval = sval.substring(idHexa2.length());
					int signo = 1;

					if(sval.charAt(0) > '7'){
						switch(sval.toLowerCase().charAt(0)){
							case '8':
								sval = "0" + sval.substring(1);
								break;
							case '9':
								sval = "1" + sval.substring(1);
								break;
							case 'a':
								sval = "2" + sval.substring(1);
								break;
							case 'b':
								sval = "3" + sval.substring(1);
								break;
							case 'c':
								sval = "4" + sval.substring(1);
								break;
							case 'd':
								sval = "5" + sval.substring(1);
								break;
							case 'e':
								sval = "6" + sval.substring(1);
								break;
							case 'f':
								sval = "7" + sval.substring(1);
								break;
						}
						signo = -1;
					}
					nval = signo * Double.longBitsToDouble(Long.valueOf(sval, 16));
					return NUMBER;
				}catch(NumberFormatException e){
					nval = Double.NaN;
					return NUMBER;
				}
			}

			// n�mero
			else{
				try{
					nval = Double.parseDouble(sval);
					ttype = NUMBER;
					return NUMBER;
				}catch(NumberFormatException e){
					ttype = TEXT;
					return TEXT;
				}
			}
		}else{
			return EOF;
		}
	}


	public int getType(){
		return ttype;
	}


	private String getSval(){
		return sval;
	}

	private double getNval(){
		return nval;
	}


	public String getString(){
		return sval;
	}

	public double getDouble(){
		return nval;
	}

	public int getInt(){
		return (int) nval;
	}

	public boolean isNotEOF(){
		boolean hayMas = false;

		if(this.getType() == TelegramaTokenizer.EOF){
			hayMas = false;
		}else{
			hayMas = true;
		}

		return hayMas;
	}
}

