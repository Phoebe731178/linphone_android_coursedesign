package com.linphone.addressbook;

import com.linphone.addressbook.view.AddressBook;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.content.Context;
import com.linphone.vo.Contact;

import java.util.Map;

//通讯录
public class AddressBookPresenter {

    AddressBookModel addressBookModel;
    AddressBook addressBook;
    Context context;
    public static Map<String, Contact> addressBookMap;

    public AddressBookPresenter(Context context, AddressBook addressBook){
        this.addressBook = addressBook;
        this.addressBookModel = new AddressBookModelImpl(context);
        this.context = context;
    }

    //调用通讯录view层显示通讯录列表，并设置点击查看详情事件
    public void showAddressBook(){
        addressBook.showAddressBookList(addressBookModel.getAddressBookInfo());
    }

    //监听器
    public void observers(){
        ContentObserver contentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                showAddressBook();
            }
        };
        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        context.getContentResolver().registerContentObserver(uri, true, contentObserver);
    }

}
