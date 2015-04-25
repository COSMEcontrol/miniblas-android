package com.miniblas.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.miniblas.model.base.BaseElement;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;
import com.miniblas.persistence.ormlite.Contract;

import java.util.ArrayList;
import java.util.List;

@DatabaseTable(tableName = Contract.BASKET_TABLE_NAME)
public class MiniBlasBag extends BaseElement{


	@DatabaseField(columnName = Contract.BASKET_REFRESHPERIOD, canBeNull = false)
	private int refreshPeriod;
	//clave ajena
	@DatabaseField(columnName = Contract.BASKET_PROFILE, foreign = true)
	private MiniBlasProfile profile;
	@ForeignCollectionField(columnName = Contract.BASKET_VARIABLE_LIST)
	ForeignCollection<BaseVariableWidget> variables;


	public MiniBlasBag(){
		super();
		// needed by ormlite
	}

	public MiniBlasBag(String _name){
		super(_name);
	}

	public MiniBlasBag(String _name, int _period){
		super(_name);
		this.refreshPeriod = _period;
	}

	/*
	public static final MiniBlasCesta convertToMiniBlasCesta(Basket _cesta){
		return new MiniBlasCesta(_cesta.getNombre(), _cesta.getRefreshPeriod());
	}

	*/
	public ArrayList<BaseVariableWidget> getVariables(){
		return new ArrayList<BaseVariableWidget>(variables);
	}

	public void setVariables(List<BaseVariableWidget> variables){
		this.variables.clear();
		this.variables.addAll(variables);
	}


	public void setProfile(MiniBlasProfile profile){
		this.profile = profile;
	}

	public int getRefreshPeriod(){
		return refreshPeriod;
	}

	public MiniBlasProfile getProfile(){
		return profile;
	}

	public void setRefreshPeriod(int _periodoRefresco){
		this.refreshPeriod = _periodoRefresco;
	}


	@Override
	public boolean equals(Object object){
		boolean same = false;
		if(object != null){
			same = getNameElement().equals(((MiniBlasBag) object).getNameElement());
		}

		return same;
	}

	@Override
	public String toString(){
		return "Cesta [id=" + id + ", nombre=" + getNameElement() + ", refreshPeriod=" + refreshPeriod + ", profile=" + profile + "]";
	}

}
