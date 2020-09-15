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
import com.linphone.addressbook.AddressBookModelImpl;
import com.linphone.addressbook.DeleteContactPresenter;
import com.linphone.call.CallOutgoingPresenter;
import com.linphone.chat.single.view.ChatActivity;
import com.linphone.chat.view.ChatRecordActivity;
import com.linphone.vo.Contact;

//联系人详情
public class ContactDetail extends Activity {

    private DeleteContactPresenter deleteContactPresenter = new DeleteContactPresenter(ContactDetail.this);
    private CallOutgoingPresenter callOutgoingPresenter;
    private ImageView messageButton;
    private ImageView backButton;
    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_contact_detail);
        messageButton = findViewById(R.id.message_button);
        backButton = findViewById(R.id.back);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactDetail.this, ChatRecordActivity.class));
            }
        });
        //从AddressBookActivity获取联系人信息
        Intent intent = getIntent();
        Contact contact = intent.getParcelableExtra("contact");
        contact = new AddressBookModelImpl(ContactDetail.this).findNameFromPhone(contact.getPhones().get(0));

        final String finalID = contact.getContactID();
        final String finalName = contact.getName();
        final String finalPhone = contact.getPhones().get(0);
        final TextView name = findViewById(R.id.contactDetailName);
        final TextView phonenumber = findViewById(R.id.phonenumber);
        //填充联系人信息
        name.setText(contact.getName());
        if(contact.getPhones() != null){
            phonenumber.setText(contact.getPhones().get(0));
        }


        ImageView bt_edit = findViewById(R.id.editContact);
        bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactDetail.this, EditContactImpl.class);
                intent.putExtra("contact_name", finalName);
                intent.putExtra("contact_phone",finalPhone);
                intent.putExtra("contact_id", finalID);
                Log.i("tag2", "contact_name" + finalName);
                Log.i("tag2", "contact_phone" + finalPhone);
                startActivity(intent);
            }
        });

        ImageView bt_delete = findViewById(R.id.deleteContact);
        final Contact finalContact = contact;
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
                        deleteContactPresenter.deleteContact(finalContact);
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

        callOutgoingPresenter = new CallOutgoingPresenter();
        ImageButton callButton = findViewById(R.id.phone);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callOutgoingPresenter.makeCall(finalPhone);
            }
        });
        messageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent1 = new Intent(ContactDetail.this, ChatActivity.class);
                intent1.putExtra("contact", finalContact);
                startActivity(intent1);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(ContactDetail.this, AddressBookImpl.class));
            }
        });
    }
}
