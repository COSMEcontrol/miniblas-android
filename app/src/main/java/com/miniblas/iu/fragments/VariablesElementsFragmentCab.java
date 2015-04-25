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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.FabActivity;
import com.miniblas.iu.builder.VariableRendererBuilder;
import com.miniblas.iu.cab.VariableCab;
import com.miniblas.iu.controllers.VariablesController;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.dialog.DialogNewSeekbarWidget;
import com.miniblas.iu.dialog.DialogNewSwitchWidget;
import com.miniblas.iu.dialog.DialogNewValueWidget;
import com.miniblas.iu.dialog.alert.DialogEditRefreshBag;
import com.miniblas.iu.fragments.base.CabOrdenableElementsFragment;
import com.miniblas.iu.renderers.SeekVariableRenderer;
import com.miniblas.iu.renderers.SwitchVariableRenderer;
import com.miniblas.iu.renderers.VisualizadorVariableRenderer;
import com.miniblas.iu.utils.SeleccionableBaseElementsListRendererAdapter;
import com.miniblas.model.base.BaseElement;
import com.miniblas.model.base.BaseElementList;
import com.miniblas.model.variableWidgets.VariableSeekWidget;
import com.miniblas.model.variableWidgets.VariableSwitchWidget;
import com.miniblas.model.variableWidgets.VariableValueWidget;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;
import com.miniblas.persistence.ormlite.Contract;
import com.pedrogomez.renderers.Renderer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alberto on 13/11/14.
 */
public class VariablesElementsFragmentCab extends CabOrdenableElementsFragment<BaseVariableWidget>{

