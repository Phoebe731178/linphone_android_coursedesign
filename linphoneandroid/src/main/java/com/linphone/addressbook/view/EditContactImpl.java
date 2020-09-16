package com.linphone.addressbook.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.linphone.R;
import com.linphone.addressbook.AddressBookModelImpl;
import com.linphone.addressbook.EditContactPresenter;
import com.linphone.vo.Contact;

import java.util.Arrays;
import java.util.List;

public class EditContactImpl extends AppCompatActivity implements EditContact, TextWatcher {

    EditContactPresenter editContactPresenter = new EditContactPresenter(EditContactImpl.this, this);

    private EditText et_name;
    private EditText editphone;
    private AddressBookModelImpl.UpdateType updateType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        initViews();
        Log.i("tag2", "editContactPresenter editContactNameAndPhone");
        editContactPresenter.editContactNameAndPhone();
    }

    private void initViews()
    {
        et_name = findViewById(R.id.editName);
        editphone = findViewById(R.id.editPhone);
        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                updateType = AddressBookModelImpl.UpdateType.NAME;
            }
        });
        editphone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                updateType = AddressBookModelImpl.UpdateType.PHONE;
            }
        });
    }

    @Override
    public void EditContactNameAndPhone()
    {
        Intent intent = getIntent();
        final String name = intent.getStringExtra("contact_name");
        et_name.setText(name);
        final String phone = intent.getStringExtra("contact_phone");
        editphone.setText(phone);
        final String id = intent.getStringExtra("contact_id");
        Log.i("tag2", "contact_name: " + name);
        Log.i("tag2", "phone: " + phone);
        final ImageView editok = findViewById(R.id.ok);
        editok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(EditContactImpl.this,ContactDetail.class);
                //更新联系人姓名
                String newName = et_name.getText().toString();
                //更新联系人电话
                String newPhone = editphone.getText().toString();
                //更新联系人sip地址
                Log.i("EditContactNameAndPhone ", "new contact_name: " + newName);
                Log.i("EditContactNameAndPhone ", "new phone: " + newPhone);
                Log.i("EditContactNameAndPhone ", updateType + "");
                switch (updateType){
                    case NAME:
                        Log.i("updateContact", "update name");
                        editContactPresenter.editContact(id,name,newName, AddressBookModelImpl.UpdateType.NAME);
                    case PHONE:
                        Log.i("updateContact", "update phone");
                        editContactPresenter.editContact(id,phone, newPhone, AddressBookModelImpl.UpdateType.PHONE);
                }
                intent1.putExtra("contact", new Contact(newName, Arrays.asList(newPhone)));
                startActivity(intent1);
            }
        });

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
