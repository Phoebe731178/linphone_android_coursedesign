package com.linphone.addressbook;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import com.github.promeg.pinyinhelper.Pinyin;
import com.linphone.vo.Contact;

import java.util.*;


//通讯录
public class AddressBookModelImpl implements AddressBookModel {

    /**
     * 接口使用样例
     * 删除联系人号码，号码为一时删除联系人
     * deleteContactFromMachine(phone);
     *
     * 删除联系人
     * deleteContactByContactID(contactID);
     *
     * 新增联系人
     * insertContactToMachine(contact);
     *
     * 更新联系人姓名
     * updateContactToMachine(contactID, old_name, new_name, UpdateType.NAME);
     * old_name为更新前的联系人姓名 new_name为更新后的联系人姓名 UpdateType.NAME表示此方法用于更新联系人电话
     *
     * 更新联系人电话
     * updateContactToMachine(contactID, old_phone, new_phone, UpdateType.PHONE);
     * old_phone为更新前的手机号码 new_phone为更新后的手机号码 UpdateType.PHONE表示此方法用于更新联系人电话
     *
     */
    private Context context;
    private Map<String, Contact> addressBookMap = new LinkedHashMap<>();
    private List<Contact> contactList = new ArrayList<>();
    public enum UpdateType {NAME, PHONE, SIP}    //更新的种类

    public AddressBookModelImpl(Context context){
        this.context = context;
    }

