package com.miniblas.iu.utils;


import android.content.Context;
import android.preference.PreferenceManager;

import com.miniblas.app.R;
import com.miniblas.iu.dialog.ColorChooserDialog;
/**
 *
 * @author A. Azuara
 */
public class ThemeUtils{

	public ThemeUtils(Context context){
		mContext = context;
		isChanged(false); // invalidate stored booleans
	}

	private Context mContext;
	private boolean mDarkMode;
	private int mLastPrimaryColor;
	private int mLastAccentColor;
	private boolean mLastColoredNav;


	public static int positiveColor;
	public static int neutralColor;
	public static int negativeColor;


	public static boolean isDarkMode(Context paramContext){
		return PreferenceManager.getDefaultSharedPreferences(paramContext).getBoolean("dark_mode", false);
	}

	public int primaryColor() {
		final int defaultColor = mContext.getResources().getColor(R.color.miniblas_color);
		return PreferenceManager.getDefaultSharedPreferences(mContext).getInt("primary_color", defaultColor);
	}

	public void setPrimaryColor(int newColor) {
		PreferenceManager.getDefaultSharedPreferences(mContext).edit().putInt("primary_color", newColor).commit();
	}

	public int primaryColorDark() {
		return ColorChooserDialog.shiftColorDown(primaryColor());
	}

	public int accentColor() {
		final int defaultColor = mContext.getResources().getColor(R.color.miniblas_accent_color);
		return PreferenceManager.getDefaultSharedPreferences(mContext).getInt("accent_color", defaultColor);
	}

	public int accentColorLight() {
		return ColorChooserDialog.shiftColorUp(accentColor());
	}

	public int accentColorDark() {
		return ColorChooserDialog.shiftColorDown(accentColor());
	}

	public void accentColor(int newColor) {
		PreferenceManager.getDefaultSharedPreferences(mContext).edit().putInt("accent_color", newColor).commit();
	}

	public boolean isColoredNavBar() {
		return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("colored_navbar", true);
	}

	public boolean isChanged(boolean checkForChanged) {
		final boolean darkTheme = isDarkMode(mContext);
		final int primaryColor = primaryColor();
		final int accentColor = accentColor();
		final boolean coloredNav = isColoredNavBar();

		boolean changed = false;
		if (checkForChanged) {
			changed = mDarkMode != darkTheme ||
					mLastPrimaryColor != primaryColor || mLastAccentColor != accentColor ||
					coloredNav != mLastColoredNav;
		}

		mDarkMode = darkTheme;
		mLastPrimaryColor = primaryColor;
		mLastAccentColor = accentColor;
		mLastColoredNav = coloredNav;

		return changed;
	}

	public int getCurrent(boolean hasNavDrawer) {
		if (hasNavDrawer) {
			if (mDarkMode) {
				return R.style.MiniblasBasic;
			} else {
				return R.style.MiniblasMaterialDesing;
			}
		} else {
			if (mDarkMode) {
				return R.style.MiniblasBasic;
			} else {
				return R.style.MiniblasMaterialDesing;
			}
		}
	}


}
