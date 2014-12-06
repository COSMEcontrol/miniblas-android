package com.miniblas.persistence;

import com.miniblas.model.MiniBlasItemVariable;

import java.util.List;

public interface ItemVariableStorage{
	/**
	 * Guarda la cesta pasada por parámetros.
	 */
	public void persist(MiniBlasItemVariable _itemVariable) throws BdException;

	/**
	 * Guarda una coleccion de cestas pasados por parámetros.
	 * Si ya existe no lo modifica, si no existe lo crea
	 */
	public void persistCollection(List<MiniBlasItemVariable> _collection) throws BdException;

	/**
	 * Obtiene una cesta con un id especificado.
	 */
	public MiniBlasItemVariable getItemVariableByid(int _id) throws BdException;

	/**
	 * Obtiene una coleccion de cestas ordenados.
	 */
	public List<MiniBlasItemVariable> getItemVariableOrdered() throws BdException;

	/**
	 * Elimina todos los elementos
	 */
	public void deleteItemVariables(List<MiniBlasItemVariable> _itemVariable) throws BdException;


}
