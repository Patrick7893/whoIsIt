package com.whois.whoiswho.utils;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.whois.whoiswho.R;
import com.whois.whoiswho.model.Call;
import com.whois.whoiswho.model.Contact;
import com.whois.whoiswho.model.ContactNumber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by stasenkopavel on 4/4/16.
 */
public class ContactsManager {

    public static final int DONT_PRESENT_IN_CONTACTS = -1;
    public static final int PRESENT_IN_CONTACTS_NOT_STARRED = 0;
    public static final int PRESENT_IN_CONTACTS_STARRED = 1;

    public static ArrayList<Contact> readContacts(Context ctx, boolean favourite) {
        ArrayList<Contact> contacts = new ArrayList<>();
        TelephonyManager tMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        Cursor phones;
        if (!favourite)
            phones = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null,  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        else
            phones = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "starred=?", new String[]{"1"}, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

        int lastId = -1;
        while (phones.moveToNext()) {
            Contact contact = new Contact();
            int id = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Integer type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            String photo = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));

            contact.setName(name);
            contact.setId(id);
            if (!TextUtils.isEmpty(photo))
                contact.setAvatar(photo);

            ContactNumber contactNumber = new ContactNumber(phoneNumber, type);
            if (phoneNumber.startsWith("+"))
                contactNumber.setCountryIso(CountryManager.getIsoFromPhone(phoneNumber));
            else
                contactNumber.setCountryIso(tMgr.getSimCountryIso().toUpperCase());

            if (lastId != -1 && lastId == id) {
                contacts.get(contacts.size()-1).addNumber(contactNumber);
            }
            else {
                contact.addNumber(contactNumber);
                contacts.add(contact);
            }
            lastId = id;

        }
        phones.close();


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

    public static int checkNumberInContacts(Context ctx, String number) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.STARRED, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = ctx.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            Log.d("COUNT", String.valueOf(cur.getCount()));
            if (cur.getCount() > 0) {
                cur.moveToNext();
                if (cur.getString(cur.getColumnIndex(ContactsContract.Contacts.STARRED)).equals(String.valueOf(1)))
                    return PRESENT_IN_CONTACTS_STARRED;
                else
                    return PRESENT_IN_CONTACTS_NOT_STARRED;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return DONT_PRESENT_IN_CONTACTS;
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
                        if (typeMap!=null)
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

    public static void changeContactStarred(Context context, String number, int starred) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        if (cur.getCount()>0) {
            cur.moveToNext();
            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            ContentValues contentValues = new ContentValues();
            contentValues.put(ContactsContract.CommonDataKinds.Phone.STARRED, starred);
            context.getContentResolver().update(ContactsContract.Contacts.CONTENT_URI, contentValues, ContactsContract.Contacts.DISPLAY_NAME + "= ?", new String[]{name});
            cur.close();
        }
    }


}
