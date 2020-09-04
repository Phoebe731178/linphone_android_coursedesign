package Presenter;

import Model.AddressBookModel;
import Model.AddressBookModelImpl;
import View.AddressBook;
import View.AddressBookImpl;
import android.content.Context;

public class AddressBookPresenter {

    AddressBookModel addressBookModel;
    AddressBook addressBook;

    public AddressBookPresenter(Context context, AddressBook addressBook){
        this.addressBook = addressBook;
        this.addressBookModel = new AddressBookModelImpl(context);
    }

//    public AddressBookPresenter(AddressBook addressBook){
//        this.addressBook = addressBook;
//        this.addressBookModel = new AddressBookModelImpl(context);
//    }


    public void showAddressBook(){
        addressBook.showAddressBookList(addressBookModel.getAddressBookInfo());
        addressBook.makeContactDetail(addressBookModel.getContactList());
    }



}
