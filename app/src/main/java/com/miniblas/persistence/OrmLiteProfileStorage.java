package com.miniblas.persistence;

import com.j256.ormlite.dao.Dao;
import com.miniblas.model.MiniBlasProfile;
import com.miniblas.model.base.BaseElementList;

import java.sql.SQLException;
import java.util.Collections;

public class OrmLiteProfileStorage implements ProfileStorage{
	private static OrmLiteProfileStorage moimeme;
	private Dao perfilDao;


	public static OrmLiteProfileStorage getInstance(Dao _dao) throws SQLException{
		if(moimeme == null){
			return new OrmLiteProfileStorage(_dao);
		}
		return moimeme;
	}

	private OrmLiteProfileStorage(Dao _dao) throws SQLException{
		this.perfilDao = _dao;
	}

	@Override
	public synchronized void persist(final MiniBlasProfile _profile) throws BdException{
		try{
			perfilDao.createOrUpdate(_profile);
		}catch(SQLException e){
			throw new BdException(e.toString());
		}
	}

	@Override
	public void persistCollection(BaseElementList<MiniBlasProfile> _collection) throws BdException{
		for(MiniBlasProfile elemento : _collection){
			try{
				perfilDao.createOrUpdate(elemento);
			}catch(SQLException e){
				throw new BdException(e.toString());
			}
		}
	}

	@Override
	public synchronized MiniBlasProfile getProfileById(int _id) throws BdException{
		MiniBlasProfile perfil = null;
		try{
			perfil = (MiniBlasProfile) perfilDao.queryForId(_id);
		}catch(SQLException e){
			throw new BdException(e.toString());
		}
		return perfil;
	}

	@Override
	public synchronized BaseElementList<MiniBlasProfile> getProfilesOrdered() throws BdException{
		BaseElementList<MiniBlasProfile> listaPerfiles = null;
		try{
			listaPerfiles = new BaseElementList<>(perfilDao.queryForAll());
			Collections.sort(listaPerfiles);
		}catch(SQLException e){
			throw new BdException(e.toString());
		}
		return listaPerfiles;
	}

	@Override
	public void deleteProfiles(BaseElementList<MiniBlasProfile> _profiles) throws BdException{
		try{
			perfilDao.delete(_profiles);
		}catch(SQLException e){
			throw new BdException(e.toString());
		}
	}



}
