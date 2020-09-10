package com.linphone.addressbook;

import android.content.Context;
import android.util.Log;
import com.linphone.addressbook.view.EditContact;


public class EditContactPresenter {

    AddressBookModel addressBookModel;
    EditContact editContact;


    public EditContactPresenter(Context context, EditContact editContact ){
        this.editContact = editContact;
        this.addressBookModel = new AddressBookModelImpl(context);
    }


    public void editContact(String id,String oldNameorName,String newNameorPhone, AddressBookModelImpl.UpdateType updateType){
        Log.i("tag3","oldPhone" + oldNameorName);
        Log.i("tag3","newPhone" + newNameorPhone);
        Log.i("tag3",  updateType+"");
        addressBookModel.updateContactToMachine(id,oldNameorName,newNameorPhone, updateType);
    }
    /*public void editContactPhone(Contact contact,String newNameorPhone, AddressBookModelImpl.UpdateType updateType){

        addressBookModel.updateContactToMachine(contact.getContactID(),contact.getPhones().get(0),newNameorPhone,updateType);
    }*/

    public void editContactNameAndPhone(){
        editContact.EditContactNameAndPhone();
    }


}
