package com.miniblas.iu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.AcercaDe;
import com.miniblas.iu.FabActivity;
import com.miniblas.iu.Preferences;
import com.miniblas.iu.alertdialog.AlertDialogNuevoPerfil;
import com.miniblas.iu.cab.PerfilCab;
import com.miniblas.iu.controllers.NewVariablesController;
import com.miniblas.iu.controllers.ProfilesController;
import com.miniblas.iu.controllers.VariablesController;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.fragments.base.OrdenableElementsFragment;
import com.miniblas.iu.utils.SeleccionableRendererAdapter;
import com.miniblas.model.MiniBlasItemVariable;
import com.miniblas.model.MiniBlasPerfil;
import com.miniblas.perfistence.ormlite.Constantes;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by alberto on 13/11/14.
 */
public class NewVariableElementsFragment extends OrdenableElementsFragment<MiniBlasItemVariable> {
    @Inject
    public SeleccionableRendererAdapter<MiniBlasItemVariable> adaptador;

    private NewVariablesController controller;
    private Menu menu;
    private android.view.ActionMode mode;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AplicacionPrincipal application =  (AplicacionPrincipal) getActivity().getApplication();
        application.inject(this);
        setAdapter(adaptador);
        setListAdapter(getAdapter());
        //getListView().setOnItemClickListener(new OnPerfilClickedListener());
        controller = NewVariablesController.getInstance(application);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        setHasOptionsMenu(true);
        FabActivity act = (FabActivity) getActivity();
        act.disableFab(true);
        act.setTitle(getResources().getString(R.string.nuevaVariable));
        act.setFabListener(new FabActivity.FabListener() {
            @Override
            public void onFabPressed() {
               // AlertDialogNuevoPerfil.newInstance(controller,new ArrayList<MiniBlasPerfil>()).show(getFragmentManager(),"");
            }
        });
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adaptador.notifyDataSetChanged();
            }
        });
        Bundle extras = getArguments();
        int id_basket = extras.getInt(Constantes.BASKET_ID);
        int id_profile = extras.getInt(Constantes.PROFILE_ID);
        controller.onViewChange(this, id_profile, id_basket);
    }
    @Override
    public void onStop() {
        super.onStop();
        controller.saveElements();

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_nueva_variable, menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                ((FabActivity) getActivity()).backStackFragment();
                return(true);
            case R.id.save_variables:
                controller.guardarVariables();
                ((FabActivity) getActivity()).backStackFragment();
                return true;
            default:
                return false;
        }
    }


    @Override
    public void setConnectIcon() {
        //no aplicable
    }

    @Override
    public void setDisconnectIcon() {
        //no aplicable
    }
    public void setResultIU(int _result){
        getActivity().setResult(_result);
    }

    @Override
    public void msgErrorSavingElementsToBD() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.errorAccessBd), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void msgErrorGettingElementsInBD() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.errorAccessBd), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void msgButtonNewSave() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.perfilGuardado), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void msgButtonNewCancel() {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.perfilDescartado), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void msgButtonEditSave() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.perfilEditado), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void msgButtonEditCancel() {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.perfilNoModificado), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void msgErrorDeleteElementsInBD() {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.errorDelPerfilBd), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void runOnUiThread(Runnable runnable){
        if(getActivity()!=null){
            getActivity().runOnUiThread(runnable);
        }
    }
    /*
    public void addItemSelected(final int _position){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getListView().setItemChecked(_position, true);
            }
        });
    }
    */
    public BaseController<MiniBlasItemVariable> getController(){
        return controller;
    }

    public void addItemSelected(final int _position){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getListView().setItemChecked(_position, true);
            }
        });
    }
    public SeleccionableRendererAdapter<MiniBlasItemVariable> getAdapter(){
        return adaptador;
    }

}
