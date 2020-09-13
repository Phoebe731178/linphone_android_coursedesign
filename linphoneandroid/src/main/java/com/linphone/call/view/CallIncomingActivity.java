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
import com.linphone.call.LinphoneCallImpl;
import com.linphone.util.LinphoneManager;
import com.linphone.util.LinphoneUtils;
import com.linphone.vo.Contact;
import org.linphone.core.Address;
import org.linphone.core.Call;

public class CallIncomingActivity extends Activity {

    private Call mCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_incoming);
        TextView callName = findViewById(R.id.callIncomingName);
        TextView callPhone = findViewById(R.id.callIncomingPhone);
        ImageButton drop = findViewById(R.id.callIncomingHangup);
        ImageButton answer = findViewById(R.id.callIncomingAnswer);

        lookupCurrentCall();
        if(mCall != null){
            answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinphoneUtils.dispatchOnUIThreadAfter(new Runnable() {
                        @Override
                        public void run() {
                            new LinphoneCallImpl().acceptCall(mCall);
                        }
                    },0);
                }
            });
            drop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new LinphoneCallImpl().callDecline();
                }
            });
            Address address = mCall.getRemoteAddress();
            Log.i("callActivity", address.getUsername());
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



        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LinphoneCallImpl().callDecline();
            }
        });
    }

    public void lookupCurrentCall(){
        if (LinphoneManager.getCore() != null) {
            for (Call call : LinphoneManager.getCore().getCalls()) {
                if (Call.State.IncomingReceived == call.getState()
                        || Call.State.IncomingEarlyMedia == call.getState()) {
                    mCall = call;
                    break;
                }
            }
        }
    }

}