	private VariablesController controller;
	private MenuItem iconoEstado;
	public static final int RESULT_OK = 1;
	public static final int NO_RESULT = 0;
	private FabActivity act;
	private VariableCab vc;
	private AplicacionPrincipal application;
	private ListView list;



	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		application = (AplicacionPrincipal) getActivity().getApplication();
		//application.inject(this);
		setAdapter(provideVariablesRendererAdapter(LayoutInflater.from(getActivity()), provideVariableRendererBuilder()));
		setListAdapter(getAdapter());
		vc = new VariableCab();
		vc.setFragment(this);
		controller = VariablesController.getInstance(application);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		//getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);
		act = (FabActivity) getActivity();
		act.setTitle(getResources().getString(R.string.listaVariables));
		//act.disableFab(true);
		Bundle extras = getArguments();
		int id_basket = extras.getInt(Contract.BASKET_TABLE_NAME);
		int id_profile = extras.getInt(Contract.PROFILE_TABLE_NAME);
		controller.onViewChange(this, id_profile, id_basket);
		//(((ActionBarActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
		mToolbar.setNavigationIcon(R.drawable.ic_arrow_left_white_24dp);
		act.setFabListener(new FabActivity.FabListener(){
			@Override
			public void onFabPressed(){
				new MaterialDialog.Builder(getActivity()).title(R.string.widgetVisualizacion).items(getResources().getStringArray(R.array.widget_list)).itemsCallback(new MaterialDialog.ListCallback(){
					@Override
					public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text){
						String[] widgets_list = getResources().getStringArray(R.array.widget_list);
						if(text.equals(widgets_list[0])){
							gotoNewVariableFragment(VariableValueWidget.REQUEST_CODE_NEW_VALUE);
						}else if(text.equals(widgets_list[1])){
							gotoNewVariableFragment(VariableSeekWidget.REQUEST_CODE_NEW_SEEKBAR);
						}else if(text.equals(widgets_list[2])){
							gotoNewVariableFragment(VariableSwitchWidget.REQUEST_CODE_NEW_SWITCH);
						}
					}
				}).show();
			}
		});
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				BaseElement element = getAdapter().getItem(position);
				if(vc.isActive()){
					boolean added = !getAdapter().isSelected(element);
					if(added){
						getAdapter().addNewSelection(element);
						vc.addElement(element);
					}else{
						getAdapter().removeSelection(element);
						vc.removeElement(element);
					}
					getAdapter().notifyDataSetChanged();
				}else{
					//onclick
				}

			}
		});
		vc.setListView(getListView());
		setCabInFragment(vc);
		list = getListView();
	}

	public void gotoNewVariableFragment(int request_code){
		Bundle data = new Bundle();
		data.putInt(Contract.PROFILE_TABLE_NAME, controller.getIdProfile());
		data.putInt(Contract.BASKET_TABLE_NAME, controller.getBasket().getId());
		//getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		getFragmentManager().executePendingTransactions();
		FragmentTransaction trans = getFragmentManager().beginTransaction();
		trans.setCustomAnimations(R.anim.left_in, R.anim.left_out, R.anim.right_in, R.anim.right_out);
		NewVariableElementsFragmentCab fragment = new NewVariableElementsFragmentCab();
		fragment.setArguments(data);
		fragment.setTargetFragment(this, request_code);
		trans.replace(R.id.container, fragment);
		trans.addToBackStack(null);
		trans.commit();
	}

	public void showFab(){
		act.showFab();
	}
	public void hideFab(){
		act.hideFab();
	}

	@Override
	public void onStop(){
		super.onStop();
		controller.saveElements();

	}

	@Override
	public void loadState(){
	}

	public void onSelectVariablesResult(int requestCode, int resultCode, ArrayList<BaseVariableWidget> variableList){
		if(requestCode == VariableSeekWidget.REQUEST_CODE_NEW_SEEKBAR){
			if(resultCode == RESULT_OK){
				DialogNewSeekbarWidget.newInstance(controller, variableList).show(getFragmentManager(), "");
			}else{
				Toast.makeText(getActivity(), getResources().getString(R.string.noAñadidaVariable), Toast.LENGTH_SHORT).show();
			}
		}else if(requestCode == VariableSwitchWidget.REQUEST_CODE_NEW_SWITCH){
			if(resultCode == RESULT_OK){
				DialogNewSwitchWidget.newInstance(controller, variableList).show(getFragmentManager(), "");
			}else{
				Toast.makeText(getActivity(), getResources().getString(R.string.noAñadidaVariable), Toast.LENGTH_SHORT).show();
			}
		}else if(requestCode == VariableValueWidget.REQUEST_CODE_NEW_VALUE){
			if(resultCode == RESULT_OK){
				DialogNewValueWidget.newInstance(controller, variableList).show(getFragmentManager(), "");
			}else{
				Toast.makeText(getActivity(), getResources().getString(R.string.noAñadidaVariable), Toast.LENGTH_SHORT).show();
			}
		}
		//application.setVariablesObserver(controller);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		getActivity().getMenuInflater().inflate(R.menu.menu_variables, menu);
		iconoEstado = (MenuItem) menu.findItem(R.id.estado);
		((AplicacionPrincipal) getActivity().getApplication()).setIconObserver(controller);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()){
			case R.id.menu_ajustes_cesta:
				DialogEditRefreshBag.newInstance(controller).show(getFragmentManager(), "");
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
				Toast.makeText(getActivity(), getResources().getString(R.string.variablesAñadidas), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void msgButtonNewCancel(){
		runOnUiThread(new Runnable(){
			@Override
			public void run(){
				Toast.makeText(getActivity(), getResources().getString(R.string.noAñadidaVariable), Toast.LENGTH_SHORT).show();
			}
		});
	}
	//msg modify variable
	@Override
	public void msgButtonEditSave(){
		runOnUiThread(new Runnable(){
			@Override
			public void run(){
				Toast.makeText(getActivity(), getResources().getString(R.string.variableModificada), Toast.LENGTH_SHORT).show();
			}
		});
	}

	//msg modify variable
	@Override
	public void msgButtonEditCancel(){
		runOnUiThread(new Runnable(){
			@Override
			public void run(){
				Toast.makeText(getActivity(), getResources().getString(R.string.variableNoModificada), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void msgErrorDeleteElementsInBD(){
	}


	public void runOnUiThread(Runnable runnable){
		if(getActivity() != null){
			getActivity().runOnUiThread(runnable);
		}
	}

	public BaseController<BaseVariableWidget> getController(){
		return controller;
	}


	SeleccionableBaseElementsListRendererAdapter<BaseVariableWidget> provideVariablesRendererAdapter(LayoutInflater layoutInflater, VariableRendererBuilder rendererBuilder){
		BaseElementList<BaseVariableWidget> itemVariableCollection = new BaseElementList<BaseVariableWidget>();
		final SeleccionableBaseElementsListRendererAdapter<BaseVariableWidget> adapter = new SeleccionableBaseElementsListRendererAdapter<BaseVariableWidget>(layoutInflater, rendererBuilder, itemVariableCollection);
		return adapter;
	}

	private List<Renderer<BaseVariableWidget>> getPrototypesVariable(){
		List<Renderer<BaseVariableWidget>> prototypes = new LinkedList<Renderer<BaseVariableWidget>>();
		VisualizadorVariableRenderer visualizadorVariableRenderer = new VisualizadorVariableRenderer(getActivity());
		SwitchVariableRenderer switchVariableRenderer = new SwitchVariableRenderer(getActivity());
		SeekVariableRenderer seekvariableRenderer = new SeekVariableRenderer(getActivity());
		prototypes.add(visualizadorVariableRenderer);
		prototypes.add(seekvariableRenderer);
		prototypes.add(switchVariableRenderer);
		return prototypes;
	}
	private VariableRendererBuilder provideVariableRendererBuilder(){
		return new VariableRendererBuilder(getPrototypesVariable());
	}

	public void refreshElement(){
		runOnUiThread(new Runnable(){
			@Override
			public void run(){
					//controlar si por si acaso no se ha creado la vista todavia
					if(list != null){
						int start = list.getFirstVisiblePosition();
						for(int i = start, j = list.getLastVisiblePosition(); i <= j; i++){
							View view = list.getChildAt(i - start);
							list.getAdapter().getView(i, view, list);
						}
					}
			}
		});

	}

}
