/*
 * Configuracion.java
 *
 * Created on 13 de febrero de 2007, 17:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arcadio.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * @author fserna
 */
public class Configuracion{

	private HashMap<String, Instancia> instancias = new HashMap();
	private HashMap<String, Propiedad> propiedades = new HashMap();
	private String nombreFicheroXML = "configuracion.xml";

	/** Creates a new instance of Configuracion */
	public Configuracion(){
	}

	public Configuracion(String _nombreFichero){
		this.nombreFicheroXML = _nombreFichero;
		this.cargarXML();
	}

	public void addInstancia(Instancia _instancia){
		instancias.put(_instancia.getNombreInstancia(), _instancia);
	}

	public void addPropiedad(Propiedad _propiedad){
		propiedades.put(_propiedad.getNombrePropiedad(), _propiedad);
	}

	public Propiedad getPropiedad(String _nombre){
		return propiedades.get(_nombre);
	}

	public Instancia getInstancia(String _nombre){
		return instancias.get(_nombre);
	}

	public Collection<Instancia> getListaInstancias(){
		return instancias.values();
	}

	public Collection<Propiedad> getListaPropiedades(){
		return propiedades.values();
	}


	public void printValores(){
		for(Instancia i : instancias.values()){
			System.out.println("Instancia: " + i.getNombreInstancia() + "\t" + i.getClase() + "\t" + i.getOrden());
		}
		for(Propiedad p : propiedades.values()){
			System.out.println("Propiedad: " + p.getNombrePropiedad() + "\t" + p.getValor());
		}
	}


	public void generarXML(){
		generarXML(this.nombreFicheroXML);
	}


	public void generarXML(String _nombreFichero){
		//  static public void escribirFicheroSistema (ItemSistema _sistema, String _nombre_fichero){

		Document doc = generarArbolConfiguracion();
		if(doc != null){
			try{
				TransformerFactory tFactory = TransformerFactory.newInstance();
				Transformer transformer = tFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				File fich = new File(_nombreFichero);
				StreamResult result = new StreamResult(fich);

				transformer.transform(source, result);

			}catch(Exception e){
				e.printStackTrace();
			}
		}

	}


	private Document generarArbolConfiguracion(){
		Document doc = null;

		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			DocumentBuilder builder = factory.newDocumentBuilder();

			doc = builder.newDocument();


			Element root = (Element) doc.createElement("emcos");
			doc.appendChild(root);

			Element elementoInstancias = (Element) doc.createElement("instancias");
			root.appendChild(elementoInstancias);
			Element elementoPropiedades = (Element) doc.createElement("propiedades");
			root.appendChild(elementoPropiedades);

			Element elementoInstancia;
			Element elementoPropiedad;
			Element elemento;

			// Instancias =================================
			for(Instancia i : instancias.values()){
				elementoInstancia = (Element) doc.createElement("instancia");
				elementoInstancias.appendChild(elementoInstancia);
				elemento = (Element) doc.createElement("nombreInstancia");
				elementoInstancia.appendChild(elemento);
				elemento.appendChild(doc.createTextNode(i.getNombreInstancia()));

				elemento = (Element) doc.createElement("clase");
				elementoInstancia.appendChild(elemento);
				elemento.appendChild(doc.createTextNode(i.getClase()));

				elemento = (Element) doc.createElement("orden");
				elementoInstancia.appendChild(elemento);
				elemento.appendChild(doc.createTextNode(String.valueOf(i.getOrden())));

				elemento = (Element) doc.createElement("habilitado");
				elementoInstancia.appendChild(elemento);
				if(i.isHabilitado()){
					elemento.appendChild(doc.createTextNode("true"));
				}else{
					elemento.appendChild(doc.createTextNode("false"));
				}


			} // for instancias


			// Propiedades =====================
			for(Propiedad p : propiedades.values()){
				elementoPropiedad = (Element) doc.createElement("propiedad");
				elementoPropiedades.appendChild(elementoPropiedad);
				elemento = (Element) doc.createElement("nombrePropiedad");
				elementoPropiedad.appendChild(elemento);
				elemento.appendChild(doc.createTextNode(p.getNombrePropiedad()));

				elemento = (Element) doc.createElement("valor");
				elementoPropiedad.appendChild(elemento);
				elemento.appendChild(doc.createTextNode(p.getValor()));

			} // for propiedades


		}catch(Exception e){
			e.printStackTrace();
		}

		return doc;


	} // generarArbolSistema


	public void cargarXML(){
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse(new org.xml.sax.InputSource(this.nombreFicheroXML));
			ConfiguracionScanner scanner = new ConfiguracionScanner(document, this);
			scanner.visitDocument();
		}catch(Exception e){

		}
	}
}
