package com.miniblas.iu.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.FabActivity;
import com.miniblas.iu.controllers.NewVariablesController;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.fragments.base.CabOrdenableElementsFragment;
import com.miniblas.iu.utils.SeleccionableRendererAdapter;
import com.miniblas.model.MiniBlasItemVariable;
import com.miniblas.perfistence.ormlite.Constantes;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by alberto on 13/11/14.
 */
public class NewVariableElementsFragmentCab extends CabOrdenableElementsFragment<MiniBlasItemVariable>{
	@Inject
	@Named("provideNewVariablesRendererAdapter")
	public SeleccionableRendererAdapter<MiniBlasItemVariable> adaptador;

	private NewVariablesController controller;
	private Menu menu;
	private android.view.ActionMode mode;
	private FabActivity act;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		AplicacionPrincipal application = (AplicacionPrincipal) getActivity().getApplication();
		application.inject(this);
		setAdapter(adaptador);
		setListAdapter(getAdapter());
		//getListView().setOnItemClickListener(new OnPerfilClickedListener());
		controller = NewVariablesController.getInstance(application);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		setHasOptionsMenu(true);
		act = (FabActivity) getActivity();
		act.disableFab(false);
		act.setTitle(getResources().getString(R.string.nuevaVariable));
		act.setFabListener(new FabActivity.FabListener(){
			@Override
			public void onFabPressed(){
				controller.guardarVariables();
				((FabActivity) getActivity()).backStackFragment();
				act.setImageFab(R.drawable.ic_action_content_new);
			}
		});
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				adaptador.notifyDataSetChanged();
			}
		});
		act.setImageFab(R.drawable.ic_action_save);
		Bundle extras = getArguments();
		int id_basket = extras.getInt(Constantes.BASKET_TABLE_NAME);
		int id_profile = extras.getInt(Constantes.PROFILE_TABLE_NAME);
		controller.onViewChange(this, id_profile, id_basket);
		(((ActionBarActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onStop(){
		super.onStop();
		controller.saveElements();

	}

	@Override
	public View provideDropView(){
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.lyt_fragment_no_ordenable, null, false);
		return view;

	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		getActivity().getMenuInflater().inflate(R.menu.menu_nueva_variable, menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()){
			case android.R.id.home:
				((FabActivity) getActivity()).backStackFragment();
				act.setImageFab(R.drawable.ic_action_content_new);
				return (true);
			case R.id.save_variables:
				controller.guardarVariables();
				((FabActivity) getActivity()).backStackFragment();
				act.setImageFab(R.drawable.ic_action_content_new);
				return true;
			default:
				return false;
		}
	}

	@Override
	public void setConnectIcon(){
		//no aplicable
	}

	@Override
	public void setDisconnectIcon(){
		//no aplicable
	}

	public void setResultIU(int _result){
		getActivity().setResult(_result);
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
				Toast.makeText(getActivity(), getResources().getString(R.string.perfilGuardado), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void msgButtonNewCancel(){
		runOnUiThread(new Runnable(){
			public void run(){
				Toast.makeText(getActivity(), getResources().getString(R.string.perfilDescartado), Toast.LENGTH_SHORT).show();
			}
		});

	}

	@Override
	public void msgButtonEditSave(){
		runOnUiThread(new Runnable(){
			@Override
			public void run(){
				Toast.makeText(getActivity(), getResources().getString(R.string.perfilEditado), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void msgButtonEditCancel(){
		runOnUiThread(new Runnable(){
			public void run(){
				Toast.makeText(getActivity(), getResources().getString(R.string.perfilNoModificado), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void msgErrorDeleteElementsInBD(){
		runOnUiThread(new Runnable(){
			public void run(){
				Toast.makeText(getActivity(), getResources().getString(R.string.errorDelPerfilBd), Toast.LENGTH_SHORT).show();
			}
		});
	}


	public void runOnUiThread(Runnable runnable){
		if(getActivity() != null){
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
		runOnUiThread(new Runnable(){
			@Override
			public void run(){
				if(getView() != null){
					getListView().setItemChecked(_position, true);
				}
			}
		});
	}

	public SeleccionableRendererAdapter<MiniBlasItemVariable> getAdapter(){
		return adaptador;
	}

}
