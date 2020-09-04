package View;

import vo.Contact;

import java.util.List;
import java.util.Map;

public interface AddressBook {
    void showAddressBookList(Map<String, List<String>> addressBookMap);
    void makeContactDetail(List<Contact> contactList);
}
