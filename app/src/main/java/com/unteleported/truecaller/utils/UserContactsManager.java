package com.unteleported.truecaller.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.model.Call;
import com.unteleported.truecaller.model.Contact;
import com.unteleported.truecaller.model.ContactNumber;
import com.unteleported.truecaller.model.Phone;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by stasenkopavel on 4/4/16.
 */
public class UserContactsManager {

    public static ArrayList<Contact> readContacts(Context ctx, boolean favourite) {
        ArrayList<Contact> contacts = new ArrayList<>();
        TelephonyManager tMgr = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
        ContentResolver cr = ctx.getContentResolver();
        Cursor cur;
        if (!favourite) {
            cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
        }
        else {
            cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, "starred=?", new String[] {"1"}, null);
        }

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                Contact contact = new Contact();
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String photo = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    contact.setId(Integer.parseInt(id));
                    contact.setName(name);
                    if (!TextUtils.isEmpty(photo)) {
                        contact.setAvatar(photo);
                    }

                    // get the phone number
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Integer type = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        ContactNumber contactNumber = new ContactNumber(number, type);
                        if (number.startsWith("+")) {
                            contactNumber.setCountryIso(CountryManager.getIsoFromPhone(number));
                        }
                        else {
                            contactNumber.setCountryIso(tMgr.getSimCountryIso().toUpperCase());
                        }
                        contact.addNumber(contactNumber);
                    }
                    contacts.add(contact);
                    pCur.close();

                }
            }
            cur.close();
        }
        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                return lhs.getName().toUpperCase().compareTo(rhs.getName().toUpperCase());
            }
        });
        if (!favourite) {
            Contact lastContact = null;
            for (Contact contact : contacts) {
                if (lastContact == null || !contact.getName().substring(0, 1).toUpperCase().equals(lastContact.getName().substring(0, 1).toUpperCase())) {
                    contact.setTitle(contact.getName().substring(0, 1).toUpperCase());
                    lastContact = contact;
                }
            }
        }
        return contacts;
    }

    public static Boolean checkNumberInContacts(Context ctx, String number) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = ctx.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    public static ArrayList<Call> getUserCallsList(Context ctx) {
        ArrayList<Call> calls = new ArrayList<>();
        ContentResolver cr = ctx.getContentResolver();
        Cursor cur = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = cur.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cur.getColumnIndex(CallLog.Calls.TYPE);
        int date = cur.getColumnIndex(CallLog.Calls.DATE);
        int duration = cur.getColumnIndex(CallLog.Calls.DURATION);
        int name = cur.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int typeOfNumber = cur.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE);
        Call lastcall = null;
        int repeat = 0;
        HashMap<String, String> namesMap = new HashMap<>();
        HashMap<String, Integer> typeMap = new HashMap<>();
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                Call call = new Call();
                String phNumber = cur.getString(number);
                String callType = cur.getString(type);
                String callDate = cur.getString(date);
                String name1 = cur.getString(name);
                Date callDayTime = new Date(Long.valueOf(callDate));
                String callDuration = cur.getString(duration);
                int numberType = cur.getInt(typeOfNumber);
                int dircode = Integer.parseInt(callType);
                call.setNumber(phNumber);
                call.setDate(callDayTime);
                call.setDuration(callDuration);
                call.setType(dircode);
                if (name1!=null) {
                    call.setName(name1);
                }
                else {
                    call.setName(namesMap.get(call.getNumber()));
                }
                if (!TextUtils.isEmpty(call.getName())) {
                    if (numberType == 0) {
                        numberType = typeMap.get(call.getName());
                    }
                }
                switch (numberType) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        call.setTypeOfNumber(ctx.getString(R.string.mobile));
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        call.setTypeOfNumber(ctx.getString(R.string.home));
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                        call.setTypeOfNumber(ctx.getString(R.string.work));
                        break;
                    default:
                        call.setTypeOfNumber(ctx.getString(R.string.other));
                }
                if (lastcall == null || !call.getNumber().equals(lastcall.getNumber())) {
                    repeat = 0;
                    calls.add(0, call);

                }
                else {
                    repeat++;
                    calls.get(0).setCallsNumber(repeat);
                    calls.get(0).setDate(call.getDate());
                    calls.get(0).setType(call.getType());
                    calls.get(0).setTypeOfNumber(call.getTypeOfNumber());
                }
                lastcall = call;
                if (name1 != null) {
                    namesMap.put(call.getNumber(), name1);
                    typeMap.put(call.getName(), numberType);
                }
            }

        }
        return calls;
    }

    public static ArrayList<Call> getCallListOfContact(Context ctx, String name) {
        Uri lookupUri = Uri.withAppendedPath(CallLog.Calls.CONTENT_FILTER_URI, Uri.encode(name));
        ArrayList<Call> calls = new ArrayList<>();
        ContentResolver cr = ctx.getContentResolver();
        Cursor cur = cr.query(lookupUri, null, null, null, null);
        int number = cur.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cur.getColumnIndex(CallLog.Calls.TYPE);
        int date = cur.getColumnIndex(CallLog.Calls.DATE);
        int typeOfNumber = cur.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE);
        HashMap<String, String> namesMap = new HashMap<>();
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                Call call = new Call();
                String phNumber = cur.getString(number);
                String callType = cur.getString(type);
                String callDate = cur.getString(date);
                Date callDayTime = new Date(Long.valueOf(callDate));
                int numberType = cur.getInt(typeOfNumber);
                int dircode = Integer.parseInt(callType);
                call.setNumber(phNumber);
                call.setDate(callDayTime);
                call.setType(dircode);
                switch (numberType) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        call.setTypeOfNumber(ctx.getString(R.string.mobile));
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        call.setTypeOfNumber(ctx.getString(R.string.home));
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                        call.setTypeOfNumber(ctx.getString(R.string.work));
                        break;
                    default:
                        call.setTypeOfNumber(ctx.getString(R.string.other));
                }
                calls.add(0, call);
            }
        }
        return calls;
    }


}
