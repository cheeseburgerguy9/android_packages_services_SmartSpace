import os

def read_file(path):
    with open(path, 'r') as f:
        return f.readlines()

def write_patch():
    patch_content = []

    # Header
    patch_content.append("From 0000000000000000000000000000000000000000 Mon Sep 17 00:00:00 2001\n")
    patch_content.append("From: Jules <jules@google.com>\n")
    patch_content.append("Date: Fri, 01 Mar 2024 12:00:00 +0000\n")
    patch_content.append("Subject: [PATCH] SystemUI: Add bcsmartspace-google support\n")
    patch_content.append("\n")
    patch_content.append("Integrates bcsmartspace-google library into SystemUI.\n")
    patch_content.append("\n")
    patch_content.append("Includes:\n")
    patch_content.append("- Permissions and flags for smartspace\n")
    patch_content.append("- Integration of KeyguardSmartspaceStartable via SystemUIModule\n")
    patch_content.append("- Smartspace View Controllers and Modules\n")
    patch_content.append("\n")
    patch_content.append("Change-Id: Ibcsmartspace00000000000000000000000000000000\n")
    patch_content.append("---\n")

    # File stats (approximate, git ignores this usually but good to have)
    patch_content.append(" packages/SystemUI/Android.bp                  |   1 +\n")
    patch_content.append(" packages/SystemUI/AndroidManifest.xml         |  13 ++\n")
    patch_content.append(" packages/SystemUI/res/values/flags.xml        |   2 +\n")
    patch_content.append(" .../systemui/dagger/SystemUIModule.java       |   5 +\n")
    patch_content.append(" .../KeyguardMediaViewController.kt            | 107 +++++++++++++\n")
    patch_content.append(" .../KeyguardSmartspaceStartable.kt            |  35 +++++\n")
    patch_content.append(" .../KeyguardZenAlarmViewController.kt         | 145 ++++++++++++++++++\n")
    patch_content.append(" .../dagger/SmartspaceGoogleModule.java        |  25 ++++\n")
    patch_content.append(" .../dagger/SmartspaceStartableModule.kt       |  32 ++++\n")
    patch_content.append(" 9 files changed, 365 insertions(+)\n")
    patch_content.append(" create mode 100644 packages/SystemUI/src/com/google/android/systemui/smartspace/KeyguardMediaViewController.kt\n")
    patch_content.append(" create mode 100644 packages/SystemUI/src/com/google/android/systemui/smartspace/KeyguardSmartspaceStartable.kt\n")
    patch_content.append(" create mode 100644 packages/SystemUI/src/com/google/android/systemui/smartspace/KeyguardZenAlarmViewController.kt\n")
    patch_content.append(" create mode 100644 packages/SystemUI/src/com/google/android/systemui/smartspace/dagger/SmartspaceGoogleModule.java\n")
    patch_content.append(" create mode 100644 packages/SystemUI/src/com/google/android/systemui/smartspace/dagger/SmartspaceStartableModule.kt\n")
    patch_content.append("\n")

    # Android.bp
    patch_content.append("diff --git a/packages/SystemUI/Android.bp b/packages/SystemUI/Android.bp\n")
    patch_content.append("index 7c8b683d2d6d..513512d80642 100644\n")
    patch_content.append("--- a/packages/SystemUI/Android.bp\n")
    patch_content.append("+++ b/packages/SystemUI/Android.bp\n")
    patch_content.append("@@ -540,6 +540,7 @@ android_library {\n")
    patch_content.append("         \"kairos\",\n")
    patch_content.append("         \"displaylib\",\n")
    patch_content.append("         \"aconfig_settings_flags_lib\",\n")
    patch_content.append("+        \"bcsmartspace-google\",\n")
    patch_content.append("     ],\n")
    patch_content.append("     manifest: \"AndroidManifest.xml\",\n")
    patch_content.append("     libs: [\n")

    # AndroidManifest.xml
    patch_content.append("diff --git a/packages/SystemUI/AndroidManifest.xml b/packages/SystemUI/AndroidManifest.xml\n")
    patch_content.append("index 73b77979cd91..6c3f4bc5849c 100644\n")
    patch_content.append("--- a/packages/SystemUI/AndroidManifest.xml\n")
    patch_content.append("+++ b/packages/SystemUI/AndroidManifest.xml\n")
    patch_content.append("@@ -407,6 +407,16 @@\n")
    patch_content.append("     <protected-broadcast android:name=\"com.android.systemui.action.ACTION_LAUNCH_MEDIA_OUTPUT_BROADCAST_DIALOG\" />\n")
    patch_content.append("     <protected-broadcast android:name=\"com.android.systemui.STARTED\" />\n")
    patch_content.append(" \n")
    patch_content.append("+    <!-- Smartspace -->\n")
    patch_content.append("+    <uses-permission android:name=\"android.permission.ACCESS_NOTIFICATION_POLICY\"/>\n")
    patch_content.append("+    <uses-permission android:name=\"android.permission.INTERACT_ACROSS_USERS_FULL\"/>\n")
    patch_content.append("+    <permission android:name=\"com.android.systemui.permission.SEND_ALERT_BROADCASTS\" android:protectionLevel=\"preinstalled|signature\"/>\n")
    patch_content.append("+    <uses-permission android:name=\"com.google.android.deskclock.permission.RECEIVE_ALERT_BROADCASTS\"/>\n")
    patch_content.append("+    <uses-permission android:name=\"com.google.android.apps.nexuslauncher.permission.QSB\"/>\n")
    patch_content.append("+    <protected-broadcast android:name=\"com.google.android.systemui.smartspace.CLICK_EVENT\"/>\n")
    patch_content.append("+    <protected-broadcast android:name=\"com.google.android.systemui.smartspace.ENABLE_UPDATE\"/>\n")
    patch_content.append("+    <protected-broadcast android:name=\"com.google.android.systemui.smartspace.EXPIRE_EVENT\"/>\n")
    patch_content.append("+\n")
    patch_content.append("     <application\n")
    patch_content.append("         android:name=\".SystemUIApplication\"\n")
    patch_content.append("         android:persistent=\"true\"\n")
    patch_content.append("@@ -1077,7 +1087,8 @@\n")
    patch_content.append("         <provider android:name=\".keyguard.KeyguardSliceProvider\"\n")
    patch_content.append("                   android:authorities=\"com.android.systemui.keyguard\"\n")
    patch_content.append("                   android:grantUriPermissions=\"true\"\n")
    patch_content.append("-                  android:exported=\"true\">\n")
    patch_content.append("+                  android:exported=\"true\"\n")
    patch_content.append("+                  android:enabled=\"false\">\n")
    patch_content.append("         </provider>\n")
    patch_content.append(" \n")
    patch_content.append("         <receiver\n")

    # flags.xml
    patch_content.append("diff --git a/packages/SystemUI/res/values/flags.xml b/packages/SystemUI/res/values/flags.xml\n")
    patch_content.append("index 07a40c86d03a..5d80b46f65a4 100644\n")
    patch_content.append("--- a/packages/SystemUI/res/values/flags.xml\n")
    patch_content.append("+++ b/packages/SystemUI/res/values/flags.xml\n")
    patch_content.append("@@ -30,6 +30,8 @@\n")
    patch_content.append(" \n")
    patch_content.append("     <bool name=\"flag_charging_ripple\">false</bool>\n")
    patch_content.append(" \n")
    patch_content.append("+    <bool name=\"flag_smartspace\">true</bool>\n")
    patch_content.append("+\n")
    patch_content.append("     <!--  Whether the user switcher chip shows in the status bar. When true, the multi user\n")
    patch_content.append("       avatar will no longer show on the lockscreen -->\n")
    patch_content.append("     <bool name=\"flag_user_switcher_chip\">false</bool>\n")

    # SystemUIModule.java
    patch_content.append("diff --git a/packages/SystemUI/src/com/android/systemui/dagger/SystemUIModule.java b/packages/SystemUI/src/com/android/systemui/dagger/SystemUIModule.java\n")
    patch_content.append("index 38c456f03c72..6a5e4e46daeb 100644\n")
    patch_content.append("--- a/packages/SystemUI/src/com/android/systemui/dagger/SystemUIModule.java\n")
    patch_content.append("+++ b/packages/SystemUI/src/com/android/systemui/dagger/SystemUIModule.java\n")
    patch_content.append("@@ -176,6 +176,9 @@\n")
    patch_content.append(" import com.android.systemui.wmshell.BubblesManager;\n")
    patch_content.append(" import com.android.wm.shell.bubbles.Bubbles;\n")
    patch_content.append(" \n")
    patch_content.append("+import com.google.android.systemui.smartspace.dagger.SmartspaceGoogleModule;\n")
    patch_content.append("+import com.google.android.systemui.smartspace.dagger.SmartspaceStartableModule;\n")
    patch_content.append("+\n")
    patch_content.append(" import dagger.Binds;\n")
    patch_content.append(" import dagger.BindsOptionalOf;\n")
    patch_content.append(" import dagger.Module;\n")
    patch_content.append("@@ -236,6 +239,8 @@\n")
    patch_content.append("         ScreenBrightnessModule.class,\n")
    patch_content.append("         SettingsModule.class,\n")
    patch_content.append("         SettingsProviderModule.class,\n")
    patch_content.append("+        SmartspaceGoogleModule.class,\n")
    patch_content.append("+        SmartspaceStartableModule.class,\n")
    patch_content.append("         StatusBarPipelineModule.class,\n")
    patch_content.append("         SysUIConcurrencyModule.class,\n")
    patch_content.append("         SysUICoroutinesModule.class,\n")

    # New Files
    new_files = [
        ("src/com/google/android/systemui/smartspace/KeyguardMediaViewController.kt", "packages/SystemUI/src/com/google/android/systemui/smartspace/KeyguardMediaViewController.kt"),
        ("src/com/google/android/systemui/smartspace/KeyguardSmartspaceStartable.kt", "packages/SystemUI/src/com/google/android/systemui/smartspace/KeyguardSmartspaceStartable.kt"),
        ("src/com/google/android/systemui/smartspace/KeyguardZenAlarmViewController.kt", "packages/SystemUI/src/com/google/android/systemui/smartspace/KeyguardZenAlarmViewController.kt"),
        ("src/com/google/android/systemui/smartspace/dagger/SmartspaceGoogleModule.java", "packages/SystemUI/src/com/google/android/systemui/smartspace/dagger/SmartspaceGoogleModule.java"),
        ("src/com/google/android/systemui/smartspace/dagger/SmartspaceStartableModule.kt", "packages/SystemUI/src/com/google/android/systemui/smartspace/dagger/SmartspaceStartableModule.kt")
    ]

    for local_path, target_path in new_files:
        lines = read_file(local_path)
        count = len(lines)
        if not lines[-1].endswith('\n'):
             # If file doesn't end with newline, git diff usually says "No newline at end of file"
             # But for simplicity, we assume we want it to end with newline.
             lines[-1] = lines[-1] + '\n'

        patch_content.append(f"diff --git a/{target_path} b/{target_path}\n")
        patch_content.append(f"new file mode 100644\n")
        patch_content.append(f"index 000000000000..000000000000\n") # Placeholder index
        patch_content.append(f"--- /dev/null\n")
        patch_content.append(f"+++ b/{target_path}\n")
        patch_content.append(f"@@ -0,0 +1,{count} @@\n")
        for line in lines:
            patch_content.append(f"+{line}")

    patch_content.append("-- \n")
    patch_content.append("2.44.0\n")

    with open("frameworks_base.patch", "w") as f:
        f.writelines(patch_content)

write_patch()
