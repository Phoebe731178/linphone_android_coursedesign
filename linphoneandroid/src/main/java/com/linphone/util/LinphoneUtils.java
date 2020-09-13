package com.linphone.util;

import android.os.Handler;
import android.os.Looper;

public class LinphoneUtils {

    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    public static void dispatchOnUIThreadAfter(Runnable r, long after) {
        sHandler.postDelayed(r, after);
    }
}
