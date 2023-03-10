package com.ovi.skyblock.utilities;


import androidx.annotation.NonNull;

public class SkyblockTime {

    private final long year;
    private final long month;
    private final long day;
    private final long hour;

    public SkyblockTime(long year, long month, long day, long hour) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
    }

    public long getYear() {
        return year;
    }

    public long getMonth() {
        return month;
    }

    public long getDay() {
        return day;
    }

    public long getHour() {
        return hour;
    }

    public long getAgeInSeconds() {
        return TimeConverter.getTimestampInSeconds(this) - TimeConverter.getBirthday();
    }

    @NonNull
    @Override
    public String toString() {
        return  year + "-" + month + "-" + day + " " + (hour > 12 ? hour - 12 : hour) + ":00" + (hour >= 12 ? "pm" : "am");
    }
}
