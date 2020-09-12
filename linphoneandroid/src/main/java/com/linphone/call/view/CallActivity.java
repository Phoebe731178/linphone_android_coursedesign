package com.linphone.call.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.linphone.R;
import com.linphone.call.LinphoneCall;
import com.linphone.call.LinphoneCallImpl;
import com.linphone.vo.Contact;

public class CallActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_connect);
        TextView callName = findViewById(R.id.contactName);
        TextView callPhone = findViewById(R.id.phoneNumber);
        ImageButton drop = findViewById(R.id.hangup);

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
}
