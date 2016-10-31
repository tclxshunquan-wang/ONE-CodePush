package com.android.codepush;

import android.graphics.drawable.GradientDrawable;
import android.util.Log;

/**
 * Created by zhang on 2016/10/26.
 */

public class SQ_Utils {

    public static void log(String value){
        Log.v("[SQ_Push]",value);
    }

    /**
     * 画边框  倒圆角
     * @param color
     * *
     * @param topLeftRadius
     * *
     * @param topRightRadiu
     * *
     * @param bottomLeftRadius
     * *
     * @param bottomRightRadius
     * *
     * @param stockWidth
     * *
     * @param stockColor
     * *
     * @return
     */
    public static GradientDrawable getShapeDrawable(int color, float topLeftRadius, float topRightRadiu, float bottomLeftRadius, float bottomRightRadius, int stockWidth, int stockColor){
        GradientDrawable gradientDrawable=new GradientDrawable();
        float[] f=new float[]{topLeftRadius, topLeftRadius, topRightRadiu, topRightRadiu, bottomLeftRadius, bottomLeftRadius, bottomRightRadius, bottomRightRadius};
        gradientDrawable.setCornerRadii(f);
        gradientDrawable.setColor(color);
        gradientDrawable.setStroke(stockWidth, stockColor);
        return gradientDrawable;
    }
}
