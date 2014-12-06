package com.miniblas.persistence;

import com.miniblas.model.MiniBlasCesta;
import com.miniblas.model.MiniBlasPerfil;

import java.util.List;

public interface BasketStorage{
	/**
	 * Guarda la cesta pasada por parámetros.
	 *
	 * @param _basket
	 */
	public void persist(MiniBlasCesta _basket) throws BdException;

	/**
	 * Guarda una coleccion de cestas pasados por parámetros.
	 * Si ya existe no lo modifica, si no existe lo crea
	 */
	public void persistCollection(List<MiniBlasCesta> _collection) throws BdException;

	/**
	 * Obtiene una cesta con un id especificado.
	 */
	public List<MiniBlasCesta> getBasketsByProfile(MiniBlasPerfil _profile) throws BdException;

	/**
	 * Obtiene una coleccion de cestas ordenados.
	 */
	public List<MiniBlasCesta> getBasketsOrdered() throws BdException;

	/**
	 * Elimina una cesta especificado.
	 */
	public void deleteBaskets(List<MiniBlasCesta> _baskets) throws BdException;

	public MiniBlasCesta getBasketById(int _id) throws BdException;


}
