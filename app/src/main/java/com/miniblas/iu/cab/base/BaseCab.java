package com.miniblas.iu.cab.base;

import android.app.Activity;
import android.os.Build;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.miniblas.app.R;
import com.miniblas.iu.FabActivity;
import com.miniblas.iu.fragments.base.CabOrdenableElementsFragment;
import com.miniblas.iu.utils.ThemeUtils;

import java.io.Serializable;

public abstract class BaseCab implements ActionMode.Callback, Serializable{

	private transient ActionMode mActionMode;
	private transient Activity context;
	private transient CabOrdenableElementsFragment fragment;
	private transient ListView listView;


	public final BaseCab start() {
		getContext().startSupportActionMode(this);
		return this;
	}

	public BaseCab setContext(Activity context){
		this.context = context;
		return this;
	}

	public BaseCab setFragment(CabOrdenableElementsFragment fragment){
		this.context = fragment.getActivity();
		this.fragment = fragment;
		return this;
	}

	public void setListView(ListView listView){
		this.listView = listView;
	}

	protected ListView getListView(){
		return listView;
	}

	public final boolean isActive(){
		return mActionMode != null;
	}

	public CabOrdenableElementsFragment getFragment(){
		return fragment;
	}

	public FabActivity getContext(){
		return (FabActivity) context;
	}

	public abstract CharSequence getTitle();

	public abstract int getMenu();



	public void invalidate(){
		if(mActionMode != null){
			mActionMode.invalidate();
		}
	}

	public void finish(){
		if(mActionMode != null){
			mActionMode.finish();
			mActionMode = null;
		}
	}

	@Override
	public boolean onCreateActionMode(ActionMode actionMode, Menu menu){
		mActionMode = actionMode;
		FabActivity act = getContext();
		if(getMenu() != -1){
			actionMode.getMenuInflater().inflate(getMenu(), menu);
		}

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
			ThemeUtils utils = act.getThemeUtils();
			final int darkGray = act.getResources().getColor(R.color.statusbar_color_cab);
			getContext().getWindow().setStatusBarColor(getContext().getResources().getColor(R.color.statusbar_color_cab));
			if (utils.isColoredNavBar())
				act.getWindow().setNavigationBarColor(getContext().getResources().getColor(R.color.statusbar_color_cab));

		}
		act.invalidateToolbarMenu(true);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode actionMode, Menu menu){
		//mActionMode = actionMode;
		actionMode.setTitle(getTitle());
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem){
		//mActionMode = actionMode;
		finish();
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode actionMode){
		//mActionMode = actionMode;
		FabActivity act = getContext();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			ThemeUtils utils = act.getThemeUtils();
			final int oldColor = utils.primaryColorDark();
			final int oldColor_statusBar = utils.primaryColorDark();
			act.getWindow().setStatusBarColor(oldColor_statusBar);
			if (utils.isColoredNavBar())
				act.getWindow().setNavigationBarColor(oldColor);
		}
		mActionMode = null;
		act.invalidateToolbarMenu(false);
	}

}