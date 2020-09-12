package com.linphone.call.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.linphone.R;
import com.linphone.call.LinphoneCallImpl;
import com.linphone.util.LinphoneManager;
import com.linphone.vo.Contact;
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

        lookupCurrentCall();
        if(mCall != null){
            new LinphoneCallImpl().acceptCall(mCall);
        }

        try {
            Contact contact = LinphoneCallImpl.contactDetail;
            callName.setText(contact.getName());
            callPhone.setText(contact.getPhones().get(0));
        }
        catch (Exception ignore){}

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
