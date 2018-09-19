package com.example.androidgesturedemo.tool;

import com.dianming.support.Fusion;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

public class ToolUtils {
    /**
     * 是否有外存卡
     * @return
     */
    public static boolean isExistExternalStore() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
    
    public static void syncToast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
        Fusion.syncTTS(content);
    }
    
    public static final String DM_CURRENT_ACCESSIBILITY_SERVICE = "com.dianming.phoneapp.MyAccessibilityService";
    public static boolean isAccessibilityEnabled(Context context) {
        final int accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                Settings.Secure.ACCESSIBILITY_ENABLED, 0);
        return accessibilityEnabled == 1;
    }
    
    public static boolean isAccessibilityServiceEnabled(Context context, String accessibilityServiceName) {
        final String services = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return services != null && services.contains(accessibilityServiceName);
    }
}
