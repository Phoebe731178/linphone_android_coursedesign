package Model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import org.linphone.core.Content;
import vo.Contact;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

//通讯录
public class AddressBookModelImpl implements AddressBookModel {

    private static final int PHONE_NAME = 0;       //联系人姓名
    private static final int PHONE_NUMBER = 1;    //联系人电话
    private Context context;
    private Map<String, List<String>> addressBookMap = new HashMap<>();
    private List<Contact> contactList = new ArrayList<>();

    public AddressBookModelImpl(Context context){
        this.context = context;
    }

    //从本机通讯录获取联系人信息
    @Override
    public Map<String, List<String>> getAddressBookInfo() {
        //查询本机数据库
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

    //获取本机联系人详情(姓名，电话，SIP)
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

    //插入本机联系人数据库
    @Override
    public void insertContactToMachine(Context context, Contact contact){
        String name = contact.getName();
        List<String> phones = contact.getPhones();
        ContentValues contentValues = new ContentValues();
        ContentResolver contentResolver = context.getContentResolver();
        long id;
        //循环插入联系人电话
        for(String phone: phones) {
            contentValues.clear();
            //插入raw_contacts，获取id属性
            Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
            id = ContentUris.parseId(context.getContentResolver().insert(uri, contentValues));
            //姓名插入data表
            uri = Uri.parse("content://com.android.contacts/data");
            contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, id);
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
            contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            contentResolver.insert(uri, contentValues);
            contentValues.clear();
            //电话号码插入data表
            contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, id);
            contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);
            contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            context.getContentResolver().insert(uri, contentValues);
        }
    }

}


