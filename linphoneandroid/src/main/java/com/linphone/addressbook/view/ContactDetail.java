package com.linphone.addressbook.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.linphone.R;
import com.linphone.addressbook.DeleteContactPresenter;
import com.linphone.vo.Contact;


import java.util.List;

//联系人详情
public class ContactDetail extends Activity {

    private DeleteContactPresenter deleteContactPresenter = new DeleteContactPresenter(ContactDetail.this);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_contact_detail);
        //从AddressBookActivity获取联系人信息
        Intent intent = getIntent();
        final Contact contact = intent.getParcelableExtra("contact");
        final TextView name = findViewById(R.id.contactName);
        TextView phonenumber = findViewById(R.id.phonenumber);
        TextView sip = findViewById(R.id.SIP);
        // ListView phones = findViewById(R.id.contactPhonesList);
        //ListView SIP = findViewById(R.id.contactSIPList);
        //填充联系人信息
        name.setText(contact.getName());
        if(contact.getPhones() != null){
           /* String[] phoneList = contact.getPhones().toArray(new String[contact.getPhones().size()]);
            ArrayAdapter<String> phonesAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, phoneList);
            phones.setAdapter(phonesAdapter);*/
            phonenumber.setText(contact.getPhones().get(0));
        }
        if(contact.getSIP() != null) {
           /* String[] SIPList = contact.getPhones().toArray(new String[contact.getSIP().size()]);
            ArrayAdapter<String> phonesAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, SIPList);
            SIP.setAdapter(phonesAdapter);*/
            sip.setText(contact.getSIP().get(0));
        }


        ImageView bt_edit = findViewById(R.id.editContact);
        bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactDetail.this, EditContactImpl.class);
                intent.putExtra("contact_name",contact.getName());
                intent.putExtra("contact_phone",contact.getPhones().get(0));
                intent.putExtra("contact_id",contact.getContactID());
//                if(contact.getSIP() != null) {
//                    intent.putExtra("contact_sip", contact.getSIP().get(0));
//                }
                Log.i("tag2", "contact_name" + contact.getName());
                Log.i("tag2", "contact_phone" +contact.getPhones().get(0));
                //intent.putExtra("contacts",contact);
                startActivity(intent);
            }
        });

        ImageView bt_delete = findViewById(R.id.deleteContact);
        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ContactDetail.this);

                builder.setTitle("温馨提示");
                builder.setMessage("确定要删除吗？");
                builder .setIcon(R.mipmap.ic_launcher);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ContactDetail.this, "删除成功", Toast.LENGTH_SHORT).show();
                        deleteContactPresenter.deleteContact(contact);
                        Intent intent1 = new Intent(ContactDetail.this,AddressBookImpl.class);
                        startActivity(intent1);
                    }
                })

                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(ContactDetail.this, "取消删除", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create();
                builder.show();
            }
        });

    }
}
