package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.android.app.animation.Interpolators;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.systemui.bcsmartspace.R;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardMetadataLoggingInfo;
import com.google.android.systemui.smartspace.logging.BcSmartspaceSubcardLoggingInfo;
import com.google.android.systemui.smartspace.utils.ContentDescriptionUtil;
import java.util.List;
import java.util.Locale;

public class BcSmartspaceCard extends ConstraintLayout implements SmartspaceCard {
    public final DoubleShadowIconDrawable mBaseActionIconDrawable;
    public Rect mBaseActionIconSubtitleHitRect;
    public DoubleShadowTextView mBaseActionIconSubtitleView;
    public float mDozeAmount;
    public BcSmartspaceDataPlugin.SmartspaceEventNotifier mEventNotifier;
    public final DoubleShadowIconDrawable mIconDrawable;
    public int mIconTintColor;
    public BcSmartspaceCardLoggingInfo mLoggingInfo;
    public BcSmartspaceCardSecondary mSecondaryCard;
    public ViewGroup mSecondaryCardGroup;
    public TextView mSubtitleTextView;
    public SmartspaceTarget mTarget;
    public ViewGroup mTextGroup;
    public TextView mTitleTextView;
    public boolean mTouchDelegateIsDirty;
    public String mUiSurface;
    public boolean mUsePageIndicatorUi;
    public boolean mValidSecondaryCard;

    public BcSmartspaceCard(Context context) {
        this(context, null);
    }

    public BcSmartspaceCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSecondaryCard = null;
        this.mIconTintColor = GraphicsUtils.getAttrColor(getContext(), 16842806);
        this.mTextGroup = null;
        this.mSecondaryCardGroup = null;
        this.mTitleTextView = null;
        this.mSubtitleTextView = null;
        this.mBaseActionIconSubtitleView = null;
        this.mBaseActionIconSubtitleHitRect = null;
        this.mUiSurface = null;
        this.mTouchDelegateIsDirty = false;

        context.getTheme().applyStyle(R.style.Smartspace, false);

