package com.linphone.baritem.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import com.linphone.R;
import com.linphone.about.view.AboutActivity;
import com.linphone.login.view.LoginPhoneActivity;


public class BarItemActivity extends Activity {

    private LinearLayout assistant;
    private LinearLayout about;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_top_guillotine);

        this.assistant = findViewById(R.id.assistant_group);
        this.about = findViewById(R.id.about_group);

        assistant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BarItemActivity.this,
                        LoginPhoneActivity.class));
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BarItemActivity.this,
                        AboutActivity.class));
            }
        });
    }
}
