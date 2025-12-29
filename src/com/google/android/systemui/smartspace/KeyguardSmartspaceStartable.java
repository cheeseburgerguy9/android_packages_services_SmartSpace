package com.google.android.systemui.smartspace;

import com.android.systemui.CoreStartable;
import com.android.systemui.util.InitializationChecker;
import javax.inject.Inject;

public class KeyguardSmartspaceStartable implements CoreStartable {
    private final InitializationChecker initializationChecker;
    private final KeyguardMediaViewController mediaController;
    private final KeyguardZenAlarmViewController zenController;

    @Inject
    public KeyguardSmartspaceStartable(KeyguardZenAlarmViewController zenController, KeyguardMediaViewController mediaController, InitializationChecker initializationChecker) {
        this.zenController = zenController;
        this.mediaController = mediaController;
        this.initializationChecker = initializationChecker;
    }

    @Override
    public void start() {
        if (!initializationChecker.initializeComponents()) {
            return;
        }

        zenController.init();
        mediaController.init();
    }
}
