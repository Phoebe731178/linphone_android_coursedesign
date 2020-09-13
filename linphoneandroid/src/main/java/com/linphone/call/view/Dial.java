package com.linphone.call.view;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.linphone.R;
import com.linphone.call.LinphoneCallImpl;

public class Dial extends AppCompatActivity {
    private EditText editNumber;
    private String number;
    private ImageButton deleteNumber;
    private ImageButton dialCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial);

        editNumber = (EditText) findViewById(R.id.editText1);
        dialCall = findViewById(R.id.dialCall);

        deleteNumber = (ImageButton) findViewById(R.id.deleteNumber);
        deleteNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = editNumber.getText().toString();
                if (number.length() > 0)
                    editNumber.setText(number.substring(0, number.length() - 1));
            }
        });

        dialCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LinphoneCallImpl().newCall(editNumber.getText().toString());
            }
        });
    }

    public void telNumber(View view) {
        editNumber.append(view.getTag().toString());
    }


}
