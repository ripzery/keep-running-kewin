package com.example.ripzery.projectx01.ar.util;

import android.content.Context;

/**
 * Created by Rawipol on 1/9/15 AD.
 */
public class Utility {
    public static int convertPixtoDip(Context context, int pixel){
        float scale = getDensity(context);
        return (int)((pixel - 0.5f)/scale);
    }

    public static float getDensity(Context context){
        float scale = context.getResources().getDisplayMetrics().density;
        return scale;
    }

    public static int convertDiptoPix(Context context, int dip){
        float scale = getDensity(context);
        return (int) (dip * scale + 0.5f);
    }
}
