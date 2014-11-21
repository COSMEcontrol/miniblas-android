package com.miniblas.iu.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Toast;
import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.AcercaDe;
import com.miniblas.iu.FabActivity;
import com.miniblas.iu.Preferences;
import com.miniblas.iu.alertdialog.AlertDialogNuevoPerfil;
import com.miniblas.iu.cab.PerfilCab;
import com.miniblas.iu.controllers.ProfilesController;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.fragments.base.OrdenableElementsFragment;
import com.miniblas.iu.utils.SeleccionableRendererAdapter;
import com.miniblas.model.MiniBlasPerfil;
import com.miniblas.perfistence.ormlite.Constantes;


import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by alberto on 13/11/14.
 */
public class ProfilesElementsFragment extends OrdenableElementsFragment<MiniBlasPerfil> {
    @Inject
    public SeleccionableRendererAdapter<MiniBlasPerfil> adaptador;

    private ProfilesController controller;
    private android.view.Menu menu;
    private android.view.ActionMode mode;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AplicacionPrincipal application =  (AplicacionPrincipal) getActivity().getApplication();
        application.inject(this);
        setAdapter(adaptador);
        setListAdapter(getAdapter());
        //getListView().setOnItemClickListener(new OnPerfilClickedListener());
        PerfilCab pc = new PerfilCab();
        //pc.setContext(getActivity());
        ((FabActivity) getActivity()).setCab(pc);
        pc.setFragment(this);
        controller = ProfilesController.getInstance(application);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        (((ActionBarActivity)getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        FabActivity act = (FabActivity) getActivity();
        act.setTitle(getResources().getString(R.string.lista_perfiles));
        act.setFabListener(new FabActivity.FabListener() {
            @Override
            public void onFabPressed() {
                AlertDialogNuevoPerfil.newInstance(controller,new ArrayList<MiniBlasPerfil>()).show(getFragmentManager(),"");
            }
        });
        controller.onViewChange(this);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Adapter adapter = parent.getAdapter();
                MiniBlasPerfil profile = (MiniBlasPerfil) adapter.getItem(position);
                Bundle data = new Bundle();
                data.putInt(Constantes.PROFILE_ID,profile.getId());

                //getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getFragmentManager().executePendingTransactions();
                FragmentTransaction trans = getFragmentManager().beginTransaction();
                trans.setCustomAnimations(R.anim.left_in, R.anim.left_out,R.anim.right_in, R.anim.right_out);
                BasketsElementsFragment fragment = new BasketsElementsFragment();
                fragment.setArguments(data);
                trans.replace(R.id.container, fragment);
                trans.addToBackStack("a");
                trans.commit();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        controller.saveElements();

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_perfiles, menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_anadir_perfil:
                AlertDialogNuevoPerfil.newInstance(controller,new ArrayList<MiniBlasPerfil>()).show(getFragmentManager(), "");
                return true;
            case R.id.Acercade:
                Intent i = new Intent(getActivity(), AcercaDe.class);
                startActivity(i);
                return true;
            case R.id.menu_ajustes:
                Intent intent = new Intent(getActivity(), Preferences.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void setConnectIcon() {
        //no aplicable
    }

    @Override
    public void setDisconnectIcon() {
        //no aplicable
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
    public BaseController<MiniBlasPerfil> getController(){
        return controller;
    }


}
