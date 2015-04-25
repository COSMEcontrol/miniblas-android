package com.miniblas.iu.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.FabActivity;
import com.miniblas.iu.builder.BagRendererBuilder;
import com.miniblas.iu.cab.BasketCab;
import com.miniblas.iu.controllers.BagController;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.dialog.alert.AlertDialogNewBag;
import com.miniblas.iu.fragments.base.CabOrdenableElementsFragment;
import com.miniblas.iu.renderers.BagRenderer;
import com.miniblas.iu.utils.SeleccionableBaseElementsListRendererAdapter;
import com.miniblas.model.MiniBlasBag;
import com.miniblas.model.base.BaseElementList;
import com.miniblas.persistence.ormlite.Contract;
import com.pedrogomez.renderers.Renderer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alberto on 13/11/14.
 */
public class BagElementsFragmentCab extends CabOrdenableElementsFragment<MiniBlasBag>{

	private BagController controller;
	private MenuItem iconoEstado;
	private MenuItem checkAutoConexion;
	private BasketCab basketCab;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		AplicacionPrincipal application = (AplicacionPrincipal) getActivity().getApplication();
		//application.inject(this);
		setAdapter(provideBagRendererAdapter(LayoutInflater.from(getActivity()), provideBagRendererBuilder()));
		setListAdapter(getAdapter());
		//getListView().setOnItemClickListener(new OnPerfilClickedListener());
		controller = BagController.getInstance(application);
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
				AlertDialogNewBag.newInstance(controller, getAdapter().getElements(), controller.getProfile()).show(getFragmentManager(), "");
			}
		});
		Bundle extras = getArguments();
		int id_profile = extras.getInt(Contract.PROFILE_TABLE_NAME);
		controller.onViewChange(this, id_profile);
		//(((ActionBarActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				MiniBlasBag basket = getAdapter().getItem(position);
				if(basketCab.isActive()){
					boolean added = !getAdapter().isSelected(basket);
					if (added){
						getAdapter().addNewSelection(basket);
						basketCab.addElement(basket);
					}
					else{
						getAdapter().removeSelection(basket);
						basketCab.removeElement(basket);
					}
					getAdapter().notifyDataSetChanged();
				}else{
					Bundle data = new Bundle();
					data.putInt(Contract.PROFILE_TABLE_NAME, controller.getProfile().getId());
					data.putInt(Contract.BASKET_TABLE_NAME, basket.getId());
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
			}
		});
		basketCab.setListView(getListView());
		setCabInFragment(basketCab);
		Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
		mToolbar.setNavigationIcon(R.drawable.ic_arrow_left_white_24dp);
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
		controller.autoConexionControl();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()){
			case R.id.menu_cestas_autoconexion:
				controller.onClickAutoConexion();
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

	public BaseController<MiniBlasBag> getController(){
		return controller;
	}

	public void setChekedAutoConect(){
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				checkAutoConexion.setChecked(true);
			}
		});

	}
	public void setNotChekedAutoConect(){
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				checkAutoConexion.setChecked(false);
			}
		});

	}

	SeleccionableBaseElementsListRendererAdapter<MiniBlasBag> provideBagRendererAdapter(LayoutInflater layoutInflater, BagRendererBuilder rendererBuilder){
		BaseElementList<MiniBlasBag> cestaCollection = new BaseElementList<MiniBlasBag>();
		final SeleccionableBaseElementsListRendererAdapter<MiniBlasBag> adapter = new SeleccionableBaseElementsListRendererAdapter<MiniBlasBag>(layoutInflater, rendererBuilder, cestaCollection);
		return adapter;
	}

	BagRendererBuilder provideBagRendererBuilder(){
		return new BagRendererBuilder(getPrototypesBag());
	}

	private List<Renderer<MiniBlasBag>> getPrototypesBag(){
		List<Renderer<MiniBlasBag>> prototypes = new LinkedList<Renderer<MiniBlasBag>>();
		BagRenderer bagRenderer = new BagRenderer(getActivity());
		prototypes.add(bagRenderer);
		return prototypes;
	}
}
