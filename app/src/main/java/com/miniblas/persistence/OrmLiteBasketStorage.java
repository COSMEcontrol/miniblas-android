package com.miniblas.persistence;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.miniblas.model.MiniBlasCesta;
import com.miniblas.model.MiniBlasItemVariable;
import com.miniblas.model.MiniBlasPerfil;
import com.miniblas.perfistence.ormlite.Constantes;

public class OrmLiteBasketStorage implements BasketStorage{
	private static OrmLiteBasketStorage moimeme;
	private Dao basketDao;
	private Dao profile;
	
	
	public static OrmLiteBasketStorage getInstance(Dao _basketDao) throws SQLException{
		if(moimeme==null){
			return new OrmLiteBasketStorage(_basketDao);
		}
		return moimeme;
	}
	private OrmLiteBasketStorage(Dao _basketDao) throws SQLException {
		this.basketDao=_basketDao;
		
	}
	
	@Override
	public void persist(final MiniBlasCesta _basket) throws BdException{
		try {
			Log.v(_basket.toString(), _basket.toString());
			basketDao.createOrUpdate(_basket);
		} catch (SQLException e) {
			throw new BdException(e.toString());
		}
	}

	@Override
	public void persistCollection(final List<MiniBlasCesta> _collection) throws BdException{
		for(MiniBlasCesta basket : _collection){
			try {
				basketDao.createOrUpdate(basket);
			} catch (SQLException e) {
				throw new BdException(e.toString());
			}
		}
	}

	@Override
	public List<MiniBlasCesta> getBasketsOrdered() throws BdException {
		List<MiniBlasCesta> listaCestas= null;
		try {
			listaCestas = basketDao.queryForAll();
			Collections.sort(listaCestas);
		} catch (SQLException e) {
			throw new BdException(e.toString());
		}
		return listaCestas;
	}
	@Override
	public void deleteBaskets(List<MiniBlasCesta> _baskets) throws BdException {
		try {
			basketDao.delete(_baskets);
		} catch (SQLException e) {
			throw new BdException(e.toString());
		}
		
	}
	@Override
	public List<MiniBlasCesta> getBasketsByProfile(MiniBlasPerfil _profile) throws BdException {
		List<MiniBlasCesta> baskets = null;
		try {
			baskets =  basketDao.queryForEq(Constantes.BASKET_PROFILE, _profile);
			Collections.sort(baskets);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new BdException(e.toString());
		}
		return baskets;
	}
	@Override
	public MiniBlasCesta getBasketById(int _id) throws BdException{
		MiniBlasCesta basket = null;
			try {
				basket =  (MiniBlasCesta) basketDao.queryForId(_id);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new BdException(e.toString());
			}

		return basket;
	}



}
