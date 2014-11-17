package com.miniblas.perfistence.ormlite;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.miniblas.model.MiniBlasCesta;
import com.miniblas.model.MiniBlasItemVariable;
import com.miniblas.model.MiniBlasPerfil;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "bd_miniblas.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 1;

	// the DAO object we use to access the SimpleData table
	private Dao<MiniBlasPerfil, Integer> perfilDao;
	private Dao<MiniBlasCesta, Integer> cestaDao;
	private Dao<MiniBlasItemVariable, Integer> variableDao;
	

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DBHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, MiniBlasPerfil.class);
			TableUtils.createTable(connectionSource, MiniBlasCesta.class);
			TableUtils.createTable(connectionSource, MiniBlasItemVariable.class);
		} catch (SQLException e) {
			Log.e(DBHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(DBHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, MiniBlasPerfil.class, true);
			TableUtils.dropTable(connectionSource, MiniBlasCesta.class, true);
			TableUtils.dropTable(connectionSource, MiniBlasItemVariable.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DBHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	public Dao<MiniBlasPerfil, Integer> getPerfilDao() throws SQLException{
		if (perfilDao == null){
			perfilDao = getDao(MiniBlasPerfil.class);
		}
		return perfilDao;
	}
	
	
	public Dao<MiniBlasCesta, Integer> getCestaDao() throws SQLException{
		if (cestaDao == null){
			cestaDao = getDao(MiniBlasCesta.class);
		}
		return cestaDao;
	}
	
	public Dao<MiniBlasItemVariable, Integer> getVariableDao() throws SQLException{
		if (variableDao == null){
			variableDao = getDao(MiniBlasItemVariable.class);
		}
		return variableDao;
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		variableDao = null;
		cestaDao = null;
		perfilDao = null;
	}
}
