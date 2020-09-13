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
import com.linphone.call.CallOutgoingPresenter;
import com.linphone.call.LinphoneCallImpl;
import com.linphone.call.view.CallActivity;
import com.linphone.util.LinphoneManager;
import com.linphone.vo.Contact;
import org.linphone.core.*;

//联系人详情
public class ContactDetail extends Activity {

    private DeleteContactPresenter deleteContactPresenter = new DeleteContactPresenter(ContactDetail.this);
    private CallOutgoingPresenter callOutgoingPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_contact_detail);
        //从AddressBookActivity获取联系人信息
        Intent intent = getIntent();
        final Contact contact = intent.getParcelableExtra("contact");
        final TextView name = findViewById(R.id.contactName);
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
                intent.putExtra("contact_name",contact.getName());
                intent.putExtra("contact_phone",contact.getPhones().get(0));
                intent.putExtra("contact_id",contact.getContactID());
                Log.i("tag2", "contact_name" + contact.getName());
                Log.i("tag2", "contact_phone" +contact.getPhones().get(0));
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

        callOutgoingPresenter = new CallOutgoingPresenter();
        ImageButton callButton = findViewById(R.id.phone);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callOutgoingPresenter.makeCall(contact.getPhones().get(0));
            }
        });

    }
}
