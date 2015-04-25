package com.miniblas.persistence.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.miniblas.model.MiniBlasBag;
import com.miniblas.model.MiniBlasProfile;
import com.miniblas.model.variableWidgets.VariableSeekWidget;
import com.miniblas.model.variableWidgets.VariableSwitchWidget;
import com.miniblas.model.variableWidgets.VariableValueWidget;

import java.sql.SQLException;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper{

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "bd_miniblas.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 1;

	// the DAO object we use to access the SimpleData table
	private Dao<MiniBlasProfile, Integer> perfilDao;
	private Dao<MiniBlasBag, Integer> cestaDao;
	/*
		Variable Widgets DAO
	 */
	private Dao<VariableValueWidget, Integer> variableValueDao;
	private Dao<VariableSeekWidget, Integer> variableSeekWidgetDao;
	private Dao<VariableSwitchWidget, Integer> variableSwitchWidgetDao;


	public DBHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource){
		try{
			Log.i(DBHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, MiniBlasProfile.class);
			TableUtils.createTable(connectionSource, MiniBlasBag.class);
			TableUtils.createTable(connectionSource, VariableSeekWidget.class);
			TableUtils.createTable(connectionSource, VariableSwitchWidget.class);
			TableUtils.createTable(connectionSource, VariableValueWidget.class);
		}catch(SQLException e){
			Log.e(DBHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion){
		try{
			Log.i(DBHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, MiniBlasProfile.class, true);
			TableUtils.dropTable(connectionSource, MiniBlasBag.class, true);
			TableUtils.createTable(connectionSource, VariableSeekWidget.class);
			TableUtils.createTable(connectionSource, VariableSwitchWidget.class);
			TableUtils.createTable(connectionSource, VariableValueWidget.class);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		}catch(SQLException e){
			Log.e(DBHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	public Dao<MiniBlasProfile, Integer> getPerfilDao() throws SQLException{
		if(perfilDao == null){
			perfilDao = getDao(MiniBlasProfile.class);
		}
		return perfilDao;
	}


	public Dao<MiniBlasBag, Integer> getCestaDao() throws SQLException{
		if(cestaDao == null){
			cestaDao = getDao(MiniBlasBag.class);
		}
		return cestaDao;
	}

	public Dao<VariableSwitchWidget, Integer> getVariableSwitchWidgetDao() throws SQLException{
		if(variableSwitchWidgetDao == null){
			variableSwitchWidgetDao = getDao(VariableSwitchWidget.class);
		}
		return variableSwitchWidgetDao;
	}

	public Dao<VariableSeekWidget, Integer> getVariableSeekWidgetDao() throws SQLException{
		if(variableSeekWidgetDao == null){
			variableSeekWidgetDao = getDao(VariableSeekWidget.class);
		}
		return variableSeekWidgetDao;
	}

	public Dao<VariableValueWidget, Integer> getVariableValueWidgetDao() throws SQLException{
		if(variableValueDao == null){
			variableValueDao = getDao(VariableValueWidget.class);
		}
		return variableValueDao;
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close(){
		super.close();
		variableValueDao = null;
		variableSeekWidgetDao = null;
		variableSwitchWidgetDao = null;
		cestaDao = null;
		perfilDao = null;
	}
}
