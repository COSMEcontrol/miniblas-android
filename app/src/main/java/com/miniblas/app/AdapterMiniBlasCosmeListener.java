package com.miniblas.app;
import com.arcadio.*;
import com.arcadio.modelo.Cesta;
import com.arcadio.modelo.ItemVariable;
import com.miniblas.model.MiniBlasCesta;
import com.miniblas.model.MiniBlasItemVariable;

import java.util.ArrayList;

public class AdapterMiniBlasCosmeListener implements CosmeListener{
	private MiniBlasCosmeListener miniBlasCosmeListener;
	
	public AdapterMiniBlasCosmeListener(MiniBlasCosmeListener _miniBlasCosmeListener){
		this.miniBlasCosmeListener=_miniBlasCosmeListener;
	}

	@Override
	public void notificarRefrescoVariables(String _nombreCesta,
			ArrayList<ItemVariable> _listaVariables) {
		ArrayList<MiniBlasItemVariable> listaMiniBlasVariables = 
				new ArrayList<MiniBlasItemVariable>(_listaVariables.size());
		for(ItemVariable variable:_listaVariables){
			listaMiniBlasVariables.add(MiniBlasItemVariable.convertToMiniBlasVariable(variable));
		}

		miniBlasCosmeListener.notificarRefrescoVariables(_nombreCesta, listaMiniBlasVariables);
	}

	@Override
	public void notificarEstadoConexion(EstadosCosme _estado) {
		miniBlasCosmeListener.notificarEstadoConexion(_estado);
		
	}

	@Override
	public void notificarError(CosmeError _error) {
		miniBlasCosmeListener.notificarError(_error);
		
	}

	@Override
	public void notificarListaNombres(ArrayList<ItemVariable> listaNombres) {
		ArrayList<MiniBlasItemVariable> listaMiniBlasVariables = 
				new ArrayList<MiniBlasItemVariable>(listaNombres.size());
		for(ItemVariable variable:listaNombres){
			listaMiniBlasVariables.add(MiniBlasItemVariable.convertToMiniBlasVariable(variable));
		}
		miniBlasCosmeListener.notificarListaNombres(listaMiniBlasVariables);
		
	}

	@Override
	public void notificarIsNumeric(ItemVariable variable) {
		miniBlasCosmeListener.notificarIsNumeric(MiniBlasItemVariable.convertToMiniBlasVariable(variable));
		
	}

	@Override
	public void notificarCestaCreada(Cesta cesta) {
		miniBlasCosmeListener.notificarCestaCreada(MiniBlasCesta.convertToMiniBlasCesta(cesta));
		
	}

	@Override
	public void notificarNomACesta(Cesta cesta, ItemVariable variable) {
		miniBlasCosmeListener.notificarNomACesta(MiniBlasCesta.convertToMiniBlasCesta(cesta),
				MiniBlasItemVariable.convertToMiniBlasVariable(variable));
		
	}

}
