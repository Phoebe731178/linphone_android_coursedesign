package com.linphone.view.addressbook;

import android.util.Log;
import com.linphone.R;
import com.linphone.model.addressbook.AddressBookModel;
import com.linphone.model.addressbook.AddressBookModelImpl;
import com.linphone.presenter.addressbook.AddressBookPresenter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.Nullable;
import com.linphone.vo.Contact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//通讯录
public class AddressBookImpl extends Activity implements AddressBook {

    AddressBookPresenter addressBookPresenter = new AddressBookPresenter(AddressBookImpl.this, this);
    private ListView listView;
    private ArrayAdapter<String> addressBookAdapter;
    private List<Contact> contactList;
    private List<String> nameList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_listview);
        addressBookPresenter.showAddressBook();
        addressBookPresenter.observers();
    }

    @Override
    public void showAddressBookList(Map<String, Contact> addressBookMap) {

        try{
            addressBookAdapter.clear();
        }
        catch (Exception ignored){
        }
        contactList = new ArrayList<>();
        nameList = new ArrayList<>();
        listView = findViewById(R.id.nameListView);
        for(Map.Entry<String, Contact> entry: addressBookMap.entrySet()){
            String name = entry.getValue().getName();
            nameList.add(name);
            contactList.add(entry.getValue());
        }
        String[] nameData = nameList.toArray(new String[nameList.size()]);
        addressBookAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nameData);
        listView.setAdapter(addressBookAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(AddressBookImpl.this, ContactDetail.class);
                intent.putExtra("contact", contactList.get(i));
                startActivity(intent);
            }
        });
        Log.i("tag1", "count: " + addressBookMap.size());
    }



}
