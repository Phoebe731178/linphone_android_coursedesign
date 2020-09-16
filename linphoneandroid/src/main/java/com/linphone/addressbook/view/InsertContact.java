package com.linphone.addressbook.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import com.linphone.R;
import com.linphone.addressbook.AddressBookModelImpl;
import com.linphone.vo.Contact;

import java.util.Arrays;

public class InsertContact extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        final EditText contactName = findViewById(R.id.editName);
        final EditText contactPhone = findViewById(R.id.editPhone);

        contactName.setText("");
        contactPhone.setText("");

        ImageView insertOK = findViewById(R.id.ok);
        insertOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = contactName.getText().toString();
                String phone = contactPhone.getText().toString();
                Contact contact = new Contact(name, Arrays.asList(phone));
                new AddressBookModelImpl(InsertContact.this).insertContactToMachine(contact);
                Intent intent = new Intent(InsertContact.this, ContactDetail.class);
                intent.putExtra("contact", contact);
                startActivity(intent);
            }
        });

        ImageView back = findViewById(R.id.cancel);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InsertContact.this, AddressBookImpl.class);
                startActivity(intent);
            }
        });

    }
}