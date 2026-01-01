package com.google.android.systemui.smartspace

import com.android.systemui.CoreStartable
import com.android.systemui.util.InitializationChecker

import javax.inject.Inject

import kotlinx.coroutines.launch

class KeyguardSmartspaceStartable
@Inject
constructor(
    private val zenController: KeyguardZenAlarmViewController,
    private val mediaController: KeyguardMediaViewController,
    private val initializationChecker: InitializationChecker
) : CoreStartable {

    override fun start() {
        if (initializationChecker.initializeComponents()) {
            val datePlugin = zenController.datePlugin
            if (datePlugin is BcSmartspaceDataProvider) {
                datePlugin.addOnAttachStateChangeListener(zenController.attachStateChangeListener)
            }

            zenController.applicationScope.launch { 
                zenController.updateNextAlarm() 
            }

            val mediaPlugin = mediaController.plugin
            if (mediaPlugin is BcSmartspaceDataProvider) {
                mediaPlugin.addOnAttachStateChangeListener(mediaController.attachStateChangeListener)
            }
        }
    }
}
