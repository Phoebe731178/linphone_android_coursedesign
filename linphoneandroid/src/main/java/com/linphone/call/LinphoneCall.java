package com.linphone.call;

import org.linphone.core.Call;

public interface LinphoneCall {
    void newCall(String SIPAddress);
    void callDecline();
    void acceptCall(Call call);
}