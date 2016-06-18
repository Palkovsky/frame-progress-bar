package pl.owsica.andrzej.frameprogressbar.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by mrwonderman
 */
public class CalculationUtil {

    public static int convertDpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}

