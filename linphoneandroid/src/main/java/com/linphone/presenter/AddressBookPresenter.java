package com.linphone.presenter;

import com.linphone.model.AddressBookModel;
import com.linphone.model.AddressBookModelImpl;
import com.linphone.view.AddressBook;
import android.content.Context;

//通讯录
public class AddressBookPresenter {

    AddressBookModel addressBookModel;
    AddressBook addressBook;

    public AddressBookPresenter(Context context, AddressBook addressBook){
        this.addressBook = addressBook;
        this.addressBookModel = new AddressBookModelImpl(context);
    }

    //调用通讯录view层显示通讯录列表，并设置点击查看详情事件
    public void showAddressBook(){
        addressBook.showAddressBookList(addressBookModel.getAddressBookInfo());
        addressBook.makeContactDetail(addressBookModel.getContactList());
    }



}