        this.mIconDrawable = new DoubleShadowIconDrawable(context);
        this.mBaseActionIconDrawable = new DoubleShadowIconDrawable(context);
        setDefaultFocusHighlightEnabled(false);
    }

    public static int getClickedIndex(BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo, int i) {
        List<BcSmartspaceCardMetadataLoggingInfo> list;
        if (bcSmartspaceCardLoggingInfo == null || (bcSmartspaceCardLoggingInfo.mSubcardInfo) == null) {
            return 0;
        }
        BcSmartspaceSubcardLoggingInfo bcSmartspaceSubcardLoggingInfo = bcSmartspaceCardLoggingInfo.mSubcardInfo;
        if (bcSmartspaceSubcardLoggingInfo == null || (list = bcSmartspaceSubcardLoggingInfo.mSubcards) == null) {
             return 0;
        }
        for (int i2 = 0; i2 < list.size(); i2++) {
            BcSmartspaceCardMetadataLoggingInfo bcSmartspaceCardMetadataLoggingInfo = list.get(i2);
            if (bcSmartspaceCardMetadataLoggingInfo != null && bcSmartspaceCardMetadataLoggingInfo.mCardTypeId == i) {
                return i2 + 1;
            }
        }
        return 0;
    }

    @Override
    public void bindData(SmartspaceTarget target, BcSmartspaceDataPlugin.SmartspaceEventNotifier notifier, BcSmartspaceCardLoggingInfo loggingInfo, boolean usePageIndicatorUi) {
        this.mLoggingInfo = null;
        this.mEventNotifier = null;
        BcSmartspaceTemplateDataUtils.updateVisibility(this.mSecondaryCardGroup, 8);
        BcSmartspaceTemplateDataUtils.updateVisibility(this.mBaseActionIconSubtitleView, 8);
        this.mIconDrawable.setIconDrawable(null);
        this.mBaseActionIconDrawable.setIconDrawable(null);
        setTitle(null, null, false);
        setSubtitle(null, null, false);
        setBaseActionIconSubtitle(null, null, null);
        updateIconTint();
        setOnClickListener(null);
        if (this.mTitleTextView != null) {
            this.mTitleTextView.setOnClickListener(null);
            this.mTitleTextView.setClickable(false);
        }
        if (this.mSubtitleTextView != null) {
            this.mSubtitleTextView.setOnClickListener(null);
            this.mSubtitleTextView.setClickable(false);
        }
        if (this.mBaseActionIconSubtitleView != null) {
             this.mBaseActionIconSubtitleView.setOnClickListener(null);
             this.mBaseActionIconSubtitleView.setClickable(false);
        }

        this.mTarget = target;
        this.mEventNotifier = notifier;
        SmartspaceAction headerAction = target.getHeaderAction();
        SmartspaceAction baseAction = target.getBaseAction();
        this.mLoggingInfo = loggingInfo;
        this.mUsePageIndicatorUi = usePageIndicatorUi;
        this.mValidSecondaryCard = false;

        if (this.mTextGroup != null) {
             this.mTextGroup.setTranslationX(0.0f);
        }

        boolean hasHeaderAction = headerAction != null;
        boolean hasBaseAction = baseAction != null;

        if (hasHeaderAction) {
            if (this.mSecondaryCard != null) {
                this.mSecondaryCard.reset(target.getSmartspaceTargetId());
                this.mValidSecondaryCard = this.mSecondaryCard.setSmartspaceActions(target, notifier, loggingInfo);
            }
            if (this.mSecondaryCardGroup != null) {
                this.mSecondaryCardGroup.setAlpha(1.0f);
            }
            int secondaryCardVisibility = (this.mDozeAmount == 1.0f || !this.mValidSecondaryCard) ? 8 : 0;
            BcSmartspaceTemplateDataUtils.updateVisibility(this.mSecondaryCardGroup, secondaryCardVisibility);

            Drawable iconDrawable = BcSmartSpaceUtil.getIconDrawableWithCustomSize(headerAction.getIcon(), getContext(), getResources().getDimensionPixelSize(R.dimen.enhanced_smartspace_icon_size));
            boolean hasIcon = iconDrawable != null;
            this.mIconDrawable.setIcon(iconDrawable);

            CharSequence title = headerAction.getTitle();
            CharSequence subtitle = headerAction.getSubtitle();
            boolean isFeatureType1 = target.getFeatureType() == 1;
            boolean hasTitle = !TextUtils.isEmpty(title) || isFeatureType1;
            boolean hasSubtitle = !TextUtils.isEmpty(subtitle);
            boolean titleHasIcon = hasTitle && !hasSubtitle && hasIcon && isFeatureType1;

            setTitle(title, headerAction.getContentDescription(), titleHasIcon);

            CharSequence subtitleText = hasSubtitle ? subtitle : null;
            boolean subtitleHasIcon = hasIcon;
            if (isFeatureType1 && !hasSubtitle) {
                subtitleHasIcon = false;
            }
             setSubtitle(subtitleText, headerAction.getContentDescription(), subtitleHasIcon);
        }

        if (hasBaseAction) {
            Bundle extras = baseAction.getExtras();
            int subcardType = -1;
            if (extras != null && !extras.isEmpty()) {
                 subcardType = extras.getInt("subcardType", -1);
            }

            Drawable baseActionIcon = BcSmartSpaceUtil.getIconDrawableWithCustomSize(baseAction.getIcon(), getContext(), getResources().getDimensionPixelSize(R.dimen.enhanced_smartspace_icon_size));
            this.mBaseActionIconDrawable.setIcon(baseActionIcon);
            setBaseActionIconSubtitle(baseAction.getSubtitle(), baseAction.getContentDescription(), this.mBaseActionIconDrawable);

            int clickIndex = 0;
            if (subcardType != -1) {
                clickIndex = getClickedIndex(loggingInfo, subcardType);
            } else {
                 Log.d("BcSmartspaceCard", "Subcard expected but missing type. loggingInfo=" + loggingInfo + ", baseAction=" + baseAction);
            }

            BcSmartSpaceUtil.setOnClickListener(this.mBaseActionIconSubtitleView, target, baseAction, notifier, "BcSmartspaceCard", loggingInfo, clickIndex);
        }

        updateIconTint();

        SmartspaceAction tapAction = hasHeaderAction ? headerAction : baseAction;
        if (headerAction != null && (headerAction.getIntent() != null || headerAction.getPendingIntent() != null)) {
             tapAction = headerAction;
        } else if (baseAction != null && (baseAction.getIntent() != null || baseAction.getPendingIntent() != null)) {
             tapAction = baseAction;
        }

        if (tapAction != null) {
            int clickIndex = 0;
             if (target.getFeatureType() == 1 && loggingInfo.mFeatureType == 39) {
                   clickIndex = getClickedIndex(loggingInfo, 1);
             }
             BcSmartSpaceUtil.setOnClickListener((View)this, target, tapAction, notifier, "BcSmartspaceCard", loggingInfo, clickIndex);
        }

        if (this.mSecondaryCardGroup != null) {
            ViewGroup.LayoutParams lp = this.mSecondaryCardGroup.getLayoutParams();
            if (lp instanceof ConstraintLayout.LayoutParams) {
                 ConstraintLayout.LayoutParams clp = (ConstraintLayout.LayoutParams) lp;
                 int width = getWidth();
                 if (BcSmartSpaceUtil.getFeatureType(target) == -2) {
                     clp.matchConstraintMaxWidth = (width * 3) / 4;
                 } else {
                     clp.matchConstraintMaxWidth = width / 2;
                 }
                 this.mSecondaryCardGroup.setLayoutParams(clp);
            }
        }
        this.mTouchDelegateIsDirty = true;
    }

    @Override
    public AccessibilityNodeInfo createAccessibilityNodeInfo() {
        AccessibilityNodeInfo nodeInfo = super.createAccessibilityNodeInfo();
        AccessibilityNodeInfoCompat.wrap(nodeInfo).setRoleDescription(" ");
        return nodeInfo;
    }

    @Override
    public BcSmartspaceCardLoggingInfo getLoggingInfo() {
        if (this.mLoggingInfo != null) {
            return this.mLoggingInfo;
        }
        int displaySurface = BcSmartSpaceUtil.getLoggingDisplaySurface(this.mUiSurface, this.mDozeAmount);
        int featureType = 0;
        if (this.mTarget != null) {
            featureType = this.mTarget.getFeatureType();
        }

        BcSmartspaceCardLoggingInfo.Builder builder = new BcSmartspaceCardLoggingInfo.Builder();
        builder.mInstanceId = 0;
        builder.mDisplaySurface = displaySurface;
        builder.mRank = 0;
        builder.mCardinality = 0;
        builder.mFeatureType = featureType;
        builder.mReceivedLatency = 0;
        builder.mUid = -1;
        builder.mSubcardInfo = null;
        builder.mDimensionalInfo = null;
        return new BcSmartspaceCardLoggingInfo(builder);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mTextGroup = findViewById(R.id.text_group);
        this.mSecondaryCardGroup = findViewById(R.id.secondary_card_group);
        this.mTitleTextView = findViewById(R.id.title_text);
        this.mSubtitleTextView = findViewById(R.id.subtitle_text);
        this.mBaseActionIconSubtitleView = findViewById(R.id.base_action_icon_subtitle);

        if (this.mBaseActionIconSubtitleView != null) {
            this.mBaseActionIconSubtitleHitRect = new Rect();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!changed && !this.mTouchDelegateIsDirty) {
            return;
        }
        this.mTouchDelegateIsDirty = false;
        setTouchDelegate(null);
        if (this.mBaseActionIconSubtitleView == null || this.mBaseActionIconSubtitleView.getVisibility() != 0) {
            return;
        }

        int height = this.mBaseActionIconSubtitleView.getHeight();
        int hitRectHeight = getResources().getDimensionPixelSize(R.dimen.subtitle_hit_rect_height);
        int offset = (hitRectHeight - height) / 2;

        this.mBaseActionIconSubtitleView.getHitRect(this.mBaseActionIconSubtitleHitRect);
        offsetDescendantRectToMyCoords((View)this.mBaseActionIconSubtitleView.getParent(), this.mBaseActionIconSubtitleHitRect);

        if (offset > 0) {
             if (this.mBaseActionIconSubtitleHitRect.top - offset >= 0) {
                   this.mBaseActionIconSubtitleHitRect.top -= offset;
             }
        }
        this.mBaseActionIconSubtitleHitRect.bottom = getHeight();
        setTouchDelegate(new TouchDelegate(this.mBaseActionIconSubtitleHitRect, this.mBaseActionIconSubtitleView));
    }

    public void setBaseActionIconSubtitle(CharSequence text, CharSequence contentDescription, Drawable icon) {
        if (this.mBaseActionIconSubtitleView == null) {
            Log.w("BcSmartspaceCard", "No base action icon subtitle view to update");
            return;
        }
        if (TextUtils.isEmpty(text)) {
            BcSmartspaceTemplateDataUtils.updateVisibility(this.mBaseActionIconSubtitleView, 8);
            return;
        }
        BcSmartspaceTemplateDataUtils.updateVisibility(this.mBaseActionIconSubtitleView, 0);
        this.mBaseActionIconSubtitleView.setText(text);
        this.mBaseActionIconSubtitleView.setCompoundDrawablesRelative(icon, null, null, null);
        ContentDescriptionUtil.setFormattedContentDescription("BcSmartspaceCard", this.mBaseActionIconSubtitleView, text, contentDescription);
    }

    @Override
    public void setDozeAmount(float f) {
        this.mDozeAmount = f;
        SmartspaceTarget target = this.mTarget;
        if (target != null) {
             SmartspaceAction baseAction = target.getBaseAction();
             if (baseAction != null && baseAction.getExtras() != null) {
                 Bundle extras = baseAction.getExtras();
                 if (this.mTitleTextView != null && extras.getBoolean("hide_title_on_aod")) {
                     this.mTitleTextView.setAlpha(1.0f - f);
                 }
                 if (this.mSubtitleTextView != null && extras.getBoolean("hide_subtitle_on_aod")) {
                     this.mSubtitleTextView.setAlpha(1.0f - f);
                 }
             }
        }

        if (this.mTextGroup != null) {
            int visibility = 8;
            if (this.mDozeAmount != 1.0f && this.mValidSecondaryCard) {
                visibility = 0;
            }
            BcSmartspaceTemplateDataUtils.updateVisibility(this.mSecondaryCardGroup, visibility);

            if (this.mTarget != null && this.mTarget.getFeatureType() == 30) {
                 return;
            }

            if (this.mSecondaryCardGroup != null && this.mSecondaryCardGroup.getVisibility() != 8) {
                 int direction = isRtl() ? 1 : -1;
                 int width = this.mSecondaryCardGroup.getWidth();
                 this.mTextGroup.setTranslationX(Interpolators.EMPHASIZED.getInterpolation(this.mDozeAmount) * width * direction);
                 this.mSecondaryCardGroup.setAlpha(Math.max(0.0f, Math.min(1.0f, (1.0f - this.mDozeAmount) * 9.0f - 6.0f)));
            } else {
                 this.mTextGroup.setTranslationX(0.0f);
            }
        }
    }

    @Override
    public void setPrimaryTextColor(int i) {
        if (this.mTitleTextView != null) {
            this.mTitleTextView.setTextColor(i);
        }
        if (this.mSubtitleTextView != null) {
            this.mSubtitleTextView.setTextColor(i);
        }
        if (this.mBaseActionIconSubtitleView != null) {
            this.mBaseActionIconSubtitleView.setTextColor(i);
        }
        if (this.mSecondaryCard != null) {
            this.mSecondaryCard.setTextColor(i);
        }
        this.mIconTintColor = i;
        updateIconTint();
    }

    @Override
    public void setScreenOn(boolean z) {
    }

    public void setSecondaryCard(BcSmartspaceCardSecondary secondaryCard) {
        this.mSecondaryCard = secondaryCard;
        if (this.mSecondaryCardGroup != null) {
            BcSmartspaceTemplateDataUtils.updateVisibility(this.mSecondaryCardGroup, 8);
            this.mSecondaryCardGroup.removeAllViews();
            if (secondaryCard != null) {
                ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(-2, getResources().getDimensionPixelSize(R.dimen.enhanced_smartspace_card_height));
                lp.setMarginStart(getResources().getDimensionPixelSize(R.dimen.enhanced_smartspace_secondary_card_start_margin));
                lp.startToStart = 0;
                lp.topToTop = 0;
                lp.bottomToBottom = 0;
                this.mSecondaryCardGroup.addView(secondaryCard, lp);
            }
        }
    }

    public void setSubtitle(CharSequence charSequence, CharSequence charSequence2, boolean z) {
        if (this.mSubtitleTextView == null) {
            Log.w("BcSmartspaceCard", "No subtitle view to update");
            return;
        }
        this.mSubtitleTextView.setText(charSequence);
        DoubleShadowIconDrawable doubleShadowIconDrawable = null;
        if (!TextUtils.isEmpty(charSequence) && z) {
            doubleShadowIconDrawable = this.mIconDrawable;
        }
        this.mSubtitleTextView.setCompoundDrawablesRelative(doubleShadowIconDrawable, null, null, null);

        int maxLines = 1;
        if (this.mTarget != null && this.mTarget.getFeatureType() == 5 && !this.mUsePageIndicatorUi) {
            maxLines = 2;
        }
        this.mSubtitleTextView.setMaxLines(maxLines);

        ContentDescriptionUtil.setFormattedContentDescription("BcSmartspaceCard", this.mSubtitleTextView, charSequence, charSequence2);

        DoubleShadowIconDrawable icon = z ? this.mIconDrawable : null;
        BcSmartspaceTemplateDataUtils.offsetTextViewForIcon(this.mSubtitleTextView, icon, isRtl());
    }

    public void setTitle(CharSequence charSequence, CharSequence charSequence2, boolean z) {
        if (this.mTitleTextView == null) {
            Log.w("BcSmartspaceCard", "No title view to update");
            return;
        }
        this.mTitleTextView.setText(charSequence);
        SmartspaceAction headerAction = (this.mTarget != null) ? this.mTarget.getHeaderAction() : null;
        Bundle extras = (headerAction != null) ? headerAction.getExtras() : null;

        if (extras != null && extras.containsKey("titleEllipsize")) {
            String truncateAt = extras.getString("titleEllipsize");
            try {
                this.mTitleTextView.setEllipsize(TextUtils.TruncateAt.valueOf(truncateAt));
            } catch (IllegalArgumentException e) {
                Log.e("BcSmartspaceCard", "Invalid TruncateAt value: " + truncateAt);
            }
        } else if (this.mTarget != null && this.mTarget.getFeatureType() == 2 && Locale.ENGLISH.getLanguage().equals(getContext().getResources().getConfiguration().locale.getLanguage())) {
            this.mTitleTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        } else {
            this.mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        }

        int titleMaxLines = 0;
        boolean disableTitleIcon = false;
        if (extras != null) {
            titleMaxLines = extras.getInt("titleMaxLines");
            disableTitleIcon = extras.getBoolean("disableTitleIcon");
        }
        if (titleMaxLines != 0) {
            this.mTitleTextView.setMaxLines(titleMaxLines);
        }

        boolean showIcon = z && !disableTitleIcon;
        if (showIcon) {
             ContentDescriptionUtil.setFormattedContentDescription("BcSmartspaceCard", this.mTitleTextView, charSequence, charSequence2);
        }

        DoubleShadowIconDrawable icon = showIcon ? this.mIconDrawable : null;
        this.mTitleTextView.setCompoundDrawablesRelative(icon, null, null, null);
        BcSmartspaceTemplateDataUtils.offsetTextViewForIcon(this.mTitleTextView, icon, isRtl());
    }

    public void updateIconTint() {
        if (this.mTarget == null) return;

        if (this.mTarget.getFeatureType() == 1) {
            this.mIconDrawable.setTintList(null);
        } else {
            this.mIconDrawable.setTint(this.mIconTintColor);
        }

        SmartspaceAction baseAction = this.mTarget.getBaseAction();
        int subcardType = -1;
        if (baseAction != null && baseAction.getExtras() != null) {
             subcardType = baseAction.getExtras().getInt("subcardType", -1);
        }

        if (subcardType == 1) {
            this.mBaseActionIconDrawable.setTintList(null);
        } else {
            this.mBaseActionIconDrawable.setTint(this.mIconTintColor);
        }
    }

    private boolean isRtl() {
        return getLayoutDirection() == 1;
    }
}
