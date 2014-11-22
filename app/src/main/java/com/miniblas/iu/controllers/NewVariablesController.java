package com.miniblas.iu.controllers;

import android.util.SparseBooleanArray;

import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.fragments.NewVariableElementsFragment;
import com.miniblas.iu.fragments.VariablesElementsFragment;
import com.miniblas.iu.fragments.base.OrdenableElementsFragment;
import com.miniblas.model.MiniBlasCesta;
import com.miniblas.model.MiniBlasItemVariable;
import com.miniblas.model.MiniBlasPerfil;
import com.miniblas.persistence.BdException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NewVariablesController extends BaseController<MiniBlasItemVariable> implements ListaVariablesListener.ObservadorListaVariables<ArrayList<MiniBlasItemVariable>> {

   // private ProfilesFragment profilesView;
    public static NewVariablesController instance;
    private MiniBlasCesta basket;

    public static NewVariablesController getInstance(AplicacionPrincipal _aplicacionPrincipal){
        if(instance == null) {
            instance = new NewVariablesController(_aplicacionPrincipal);
        }
        return instance;
    }

    private NewVariablesController(AplicacionPrincipal _aplicacionPrincipal){
        super(_aplicacionPrincipal);
        application.setListaNombresObserver(this);
    }

    @Override
    public void resetController() {

    }

    public void onViewChange(OrdenableElementsFragment _vista, int _id_profile, int _id_basket){
        try {
            basket = application.getBasketStorage().getBasketById(_id_basket);
            basket.setPerfil(application.getProfileStorage().getProfileByid(basket.getPerfil().getId()));
        } catch (BdException e) {
            e.printStackTrace();
        }
		super.onViewChange(_vista);
        application.variablesRequest();
        vista.showIconLoading();
	}


    @Override
    protected void loadPreferences(){
	}

    @Override
    protected List<MiniBlasItemVariable> getElementsToRepository() throws BdException {
        return new ArrayList<MiniBlasItemVariable>();
    }

    @Override
    protected void saveElementsToRepository(List<MiniBlasItemVariable> _elements) throws BdException {
        //application.getVariableStorage().persistCollection(_elements);
    }

    @Override
    protected void deleteElements(List<MiniBlasItemVariable> elements) throws BdException {
        //application.getVariableStorage().deleteItemVariables(elements);
    }

    @Override
    public void OnReceivedVariablesList(ArrayList<MiniBlasItemVariable> _variables) {
        Comparator<MiniBlasItemVariable> comparator = new Comparator<MiniBlasItemVariable>() {

            @Override
            public int compare(MiniBlasItemVariable lhs, MiniBlasItemVariable rhs) {
                return rhs.getNombre().compareTo(lhs.getNombre());
            }
        };
        Collections.sort(_variables, comparator);
        vista.getAdapter().addAll(_variables);
        //consultar base de datos para ver cuales tiene seleccionadas
        int i;

        for(MiniBlasItemVariable variable:basket.getVariables()){
            if((i = ((NewVariableElementsFragment)vista).getAdapter().indexOf(variable)) != -1){
                ((NewVariableElementsFragment)vista).addItemSelected(i);
            }
        }
        vista.refreshList();
        vista.dismissIconLoading();
    }
    public void guardarVariables(){
        final SparseBooleanArray sba = vista.getListView().getCheckedItemPositions();
        application.addSingleTask(new Runnable() {
            @Override
            public void run() {
                    try {
                        application.getVariableStorage().deleteItemVariables(basket.getVariables());
                    } catch (BdException e) {
                        e.printStackTrace();
                    }

                for(int i =0; i<sba.size();i++){
                    if (sba.valueAt(i)) {
                        try {
                            MiniBlasItemVariable variable = vista.getAdapter().getItem(sba.keyAt(i));
                            variable.setCesta(basket);
                            application.getVariableStorage().persist(variable);

                        } catch (BdException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        ((NewVariableElementsFragment)vista).setResultIU(VariablesElementsFragment.RESULT_OK);
    }
}
