package com.unteleported.truecaller.model;

import android.content.Context;
import android.provider.ContactsContract;

import com.unteleported.truecaller.R;

/**
 * Created by stasenkopavel on 5/19/16.
 */
public class ContactNumber {

    private String number;
    private int typeOfNumber;
    private String typeDescription;
    private String countryIso;

    public ContactNumber() {}

    public ContactNumber (String number, int typeOfNumber) {
        this.number = number;
        this.typeOfNumber = typeOfNumber;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getTypeOfNumber() {
        return typeOfNumber;
    }

    public void setTypeOfNumber(int typeOfNumber) {
        this.typeOfNumber = typeOfNumber;
    }

    public String getCountryIso() {
        return countryIso;
    }

    public void setCountryIso(String countryIso) {
        this.countryIso = countryIso;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public void setTypeDescriptionFromType(Context ctx, int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                this.setTypeDescription(ctx.getString(R.string.mobile));
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                this.setTypeDescription(ctx.getString(R.string.home));
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                this.setTypeDescription(ctx.getString(R.string.work));
                break;
            default:
                this.setTypeDescription(ctx.getString(R.string.other));
                break;
        }
    }
}
