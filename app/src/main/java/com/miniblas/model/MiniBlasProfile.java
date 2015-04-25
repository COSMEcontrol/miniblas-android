package com.miniblas.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.miniblas.model.base.BaseElement;
import com.miniblas.persistence.ormlite.Contract;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@DatabaseTable(tableName = Contract.PROFILE_TABLE_NAME)
@DefaultContentUri(authority = MiniBlasProfile.PerfilContract.AUTHORITY, path = MiniBlasProfile.PerfilContract.CONTENT_URI_PATH)
public class MiniBlasProfile extends BaseElement{
	/**
	 * Define las constantes necesarias para crear un ContentProvider.
	 *
	 * @author alberto
	 */
	public static final class PerfilContract implements BaseColumns{
		//tiene que coincidir el texto con AUTHORITY el campo correspondiente al ContentProvider en el manifest
		public static final String AUTHORITY = "com.miniblas.api.v1.provider";

		public static final String CONTENT_URI_PATH = "profiles";

		public static final String MIMETYPE_TYPE = "profiles";


		public static final Uri CONTENT_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY).appendPath(CONTENT_URI_PATH).build();
	}

	@DatabaseField(columnName = Contract.PROFILE_IP, canBeNull = false)
	private String ip;
	@DatabaseField(columnName = Contract.PROFILE_PORT, canBeNull = false)
	private int port;
	@DatabaseField(columnName = Contract.PROFILE_PASSWORD, canBeNull = false)
	private String password;

	@ForeignCollectionField(columnName = Contract.BASKET_TABLE_NAME)
	ForeignCollection<MiniBlasBag> bags;

	public static int PUERTO_POR_DEFECTO = 15150;
	public static String CONTRASEÑA_POR_DEFECTO = "GTA70";


	public List<MiniBlasBag> getBags(){
		return new ArrayList<MiniBlasBag>(bags);
	}

	public void setBags(List<MiniBlasBag> _cestas){
		this.bags.addAll(_cestas);
	}


	public MiniBlasProfile(){
		super();
	}

	public MiniBlasProfile(String _nombre){
		this.setNameElement(_nombre);
	}


	public MiniBlasProfile(String _nombre, String ip, String port, String password){
		//		super(nombre,ip,port,password);
		this.setNameElement(_nombre);
		this.ip = ip;
		this.id = UUID.randomUUID().hashCode();
		//comprobar si vienen vacios para poner los valores por defecto
		if(port.isEmpty()){
			this.port = PUERTO_POR_DEFECTO;
		}else{
			this.port = Integer.valueOf(port);
		}
		if(password.isEmpty()){
			this.password = CONTRASEÑA_POR_DEFECTO;
		}else{
			this.password = password;
		}

	}

	public String getIp(){
		return ip;
	}

	public void setIp(String ip){
		this.ip = ip;
	}

	public int getPort(){
		return port;
	}

	public void setPort(int port){
		this.port = port;
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
		if(object != null){
			same = getNameElement().equals(((MiniBlasProfile) object).getNameElement());
		}

		return same;
	}

	@Override
	public String toString(){
		return "Perfil [id=" + id + ", orden=" + getOrder() + ", nombre=" + getNameElement() + ", ip=" + ip + ", port=" + port + ", password=" + password + "]";
	}



}
