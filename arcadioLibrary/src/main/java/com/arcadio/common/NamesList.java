/*
 * ListaNombres.java
 *
 *
 */

package com.arcadio.common;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.*;

/**
* Class description goes here.
*
* @version 1.0 02/12/2014
* @author Alberto Azuara
*/
public class NamesList implements Parcelable {

    /**
     * contiene una lista de variables, ItemVariable
     */
    private SortedMap<String ,String> names_list = new TreeMap();   // <nombre, tipo>
    //private SortedMap <String, String> names_list  = Collections.synchronizedSortedMap(new TreeMap());

    // Estados en los que puede estar la lista
    static final public int LISTA_VACIA = 0;
    static final public int RECIBIENDO_LISTA = 1;
    static final public int LISTA_RECIBIDA = 2;

    // Indica si ya se han recibido TODOS los names_list de la lista.
    private int state = LISTA_VACIA;
    /**
    *  
    * 
    */
    public NamesList(){
	
    }

    /**
    *  
    * 
    */
    public void add(String _name) {
	    names_list.put(_name, "?");
    } 
    /**
    *  
    * 
    */
    public void add(String _name, String _type) {
	    names_list.put(_name, _type);
    } 


    public SortedMap<String ,String> getNameTypeList(){
        return this.names_list;
    }
    
    public Collection<String> getNameList(){
        return names_list.keySet();
    }
    

    public Collection<String> getTypeList(){
        return names_list.values();
    }
    
    public void setType(String _variable, String _type){
        names_list.put(_variable, _type);
    }
        
    
    public int getCountNames(){
        return names_list.size();
    }

    public String getType(String _variableName) {
        return names_list.get(_variableName);
    }

    /**
     * @return the state
     */
    public int getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(int state) {
        this.state = state;
    }

    public void removeAll(){
        this.names_list.clear();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(names_list);
    }
    public NamesList readFromParcel(Parcel in) {
        in.readMap(names_list, NamesList.class.getClassLoader());
        return this;
    }
    public NamesList(Parcel in) {
        readFromParcel(in);
    }
    public static final Parcelable.Creator<NamesList> CREATOR
            = new Parcelable.Creator<NamesList>() {
        public NamesList createFromParcel(Parcel in) {
            return new NamesList(in);
        }

        public NamesList[] newArray(int size) {
            return new NamesList[size];
        }
    };


}
