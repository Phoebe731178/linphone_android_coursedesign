package com.linphone.call.view;

import android.view.WindowManager;

public interface LinphoneOverlay {
    WindowManager.LayoutParams getWindowManagerLayoutParams();

    void addToWindowManager(WindowManager mWindowManager, WindowManager.LayoutParams params);

    void removeFromWindowManager(WindowManager mWindowManager);

    void destroy();
}