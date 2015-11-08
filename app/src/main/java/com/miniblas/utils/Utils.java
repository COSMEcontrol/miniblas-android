package com.miniblas.utils;

import android.content.Context;
import android.content.res.TypedArray;

import com.miniblas.app.R;
/**
 *
 * @author A. Azuara
 */
public class Utils {


    public static int resolveColor(Context context, int color) {
        TypedArray a = context.obtainStyledAttributes(new int[]{color});
        int resId = a.getColor(0, context.getResources().getColor(R.color.miniblas_color));
        a.recycle();
        return resId;
    }

    public static int resolveDrawable(Context context, int drawable) {
        TypedArray a = context.obtainStyledAttributes(new int[]{drawable});
        int resId = a.getResourceId(0, 0);
        a.recycle();
        return resId;
    }



}