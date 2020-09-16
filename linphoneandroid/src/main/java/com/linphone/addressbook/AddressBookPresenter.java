package com.linphone.addressbook;

import android.os.Bundle;
import android.os.Message;
import com.linphone.addressbook.view.AddressBook;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.content.Context;
import com.linphone.chat.view.ChatRecordActivity;
import com.linphone.vo.Contact;

import java.util.Map;

//通讯录
public class AddressBookPresenter {

    AddressBookModel addressBookModel;
    AddressBook addressBook;
    Context context;
    public static Map<String, Contact> addressBookMap;
    public static final int ON_CHANGE = 0;

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
            public void onChange(boolean selfChange)
            {
                super.onChange(selfChange);
                showAddressBook();
                if (ChatRecordActivity.getHandler() != null)
                {
                    ChatRecordActivity.getHandler().sendEmptyMessage(ON_CHANGE);
                }
            }
        };
        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        context.getContentResolver().registerContentObserver(uri, true, contentObserver);
    }

}