package com.unteleported.truecaller.model;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by stasenkopavel on 4/7/16.
 */
public class Call {

    private String number;
    private String name;
    private Date date;
    private String duration;
    private int type;
    private int callsNumber = 0;
    private String typeOfNumber;

    public String getNumber() {
        return number;
    }

    public Date getDate() {
        return date;
    }

    public String getDuration() {
        return duration;
    }

    public int getType() {
        return type;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCallsNumber() {
        return callsNumber;
    }

    public void setCallsNumber(int callsNumber) {
        this.callsNumber = callsNumber;
    }

    public String getTypeOfNumber() {
        return typeOfNumber;
    }

    public void setTypeOfNumber(String typeOfNumber) {
        this.typeOfNumber = typeOfNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Call))return false;
        Call oCall = (Call)o;
        Calendar curCalnedar = Calendar.getInstance();
        curCalnedar.setTime(this.getDate());
        Calendar oCalendar = Calendar.getInstance();
        oCalendar.setTime(oCall.getDate());
        if (curCalnedar.get(Calendar.DAY_OF_MONTH) == oCalendar.get(Calendar.DAY_OF_MONTH) && curCalnedar.get(Calendar.MONTH) == oCalendar.get(Calendar.MONTH) && this.number == oCall.getNumber()) {
            this.callsNumber += 1;
            return true;
        }
        else {
            return false;
        }
    }
}
