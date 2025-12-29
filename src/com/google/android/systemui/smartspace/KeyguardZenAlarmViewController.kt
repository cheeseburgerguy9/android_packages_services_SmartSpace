package com.google.android.systemui.smartspace

import android.app.AlarmManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.text.format.DateFormat
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.android.systemui.lifecycle.repeatWhenAttached
import com.android.systemui.plugins.BcSmartspaceDataPlugin
import com.android.systemui.statusbar.policy.NextAlarmController
import com.android.systemui.statusbar.policy.ZenModeController
import com.android.systemui.statusbar.policy.domain.interactor.ZenModeInteractor
import com.android.systemui.statusbar.policy.domain.model.ZenModeInfo
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class KeyguardZenAlarmViewController @Inject constructor(
    val context: Context,
    val datePlugin: BcSmartspaceDataPlugin,
    val zenModeController: ZenModeController,
    val alarmManager: AlarmManager,
    val nextAlarmController: NextAlarmController,
    val handler: Handler,
    val applicationScope: CoroutineScope,
    val bgDispatcher: CoroutineDispatcher,
    val zenModeInteractor: ZenModeInteractor
) {
    val smartspaceViews = mutableSetOf<BcSmartspaceDataPlugin.SmartspaceView>()
    var alarmImage: Drawable? = null

    private val showNextAlarm = AlarmManager.OnAlarmListener { showAlarm() }

    // We need to keep track of the callback wrapper to remove it later if needed
    private val nextAlarmCallback = NextAlarmController.NextAlarmChangeCallback { updateNextAlarm() }

    fun init() {
        // Cast datePlugin to BcSmartspaceDataProvider to access addOnAttachStateChangeListener
        if (datePlugin is BcSmartspaceDataProvider) {
             datePlugin.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    if (!smartspaceViews.contains(v as BcSmartspaceDataPlugin.SmartspaceView)) {
                        smartspaceViews.add(v as BcSmartspaceDataPlugin.SmartspaceView)

                        // Start collecting Zen Mode flow for this view
                        (v as View).repeatWhenAttached {
                             zenModeInteractor.mainActiveMode.collect { mode ->
                                 updateModeIcon(v, mode)
                             }
                        }
                    }

                    if (smartspaceViews.size == 1) {
                        nextAlarmController.addCallback(nextAlarmCallback)
                    }

                    updateNextAlarm()
                }

                override fun onViewDetachedFromWindow(v: View) {
                    smartspaceViews.remove(v as BcSmartspaceDataPlugin.SmartspaceView)
                    if (smartspaceViews.isEmpty()) {
                        nextAlarmController.removeCallback(nextAlarmCallback)
                    }
                }
            })
        }

        // Also start observing alarm immediately in background
        updateNextAlarm()
    }

    fun updateNextAlarm() {
        applicationScope.launch {
            alarmManager.cancel(showNextAlarm)

            var alarm = 0L
            val info = withContext(bgDispatcher) {
                alarmManager.nextAlarmClock
            }
            if (info != null) {
                alarm = info.triggerTime
            }

            if (alarm > 0) {
                val triggerTime = alarm - TimeUnit.HOURS.toMillis(12)
                if (triggerTime > 0) {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        "lock_screen_next_alarm",
                        showNextAlarm,
                        handler
                    )
                }
            }
            showAlarm(alarm)
        }
    }

    private fun showAlarm() {
        updateNextAlarm()
    }

    private fun showAlarm(alarmTime: Long): Job {
         return applicationScope.launch {
             var showTime = alarmTime
             if (showTime > 0) {
                 val limit = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(12)
                 if (showTime <= limit) {
                     val is24 = DateFormat.is24HourFormat(context, android.app.ActivityManager.getCurrentUser())
                     val format = if (is24) "HH:mm" else "h:mm"
                     val timeStr = DateFormat.format(format, showTime).toString()

                     // TODO: Load alarmImage from resources (ic_access_alarms)

                     for (view in smartspaceViews) {
                         if (view is BcSmartspaceView) {
                             view.setNextAlarm(alarmImage, timeStr)
                         }
                     }
                     return@launch
                 }
             }

             for (view in smartspaceViews) {
                 if (view is BcSmartspaceView) {
                     view.setNextAlarm(null, null)
                 }
             }
         }
    }

    fun updateZenMode() {
        // This is kept for compatibility if needed, but the main logic is now in the flow collector
    }

    private suspend fun updateModeIcon(view: BcSmartspaceDataPlugin.SmartspaceView, mainActiveMode: ZenModeInfo?) {
         if (view is BcSmartspaceView) {
             if (mainActiveMode != null) {
                 val icon = mainActiveMode.icon?.drawable
                 val name = mainActiveMode.name
                 val description = context.getString(com.android.systemui.R.string.active_mode_content_description, name)
                 view.setDnd(icon, description)
             } else {
                 view.setDnd(null, null)
             }
         }
    }
}
