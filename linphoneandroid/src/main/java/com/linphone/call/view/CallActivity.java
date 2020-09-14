package com.linphone.call.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.linphone.R;
import com.linphone.addressbook.AddressBookModelImpl;
import com.linphone.addressbook.view.AddressBookImpl;
import com.linphone.call.LinphoneCall;
import com.linphone.call.LinphoneCallImpl;
import com.linphone.util.LinphoneManager;
import com.linphone.vo.Contact;
import org.linphone.core.Address;
import org.linphone.core.Call;

public class CallActivity extends Activity {

    private Call mCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_connect);
        TextView callName = findViewById(R.id.contactName);
        TextView callPhone = findViewById(R.id.phoneNumber);
        ImageButton drop = findViewById(R.id.hangup);

        for (Call call : LinphoneManager.getCore().getCalls()) {
            Call.State cstate = call.getState();
            if (Call.State.OutgoingInit == cstate
                    || Call.State.OutgoingProgress == cstate
                    || Call.State.OutgoingRinging == cstate
                    || Call.State.OutgoingEarlyMedia == cstate
                    || Call.State.Connected == cstate
                    || Call.State.StreamsRunning == cstate) {
                mCall = call;
                break;
            }
        }

        try {
            String phone = mCall.getRemoteAddress().getUsername().substring(3);
            Log.i("getContact", phone);
            String name = new AddressBookModelImpl(this).findNameFromPhone(phone).getName();
            Log.i("getContact", name);
            callName.setText(name);
            callPhone.setText(phone);
        }
        catch (Exception ignore){}

        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LinphoneCallImpl().callDecline();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME){
            new LinphoneCallImpl().callDecline();
        }
        return super.onKeyDown(keyCode, event);
    }

}
