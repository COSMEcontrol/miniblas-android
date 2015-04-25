package com.miniblas.persistence;

import com.miniblas.model.MiniBlasBag;
import com.miniblas.model.MiniBlasProfile;
import com.miniblas.model.base.BaseElementList;


public interface BasketStorage{
	/**
	 * Guarda la cesta pasada por parámetros.
	 *
	 * @param _basket
	 */
	public void persist(MiniBlasBag _basket) throws BdException;

	/**
	 * Guarda una coleccion de cestas pasados por parámetros.
	 * Si ya existe no lo modifica, si no existe lo crea
	 */
	public void persistCollection(BaseElementList<MiniBlasBag> _collection) throws BdException;

	/**
	 * Obtiene una cesta con un id especificado.
	 */
	public BaseElementList<MiniBlasBag> getBagsByProfile(MiniBlasProfile _profile) throws BdException;

	/**
	 * Obtiene una coleccion de cestas ordenados.
	 */
	public BaseElementList<MiniBlasBag> getBagsOrdered() throws BdException;

	/**
	 * Elimina una cesta especificado.
	 */
	public void deleteBags(BaseElementList<MiniBlasBag> _bag) throws BdException;

	public MiniBlasBag getBagById(int _id) throws BdException;


}
