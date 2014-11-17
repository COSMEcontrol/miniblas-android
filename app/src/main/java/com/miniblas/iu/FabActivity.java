package com.miniblas.iu;

import com.melnykov.fab.FloatingActionButton;
import com.miniblas.app.R;
import com.miniblas.iu.base.ThemableActivity;
import com.miniblas.iu.cab.base.BaseCab;
import com.miniblas.iu.fragments.ProfilesElementsFragment;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;


public class FabActivity extends ThemableActivity {
	
	private FloatingActionButton fab; // the floating blue add/paste button
    private boolean fabDisabled; // flag indicating whether fab should stay hidden while scrolling
    private FabListener fabListener;
    //private RecyclerView rv ;
    private BaseCab mCab; // the current contextual action bar, saves state throughout fragments

    public interface FabListener {
        public abstract void onFabPressed();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabListener.onFabPressed();
            }
        });
        /*
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(FabActivity.this, "hola", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        */
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("cab")) {
                mCab = (BaseCab) savedInstanceState.getSerializable("cab");
               // mCab.setContext(this);
            }
        }


        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        // trans.setCustomAnimations(R.anim.frag_enter, R.anim.frag_exit);
        trans.replace(R.id.container, new ProfilesElementsFragment());
        try {
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mCab != null && mCab.isActive())
            Log.v("cab", "saving cab");
            outState.putSerializable("cab", mCab);
        super.onSaveInstanceState(outState);
    }

    public BaseCab getCab() {
        return mCab;
    }

    public void setCab(BaseCab cab) {
        mCab = cab;
    }
    public void disableFab(boolean disable) {
        if (!disable) {
            fab.show(true);
        } else {
            fab.hide(true);
        }
        fabDisabled = disable;
    }
    public void toggleFab(boolean hide) {
        if (fabDisabled) fab.hide(false);
        else if (hide) fab.hide(true);
        else fab.show(true);
    }
    public void setFabListener(FabListener _fabListener) {
        this.fabListener = _fabListener;
    }

    public void attachFabToListView(ListView _lv){
        this.fab.attachToListView(_lv);
    }

}
