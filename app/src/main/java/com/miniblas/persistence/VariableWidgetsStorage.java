package com.miniblas.persistence;

import com.miniblas.model.base.BaseElementList;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;
/**
 *
 * @author A. Azuara
 */
public interface VariableWidgetsStorage{
	/**
	 * Guarda la cesta pasada por parámetros.
	 */
	public void persist(BaseVariableWidget _itemVariable) throws BdException;

	/**
	 * Guarda una coleccion de cestas pasados por parámetros.
	 * Si ya existe no lo modifica, si no existe lo crea
	 */
	public void persistCollection(BaseElementList<BaseVariableWidget> _collection) throws BdException;

	/**
	 * Obtiene una cesta con un id especificado.
	 */
	public BaseVariableWidget getItemVariableByid(int _id) throws BdException;

	/**
	 * Obtiene una coleccion de cestas ordenados.
	 */
	public BaseElementList<BaseVariableWidget> getItemVariableOrdered(int id_item) throws BdException;

	/**
	 * Elimina todos los elementos
	 */
	public void deleteItemVariables(BaseElementList<BaseVariableWidget> _itemVariable) throws BdException;


}
