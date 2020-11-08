package net.finch.fastcontacts;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashMap;

public class Contacts {

    private static final String CONTACT_ID = ContactsContract.Contacts._ID;
    private static final String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private static final String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private static final String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    private static final String CONTACT_PHOTO = ContactsContract.Contacts.PHOTO_THUMBNAIL_URI;
    private static final String PHONE_LABEL = ContactsContract.CommonDataKinds.Phone.LABEL;
    private static final String PHONE_TYPE = ContactsContract.CommonDataKinds.Phone.TYPE;

    public static ArrayList<Contact> getAll(Context context) {
        ContentResolver cr = context.getContentResolver();

        Cursor pCur = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{PHONE_NUMBER, PHONE_LABEL, PHONE_TYPE, PHONE_CONTACT_ID},
                null,
                null,
                null
        );
        if(pCur != null) {
          if(pCur.getCount() > 0) {
              HashMap<Integer, ArrayList<String[]>> phones = new HashMap<>();
              while (pCur.moveToNext()){
                  Integer contactId = pCur.getInt(pCur.getColumnIndex(PHONE_CONTACT_ID));
                  ArrayList<String[]> curPhones = new ArrayList<>();

                  if(phones.containsKey(contactId)) {
                      curPhones = phones.get(contactId);
                  }
                  assert curPhones != null;
                  int intType = pCur.getInt(pCur.getColumnIndex(PHONE_TYPE));
                  String strType = context.getString(ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(intType));
                  if (intType == ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM) strType = pCur.getString(pCur.getColumnIndex(PHONE_LABEL));

                  curPhones.add(new String[] {strType, pCur.getString(pCur.getColumnIndex(PHONE_NUMBER))});
                  phones.put(contactId, curPhones);
              }

              Cursor cur = cr.query(
                      ContactsContract.Contacts.CONTENT_URI,
                      new String[]{CONTACT_ID, DISPLAY_NAME, HAS_PHONE_NUMBER, CONTACT_PHOTO},
                      HAS_PHONE_NUMBER + " > 0",
                      null,
                      DISPLAY_NAME + " ASC"
              );
              if (cur!= null) {
                  if (cur.getCount() > 0) {
                      ArrayList<Contact> contacts = new ArrayList<>();
                      while (cur.moveToNext()) {
                          int id = cur.getInt(cur.getColumnIndex(CONTACT_ID));
                          if (phones.containsKey(id)) {
                              String photoUrl = cur.getString(cur.getColumnIndex(CONTACT_PHOTO));
                              Contact con = new Contact(id,
                                      cur.getString(cur.getColumnIndex(DISPLAY_NAME)),
                                      photoUrl,
                                      phones.get(id)
                              );
                              contacts.add(con);
                          }
                      }
                      return contacts;
                  }
                  cur.close();
              }
          }
          pCur.close();
        }
        return null;
    }
}
