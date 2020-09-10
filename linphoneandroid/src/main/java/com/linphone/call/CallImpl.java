package com.linphone.call;

import com.linphone.util.LinphoneManager;
import org.linphone.core.Address;
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.ProxyConfig;

public class CallImpl implements Call{

    @Override
    public void newCall(String SIPAddress){
        android.util.Log.i("tag1", "make a call");
        Core core = LinphoneManager.getCore();
        android.util.Log.i("tag1", "make a core");
        Address address;
        address = core.interpretUrl(SIPAddress);
        android.util.Log.i("tag1", "make a address");
        address.setDisplayName("displayName");
        android.util.Log.i("tag1", core.isNetworkReachable()+"");
//        if (core.isNetworkReachable()){
            android.util.Log.i("tag1", "NetworkReachable");
            CallParams params = core.createCallParams(null);
            params.enableVideo(false);
            params.setAudioBandwidthLimit(40);
            core.inviteAddressWithParams(address, params);
            android.util.Log.i("tag1", "inviteAddressWithParams");
//        }

    }
}
