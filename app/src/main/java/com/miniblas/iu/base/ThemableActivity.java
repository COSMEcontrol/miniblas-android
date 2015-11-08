package com.miniblas.iu.base;

import android.app.ActivityManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.afollestad.materialdialogs.ThemeSingleton;
import com.miniblas.app.R;
import com.miniblas.iu.utils.ThemeUtils;
/**
 *
 * @author A. Azuara
 */
public class ThemableActivity extends ActionBarActivity{

	private ThemeUtils mThemeUtils;

	protected boolean hasNavDrawer() {
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		//requestWindowFeature(Window.FEATURE_ACTION_BAR);
		mThemeUtils = new ThemeUtils(this);
		setTheme(mThemeUtils.getCurrent(hasNavDrawer()));
		super.onCreate(savedInstanceState);

		//establece el color segun el accentColor
		final int accent = mThemeUtils.accentColor();
		ThemeSingleton.get().positiveColor = accent;
		ThemeSingleton.get().neutralColor = accent;
		ThemeSingleton.get().negativeColor = accent;
		ThemeSingleton.get().darkTheme = ThemeUtils.isDarkMode(this);

		//look and fell multitask LOLLIPOP
		//  https://www.bignerdranch.com/blog/polishing-your-Android-overview-screen-entry/
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
			ActivityManager.TaskDescription td = new ActivityManager.TaskDescription(
					getString(R.string.app_name),
					BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher),
					mThemeUtils.primaryColor());
			setTaskDescription(td);
		}

		//pintar el color de la status bar en LOLLIPOP o superior
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			final int dark = getThemeUtils().primaryColorDark();
			if (hasNavDrawer()) {
				getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
			} else {
				getWindow().setStatusBarColor(dark);
			}
			if (getThemeUtils().isColoredNavBar())
				getWindow().setNavigationBarColor(dark);
		}
		/*
		//cambiar el color de la barra de navegación (debajo statusbar)
		if (getThemeUtils().isColoredNavBar()){
			getWindow().setNavigationBarColor(getThemeUtils().setPrimaryColor());
		}
		//pintar la barra de navegación en KITKAT
		if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintResource(R.color.miniblas_color);
		}
		*/


	}
	@Override
	protected void onResume(){
		super.onResume();
		if (mThemeUtils.isChanged(true)) {
			setTheme(mThemeUtils.getCurrent(hasNavDrawer()));
			recreate();
		}
	}

	public ThemeUtils getThemeUtils()
	{
		return this.mThemeUtils;
	}
}
