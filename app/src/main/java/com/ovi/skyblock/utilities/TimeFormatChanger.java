package com.ovi.skyblock.utilities;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeFormatChanger {
    public static String convertTimestamp(int timestampString) {
        long timestamp = Long.parseLong(String.valueOf(timestampString));
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(timestamp * 1000);
        int days = calendar.get(Calendar.DAY_OF_YEAR) - 1;
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);


        String outputString = "";
        if (days > 0) {
            outputString += String.format("%02dd ", days);
        }
        if (hours > 0 || days > 0) {
            outputString += String.format("%02dh ", hours);
        }
        outputString += String.format("%02dm %02ds", minutes, seconds);
        return outputString;
    }
}
