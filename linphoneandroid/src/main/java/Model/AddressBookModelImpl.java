package Model;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import vo.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddressBookModelImpl implements AddressBookModel {

    private static final int PHONE_NAME = 0;       //联系人姓名
    private static final int PHONE_NUMBER = 1;    //联系人电话
    private Context context;
    private Map<String, List<String>> addressBookMap = new HashMap<>();
    private List<Contact> contactList = new ArrayList<>();

    public AddressBookModelImpl(Context context){
        this.context = context;
    }

    @Override
    public Map<String, List<String>> getAddressBookInfo() {
        String[] column = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                column, null, null, null);
        if(cursor != null) {
            while (cursor.moveToNext()) {
                String phoneName = cursor.getString(PHONE_NAME);
                String phoneNumber = cursor.getString(PHONE_NUMBER).replace(" ", "");
                if(addressBookMap.get(phoneName) == null) {
                    List<String> numberList = new ArrayList<>();
                    numberList.add(phoneNumber);
                    addressBookMap.put(phoneName, numberList);
                }
                else {
                    List<String> numberList = addressBookMap.get(phoneName);
                    numberList.add(phoneNumber);
                    addressBookMap.put(phoneName, numberList);
                }
            }
        }
        return addressBookMap;
    }

    @Override
    public List<Contact> getContactList(){
        for(Map.Entry<String, List<String>> entry: addressBookMap.entrySet()){
            Contact contact = new Contact();
            contact.setName(entry.getKey());
            contact.setPhones(entry.getValue());
            contactList.add(contact);
        }
        return contactList;
    }

    @Override
    public void setAddressBookInfo(Map<String, List<String>> addressBookInfo) {

    }

}


