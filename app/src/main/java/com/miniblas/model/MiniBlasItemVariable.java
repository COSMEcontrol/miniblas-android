package com.miniblas.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.arcadio.modelo.ItemVariable;
import com.miniblas.perfistence.ormlite.Constantes;
@DatabaseTable(tableName = Constantes.VARIABLE_TABLE)
public class MiniBlasItemVariable implements Comparable, ISortElement {
	
	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(index = true)
	private int orden;
	@DatabaseField(canBeNull = false)
	private String nombre;
	@DatabaseField()
	private String tipo;
	@DatabaseField()
	private String maximo;
	@DatabaseField()
	private String minimo;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull=true)
	private MiniBlasCesta cesta;
	
	private String valor = "0";
	
	public static final MiniBlasItemVariable convertToMiniBlasVariable(ItemVariable _variable){
		MiniBlasItemVariable iv = new MiniBlasItemVariable(_variable.getNombre());
		iv.setTipo(_variable.getTipo());
		iv.setValor(_variable.getValor());
		return iv;
	}
	

	public MiniBlasItemVariable() {
//		super();
	}

//	public MiniBlasItemVariable(String _nombre, String _valor){
//		super(_nombre, _valor);
//		this.nombre = _nombre;
//		this.valor = _valor;
//		
//	}
//	public MiniBlasItemVariable(String _nombre, String _tipo, String maximo, String minimo){
//		this.nombre = _nombre;
//		this.tipo = _tipo;
//		this.maximo=maximo;
//		this.minimo=minimo;
//	}
//	
	public MiniBlasItemVariable(String _nombre){
//		super(_nombre);
		this.nombre = _nombre;
//		this.tipo="";
//		this.maximo="";
//		this.minimo="";
//		this.cesta=new MiniBlasCesta();
	}

    public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getOrden() {
		return orden;
	}


	public void setOrden(int orden) {
		this.orden = orden;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getTipo() {
		return tipo;
	}


	public void setTipo(String tipo) {
		this.tipo = tipo;
	}


	public String getMaximo() {
		return maximo;
	}


	public void setMaximo(String maximo) {
		this.maximo = maximo;
	}


	public String getMinimo() {
		return minimo;
	}


	public void setMinimo(String minimo) {
		this.minimo = minimo;
	}


	public MiniBlasCesta getCesta() {
		return cesta;
	}


	public void setCesta(MiniBlasCesta cesta) {
		this.cesta = cesta;
	}


	public String getValor() {
		return valor;
	}


	public void setValor(String valor) {
		this.valor = valor;
	}


	@Override
	public int compareTo(Object _opject) {
		MiniBlasItemVariable _itemVariable = (MiniBlasItemVariable) _opject;
		if (this.orden <  _itemVariable.getOrden())
	        return -1;
	    if (this.orden == _itemVariable.getOrden())
	        return 0;
	    return 1;
	}
    @Override
	public boolean equals(Object object) {
		 boolean same = false;
	        if (object != null && object instanceof MiniBlasItemVariable)
	        {
	            same = this.nombre.equals(((MiniBlasItemVariable) object).nombre);
	        }

	     return same;
	}


	@Override
	public String toString() {
		return "MiniBlasItemVariable [id=" + id + ", orden=" + orden
				+ ", nombre=" + nombre + ", tipo=" + tipo + ", maximo="
				+ maximo + ", minimo=" + minimo + ", cesta=" 
				+ ", valor=" + valor + "]";
	}
    
    
}
