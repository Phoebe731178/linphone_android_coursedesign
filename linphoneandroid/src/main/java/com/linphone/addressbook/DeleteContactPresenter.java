package com.linphone.addressbook;

import android.content.Context;
import com.linphone.model.addressbook.AddressBookModel;
import com.linphone.model.addressbook.AddressBookModelImpl;
import com.linphone.view.addressbook.DeleteContact;
import com.linphone.vo.Contact;

public class DeleteContactPresenter {
    AddressBookModel addressBookModel;
    DeleteContact deleteContact;

    public DeleteContactPresenter(Context context, DeleteContact deleteContact ){
        this.deleteContact = deleteContact;
        this.addressBookModel = new AddressBookModelImpl(context);
    }

    public void deleteContact(Contact contact){
        addressBookModel.deleteContactByContactID(contact.getContactID());
    }
    public void deleteContact2(){
        deleteContact.DeleteContact();
    }
}
