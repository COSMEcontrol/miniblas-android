package com.miniblas.iu.utils;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.miniblas.app.R;

public class ThemeUtils {

    public ThemeUtils(Activity context) {
        mContext = context;
        isChanged(); // invalidate stored booleans
    }

    private Context mContext;
    private boolean otherMode;
    private boolean basicMode;

    public static boolean isOtherMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("other_mode", false);
    }

    public static boolean isBasicMode(Context context) {
        if (!isOtherMode(context)) return false;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("basic_mode", false);
    }

//    public static Theme getDialogTheme(Context context) {
//        if (isMaterialDesingMode(context) || isBasicMode(context)) return Theme.DARK;
//        else return Theme.LIGHT;
//    }

    public boolean isChanged() {
        boolean otherTheme = isOtherMode(mContext);
        boolean basicTheme = isBasicMode(mContext);

        boolean changed = otherMode != otherTheme || basicMode != basicTheme;
        otherMode = otherTheme;
        basicMode = basicTheme;
        return changed;
    }

    public int getCurrent() {
        if (basicMode) {
            return R.style.MiniblasBasic;
        } if(otherMode){
        	return R.style.MiniblasBasic;
        } else {
            return R.style.MiniblasMaterialDesing;
        }
    }
}
