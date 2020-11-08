package net.finch.fastcontacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Contact {

    private String _name ="";
    private ArrayList<String[]> _phones = new ArrayList<>();        // String[] {PHONE_LABEL, PHONE_NUMBER}
    private String _photoURL;
    private int _id;

    public Contact(){}

    public Contact(int id, String name, String url, ArrayList<String[]> phones) {
        _id = id;
        _name = name;
        _photoURL = url;
        _phones = phones;
    }

    public void setName(String name) {
        _name = name;
    }

    public void setPhones(ArrayList<String[]> phones) {
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

    public String[] getPhones(int index) {
        return _phones.get(index);
    }

    public String getPhotoUrl() {
        return _photoURL;
    }

    public ArrayList<Map<String, String>> getPhonesMap(String Tag) {
        ArrayList<Map<String, String>> phonesMap = new ArrayList<>();

        int len = 0;
        for (int i=0; i<_phones.size(); i++) {
            if (len < _phones.get(i)[0].length()) len = _phones.get(i)[0].length();
        }

        for (int i=0; i<_phones.size(); i++) {
            HashMap<String, String> m = new HashMap<>();

            String l = "";
            for (int c=0; c<len-_phones.get(i)[0].length(); c++) {
                l += "\t";
            }
//            if (l == "") l = "\t";

            m.put("phones", _phones.get(i)[0]+":"+l + "       " + _phones.get(i)[1]);
            phonesMap.add(m);
        }
        return phonesMap;
    }
}
