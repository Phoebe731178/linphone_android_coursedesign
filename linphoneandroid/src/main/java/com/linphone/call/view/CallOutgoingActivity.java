package com.linphone.call.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.linphone.R;
import com.linphone.call.LinphoneCall;
import com.linphone.call.LinphoneCallImpl;
import com.linphone.vo.Contact;


public class CallOutgoingActivity extends Activity {

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
        try {
            Contact contact = LinphoneCallImpl.contactDetail;
            callName.setText(contact.getName());
            callPhone.setText(contact.getPhones().get(0));
        }
        catch (Exception ignore){}

    }

    public void callOut(Contact contact){
//        linphoneCall.newCall("sip:+8618965057688@sip.linphone.org");
        new LinphoneCallImpl().newCall(contact);
    }



}
