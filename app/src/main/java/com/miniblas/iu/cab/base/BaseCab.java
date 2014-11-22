package com.miniblas.iu.cab.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;

import com.miniblas.app.R;
import com.miniblas.iu.FabActivity;
import com.miniblas.iu.fragments.base.OrdenableElementsFragment;

import java.io.Serializable;

public abstract class BaseCab implements AbsListView.MultiChoiceModeListener {

    private android.view.ActionMode mActionMode;
    private  Activity context;
    private  OrdenableElementsFragment fragment;


    public BaseCab setContext(Activity context) {
        this.context = context;
        return this;
    }

    public BaseCab setFragment(OrdenableElementsFragment fragment) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        return this;
    }

    public final boolean isActive() {
        return mActionMode != null;
    }

    public OrdenableElementsFragment getFragment() {
        return fragment;
    }

    public FabActivity getContext() {
        return (FabActivity) context;
    }

    public abstract int getMenu();


    public void invalidate() {
        if (mActionMode != null) mActionMode.invalidate();
    }
    public void finish() {
        if (mActionMode != null) {
            System.out.println("Desactivando cabbbbbbbbbbbbbbbbbbbbbbbb");
            mActionMode.finish();
            mActionMode = null;
        }
    }
    @Override
    public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
        mActionMode = actionMode;
        if (getMenu() != -1)
            actionMode.getMenuInflater().inflate(getMenu(), menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getContext().getWindow().setStatusBarColor(getContext().getResources().getColor(R.color.statusbar_color_cab));
        return true;
    }

    @Override
    public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
        mActionMode = actionMode;
        return false;
    }

    @Override
    public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
        mActionMode = actionMode;
        return true;
    }

    @Override
    public void onDestroyActionMode(android.view.ActionMode actionMode) {
        mActionMode = actionMode;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getContext().getWindow().setStatusBarColor(getContext().getResources().getColor(R.color.cabinet_color_darker));
        mActionMode = null;
    }

}