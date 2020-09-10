package com.linphone.addressbook;

import com.linphone.vo.Contact;

import java.util.List;
import java.util.Map;

//通讯录
public interface AddressBookModel {

    Map<String, Contact> getAddressBookInfo(); //从本机通讯录获取联系人信息
    List<Contact> getContactList(); //获取本机联系人详情(姓名，电话，SIP)
    void insertContactToMachine(Contact contact);
    void deleteContactFromMachine(String phone);
    void deleteContactByContactID(String contactID);
    void updateContactToMachine(String contactID, String old, String _new, AddressBookModelImpl.UpdateType updateType);
}
