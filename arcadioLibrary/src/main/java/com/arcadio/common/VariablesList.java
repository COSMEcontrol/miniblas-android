/*
 * ListaVariables.java
 *
 * Created on 26 de Junio de 2006
 *
 * Clase diseñada para contener las variables asociadas a una cesta.
 *
 * Cuando el "OyenteTelegramas" recibe un telegrama con el contenido de una cesta crea una instancia de esta 
 * clase (a partir de la información contenida en el telegrama) y se la pasa a la ventana principal que se 
 * encargue de mostrar el interfaz de usuario.
 * Esa ventana de interfaz de usuario podrá interrogar a esta instancia (métodos "obtVariable (queNombre)", 
 * "obtValorVariable(queNombre)" u "obtValorTextoVariable(queNombre)") por cada uno de los nombres de variables que le interese.
 * 
 */

package com.arcadio.common;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;


/**
*
* @version 1.0
* @author Alberto Azuara García
*/
public class VariablesList implements Parcelable {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
       dest.writeInt(vars.size());
		for (String s: vars.keySet()) {
            ItemVariable variable = (ItemVariable) vars.get(s);
			dest.writeParcelable(variable,flags);
		}
    }
    public VariablesList readFromParcel(Parcel in) {
			int count = in.readInt();
			for(int i = 0; i < count; i++){
                ItemVariable variable = (ItemVariable) in.readParcelable(ItemVariable.class.getClassLoader());
				vars.put(variable.getName(), variable);
			}
        return this;
    }

    public VariablesList(Parcel in) {
        readFromParcel(in);
    }

    public static final Parcelable.Creator<VariablesList> CREATOR
            = new Parcelable.Creator<VariablesList>() {
        public VariablesList createFromParcel(Parcel in) {
            return new VariablesList(in);
        }

        public VariablesList[] newArray(int size) {
            return new VariablesList[size];
        }
    };


    /**
     * variable list, ItemVariable
     */
    private HashMap<String, ItemVariable> vars = new HashMap<String, ItemVariable>();

    /**
    *  
    * 
    */
    public VariablesList(){

	}

    /**
    *  
    * 
    */
    public void add(String _name) {
        //System.out.print(_name);
        ItemVariable v = new ItemVariable (_name);
	    vars.put(_name, v);
    } 

    /**
    *  
    * 
    */
    public void add(String _name, double _value) {
        ItemVariable v = new NumericVariable(_name, _value);
	    vars.put(_name, v);
    } 
    
    /**
    *  
    * 
    */
    public void add(String _name, String _valor) {
        ItemVariable v = new TextVariable(_name, _valor);
	    vars.put (_name, v);
    } 

    /**
    *  
    * 
    */
    public void add(ItemVariable v) {
	vars.put (v.getName(), v);
    } 

    /**
    *  
    * 
    */
    public HashMap getMap() {
        return vars;
    } 
    
    public ItemVariable getVariable (String _variableName){
        ItemVariable v =  vars.get(_variableName);
        return v;
    }
    
    
    public double getValue(String _variableName){
        double dd = 0.0;
        NumericVariable v = (NumericVariable) vars.get(_variableName);
        if (v!=null){
            dd = v.getValue();
        }
        return dd;
    }
    
    
    
    public String getTextValue(String _variableName){
        String txt = null;
        TextVariable v = (TextVariable) vars.get(_variableName);
        if (v!=null){
            txt = v.getValue();
        }
        return txt;
    }
    
    
    
    public Collection<ItemVariable> getList(){
        return vars.values();
    }
    
    
    public boolean exist(String _variableName){
        return vars.containsKey(_variableName);
    }

    public void remove(String _name){
        this.vars.remove(_name);
    }

    /**
     * Erases all the ItemVariables it could contain.
     * After executing this method, the instances contains 0 ItemVariables.
     */
    public void removeAll(){
        this.vars.clear();
    }
 }
