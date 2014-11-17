package com.miniblas.persistence;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.miniblas.model.MiniBlasItemVariable;

public class OrmLiteItemVariableStorage implements ItemVariableStorage{

	private static OrmLiteItemVariableStorage moimeme;
	private Dao itemVariableDao;
	
	
	public static OrmLiteItemVariableStorage getInstance(Dao _dao) throws SQLException{
		if(moimeme==null){
			return new OrmLiteItemVariableStorage(_dao);
		}
		return moimeme;
	}
	private OrmLiteItemVariableStorage(Dao _dao) throws SQLException {
		this.itemVariableDao=_dao;
	}
	@Override
	public void persist(MiniBlasItemVariable _itemVariable) throws BdException {
		try {
			itemVariableDao.createOrUpdate(_itemVariable);
		} catch (SQLException e) {
			throw new BdException(e.toString());
		}
		
	}

	@Override
	public void persistCollection(List<MiniBlasItemVariable> _collection)
			throws BdException {
		for(MiniBlasItemVariable _variable : _collection){
			try {
				itemVariableDao.createIfNotExists(_variable);
			} catch (SQLException e) {
				throw new BdException(e.toString());
			}
		}
		
	}

	@Override
	public MiniBlasItemVariable getItemVariableByid(int _id) throws BdException {
		MiniBlasItemVariable _itemVariable = null;
		try {
			_itemVariable = (MiniBlasItemVariable) itemVariableDao.queryForId(_id);
		} catch (SQLException e) {
			throw new BdException(e.toString());
		}
		return _itemVariable;
	}

	@Override
	public List<MiniBlasItemVariable> getItemVariableOrdered() throws BdException {
		List<MiniBlasItemVariable> listaItemVariable = null;
		try {
			listaItemVariable = itemVariableDao.queryForAll();
			Collections.sort(listaItemVariable);
		} catch (SQLException e) {
			throw new BdException(e.toString());
		}
		return listaItemVariable;
	}

	@Override
	public void deleteItemVariable(MiniBlasItemVariable _itemVariable)
			throws BdException {
		try {
			itemVariableDao.delete(_itemVariable);
		} catch (SQLException e) {
			throw new BdException(e.toString());
		}
		
	}

}
