package Model;

import vo.Contact;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface AddressBookModel {

    Map<String, List<String>> getAddressBookInfo();
    List<Contact> getContactList();
    void setAddressBookInfo(Map<String, List<String>> addressBookInfo);

}
