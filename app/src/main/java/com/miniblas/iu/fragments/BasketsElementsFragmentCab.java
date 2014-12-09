package com.miniblas.iu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Toast;

import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.AcercaDe;
import com.miniblas.iu.FabActivity;
import com.miniblas.iu.Preferences;
import com.miniblas.iu.alertdialog.AlertDialogNuevaCesta;
import com.miniblas.iu.cab.BasketCab;
import com.miniblas.iu.controllers.BasketsController;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.fragments.base.CabOrdenableElementsFragment;
import com.miniblas.iu.utils.SeleccionableRendererAdapter;
import com.miniblas.model.MiniBlasCesta;
import com.miniblas.perfistence.ormlite.Constantes;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by alberto on 13/11/14.
 */
public class BasketsElementsFragmentCab extends CabOrdenableElementsFragment<MiniBlasCesta>{
	@Inject
	public SeleccionableRendererAdapter<MiniBlasCesta> adaptador;

	private BasketsController controller;
	private Menu menu;
	private android.view.ActionMode mode;
	private MenuItem iconoEstado;
	private MenuItem checkAutoConexion;
	private BasketCab basketCab;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		AplicacionPrincipal application = (AplicacionPrincipal) getActivity().getApplication();
		application.inject(this);
		setAdapter(adaptador);
		setListAdapter(getAdapter());
		//getListView().setOnItemClickListener(new OnPerfilClickedListener());
		controller = BasketsController.getInstance(application);
		basketCab = new BasketCab();
		basketCab.setFragment(this);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		FabActivity act = (FabActivity) getActivity();
		act.setTitle(getResources().getString(R.string.listaCestas));
		act.setFabListener(new FabActivity.FabListener(){
			@Override
			public void onFabPressed(){
				AlertDialogNuevaCesta.newInstance(controller, new ArrayList<MiniBlasCesta>(), controller.getProfile()).show(getFragmentManager(), "");
			}
		});
		Bundle extras = getArguments();
		int id_profile = extras.getInt(Constantes.PROFILE_TABLE_NAME);
		controller.onViewChange(this, id_profile);
		(((ActionBarActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				Adapter adapter = parent.getAdapter();
				MiniBlasCesta basket = (MiniBlasCesta) adapter.getItem(position);
				Bundle data = new Bundle();
				data.putInt(Constantes.PROFILE_TABLE_NAME, controller.getProfile().getId());
				data.putInt(Constantes.BASKET_TABLE_NAME, basket.getId());
				System.out.println("Id del perfil: " + controller.getProfile().getId());
				System.out.println("Nombre cesta: " + basket.getId());
				//getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				// getFragmentManager().executePendingTransactions();
				FragmentTransaction trans = getFragmentManager().beginTransaction();
				trans.setCustomAnimations(R.anim.left_in, R.anim.left_out, R.anim.right_in, R.anim.right_out);
				VariablesElementsFragmentCab fragment = new VariablesElementsFragmentCab();
				fragment.setArguments(data);
				trans.replace(R.id.container, fragment);
				trans.addToBackStack(null);
				trans.commit();
			}
		});
		basketCab.setListView(getListView());
		setCabInFragment(basketCab);
	}

	@Override
	public void onStop(){
		super.onStop();
		controller.saveElements();

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		getActivity().getMenuInflater().inflate(R.menu.menu_cestas, menu);
		checkAutoConexion = (MenuItem) menu.findItem(R.id.menu_cestas_autoconexion);
		iconoEstado = (MenuItem) menu.findItem(R.id.estado);
		((AplicacionPrincipal) getActivity().getApplication()).setIconObserver(controller);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()){
			case android.R.id.home:
				((FabActivity) getActivity()).backStackFragment();
				return (true);
			case R.id.menu_anadir_cesta:
				AlertDialogNuevaCesta.newInstance(controller, new ArrayList<MiniBlasCesta>(), controller.getProfile()).show(getFragmentManager(), "");
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
	public void setConnectIcon(){
		runOnUiThread(new Runnable(){
			@Override
			public void run(){
				if(iconoEstado != null){
					iconoEstado.setIcon(R.drawable.conectado);
				}
			}
		});
	}

	@Override
	public void setDisconnectIcon(){
		runOnUiThread(new Runnable(){
			@Override
			public void run(){
				if(iconoEstado != null){
					iconoEstado.setIcon(R.drawable.desconectado);
				}
			}
		});
	}


	@Override
	public void msgErrorSavingElementsToBD(){
		runOnUiThread(new Runnable(){
			@Override
			public void run(){
				Toast.makeText(getActivity(), getResources().getString(R.string.errorAccessBd), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void msgErrorGettingElementsInBD(){
		runOnUiThread(new Runnable(){
			@Override
			public void run(){
				Toast.makeText(getActivity(), getResources().getString(R.string.errorAccessBd), Toast.LENGTH_SHORT).show();
			}
		});
	}


	@Override
	public void msgButtonNewSave(){
		runOnUiThread(new Runnable(){
			@Override
			public void run(){
				Toast.makeText(getActivity(), getResources().getString(R.string.cestaAñadida), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void msgButtonNewCancel(){
		runOnUiThread(new Runnable(){
			public void run(){
				Toast.makeText(getActivity(), getResources().getString(R.string.cestaDescartada), Toast.LENGTH_SHORT).show();
			}
		});

	}

	@Override
	public void msgButtonEditSave(){
		runOnUiThread(new Runnable(){
			@Override
			public void run(){
				Toast.makeText(getActivity(), getResources().getString(R.string.cestaModificada), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void msgButtonEditCancel(){
		runOnUiThread(new Runnable(){
			public void run(){
				Toast.makeText(getActivity(), getResources().getString(R.string.cestaNoModificada), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void msgErrorDeleteElementsInBD(){
		runOnUiThread(new Runnable(){
			public void run(){
				Toast.makeText(getActivity(), getResources().getString(R.string.errorAccessBd), Toast.LENGTH_SHORT).show();
			}
		});
	}


	public void runOnUiThread(Runnable runnable){
		if(getActivity() != null){
			getActivity().runOnUiThread(runnable);
		}
	}

	public BaseController<MiniBlasCesta> getController(){
		return controller;
	}


}
