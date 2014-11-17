package com.miniblas.model;

import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.miniblas.perfistence.ormlite.Constantes;
import com.arcadio.modelo.Cesta;

@DatabaseTable(tableName = Constantes.BASKET_TABLE_NAME)
public class MiniBlasCesta implements Comparable, ISortElement {
	@DatabaseField(columnName = Constantes.BASKET_ID,generatedId = true)
	private int id;
	@DatabaseField(columnName = Constantes.BASKET_ORDER,index = true)
	private int orden;
	@DatabaseField(columnName = Constantes.BASKET_NAME,canBeNull = false)
	private String nombre;
	@DatabaseField(columnName = Constantes.BASKET_REFRESHPERIOD,canBeNull = false)
	private int periodoRefresco;
	//clave ajena
	@DatabaseField(columnName = Constantes.BASKET_PROFILE,foreign = true)
	private MiniBlasPerfil perfil;
	@ForeignCollectionField(columnName = Constantes.BASKET_VARIABLE_LIST)
    ForeignCollection<MiniBlasItemVariable> variables;
	public static final MiniBlasCesta convertToMiniBlasCesta(Cesta _cesta){
		return new MiniBlasCesta(_cesta.getNombre(), _cesta.getPeriodoRefresco());
	}
	
	
	public ArrayList<MiniBlasItemVariable> getVariables() {
		return new ArrayList<MiniBlasItemVariable>(variables);
	}

	public void setVariables(List<MiniBlasItemVariable> variables) {
		this.variables.clear();
		this.variables.addAll(variables);
	}

	public MiniBlasCesta() {
		super();
		// needed by ormlite
	}



//	public MiniBlasCesta(int orden, String nombre, int periodoRefresco, MiniBlasPerfil perfil) {
//		this.orden = orden;
//		this.nombre = nombre;
//		this.periodoRefresco = periodoRefresco;
//		this.perfil = perfil;
//	}
	public MiniBlasCesta(String nombreCesta, int periodoRefresco) {
//		super(nombreCesta, periodoRefresco);
		this.nombre=nombreCesta;
		this.periodoRefresco=periodoRefresco;
	}
	public MiniBlasCesta(String _nombreCesta){
//		super(_nombreCesta);
		this.nombre= _nombreCesta;
	}
	public int getOrden() {
		return orden;
	}

	public void setOrden(int orden) {
		this.orden = orden;
	}

	public int getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}
	public void setPerfil(MiniBlasPerfil perfil) {
		this.perfil = perfil;
	}
	public int getPeriodoRefresco() {
		return periodoRefresco;
	}

	public MiniBlasPerfil getPerfil() {
		return perfil;
	}
	public void setNombre(String _nombre){
		this.nombre=_nombre;
	}
	public void setPeriodoRefresco(int _periodoRefresco){
		this.periodoRefresco=_periodoRefresco;
	}
	@Override
	public boolean equals(Object object) {
		 boolean same = false;
	        if (object != null && object instanceof MiniBlasCesta)
	        {
	            same = nombre.equals(((MiniBlasCesta) object).getNombre());
	        }

	     return same;
	}
	
	
	@Override
	public int compareTo(Object _opject) {
		MiniBlasCesta _cesta = (MiniBlasCesta) _opject;
		if (this.orden <  _cesta.getOrden())
	        return -1;
	    if (this.orden == _cesta.getOrden())
	        return 0;
	    return 1;
	}
	@Override
	public String toString() {
		return "Cesta [id=" + id + ", nombre=" + nombre
				+ ", periodoRefresco=" + periodoRefresco + ", perfil=" + perfil
				+ "]";
	}
	
}
