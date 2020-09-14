package com.linphone.vo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

//联系人实体类
public class Contact implements Parcelable {
    private String contactID = null;
    private String name = null;
    private List<String> phones = null;

    public Contact(){ }

    public Contact(String contactID, String name, List<String> phones){
        this.contactID = contactID;
        this.name = name;
        this.phones = phones;
    }


    public Contact(String name, List<String> phones){
        this.name = name;
        this.phones = phones;
    }

    protected Contact(Parcel in) {
        contactID = in.readString();
        name = in.readString();
        phones = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contactID);
        dest.writeString(name);
        dest.writeStringList(phones);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public String getContactID() {
        return contactID;
    }

    public String getName(){
        return name;
    }

    public List<String> getPhones() {
        return phones;
    }


    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public void destroy(){
        try{
            this.contactID = "";
            this.phones.clear();
        }
        catch (Exception ignore){}
    }
}
