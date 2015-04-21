package com.viewnine.safeapp.lockPattern;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by user on 4/20/15.
 */
public class Resources {

    /**
     * This is singleton class.
     */
    private Resources() {
    }// Resources()

    /**
     * Convenient method for {@link Context#getTheme()} and
     * {@link Resources.Theme#resolveAttribute(int, TypedValue, boolean)}.
     *
     * @param context
     *            the context.
     * @param resId
     *            The resource identifier of the desired theme attribute.
     * @return the resource ID that {@link TypedValue#resourceId} points to, or
     *         {@code 0} if not found.
     */
    public static int resolveAttribute(Context context, int resId) {
        return resolveAttribute(context, resId, 0);
    }// resolveAttribute()

    /**
     * Convenient method for {@link Context#getTheme()} and
     * {@link Resources.Theme#resolveAttribute(int, TypedValue, boolean)}.
     *
     * @param context
     *            the context.
     * @param resId
     *            The resource identifier of the desired theme attribute.
     * @param defaultValue
     *            the default value if cannot resolve {@code resId}.
     * @return the resource ID that {@link TypedValue#resourceId} points to, or
     *         {@code defaultValue} if not found.
     */
    public static int resolveAttribute(Context context, int resId,
                                       int defaultValue) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(resId, typedValue, true))
            return typedValue.resourceId;
        return defaultValue;
    }// resolveAttribute()
}
