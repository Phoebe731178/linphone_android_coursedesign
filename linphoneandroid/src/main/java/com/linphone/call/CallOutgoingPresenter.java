package com.linphone.call;

import android.content.Context;
import com.linphone.call.view.CallOutgoingActivity;
import com.linphone.vo.Contact;

public class CallOutgoingPresenter {

    private CallOutgoingActivity callOutgoingActivity;

    public CallOutgoingPresenter(){
        this.callOutgoingActivity = new CallOutgoingActivity();
    }


    public void makeCall(Contact contact){
        callOutgoingActivity.callOut(contact);
    }

}
