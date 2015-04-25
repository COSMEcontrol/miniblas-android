package com.miniblas.iu.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import com.miniblas.app.AplicacionPrincipal;
import com.miniblas.app.R;
import com.miniblas.iu.FabActivity;
import com.miniblas.iu.builder.ProfileRendererBuilder;
import com.miniblas.iu.cab.PerfilCab;
import com.miniblas.iu.controllers.ProfilesController;
import com.miniblas.iu.controllers.base.BaseController;
import com.miniblas.iu.dialog.alert.AlertDialogNewProfile;
import com.miniblas.iu.fragments.base.CabOrdenableElementsFragment;
import com.miniblas.iu.renderers.ProfileRenderer;
import com.miniblas.iu.utils.SeleccionableBaseElementsListRendererAdapter;
import com.miniblas.model.MiniBlasProfile;
import com.miniblas.model.base.BaseElement;
import com.miniblas.model.base.BaseElementList;
import com.miniblas.persistence.ormlite.Contract;
import com.pedrogomez.renderers.Renderer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alberto on 13/11/14.
 */
public class ProfilesElementsFragmentCab extends CabOrdenableElementsFragment<MiniBlasProfile>{

	private ProfilesController controller;
	private PerfilCab pc;
	private ProfilesElementsFragmentCab yo;


	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		AplicacionPrincipal application = (AplicacionPrincipal) getActivity().getApplication();
		setAdapter(providePerfilRendererAdapter(LayoutInflater.from(getActivity()),providePerfilRendererBuilder()));
		setListAdapter(getAdapter());
		//getListView().setOnItemClickListener(new OnPerfilClickedListener());
		pc = new PerfilCab();
		//pc.setContext(getActivity());
		yo = this;
		pc.setFragment(this);
		controller = ProfilesController.getInstance(application);
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		//(((ActionBarActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
		Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
		mToolbar.setNavigationIcon(R.drawable.ic_launcher);
		FabActivity act = (FabActivity) getActivity();
		act.setTitle(getResources().getString(R.string.lista_perfiles));
		act.setFabListener(new FabActivity.FabListener(){
			@Override
			public void onFabPressed(){
				BaseElementList<MiniBlasProfile> listProfilesSelected = ((SeleccionableBaseElementsListRendererAdapter) getAdapter()).getCurrentCheckedItems();
				AlertDialogNewProfile.newInstance(controller, getAdapter().getElements()).show(getFragmentManager(), "");
			}
		});
		controller.onViewChange(this);
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				BaseElement element = getAdapter().getItem(position);
				if(pc.isActive()){
					boolean added = !getAdapter().isSelected(element);
					if(added){
						getAdapter().addNewSelection(element);
						pc.addElement(element);
					}else{
						getAdapter().removeSelection(element);
						pc.removeElement(element);
					}
					getAdapter().notifyDataSetChanged();
				}else{
					MiniBlasProfile profile = (MiniBlasProfile) element;
					gotoBagFragment(profile.getId());
				}

			}
		});

		pc.setListView(getListView());
		setCabInFragment(pc);
	}

	@Override
	public void onStop(){
		super.onStop();
		controller.saveElements();

	}
	public void gotoBagFragment(final int _id_profile){
		runOnUiThread(new Runnable(){
			@Override
			public void run(){
				Bundle data = new Bundle();
				data.putInt(Contract.PROFILE_TABLE_NAME, _id_profile);

				//getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				//atencion comentado porque era recursiva
				getFragmentManager().executePendingTransactions();
				FragmentTransaction trans = getFragmentManager().beginTransaction();
				trans.setCustomAnimations(R.anim.left_in, R.anim.left_out, R.anim.right_in, R.anim.right_out);
				BagElementsFragmentCab fragment = new BagElementsFragmentCab();
				fragment.setArguments(data);
				trans.replace(R.id.container, fragment);
				trans.addToBackStack(null);
				trans.commit();
			}
		});

	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		getActivity().getMenuInflater().inflate(R.menu.menu_perfiles, menu);

	}

	@Override
	public void setConnectIcon(){
		//no aplicable
	}

	@Override
	public void setDisconnectIcon(){
		//no aplicable
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

	public BaseController<MiniBlasProfile> getController(){
		return controller;
	}

	SeleccionableBaseElementsListRendererAdapter<MiniBlasProfile> providePerfilRendererAdapter(LayoutInflater layoutInflater, ProfileRendererBuilder rendererBuilder){
		BaseElementList<MiniBlasProfile> perfilCollection = new BaseElementList<MiniBlasProfile>();
		final SeleccionableBaseElementsListRendererAdapter<MiniBlasProfile> adapter = new SeleccionableBaseElementsListRendererAdapter<MiniBlasProfile>(layoutInflater, rendererBuilder, perfilCollection);
		return adapter;
	}

	private List<Renderer<MiniBlasProfile>> getPrototypesPerfil(){
		List<Renderer<MiniBlasProfile>> prototypes = new LinkedList<Renderer<MiniBlasProfile>>();
		ProfileRenderer perfilrenderer = new ProfileRenderer(getActivity());
		prototypes.add(perfilrenderer);
		return prototypes;
	}

	ProfileRendererBuilder providePerfilRendererBuilder(){
		return new ProfileRendererBuilder(getPrototypesPerfil());
	}

}
