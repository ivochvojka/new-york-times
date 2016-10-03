package cz.ich.newyorktimes.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Utilities for obtain fonts.
 *
 * @author Ivo Chvojka
 */
public class FontUtils {

    private static FontUtils mInstance = new FontUtils();
    private static Context mContext;

    private Typeface mTitleFont;

    /**
     * Private constructor.
     */
    private FontUtils() {
    }

    /**
     * Get singleton mInstance.
     *
     * @param context Instance of context
     * @return Instance of {@link FontUtils}
     */
    public static FontUtils getInstance(Context context) {
        mContext = context;
        return mInstance;
    }

    /**
     * Get custom font for toolbar title.
     *
     * @return Typeface of font
     */
    public synchronized Typeface getTitleFont() {
        if (mTitleFont == null) {
            mTitleFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/AncientMedium.ttf");
        }

        return mTitleFont;
    }

}
