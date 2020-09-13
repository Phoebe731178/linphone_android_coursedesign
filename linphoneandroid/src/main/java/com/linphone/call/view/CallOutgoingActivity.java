package com.linphone.call.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.linphone.R;
import com.linphone.addressbook.AddressBookModelImpl;
import com.linphone.call.LinphoneCall;
import com.linphone.call.LinphoneCallImpl;
import com.linphone.util.LinphoneManager;
import com.linphone.vo.Contact;
import org.linphone.core.Address;
import org.linphone.core.Call;


public class CallOutgoingActivity extends Activity {

    private Call mCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_ringup);
        TextView callName = findViewById(R.id.callOutName);
        TextView callPhone = findViewById(R.id.callOutPhone);
        ImageButton callOutHangup = findViewById(R.id.callOutHangup);
        callOutHangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LinphoneCallImpl().callDecline();
            }
        });

        lookUpCall();

        if(mCall != null) {
            Address address = mCall.getRemoteAddress();
            Log.i("callActivity", address.getUsername());
        }
        try {
            String phone = mCall.getRemoteAddress().getUsername().substring(3);
            Log.i("getContact", phone);
            String name = new AddressBookModelImpl(this).findNameFromPhone(phone);
            Log.i("getContact", name);
            callName.setText(name);
            callPhone.setText(phone);
        }
        catch (Exception ignore){}

    }

    public void callOut(String SIPAddress){
//        linphoneCall.newCall("sip:+8618965057688@sip.linphone.org");
        new LinphoneCallImpl().newCall(SIPAddress);
    }

    private void lookUpCall(){
        for (Call call : LinphoneManager.getCore().getCalls()) {
            Call.State cstate = call.getState();
            if (Call.State.OutgoingInit == cstate
                    || Call.State.OutgoingProgress == cstate
                    || Call.State.OutgoingRinging == cstate
                    || Call.State.OutgoingEarlyMedia == cstate) {
                mCall = call;
                break;
            }
        }
    }

}
