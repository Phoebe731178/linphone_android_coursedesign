package View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.hunter.linphoneandroid.R;
import vo.Contact;

//联系人详情
public class ContactDetail extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_contact_detail);
        //从AddressBookActivity获取联系人信息
        Intent intent = getIntent();
        Contact contact = intent.getParcelableExtra("contact");
        TextView name = findViewById(R.id.contactName);
        ListView phones = findViewById(R.id.contactPhonesList);
        ListView SIP = findViewById(R.id.contactSIPList);
        //填充联系人信息
        name.setText(contact.getName());
        if(contact.getPhones() != null){
            String[] phoneList = contact.getPhones().toArray(new String[contact.getPhones().size()]);
            ArrayAdapter<String> phonesAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, phoneList);
            phones.setAdapter(phonesAdapter);
        }
        if(contact.getSIP() != null) {
            String[] SIPList = contact.getPhones().toArray(new String[contact.getSIP().size()]);
            ArrayAdapter<String> phonesAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, SIPList);
            SIP.setAdapter(phonesAdapter);
        }
    }
}
