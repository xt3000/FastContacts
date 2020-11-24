package net.finch.fastcontacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Contact {

    private String _name ="";
    private ArrayList<Phone> _phones = new ArrayList<>();        // String[] {PHONE_LABEL, PHONE_NUMBER}
    private String _photoURL;
    private int _id;

    public Contact(){}

    public Contact(int id, String name, String url, ArrayList<Phone> phones) {
        _id = id;
        _name = name;
        _photoURL = url;
        _phones = phones;
    }

    public void setName(String name) {
        _name = name;
    }

    public void setPhones(ArrayList<Phone> phones) {
        phones = _phones;
    }

    public void setId(int id){
        _id = id;
    }

    public void setPhotoUrl(String url) {
        _photoURL = url;
    }


    public String getName() {
        return _name;
    }

    public Phone getPhoneByIndex(int index) {
        return _phones.get(index);
    }

    public ArrayList<Phone> getPhones() {
        return _phones;
    }

    public String getPhotoUrl() {
        return _photoURL;
    }

    public int getId() {
        return _id;
    }

}
