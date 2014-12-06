/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.arcadio.xml;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author fserna
 */
public class DecoderXML{

	StringReader fuenteXML;
	Document docXML = null;

	/** Creates a new instance of FontaneroXML */
	public DecoderXML(String _textoXML){
		fuenteXML = new StringReader(_textoXML);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			docXML = builder.parse(new org.xml.sax.InputSource(fuenteXML));
		}catch(IOException ex){
			ex.printStackTrace();
		}catch(ParserConfigurationException ex){
			ex.printStackTrace();
		}catch(SAXException ex){
			ex.printStackTrace();
		}
	}


	public String getEstado(){
		return getItemTag("estado");
	}

	public String getIP(){
		return getItemTag("ip");
	}

	public String getComando(){
		return getItemTag("comando");
	}

	public String getMaquina(){
		return getItemTag("maquina");
	}

	public String getAplicacion(){
		return getItemTag("aplicacion");
	}

	public String getClave(){
		return getItemTag("maquina") + "-" + getItemTag("aplicacion");
	}

	public String getContrasena(){
		return getItemTag("contrasena");
	}

	public String getPuerto(){
		return getItemTag("puerto");
	}

	public String getComentario(){
		return getItemTag("comentario");
	}

	public String getFecha(){
		return getItemTag("fecha");
	}

	public String getHora(){
		return getItemTag("hora");
	}


	// -------------------------------------------------------

	private String getItemTag(String _tag){
		String info = "";

		NodeList lista = docXML.getElementsByTagName(_tag);
		Node nodo = lista.item(0);
		if(nodo != null){
			Node nodoValor = nodo.getFirstChild();
			if(nodoValor != null){
				info = nodoValor.getNodeValue();
			}
		}

		return info;
	}
}