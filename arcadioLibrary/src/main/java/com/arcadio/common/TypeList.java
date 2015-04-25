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
*
* @version 1.0 02/12/2014
* @author Javier Perojo, Alberto Azuara García
*/
public class TypeList implements Parcelable {


    private SortedMap<String, NamesList> tipos = new TreeMap();
   //private SortedMap <String, ListaNombres> tipos  = Collections.synchronizedSortedMap(new TreeMap());
    
    /**
    *  
    * 
    */
    public TypeList(){
	
    }

    /**
    *  
    * 
    */
    public void addType(String _typeName) {
        NamesList names = new NamesList();
	    tipos.put(_typeName, names);
    } 

    public void anadirNombreDeTipo (String _tipo, String _nombre){
        NamesList nombres = tipos.get(_tipo);
        nombres.add(_nombre);
        tipos.put(_tipo, nombres);
    }
    
    /**
    *  
    * Dado un tipo, devuelve aquellos nombres que sean de ese tipo.
    */
    public Collection<String> getNombresDeTipo(String _tipo) { 
        NamesList names = tipos.get(_tipo);
        if (names!=null){
            return names.getNameList();
        }else{
            return null;
        }
    } 

    public Collection<String> getTypeList(){
        //Iterator<String> it = tipos.keySet().iterator();
        //return it;
        return tipos.keySet();
    }

    public void removeAll(){
        this.tipos.clear();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(tipos);
    }
    public TypeList readFromParcel(Parcel in) {
        in.readMap(tipos, TypeList.class.getClassLoader());
        return this;
    }
    public TypeList(Parcel in) {
        readFromParcel(in);
    }
    public static final Parcelable.Creator<TypeList> CREATOR
            = new Parcelable.Creator<TypeList>() {
        public TypeList createFromParcel(Parcel in) {
            return new TypeList(in);
        }

        public TypeList[] newArray(int size) {
            return new TypeList[size];
        }
    };
    
 }
