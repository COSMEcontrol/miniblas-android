package com.miniblas.model;

import android.content.ContentResolver;
import android.net.Uri;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.miniblas.perfistence.ormlite.Constantes;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@DatabaseTable(tableName = Constantes.PROFILE_TABLE_NAME)
@DefaultContentUri(authority = MiniBlasPerfil.PerfilContract.AUTHORITY, path = MiniBlasPerfil.PerfilContract.CONTENT_URI_PATH)
public class MiniBlasPerfil implements Comparable, ISortElement{
	/**
	 * Define las constantes necesarias para crear un ContentProvider.
	 *
	 * @author alberto
	 */
	public static final class PerfilContract{
		//tiene que coincidir el texto con AUTHORITY el campo correspondiente al ContentProvider en el manifest
		public static final String AUTHORITY = "com.miniblas.api.v1.provider";

		public static final String CONTENT_URI_PATH = "profiles";

		public static final String MIMETYPE_TYPE = "profiles";

		public static final int CONTENT_URI_PATTERN_MANY = 1;
		public static final int CONTENT_URI_PATTERN_ONE = 2;

		public static final Uri CONTENT_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY).appendPath(CONTENT_URI_PATH).build();
	}

	//,generatedId = true
	@DatabaseField(columnName = Constantes.PROFILE_ID, id = true)
	public int id;
	@DatabaseField(columnName = Constantes.PROFILE_ORDER, index = true)
	public int orden;
	@DatabaseField(columnName = Constantes.PROFILE_NAME, canBeNull = false)
	private String nombre;
	@DatabaseField(columnName = Constantes.PROFILE_IP, canBeNull = false)
	private String ip;
	@DatabaseField(columnName = Constantes.PROFILE_PORT, canBeNull = false)
	private int puerto;
	@DatabaseField(columnName = Constantes.PROFILE_PASSWORD, canBeNull = false)
	private String password;

	@ForeignCollectionField(columnName = Constantes.BASKET_TABLE_NAME)
	ForeignCollection<MiniBlasCesta> cestas;

	public static int PUERTO_POR_DEFECTO = 15150;
	public static String CONTRASEÑA_POR_DEFECTO = "GTA70";


	public List<MiniBlasCesta> getCestas(){
		return new ArrayList<MiniBlasCesta>(cestas);
	}

	public void setCestas(List<MiniBlasCesta> _cestas){
		this.cestas.addAll(_cestas);
	}


	public MiniBlasPerfil(){
		super();
	}

	public MiniBlasPerfil(String _nombre){
		//		super(_nombre);
		this.nombre = _nombre;
	}


	public MiniBlasPerfil(String nombre, String ip, String puerto, String password){
		//		super(nombre,ip,puerto,password);
		this.nombre = nombre;
		this.ip = ip;
		this.id = UUID.randomUUID().hashCode();
		//comprobar si vienen vacios para poner los valores por defecto
		if(puerto.isEmpty()){
			this.puerto = PUERTO_POR_DEFECTO;
		}else{
			this.puerto = Integer.valueOf(puerto);
		}
		if(password.isEmpty()){
			this.password = CONTRASEÑA_POR_DEFECTO;
		}else{
			this.password = password;
		}

	}


	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getOrden(){
		return orden;
	}

	public void setOrden(int orden){
		this.orden = orden;
	}

	public String getNombre(){
		return nombre;
	}

	public void setNombre(String nombre){
		this.nombre = nombre;
	}

	public String getIp(){
		return ip;
	}

	public void setIp(String ip){
		this.ip = ip;
	}

	public int getPuerto(){
		return puerto;
	}

	public void setPuerto(int puerto){
		this.puerto = puerto;
	}

	public String getPassword(){
		return password;
	}

	public void setPassword(String password){
		this.password = password;
	}

	@Override
	public boolean equals(Object object){
		boolean same = false;
		if(object != null && object instanceof MiniBlasPerfil){
			same = nombre.equals(((MiniBlasPerfil) object).nombre);
		}

		return same;
	}

	@Override
	public String toString(){
		return "Perfil [id=" + id + ", orden=" + orden + ", nombre=" + nombre + ", ip=" + ip + ", puerto=" + puerto + ", password=" + password + "]";
	}

	@Override
	public int compareTo(Object _opject){
		MiniBlasPerfil _perfil = (MiniBlasPerfil) _opject;
		if(this.orden < _perfil.getOrden()){
			return -1;
		}
		if(this.orden == _perfil.getOrden()){
			return 0;
		}
		return 1;
	}


}
