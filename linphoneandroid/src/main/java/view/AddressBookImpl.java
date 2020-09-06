package view;

import presenter.AddressBookPresenter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.Nullable;
import com.hunter.fastandroid.R;
import vo.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//通讯录
public class AddressBookImpl extends Activity implements AddressBook {

    AddressBookPresenter addressBookPresenter = new AddressBookPresenter(AddressBookImpl.this, this);
    private ListView listView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_listview);
        addressBookPresenter.showAddressBook();
    }

    @Override
    public void showAddressBookList(Map<String, Contact> addressBookMap) {

        listView = findViewById(R.id.nameListView);
        List<String> nameList = new ArrayList<>();
        for(Map.Entry<String, Contact> entry: addressBookMap.entrySet()){
            String name = entry.getValue().getName();
            nameList.add(name);
        }
        String[] nameData = nameList.toArray(new String[nameList.size()]);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nameData);
        listView.setAdapter(arrayAdapter);

    }

    @Override
    public void makeContactDetail(final List<Contact> contactList){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(AddressBookImpl.this, ContactDetail.class);
                intent.putExtra("contact", contactList.get(i));
                startActivity(intent);
            }
        });
    }


}
