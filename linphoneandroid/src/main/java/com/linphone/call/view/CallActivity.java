package com.linphone.call.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.linphone.R;
import com.linphone.addressbook.AddressBookModelImpl;
import com.linphone.addressbook.view.AddressBookImpl;
import com.linphone.call.LinphoneCall;
import com.linphone.call.LinphoneCallImpl;
import com.linphone.util.LinphoneManager;
import com.linphone.util.settings.LinphonePreferences;
import com.linphone.vo.Contact;
import org.linphone.core.*;

public class CallActivity extends Activity {

    private Call mCall;
    private LinphoneOverlay mOverlay;
    private WindowManager mWindowManager;
    private TextureView mLocalPreview, mRemoteVideo;
    private CoreListener callListener;
    private AlertDialog.Builder mCallUpdateDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_connect);
        TextView callName = findViewById(R.id.contactName);
        TextView callPhone = findViewById(R.id.phoneNumber);
        ImageButton drop = findViewById(R.id.hangup);
        ImageButton video = findViewById(R.id.video);
        mLocalPreview = findViewById(R.id.local_preview_texture);
        mRemoteVideo = findViewById(R.id.remote_video_texture);

        callListener = new CoreListenerStub() {
            @Override
            public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
                super.onCallStateChanged(core, call, state, message);
                if (state == Call.State.UpdatedByRemote){

                    showAcceptCallUpdateDialog();
                    boolean remoteVideo = mCall.getRemoteParams().videoEnabled();
                    boolean localVideo = mCall.getCurrentParams().videoEnabled();
                    boolean autoAcceptCameraPolicy =
                            LinphonePreferences.instance()
                                    .shouldAutomaticallyAcceptVideoRequests();
                    if (remoteVideo
                            && !localVideo
                            && !autoAcceptCameraPolicy
                            && core.getConference() == null) {
                        Log.i("accept update", "success");
                        mCall.deferUpdate();
                    }
                }
            }
        };
        LinphoneManager.getCore().addListener(callListener);

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

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openVideo();
            }
        });

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

    private void openVideo(){
        mRemoteVideo.setVisibility(View.VISIBLE);
        mLocalPreview.setVisibility(View.VISIBLE);

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        Core core = LinphoneManager.getCore();
        if ("MSAndroidOpenGLDisplay".equals(core.getVideoDisplayFilter())) {
            mOverlay = new LinphoneGL2JNIViewOverlay(CallActivity.this);
        } else {
            mOverlay = new LinphoneTextureViewOverlay(CallActivity.this);
        }
        WindowManager.LayoutParams params = mOverlay.getWindowManagerLayoutParams();
        params.x = 0;
        params.y = 0;
        mOverlay.addToWindowManager(mWindowManager, params);
        core.setNativeVideoWindowId(mRemoteVideo);
        core.setNativePreviewWindowId(mLocalPreview);
        mCall.enableCamera(true);
        CallParams callParams = core.createCallParams(mCall);
        callParams.enableVideo(true);
        callParams.setAudioBandwidthLimit(0);
        mCall.update(callParams);
    }

    private void showAcceptCallUpdateDialog(){

        mCallUpdateDialog = new AlertDialog.Builder(this);
        mCallUpdateDialog.setTitle("title");
        mCallUpdateDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Core core = LinphoneManager.getCore();
                core.enableVideoCapture(true);
                core.enableVideoDisplay(true);
                CallParams params = core.createCallParams(mCall);
                params.enableVideo(true);
                params.setAudioBandwidthLimit(0);
                mCall.acceptUpdate(params);
                mRemoteVideo.setVisibility(View.VISIBLE);
                mLocalPreview.setVisibility(View.VISIBLE);

                mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

                if ("MSAndroidOpenGLDisplay".equals(core.getVideoDisplayFilter())) {
                    mOverlay = new LinphoneGL2JNIViewOverlay(CallActivity.this);
                } else {
                    mOverlay = new LinphoneTextureViewOverlay(CallActivity.this);
                }
                WindowManager.LayoutParams layoutParams = mOverlay.getWindowManagerLayoutParams();
                layoutParams.x = 0;
                layoutParams.y = 0;
                mOverlay.addToWindowManager(mWindowManager, layoutParams);
                core.setNativeVideoWindowId(mRemoteVideo);
                core.setNativePreviewWindowId(mLocalPreview);
            }
        });
//        mCallUpdateDialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
        mCallUpdateDialog.show();
    }

}