package com.miniblas.persistence;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.miniblas.model.base.BaseElementList;
import com.miniblas.model.variableWidgets.VariableSeekWidget;
import com.miniblas.model.variableWidgets.VariableSwitchWidget;
import com.miniblas.model.variableWidgets.VariableValueWidget;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;
import com.miniblas.persistence.ormlite.Contract;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OrmLiteVariableWidgetsStorage implements VariableWidgetsStorage{

	private static OrmLiteVariableWidgetsStorage moimeme;
	//widgets dao
	private Dao variableSeekDao;
	private Dao variableSwitchDao;
	private Dao variableValueDao;


	public static OrmLiteVariableWidgetsStorage getInstance(Dao _variableSeekDao, Dao _variableSwitchDao, Dao _variableValueDao) throws SQLException{
		if(moimeme == null){
			return new OrmLiteVariableWidgetsStorage(_variableSeekDao, _variableSwitchDao, _variableValueDao);
		}
		return moimeme;
	}

	private OrmLiteVariableWidgetsStorage(Dao _variableSeekDao, Dao _variableSwitchDao, Dao _variableValueDao) throws SQLException{
		this.variableSeekDao = _variableSeekDao;
		this.variableSwitchDao = _variableSwitchDao;
		this.variableValueDao = _variableValueDao;
	}

	@Override
	public void persist(BaseVariableWidget _itemVariable) throws BdException{
		//comprobar que tipo de widget es
		try{
			if(_itemVariable instanceof VariableSeekWidget){
				variableSeekDao.createOrUpdate(((VariableSeekWidget)_itemVariable));
			} else if(_itemVariable instanceof VariableSwitchWidget){
				variableSwitchDao.createOrUpdate(((VariableSwitchWidget)_itemVariable));
			}else if(_itemVariable instanceof VariableValueWidget){
				variableValueDao.createOrUpdate(((VariableValueWidget)_itemVariable));
			}
		}catch(Exception e){
			throw new BdException(e.toString());
		}

	}

	@Override
	public void persistCollection(BaseElementList<BaseVariableWidget> _collection) throws BdException{
		//comprobar que tipo de variable es
		for(BaseVariableWidget variable: _collection){
			persist(variable);
		}
	}

	@Override
	public BaseVariableWidget getItemVariableByid(int _id) throws BdException{
		//comprobar que tipo de variable
		BaseVariableWidget _itemVariable = null;
		try{
			_itemVariable = (BaseVariableWidget) variableSeekDao.queryForId(_id);
			if(_itemVariable == null){
				_itemVariable = (BaseVariableWidget) variableSwitchDao.queryForId(_id);
				if(_itemVariable == null){
					_itemVariable = (BaseVariableWidget) variableValueDao.queryForId(_id);
				}
			}
		}catch(SQLException e){
			throw new BdException(e.toString());
		}
		return _itemVariable;
	}

	@Override
	public BaseElementList<BaseVariableWidget> getItemVariableOrdered(int _id_bag) throws BdException{
		BaseElementList<BaseVariableWidget> listaItemVariable = new BaseElementList<BaseVariableWidget>();
		Map<String, Object> p = new HashMap<>();
		p.put(Contract.VARIABLE_BAG, _id_bag);
		try{
			listaItemVariable.addAll(variableSwitchDao.queryForFieldValues(p));
			listaItemVariable.addAll(variableSeekDao.queryForFieldValues(p));
			listaItemVariable.addAll(variableValueDao.queryForFieldValues(p));
			Collections.sort(listaItemVariable);
		}catch(SQLException e){
			throw new BdException(e.toString());
		}
		return listaItemVariable;
	}

	@Override
	public void deleteItemVariables(BaseElementList<BaseVariableWidget> _itemVariable) throws BdException{
		try{
			for(BaseVariableWidget _variable : _itemVariable){
				if(_variable instanceof VariableSeekWidget){
					variableSeekDao.delete(_variable);
				} else if(_variable instanceof VariableSwitchWidget){
					variableSwitchDao.delete(_variable);
				}else if(_variable instanceof VariableValueWidget){
					variableValueDao.delete(_variable);
				}
			}
		}catch(SQLException e){
			throw new BdException(e.toString());
		}
	}


}
