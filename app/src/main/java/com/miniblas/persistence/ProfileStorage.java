package com.miniblas.persistence;

import com.miniblas.model.MiniBlasPerfil;

import java.util.List;

public interface ProfileStorage{
	/**
	 * Guarda el perfil pasado por parámetros.
	 */
	public void persist(MiniBlasPerfil _profile) throws BdException;

	/**
	 * Guarda una coleccion de perfiles pasados por parámetros.
	 * Si ya existe no lo modifica, si no existe lo crea
	 */
	public void persistCollection(List<MiniBlasPerfil> _collection) throws BdException;

	/**
	 * Obtiene un perfil con un id especificado.
	 */
	public MiniBlasPerfil getProfileByid(int _id) throws BdException;

	/**
	 * Obtiene una coleccion de perfiles ordenados.
	 */
	public List<MiniBlasPerfil> getProfilesOrdered() throws BdException;

	/**
	 * Elimina un perfil especificado.
	 */
	public void deleteProfiles(List<MiniBlasPerfil> _profiles) throws BdException;

}