    //从本机通讯录获取联系人信息
    @Override
    public Map<String, Contact> getAddressBookInfo() {
        //查询本机数据库
        try {
            addressBookMap.clear();
            AddressBookPresenter.addressBookMap.clear();
        }
        catch (Exception ignore){
        }
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
                    List<String> phonesCopy = new ArrayList<>(phones);
                    phonesCopy.add(contactNumber);
                    contact.setPhones(phonesCopy);
                    addressBookMap.put(contactID, contact);
                }
            }
        }
        setContactAddress();
        addressBookMap = sortNameList();
        AddressBookPresenter.addressBookMap = addressBookMap;
        setContactList();
        return addressBookMap;
    }

    public void setContactList(){
        for(Map.Entry<String, Contact> entry: AddressBookPresenter.addressBookMap.entrySet()){
            contactList.add(entry.getValue());
        }
    }

    public Map<String, Contact> sortNameList(){
        Map<String, Contact> result = new LinkedHashMap<>();
        List<Map.Entry<String, Contact>> mapList = new ArrayList<>(addressBookMap.entrySet());
        Collections.sort(mapList, new Comparator<Map.Entry<String, Contact>>() {
            @Override
            public int compare(Map.Entry<String, Contact> t1, Map.Entry<String, Contact> t2) {
                String char1 = Pinyin.toPinyin(t1.getValue().getName(), "");
                String char2 = Pinyin.toPinyin(t2.getValue().getName(), "");
                return char1.compareTo(char2);
            }
        });
        for (Map.Entry<String, Contact> entry: mapList){
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public Contact findNameFromPhone(String phone){
        for(Contact contact: contactList){
            Log.i("addressBook", contact.getName());
        }
        Log.i("findNameFromPhone", phone);
//        if(phone.contains("+86")){
//            phone = phone.substring(3);
//        }
        Log.i("findNameFromPhone", phone);
//        Uri uri = Uri.parse("content://com.android.contacts/data/phones");
//        String[] column = new String[] {ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
//        Cursor cursor = context.getContentResolver().query(uri,
//                column, ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[]{phone}, null);
//        try{
//            cursor.moveToFirst();
//            String contactID = cursor.getString(0);
//            String name = cursor.getString(1);
//            Log.i("getContact", name);
//            Contact contact = new Contact(contactID, name, Arrays.asList(phone));
//            return contact;
//        }
//        catch (Exception e){
//            Log.i("callActivity", phone + " is not found");
//        }
        Contact foundContact = new Contact();
        for(Map.Entry<String, Contact> entry: AddressBookPresenter.addressBookMap.entrySet()){
            Log.i("findNameFromPhone", entry.getValue().getName() + " " + entry.getValue().getPhones().get(0));
            if(entry.getValue().getPhones().get(0).equals(phone)){
                foundContact.setContactID(entry.getValue().getContactID());
                foundContact.setName(entry.getValue().getName());
                foundContact.setPhones(entry.getValue().getPhones());
                return foundContact;
            }
        }
        Log.i("findNameFromPhone", phone + " cannot be found");
        return null;
    }

    // 查询通讯录联系人SIP地址
    public void setContactAddress(){
        Uri uri = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = context.getContentResolver().query(uri, new String[]{ContactsContract.Data.CONTACT_ID, ContactsContract.Data.DATA1},
                ContactsContract.Data.MIMETYPE + "=?",
                new String[]{ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE}, null);

        try{
            cursor.moveToFirst();
            Log.i("tag1", "cursor is not null");
            Log.i("tag1", "1 " + cursor.getString(0) + " " +cursor.getString(1));
            do {
                String contactID = cursor.getString(0);
                String contactAddress = cursor.getString(1);
                if(addressBookMap.get(contactID) != null){
                    Log.i("tag1", "2 " + cursor.getString(0) + " " +cursor.getString(1));
                    Contact contact = addressBookMap.get(contactID);
                    Log.i("tag1", "3 " + cursor.getString(0) + " " +cursor.getString(1));
                    List<String> SIP = new ArrayList<>();
                    List<String> SIPCopy = new ArrayList<>(SIP);
                    Log.i("tag1", "4 " + cursor.getString(0) + " " +cursor.getString(1));
                    SIPCopy.add(contactAddress);
                }
            } while(cursor.moveToNext());
        }
        catch (Exception e){
            Log.i("tag1", "nobody has SIP address");
        }
    }

    //插入本机联系人数据库
    @Override
    public void insertContactToMachine(Contact contact){
        String name = contact.getName();
        List<String> phones = contact.getPhones();
        ContentValues contentValues = new ContentValues();
        ContentResolver contentResolver = context.getContentResolver();
        long rawContactID;
        Uri uri;
        //插入raw_contacts，获取id属性
        if(contact.getContactID() == null) {
            uri = Uri.parse("content://com.android.contacts/raw_contacts");
            rawContactID = ContentUris.parseId(context.getContentResolver().insert(uri, contentValues));
        }
        else {
            rawContactID = Long.parseLong(contact.getContactID());
        }
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        //循环插入联系人电话
        for(String phone: phones) {
            contentValues.clear();
            //姓名插入data表
            uri = Uri.parse("content://com.android.contacts/data");
            ContentProviderOperation contentProviderOperation1 = ContentProviderOperation.
                    newInsert(uri).
                    withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactID).
                    withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name).
                    withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE).
                    build();
            operations.add(contentProviderOperation1);
            //电话号码插入data表
            ContentProviderOperation contentProviderOperation2 = ContentProviderOperation.
                    newInsert(uri).
                    withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactID).
                    withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).
                    withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone).
                    withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).
                    build();
            operations.add(contentProviderOperation2);
        }
        try {
            contentResolver.applyBatch("com.android.contacts", operations);
        } catch (OperationApplicationException | RemoteException e) {
            e.printStackTrace();
        }
    }

    // 删除某一联系人的号码，号码为一时删除联系人
    @Override
    public void deleteContactFromMachine(String phone){
        Uri uri = Uri.parse("content://com.android.contacts/data/phones");
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.NUMBER+"=?", new String[]{phone}, null);
        if(cursor.moveToFirst()){
            String contactID = cursor.getString(0);
            cursor = contentResolver.query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=?", new String[]{contactID}, null);
            int phone_number = cursor.getCount();
            if(phone_number == 1){
                deleteContactByContactID(contactID);
            }
            else if(phone_number > 1){
                ArrayList<ContentProviderOperation> operations = new ArrayList<>();
                uri = Uri.parse("content://com.android.contacts/data");
                ContentProviderOperation contentProviderOperation = ContentProviderOperation.newDelete(uri).
                        withSelection(ContactsContract.Data.DATA1 + "=?", new String[]{phone}).
                        build();
                operations.add(contentProviderOperation);
                try {
                    contentResolver.applyBatch("com.android.contacts", operations);
                } catch (OperationApplicationException | RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 删除某一联系人
    @Override
    public void deleteContactByContactID(String contactID){
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        Uri uri = Uri.parse("content://com.android.contacts/data");
        ContentProviderOperation contentProviderOperation1 = ContentProviderOperation.newDelete(uri).
                withSelection(ContactsContract.Data.CONTACT_ID + "=?", new String[]{contactID}).
                build();
        operations.add(contentProviderOperation1);
        uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentProviderOperation contentProviderOperation2 = ContentProviderOperation.newDelete(uri).
                withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", new String[]{contactID}).
                build();
        operations.add(contentProviderOperation2);
        uri = Uri.parse("content://com.android.contacts/contacts");
        ContentProviderOperation contentProviderOperation3 = ContentProviderOperation.newDelete(uri).
                withSelection(ContactsContract.Contacts._ID + "=?", new String[]{contactID}).
                build();
        operations.add(contentProviderOperation3);
        try {
            context.getContentResolver().applyBatch("com.android.contacts", operations);
        } catch (OperationApplicationException | RemoteException e) {
            e.printStackTrace();
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
            case SIP:
                updateContactSIP(old, _new);
        }
    }

    private void updateContactName(String contactID, String newName){
        Uri uri = Uri.parse("content://com.android.contacts/data");
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.DATA1, newName);
        context.getContentResolver().update(uri, contentValues, ContactsContract.Data.CONTACT_ID + "=? and "
                + ContactsContract.Data.MIMETYPE + "=?", new String[]{contactID, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE});
    }

    private void updateContactPhone(String oldPhone, String newPhone){
        Uri uri = Uri.parse("content://com.android.contacts/data");
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.DATA1, newPhone);
        context.getContentResolver().update(uri, contentValues, ContactsContract.Data.DATA1 + "=? and " +
                ContactsContract.Data.MIMETYPE + "=?", new String[]{oldPhone, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE});
    }

    private void updateContactSIP(String oldSIP, String newSip){
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://com.android.contacts/data");
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.DATA1, newSip);
        contentResolver.update(uri, contentValues, ContactsContract.Data.DATA1 + "=? and " +
                ContactsContract.Data.MIMETYPE + "=?", new String[]{oldSIP, ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE});
    }

}