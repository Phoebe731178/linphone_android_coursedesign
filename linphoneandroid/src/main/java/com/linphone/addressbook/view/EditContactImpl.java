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
import com.linphone.addressbook.AddressBookModelImpl;
import com.linphone.addressbook.EditContactPresenter;


import java.util.List;

public class EditContactImpl extends AppCompatActivity implements EditContact, TextWatcher {

    EditContactPresenter editContactPresenter = new EditContactPresenter(EditContactImpl.this, this);

    private LinearLayout mRootLayout;
    private List<LinearLayout> mLayoutList;
    private List<EditText> mPhoneList;

    private ImageView mAdd;
    private ImageView mDelete;
    private EditText et_name;
    private EditText editphone;
    private EditText editSIP;
    private AddressBookModelImpl.UpdateType updateType;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);


        initViews();
       /* mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhone();
            }
        });
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePhone();
            }
        });*/
        Log.i("tag2", "editContactPresenter editContactNameAndPhone");
        editContactPresenter.editContactNameAndPhone();

//        Intent intent = getIntent();
//        final String name = intent.getStringExtra("contact_name");
//        et_name.setText(name);
//
//        final String phone = intent.getStringExtra("contact_phone");
//        editphone.setText(phone);
//
//        final String sip = intent.getStringExtra("contact_sip");
//        editSIP.setText(sip);
       /* Intent intent = getIntent();
        final Contact contact = intent.getParcelableExtra("contacts");

        et_name.setText(contact.getName());
        editphone.setText(contact.getPhones().get(0));*/



    }

    private void initViews(){
        /*mRootLayout = (LinearLayout)findViewById(R.id.controls_phone_numbers);
        mAdd = (ImageView)findViewById(R.id.add_phone);
        mDelete = (ImageView)findViewById(R.id.delete_phone);
        mPhoneList = new ArrayList<>();
        mLayoutList = new ArrayList<>();*/

        et_name = findViewById(R.id.contactName);
        editphone = findViewById(R.id.editphone);

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
        editSIP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateType = AddressBookModelImpl.UpdateType.SIP;
            }
        });


    }
    /*private void addPhone() {
        LayoutInflater mInflater = LayoutInflater.from(this);
        LinearLayout editPhoneList = (LinearLayout) mInflater.inflate(R.layout.activity_edit_contact_part, null);
        mRootLayout.addView(editPhoneList);
        EditText editPhone = (EditText) editPhoneList.findViewById(R.id.editphone);
        mPhoneList.add(editPhone);
        mLayoutList.add(editPhoneList);
    }
    private void deletePhone(){
        int size = mLayoutList.size();
        if(size == 0){
            Toast.makeText(this, "联系人方式至少需要一个！", Toast.LENGTH_SHORT).show();
        }else {
            mLayoutList.get(size-1).setVisibility(View.GONE);
            mLayoutList.remove(size-1);
        }
    }*/


    @Override
    public void EditContactNameAndPhone() {

        Intent intent = getIntent();
        // final Contact contact = intent.getParcelableExtra("contacts");
        final String name = intent.getStringExtra("contact_name");
        et_name.setText(name);
        final String phone = intent.getStringExtra("contact_phone");
        editphone.setText(phone);
//        final String sip = intent.getStringExtra("contact_sip");
//        editSIP.setText(sip);
        final String id = intent.getStringExtra("contact_id");
        Log.i("tag2", "contact_name: " + name);
        Log.i("tag2", "phone: " + phone);
//        Log.i("tag2", "contact_sip: " + sip);




        final ImageView editok = findViewById(R.id.ok);
        editok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(EditContactImpl.this,AddressBookImpl.class);

                //更新联系人姓名
                String newName = et_name.getText().toString();
                //更新联系人电话
                String newPhone = editphone.getText().toString();
                //更新联系人sip地址
//                if(editSIP.getText().toString().equals(null)) {
//                    String newSIP = editSIP.getText().toString();
//                }
                Log.i("EditContactNameAndPhone ", "new contact_name: " + newName);
                Log.i("EditContactNameAndPhone ", "new phone: " + newPhone);
//                Log.i("tag2", "new contact_sip: " + newSIP);
                Log.i("EditContactNameAndPhone ", updateType + "");
                switch (updateType){
                    case NAME:
                        editContactPresenter.editContact(id,name,newName, AddressBookModelImpl.UpdateType.NAME);
                    case PHONE:
                        editContactPresenter.editContact(id,phone, newPhone,AddressBookModelImpl.UpdateType.PHONE);
//                    case SIP:
//                        editContactPresenter.editContact(id,sip,newSIP,AddressBookModelImpl.UpdateType.SIP);
                }

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

