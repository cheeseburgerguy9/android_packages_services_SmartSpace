package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.content.ComponentName;
import android.content.Context;
import android.media.MediaMetadata;
import android.os.UserHandle;
import android.text.TextUtils;
import android.view.View;

import com.android.systemui.media.NotificationMediaManager;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.util.concurrency.DelayableExecutor;

import javax.inject.Inject;

public final class KeyguardMediaViewController {
    public CharSequence artist;
    public Context context;
    public ComponentName mediaComponent;
    public final NotificationMediaManager.MediaListener mediaListener = new NotificationMediaManager.MediaListener() {
        @Override
        public void onPrimaryMetadataOrStateChanged(MediaMetadata metadata, int state) {
            uiExecutor.execute(() -> {
                updateMediaInfo(metadata, state);
            });
        }
    };
    public NotificationMediaManager mediaManager;
    public BcSmartspaceDataPlugin plugin;
    public BcSmartspaceDataPlugin.SmartspaceView smartspaceView;
    public CharSequence title;
    public DelayableExecutor uiExecutor;
    public UserTracker userTracker;

    public final View.OnAttachStateChangeListener attachStateChangeListener = new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View v) {
            smartspaceView = (BcSmartspaceDataPlugin.SmartspaceView) v;
            mediaManager.addCallback(mediaListener);
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            smartspaceView = null;
            mediaManager.removeCallback(mediaListener);
        }
    };

    @Inject
    public KeyguardMediaViewController(
            Context context,
            NotificationMediaManager mediaManager,
            BcSmartspaceDataPlugin plugin,
            DelayableExecutor uiExecutor,
            UserTracker userTracker) {
        this.context = context;
        this.mediaManager = mediaManager;
        this.plugin = plugin;
        this.uiExecutor = uiExecutor;
        this.userTracker = userTracker;
        this.mediaComponent = new ComponentName(context, KeyguardMediaViewController.class);
    }

    public void init() {
         if (plugin instanceof BcSmartspaceDataProvider) {
             ((BcSmartspaceDataProvider) plugin).addOnAttachStateChangeListener(attachStateChangeListener);
         }
    }

    private void updateMediaInfo(MediaMetadata metadata, int state) {
        if (!NotificationMediaManager.isPlayingState(state)) {
            title = null;
            artist = null;
            if (smartspaceView != null) {
                // Cast to BcSmartspaceView to use setMediaTarget
                if (smartspaceView instanceof BcSmartspaceView) {
                    ((BcSmartspaceView) smartspaceView).setMediaTarget(null);
                }
            }
            return;
        }

        CharSequence newTitle = null;
        if (metadata != null) {
            newTitle = metadata.getText(MediaMetadata.METADATA_KEY_DISPLAY_TITLE);
            if (TextUtils.isEmpty(newTitle)) {
                newTitle = metadata.getText(MediaMetadata.METADATA_KEY_TITLE);
            }
        }

        if (TextUtils.isEmpty(newTitle)) {
             newTitle = context.getResources().getString(com.android.systemui.R.string.music_controls_no_title);
        }

        CharSequence newArtist = null;
        if (metadata != null) {
            newArtist = metadata.getText(MediaMetadata.METADATA_KEY_ARTIST);
        }

        if (TextUtils.equals(title, newTitle) && TextUtils.equals(artist, newArtist)) {
            return;
        }

        title = newTitle;
        artist = newArtist;

        if (title != null) {
            SmartspaceAction.Builder actionBuilder = new SmartspaceAction.Builder("deviceMediaTitle", title.toString())
                    .setSubtitle(artist)
                    .setIcon(mediaManager.getMediaIcon());

            SmartspaceAction action = actionBuilder.build();

            UserHandle userHandle = UserHandle.of(userTracker.getUserId());

            SmartspaceTarget.Builder targetBuilder = new SmartspaceTarget.Builder("deviceMedia", mediaComponent, userHandle)
                    .setFeatureType(SmartspaceTarget.FEATURE_MEDIA)
                    .setHeaderAction(action);

            SmartspaceTarget target = targetBuilder.build();

            if (smartspaceView != null) {
                if (smartspaceView instanceof BcSmartspaceView) {
                     ((BcSmartspaceView) smartspaceView).setMediaTarget(target);
                }
            }
        } else {
             title = null;
             artist = null;
             if (smartspaceView != null) {
                 if (smartspaceView instanceof BcSmartspaceView) {
                      ((BcSmartspaceView) smartspaceView).setMediaTarget(null);
                 }
             }
        }
    }
}
