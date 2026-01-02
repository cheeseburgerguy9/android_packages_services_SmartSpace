package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceTarget;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.systemui.bcsmartspace.R;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class BcSmartspaceDataProvider implements BcSmartspaceDataPlugin, View.OnAttachStateChangeListener {
    public final Set<BcSmartspaceDataPlugin.SmartspaceTargetListener> mSmartspaceTargetListeners = new HashSet<>();
    public final List<SmartspaceTarget> mSmartspaceTargets = new ArrayList<>();
    public final EventNotifierProxy mEventNotifier = new EventNotifierProxy();
    public View mAttachedView;

    @Override
    public final BcSmartspaceDataPlugin.SmartspaceEventNotifier getEventNotifier() {
        return mEventNotifier;
    }

    @Override
    public final BcSmartspaceDataPlugin.SmartspaceView getLargeClockView(Context context) {
        return null;
    }

    @Override
    public final BcSmartspaceDataPlugin.SmartspaceView getView(Context context) {
        BcSmartspaceView view = (BcSmartspaceView) LayoutInflater.from(context).inflate(R.layout.smartspace_enhanced, (ViewGroup) null, false);
        view.addOnAttachStateChangeListener(this);
        view.registerDataProvider(this);
        return view;
    }

    @Override
    public void onTargetsAvailable(List<SmartspaceTarget> targets) {
         mSmartspaceTargets.clear();
         mSmartspaceTargets.addAll(targets);
         mSmartspaceTargetListeners.forEach(listener -> listener.onSmartspaceTargetsUpdated(mSmartspaceTargets));
    }

    @Override
    public final void registerListener(BcSmartspaceDataPlugin.SmartspaceTargetListener listener) {
        mSmartspaceTargetListeners.add(listener);
        listener.onSmartspaceTargetsUpdated(mSmartspaceTargets);
    }

    @Override
    public final void unregisterListener(BcSmartspaceDataPlugin.SmartspaceTargetListener listener) {
        mSmartspaceTargetListeners.remove(listener);
    }

    @Override
    public final void setEventDispatcher(BcSmartspaceDataPlugin.SmartspaceEventDispatcher eventDispatcher) {
        mEventNotifier.eventDispatcher = eventDispatcher;
    }

    @Override
    public final void setIntentStarter(BcSmartspaceDataPlugin.IntentStarter intentStarter) {
        mEventNotifier.intentStarterRef = intentStarter;
    }

    @Override
    public void onViewAttachedToWindow(View v) {
        mAttachedView = v;
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        mAttachedView = null;
    }
}
