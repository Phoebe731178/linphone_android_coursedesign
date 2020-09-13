package com.linphone.call;

import android.util.Log;
import com.linphone.util.LinphoneManager;
import com.linphone.vo.Contact;
import org.linphone.core.*;

public class LinphoneCallImpl implements LinphoneCall{

    private Call mCall;

    @Override
    public void newCall(String SIPAddress){

        android.util.Log.i("Call", "make a call");
        Core core = LinphoneManager.getCore();
        if(!isSIP(SIPAddress)) {
            SIPAddress = toSIP(SIPAddress);
        }
        android.util.Log.i("Call", "make a core");
        Address address;
        address = core.interpretUrl(SIPAddress);
        android.util.Log.i("Call", "make a address");
        CallParams params = core.createCallParams(null);
        params.enableVideo(false);
        params.setAudioBandwidthLimit(40);
        Call mCall = core.inviteAddressWithParams(address, params);
        android.util.Log.i("Call", "inviteAddressWithParams");

    }

    public void callDecline(){
        for(Call call: LinphoneManager.getCore().getCalls()){
            Call.State cstate = call.getState();
            if (Call.State.OutgoingInit == cstate
                    || Call.State.OutgoingProgress == cstate
                    || Call.State.OutgoingRinging == cstate
                    || Call.State.Connected == cstate
                    || Call.State.StreamsRunning == cstate
                    || Call.State.IncomingReceived == call.getState()
                    || Call.State.IncomingEarlyMedia == call.getState()){
                mCall = call;
                break;
            }
        }
        if(mCall != null) {
            mCall.terminate();
        }
    }

    @Override
    public void acceptCall(Call call){
        if(call != null){
            Core core = LinphoneManager.getCore();
            CallParams params = core.createCallParams(call);
            call.acceptWithParams(params);
        }
    }

    private boolean isSIP(String SIPAddress){
        if(!SIPAddress.contains("@") || !SIPAddress.startsWith("sip:") || !SIPAddress.contains("+86")){
            return false;
        }
        return true;
    }

    private String toSIP(String SIPAddress){
        return "sip:+86" + SIPAddress + "@sip.linphone.org";
    }

}
