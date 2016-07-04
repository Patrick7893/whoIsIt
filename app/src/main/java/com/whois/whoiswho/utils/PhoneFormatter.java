package com.whois.whoiswho.utils;

/**
 * Created by stasenkopavel on 5/17/16.
 */
public class PhoneFormatter {

    public static String removeAllNonNumeric(String number) {
        return number.replaceAll("[^0-9+]", "");
    }
}
