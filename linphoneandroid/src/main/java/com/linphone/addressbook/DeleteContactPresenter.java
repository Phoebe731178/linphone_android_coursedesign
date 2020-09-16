package com.linphone.addressbook;

import android.content.Context;
import com.linphone.vo.Contact;

public class DeleteContactPresenter {
    AddressBookModel addressBookModel;

    public DeleteContactPresenter(Context context){
        this.addressBookModel = new AddressBookModelImpl(context);
    }

    public void deleteContact(Contact contact){
        addressBookModel.deleteContactByContactID(contact.getContactID());
    }
}