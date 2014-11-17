package com.miniblas.app;

import java.util.ArrayList;

import com.arcadio.CosmeError;
import com.arcadio.EstadosCosme;
import com.miniblas.model.MiniBlasCesta;
import com.miniblas.model.MiniBlasItemVariable;

public interface MiniBlasCosmeListener {
	 	public void notificarRefrescoVariables (String _nombreCesta, ArrayList<MiniBlasItemVariable> _listaVariables);
	   
	    public void notificarEstadoConexion (EstadosCosme _estado);
	   
	    public void notificarError (CosmeError _error);
	    
	   // public void notificarEvento(EstadosCosme _codEvento, Telegrama _tlg);
	    public void notificarListaNombres(ArrayList<MiniBlasItemVariable> listaNombres);
	    public void notificarIsNumeric(MiniBlasItemVariable variable);
	    public void notificarCestaCreada(MiniBlasCesta cesta);
	    public void notificarNomACesta(MiniBlasCesta cesta, MiniBlasItemVariable variable);
}
