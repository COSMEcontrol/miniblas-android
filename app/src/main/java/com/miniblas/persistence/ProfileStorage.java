package com.miniblas.persistence;

import com.miniblas.model.MiniBlasProfile;
import com.miniblas.model.base.BaseElementList;
/**
 *
 * @author A. Azuara
 */
public interface ProfileStorage{
	/**
	 * Guarda el perfil pasado por parámetros.
	 */
	public void persist(MiniBlasProfile _profile) throws BdException;

	/**
	 * Guarda una coleccion de perfiles pasados por parámetros.
	 * Si ya existe no lo modifica, si no existe lo crea
	 */
	public void persistCollection(BaseElementList<MiniBlasProfile> _collection) throws BdException;

	/**
	 * Obtiene un perfil con un id especificado.
	 */
	public MiniBlasProfile getProfileById(int _id) throws BdException;

	/**
	 * Obtiene una coleccion de perfiles ordenados.
	 */
	public BaseElementList<MiniBlasProfile> getProfilesOrdered() throws BdException;

	/**
	 * Elimina un perfil especificado.
	 */
	public void deleteProfiles(BaseElementList<MiniBlasProfile> _profiles) throws BdException;

}
