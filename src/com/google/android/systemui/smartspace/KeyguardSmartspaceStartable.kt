package com.google.android.systemui.smartspace;

import android.view.View
import com.android.systemui.CoreStartable
import com.android.systemui.plugins.BcSmartspaceDataPlugin
import com.android.systemui.util.InitializationChecker

class KeyguardSmartspaceStartable(
    private val zenController: KeyguardZenAlarmViewController,
    private val mediaController: KeyguardMediaViewController,
    private val initializationChecker: InitializationChecker
) : CoreStartable {

    override fun start() {
        if (!initializationChecker.initializeComponents()) {
            return
        }

        zenController.init()

        // Bind ZenController to DatePlugin
        (zenController.datePlugin as? View)?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                if (v is BcSmartspaceDataPlugin.SmartspaceView) {
                    zenController.addSmartspaceView(v)
                }
            }

            override fun onViewDetachedFromWindow(v: View) {
                // Remove view logic if needed
            }
        })

        // Bind MediaController to its Plugin
        (mediaController.plugin as? View)?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                if (v is BcSmartspaceDataPlugin.SmartspaceView) {
                    mediaController.setSmartspaceView(v)
                }
            }

            override fun onViewDetachedFromWindow(v: View) {
                mediaController.removeSmartspaceView()
            }
        })
    }
}
