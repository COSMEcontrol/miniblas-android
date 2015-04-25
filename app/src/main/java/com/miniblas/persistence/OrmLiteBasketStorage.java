package com.miniblas.persistence;


import com.j256.ormlite.dao.Dao;
import com.miniblas.model.MiniBlasBag;
import com.miniblas.model.MiniBlasProfile;
import com.miniblas.model.base.BaseElementList;
import com.miniblas.persistence.ormlite.Contract;

import java.sql.SQLException;
import java.util.Collections;


public class OrmLiteBasketStorage implements BasketStorage{
	private static OrmLiteBasketStorage moimeme;
	private Dao basketDao;
	private Dao profile;


	public static OrmLiteBasketStorage getInstance(Dao _basketDao) throws SQLException{
		if(moimeme == null){
			return new OrmLiteBasketStorage(_basketDao);
		}
		return moimeme;
	}

	private OrmLiteBasketStorage(Dao _basketDao) throws SQLException{
		this.basketDao = _basketDao;

	}

	@Override
	public void persist(final MiniBlasBag _basket) throws BdException{
		try{
			basketDao.createOrUpdate(_basket);
		}catch(SQLException e){
			throw new BdException(e.toString());
		}
	}

	@Override
	public void persistCollection(BaseElementList<MiniBlasBag> _collection) throws BdException{
		for(int i=0; i<_collection.size();i++){
			try{
				basketDao.createOrUpdate(_collection.get(i));
			}catch(SQLException e){
				throw new BdException(e.toString());
			}
		}
	}

	@Override
	public BaseElementList<MiniBlasBag> getBagsOrdered() throws BdException{
		BaseElementList<MiniBlasBag> baskets = null;
		try{
			baskets = new BaseElementList<>(basketDao.queryForAll());
			Collections.sort(baskets);
		}catch(SQLException e){
			throw new BdException(e.toString());
		}
		return baskets;
	}

	@Override
	public void deleteBags(BaseElementList<MiniBlasBag> _baskets) throws BdException{
		try{
			basketDao.delete(_baskets);
		}catch(SQLException e){
			throw new BdException(e.toString());
		}

	}

	@Override
	public BaseElementList<MiniBlasBag> getBagsByProfile(MiniBlasProfile _profile) throws BdException{
		BaseElementList<MiniBlasBag> baskets = null;
		try{
			baskets = new BaseElementList<>(basketDao.queryForEq(Contract.BASKET_PROFILE, _profile));
			Collections.sort(baskets);
		}catch(SQLException e){
			e.printStackTrace();
			throw new BdException(e.toString());
		}
		return baskets;
	}

	@Override
	public MiniBlasBag getBagById(int _id) throws BdException{
		MiniBlasBag basket = null;
		try{
			basket = (MiniBlasBag) basketDao.queryForId(_id);
		}catch(SQLException e){
			e.printStackTrace();
			throw new BdException(e.toString());
		}

		return basket;
	}


}
