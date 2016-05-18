package com.unteleported.truecaller.utils;

import android.content.Context;

import com.unteleported.truecaller.R;
import com.unteleported.truecaller.model.Call;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by stasenkopavel on 4/12/16.
 */
public class DateConverter {

    public static String dateToString(Context context, Date date) {
        String stringDate;
        Calendar calendareDate = Calendar.getInstance();
        Calendar curTime = Calendar.getInstance();
        calendareDate.setTime(date);
        if (Math.abs(calendareDate.get(Calendar.DAY_OF_MONTH) - curTime.get(Calendar.DAY_OF_MONTH))==0) {
            if (Math.abs(calendareDate.get(Calendar.HOUR_OF_DAY) - curTime.get(Calendar.HOUR_OF_DAY)) == 0) {
                stringDate = String.valueOf(Math.abs(calendareDate.get(Calendar.MINUTE) - curTime.get(Calendar.MINUTE)) + " " + context.getString(R.string.min_));
            }
            else {
                stringDate = String.valueOf(Math.abs(calendareDate.get(Calendar.HOUR_OF_DAY) - curTime.get(Calendar.HOUR_OF_DAY)) + " " + context.getString(R.string.h_));
            }
        }
        else if ((Math.abs(calendareDate.get(Calendar.DAY_OF_MONTH) - curTime.get(Calendar.DAY_OF_MONTH))==1) && (calendareDate.get(Calendar.MONTH) == curTime.get(Calendar.MONTH)))  {
            stringDate = context.getString(R.string.yesturday);
        }
        else if ((Math.abs(calendareDate.get(Calendar.DAY_OF_MONTH) - curTime.get(Calendar.DAY_OF_MONTH))==2) && (calendareDate.get(Calendar.MONTH) == curTime.get(Calendar.MONTH))) {
            stringDate = context.getString(R.string.twodayssago);
        }
        else {
            stringDate =new SimpleDateFormat("dd/MM/yyyy").format(date);
        }
        return stringDate;

    }
}
