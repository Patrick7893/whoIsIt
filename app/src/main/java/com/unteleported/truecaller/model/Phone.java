package com.unteleported.truecaller.model;

import android.content.Context;
import android.provider.ContactsContract;

import com.unteleported.truecaller.R;

import java.io.File;

/**
 * Created by stasenkopavel on 4/15/16.
 */
public class Phone {
    String number;
    int typeOfNumber;
    String typeDescription;
    private String name;
    private String countryIso;
    private File avatar;


    public Phone(String phone, int type) {
        this.number = phone;
        this.typeOfNumber = type;
    }

    public String getNumber() {
        return number;
    }

    public int getTypeOfNumber() {
        return typeOfNumber;
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

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public File getAvatar() {
        return avatar;
    }

    public void setAvatar(File avatar) {
        this.avatar = avatar;
    }
}
