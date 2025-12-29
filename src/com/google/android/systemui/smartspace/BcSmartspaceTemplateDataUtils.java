package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceUtils;
import android.app.smartspace.uitemplatedata.Icon;
import android.app.smartspace.uitemplatedata.Text;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.bcsmartspace.R;
import java.util.Map;

public abstract class BcSmartspaceTemplateDataUtils {
    public static final Map<Integer, Integer> TEMPLATE_TYPE_TO_SECONDARY_CARD_RES = Map.of(
            2, R.layout.smartspace_sub_image_template_card,
            3, R.layout.smartspace_sub_list_template_card,
            4, R.layout.smartspace_combined_cards_template_card,
            5, R.layout.smartspace_head_to_head_template_card,
            6, R.layout.smartspace_carousel_template_card,
            7, R.layout.smartspace_sub_card_template_card
    );

    public static void offsetTextViewForIcon(TextView textView, DoubleShadowIconDrawable icon, boolean isRtl) {
        if (icon == null) {
            textView.setTranslationX(0.0f);
        } else {
            int offset = isRtl ? 1 : -1;
            textView.setTranslationX(offset * icon.mIconInsetSize);
        }
    }

    public static void setIcon(ImageView imageView, Icon icon) {
        if (imageView == null) {
            Log.w("BcSmartspaceTemplateDataUtils", "Cannot set. The image view is null");
            return;
        }
        if (icon == null) {
            Log.w("BcSmartspaceTemplateDataUtils", "Cannot set. The given icon is null");
            updateVisibility(imageView, 8);
            return;
        }
        imageView.setImageIcon(icon.getIcon());
        if (icon.getContentDescription() != null) {
            imageView.setContentDescription(icon.getContentDescription());
        }
    }

    public static void setText(TextView textView, Text text) {
        if (textView == null) {
            Log.w("BcSmartspaceTemplateDataUtils", "Cannot set. The text view is null");
            return;
        }
        if (SmartspaceUtils.isEmpty(text)) {
            Log.w("BcSmartspaceTemplateDataUtils", "Cannot set. The given text is empty");
            updateVisibility(textView, 8);
            return;
        }
        textView.setText(text.getText());
        textView.setEllipsize(text.getTruncateAtType());
        textView.setMaxLines(text.getMaxLines());
    }

    public static void updateVisibility(View view, int visibility) {
        if (view != null && view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
    }
}
