package com.linphone.model;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import com.linphone.vo.Contact;

import java.util.*;

//通讯录
public class AddressBookModelImpl implements AddressBookModel {

/**
 * 接口使用样例
 * 删除联系人
 * deleteContactFromMachine("1111"); //“1111”为需要删除的手机号码
 *
 * 新增联系人
 * insertContactToMachine(new Contact("yahoo", Arrays.asList("1111", "2222")));
 *
 * 更新联系人姓名
 * updateContactToMachine(contactID, "yahoo", "AHa", UpdateType.NAME);
 * “yahoo”为更新前的联系人姓名 ""为更新后的联系人姓名 UpdateType.NAME表示此方法用于更新联系人电话
 *
 * 更新联系人电话
 * updateContactToMachine(contactID, "1111", "767336", UpdateType.PHONE);
 * “1111”为更新前的手机号码 "767336"为更新后的手机号码 UpdateType.PHONE表示此方法用于更新联系人电话
 *
 */
    private Context context;
    private Map<String, Contact> addressBookMap = new HashMap<>();
    private List<Contact> contactList = new ArrayList<>();
    public enum UpdateType {NAME, PHONE}    //更新的种类

    public AddressBookModelImpl(Context context){
        this.context = context;
    }

    //从本机通讯录获取联系人信息
    @Override
    public Map<String, Contact> getAddressBookInfo() {
        //查询本机数据库
        Uri uri = Uri.parse("content://com.android.contacts/data/phones");
        String[] column = new String[] {ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = context.getContentResolver().query(uri,
                column, null, null, null);
        if(cursor != null) {
            while (cursor.moveToNext()) {
                String contactID = cursor.getString(0);
                String contactNumber = cursor.getString(2).replace(" ", "");
                if(addressBookMap.get(contactID) == null){
                    String contactName = cursor.getString(1);
                    Contact contact = new Contact(contactID, contactName, Arrays.asList(contactNumber));
                    addressBookMap.put(contactID, contact);
                }
                else {
                    Contact contact = addressBookMap.get(contactID);
                    List<String> phones = contact.getPhones();
                    List<String> phonesCopy = new ArrayList<>();
                    phonesCopy.addAll(phones);
                    phonesCopy.add(contactNumber);
                    contact.setPhones(phonesCopy);
                    addressBookMap.put(contactID, contact);
                }
            }
        }
        return addressBookMap;

    }

    //获取本机联系人详情(姓名，电话)
    @Override
    public List<Contact> getContactList(){
        for(Map.Entry<String, Contact> entry: addressBookMap.entrySet()){
            Contact contact = entry.getValue();
            contactList.add(contact);
        }
        return contactList;
    }

    //插入本机联系人数据库
    @Override
    public void insertContactToMachine(Contact contact){
        String name = contact.getName();
        List<String> phones = contact.getPhones();
        ContentValues contentValues = new ContentValues();
        ContentResolver contentResolver = context.getContentResolver();
        //插入raw_contacts，获取id属性
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        long rawContactID = ContentUris.parseId(context.getContentResolver().insert(uri, contentValues));
        //循环插入联系人电话
        for(String phone: phones) {
            contentValues.clear();
            //姓名插入data表
            uri = Uri.parse("content://com.android.contacts/data");
            contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactID);
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
            contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            contentResolver.insert(uri, contentValues);
            contentValues.clear();
            //电话号码插入data表
            contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactID);
            contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);
            contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            contentResolver.insert(uri, contentValues);
        }
    }

    // 删除本机联系人
    @Override
    public void deleteContactFromMachine(String phone){
        Uri uri = Uri.parse("content://com.android.contacts/data/phones");
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.NUMBER+"=?", new String[]{phone}, null);
        if(cursor.moveToFirst()){
            String contactID = cursor.getString(0);
            Log.i("tag1", cursor.getString(0) + " " +cursor.getString(1));
            cursor = contentResolver.query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=?", new String[]{contactID}, null);
            int phone_number = cursor.getCount();
            if(phone_number == 1){
                uri = Uri.parse("content://com.android.contacts/contacts");
                contentResolver.delete(uri, ContactsContract.Contacts._ID +"=?", new String[]{contactID});
                uri = Uri.parse("content://com.android.contacts/data");
                contentResolver.delete(uri, ContactsContract.Data.CONTACT_ID + "=?", new String[]{contactID});
                uri = Uri.parse("content://com.android.contacts/raw_contacts");
                contentResolver.delete(uri, ContactsContract.Data.CONTACT_ID + "=?", new String[]{contactID});
            }
            else if(phone_number > 1){
                uri = Uri.parse("content://com.android.contacts/data");
                contentResolver.delete(uri, ContactsContract.Data.DATA1 + "=?", new String[]{phone});
            }
        }
    }

    @Override
    public void updateContactToMachine(String contactID, String old, String _new, UpdateType updateType){
        switch (updateType){
            case NAME:
                updateContactName(contactID, _new);
                break;
            case PHONE:
                updateContactPhone(old, _new);
                break;
        }
    }

    private void updateContactName(String contactID, String newName){
        Uri uri = Uri.parse("content://com.android.contacts/data");
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.DATA1, newName);
        contentResolver.update(uri, contentValues, ContactsContract.Data.CONTACT_ID + "=? and "
                + ContactsContract.Data.MIMETYPE + "=?", new String[]{contactID, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE});
    }

    private void updateContactPhone(String oldPhone, String newPhone){
        Uri uri = Uri.parse("content://com.android.contacts/data");
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.DATA1, newPhone);
        contentResolver.update(uri, contentValues, ContactsContract.Data.DATA1 + "=? and " +
                ContactsContract.Data.MIMETYPE + "=?", new String[]{oldPhone, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE});
    }

}


