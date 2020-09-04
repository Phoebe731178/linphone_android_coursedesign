package Presenter;

import Model.AddressBookModel;
import Model.AddressBookModelImpl;
import View.AddressBook;
import View.AddressBookImpl;
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
