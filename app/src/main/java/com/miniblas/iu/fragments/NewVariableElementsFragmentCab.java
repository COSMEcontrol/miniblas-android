package com.miniblas.iu.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.FabActivity;
import com.miniblas.iu.builder.NewVariableRendererBuilder;
import com.miniblas.iu.controllers.NewVariablesController;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.fragments.base.CabOrdenableElementsFragment;
import com.miniblas.iu.renderers.NewVariableRenderer;
import com.miniblas.iu.utils.SeleccionableBaseElementsListRendererAdapter;
import com.miniblas.model.base.BaseElementList;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;
import com.miniblas.persistence.ormlite.Contract;
import com.pedrogomez.renderers.Renderer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alberto on 13/11/14.
 */
public class NewVariableElementsFragmentCab extends CabOrdenableElementsFragment<BaseVariableWidget>{


	private NewVariablesController controller;
	private FabActivity act;
	private VariablesElementsFragmentCab pFragment;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		AplicacionPrincipal application = (AplicacionPrincipal) getActivity().getApplication();
		//application.inject(this);
		setAdapter(provideNuevaVariableRendererAdapter(LayoutInflater.from(getActivity()), provideNewVariableRendererBuilder()));
		setListAdapter(getAdapter());
		//getListView().setOnItemClickListener(new OnPerfilClickedListener());
		controller = NewVariablesController.getInstance(application);
		try {
			pFragment = (VariablesElementsFragmentCab) getTargetFragment();
		} catch (Exception e) {
			throw new ClassCastException("Imposible hacer cast con el fragment padre");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()){
			case android.R.id.home:
				setResultCode(VariablesElementsFragmentCab.NO_RESULT, null);
				((FabActivity) getActivity()).backStackFragment();
				return (true);
		}

		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		//getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		setHasOptionsMenu(true);
		act = (FabActivity) getActivity();
		act.setTitle(getResources().getString(R.string.seleccionaVariable));
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
				getAdapter().notifyDataSetChanged();
			}
		});
		act.setImageFab(R.drawable.ic_action_save);
		Bundle extras = getArguments();
		int id_basket = extras.getInt(Contract.BASKET_TABLE_NAME);
		int id_profile = extras.getInt(Contract.PROFILE_TABLE_NAME);
		controller.onViewChange(this, id_profile, id_basket);
		//(((ActionBarActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
		mToolbar.setNavigationIcon(R.drawable.ic_arrow_left_white_24dp);
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				BaseVariableWidget element = getAdapter().getItem(position);
				boolean added = !getAdapter().isSelected(element);
				if(added){
					getAdapter().addNewSelection(element);
				}else{
					getAdapter().removeSelection(element);
				}


			}
		});
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
	public void setResultCode(int resultCode, ArrayList<BaseVariableWidget> variableList){
		pFragment.onSelectVariablesResult(getTargetRequestCode(), resultCode, variableList);
	}


	@Override
	public void setConnectIcon(){
	}

	@Override
	public void setDisconnectIcon(){
	}

	@Override
	public void msgErrorSavingElementsToBD(){
	}

	@Override
	public void msgErrorGettingElementsInBD(){
	}


	@Override
	public void msgButtonNewSave(){
	}

	@Override
	public void msgButtonNewCancel(){
	}

	@Override
	public void msgButtonEditSave(){
	}

	@Override
	public void msgButtonEditCancel(){
	}

	@Override
	public void msgErrorDeleteElementsInBD(){
	}


	public void runOnUiThread(Runnable runnable){
		if(getActivity() != null){
			getActivity().runOnUiThread(runnable);
		}
	}


	public void addItemSelected(final int _position){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				getListView().setItemChecked(_position, true);
			}
		});
	}

	public BaseController<BaseVariableWidget> getController(){
		return controller;
	}


	SeleccionableBaseElementsListRendererAdapter<BaseVariableWidget> provideNuevaVariableRendererAdapter(LayoutInflater layoutInflater, NewVariableRendererBuilder rendererBuilder){
		BaseElementList<BaseVariableWidget> itemVariableCollection = new BaseElementList<BaseVariableWidget>();
		final SeleccionableBaseElementsListRendererAdapter<BaseVariableWidget> adapter = new SeleccionableBaseElementsListRendererAdapter<BaseVariableWidget>(layoutInflater, rendererBuilder, itemVariableCollection);
		return adapter;
	}

	private List<Renderer<BaseVariableWidget>> getPrototypesNewVariable(){
		List<Renderer<BaseVariableWidget>> prototypes = new LinkedList<Renderer<BaseVariableWidget>>();
		NewVariableRenderer variableRenderer = new NewVariableRenderer(getActivity());
		prototypes.add(variableRenderer);
		return prototypes;
	}
	NewVariableRendererBuilder provideNewVariableRendererBuilder(){
		return new NewVariableRendererBuilder(getPrototypesNewVariable());
	}

}
