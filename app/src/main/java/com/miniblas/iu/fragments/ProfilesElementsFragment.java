package com.miniblas.iu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.AcercaDe;
import com.miniblas.iu.FabActivity;
import com.miniblas.iu.Preferences;
import com.miniblas.iu.alertdialog.AlertDialogEditarPerfil;
import com.miniblas.iu.alertdialog.AlertDialogNuevoPerfil;
import com.miniblas.iu.cab.PerfilCab;
import com.miniblas.iu.controllers.ProfilesController;
import com.miniblas.iu.fragments.base.OrdenableElementsFragment;
import com.miniblas.iu.fragments.base.onDropListener;
import com.miniblas.iu.utils.SeleccionableRendererAdapter;
import com.miniblas.model.MiniBlasPerfil;
import com.mobeta.android.dslv.DragSortListView;

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
        super.setAdapter(adaptador);
        setListAdapter(adapter);
        //getListView().setOnItemClickListener(new OnPerfilClickedListener());
        controller = ProfilesController.getInstance(application);
        controller.onViewChange(this);
        PerfilCab pc = new PerfilCab();
        //pc.setContext(getActivity());
        ((FabActivity) getActivity()).setCab(pc);
        pc.setFragment(this);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        FabActivity act = (FabActivity) getActivity();
        act.setTitle(getResources().getString(R.string.lista_perfiles));
        act.setFabListener(new FabActivity.FabListener() {
            @Override
            public void onFabPressed() {
                AlertDialogNuevoPerfil.newInstance(controller,new ArrayList<MiniBlasPerfil>()).show(getFragmentManager(),"");
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
    public void refreshList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void setTitle(final String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().setTitle(title);
            }
        });
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
    public void showIconLoading() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //setProgressBarVisibility(true);

            }
        });
    }

    @Override
    public void dismissIconLoading() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //setProgressBarVisibility(false);

            }
        });
    }

    @Override
    public void recuperarEstado() {

    }

    @Override
    public void msgErrorSavingElementsToBD() {

    }

    @Override
    public void msgErrorGettingElementsInBD() {

    }

    @Override
    public void clearSelecction() {
        adapter.clearSelection();
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


}
