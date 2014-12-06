package com.miniblas.persistence;

import com.j256.ormlite.dao.Dao;
import com.miniblas.model.MiniBlasPerfil;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

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
	public synchronized void persist(final MiniBlasPerfil _profile) throws BdException{
		try{
			perfilDao.createOrUpdate(_profile);
		}catch(SQLException e){
			throw new BdException(e.toString());
		}
	}

	@Override
	public synchronized void persistCollection(final List<MiniBlasPerfil> _collection) throws BdException{
		for(MiniBlasPerfil elemento : _collection){
			try{
				perfilDao.createOrUpdate(elemento);
			}catch(SQLException e){
				throw new BdException(e.toString());
			}
		}

	}

	@Override
	public synchronized MiniBlasPerfil getProfileByid(int _id) throws BdException{
		MiniBlasPerfil perfil = null;
		try{
			perfil = (MiniBlasPerfil) perfilDao.queryForId(_id);
		}catch(SQLException e){
			throw new BdException(e.toString());
		}
		return perfil;
	}

	@Override
	public synchronized List<MiniBlasPerfil> getProfilesOrdered() throws BdException{
		List<MiniBlasPerfil> listaPerfiles = null;
		try{
			listaPerfiles = perfilDao.queryForAll();
			Collections.sort(listaPerfiles);
		}catch(SQLException e){
			throw new BdException(e.toString());
		}
		return listaPerfiles;
	}

	@Override
	public synchronized void deleteProfiles(List<MiniBlasPerfil> _profiles) throws BdException{
		try{
			perfilDao.delete(_profiles);
		}catch(SQLException e){
			throw new BdException(e.toString());
		}
	}


}
