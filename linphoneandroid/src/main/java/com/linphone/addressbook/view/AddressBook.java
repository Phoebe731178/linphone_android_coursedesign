package com.linphone.addressbook.view;

import com.linphone.vo.Contact;

import java.util.Map;

//通讯录
public interface AddressBook {
    void showAddressBookList(Map<String, Contact> addressBookMap); //显示通讯录列表
}
