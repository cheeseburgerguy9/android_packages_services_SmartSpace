package com.google.android.systemui.smartspace.utils;

import android.util.Log;
import android.view.View;
import android.text.TextUtils;
import com.android.systemui.bcsmartspace.R;
import java.util.Arrays;

public abstract class ContentDescriptionUtil {
    public static void setFormattedContentDescription(String tag, View view, CharSequence text, CharSequence iconDescription) {
        CharSequence contentDescription;
        if (TextUtils.isEmpty(text)) {
            contentDescription = iconDescription;
        } else if (TextUtils.isEmpty(iconDescription)) {
            contentDescription = text;
        } else {
            contentDescription = view.getContext().getString(R.string.generic_smartspace_concatenated_desc, iconDescription, text);
        }

        String logMsg = String.format("setFormattedContentDescription: text=%s, iconDescription=%s, contentDescription=%s",
            text, iconDescription, contentDescription);
        Log.i(tag, logMsg);

        view.setContentDescription(contentDescription);
    }
}
