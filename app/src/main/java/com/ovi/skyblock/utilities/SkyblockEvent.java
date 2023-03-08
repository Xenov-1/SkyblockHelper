package com.ovi.skyblock.utilities;

import android.graphics.Color;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ovi.skyblock.utilities.TimeConverter.validate;

/**
 *
 * @author doej1367
 */
public class SkyblockEvent implements Comparable<SkyblockEvent> {
    private final String name;
    private final int color;
    private final SkyblockTime eventOffsetInSeconds;
    private final int durationInDays;

    private static final SkyblockEvent BANK_INTEREST_1 = new SkyblockEvent("BANK INTEREST", Color.rgb(255, 217, 0), new SkyblockTime(0, 1, 1, 0), 0);
    private static final SkyblockEvent BANK_INTEREST_2 = new SkyblockEvent("BANK INTEREST", Color.rgb(255, 217, 0), new SkyblockTime(0, 4, 1, 0), 0);
    private static final SkyblockEvent BANK_INTEREST_3 = new SkyblockEvent("BANK INTEREST", Color.rgb(255, 217, 0), new SkyblockTime(0, 7, 1, 0), 0);
    private static final SkyblockEvent BANK_INTEREST_4 = new SkyblockEvent("BANK INTEREST", Color.rgb(255, 217, 0), new SkyblockTime(0, 10, 1, 0), 0);
    private static final SkyblockEvent TRAVELING_ZOO_1 = new SkyblockEvent("TRAVELING ZOO", Color.rgb(86, 199, 86), new SkyblockTime(0, 4, 1, 0), 3);
    private static final SkyblockEvent TRAVELING_ZOO_2 = new SkyblockEvent("TRAVELING ZOO", Color.rgb(86, 199, 86), new SkyblockTime(0, 10, 1, 0), 3);
    private static final SkyblockEvent NEW_MAYOR = new SkyblockEvent("NEW MAYOR", Color.DKGRAY, new SkyblockTime(0, 3, 27, 0), 0);
    private static final SkyblockEvent ELECTION_STARTS = new SkyblockEvent("ELECTION STARTS", Color.BLACK, new SkyblockTime(0, 6, 27, 0), 0);
    private static final SkyblockEvent SPOOKY_FISHING = new SkyblockEvent("SPOOKY FISHING", Color.rgb(107, 166, 255), new SkyblockTime(0, 8, 26, 0), 9);
    private static final SkyblockEvent SPOOKY_FESTIVAL = new SkyblockEvent("SPOOKY FESTIVAL", Color.rgb(193, 122, 255), new SkyblockTime(0, 8, 29, 0), 3);
    private static final SkyblockEvent WINTER_ISLAND = new SkyblockEvent("WINTER ISLAND", Color.LTGRAY, new SkyblockTime(0, 12, 1, 0), 31);
    private static final SkyblockEvent SEASON_OF_JERRY = new SkyblockEvent("SEASON OF JERRY", Color.rgb(250, 80, 80), new SkyblockTime(0, 12, 24, 0), 3);
    private static final SkyblockEvent NEW_YEAR = new SkyblockEvent("NEW YEAR", Color.rgb(168, 168, 168), new SkyblockTime(0, 12, 29, 0), 3);


    public static final SkyblockEvent[] EVENTS_ALL = {BANK_INTEREST_1, NEW_MAYOR, BANK_INTEREST_2, TRAVELING_ZOO_1, ELECTION_STARTS, BANK_INTEREST_3,
            SPOOKY_FISHING, SPOOKY_FESTIVAL, BANK_INTEREST_4, TRAVELING_ZOO_2, WINTER_ISLAND, SEASON_OF_JERRY, NEW_YEAR};
    public static final SkyblockEvent[] SINGLE_NEXT_EVENTS = {getNextBankInterestEvent(), NEW_MAYOR, getNextTravelingZooEvent(), ELECTION_STARTS,
            SPOOKY_FISHING, SPOOKY_FESTIVAL, WINTER_ISLAND, SEASON_OF_JERRY, NEW_YEAR};

    private static SkyblockEvent getNextBankInterestEvent() {
        List<SkyblockEvent> tmp = Arrays.asList(BANK_INTEREST_1, BANK_INTEREST_2, BANK_INTEREST_3, BANK_INTEREST_4);
        Collections.sort(tmp, SkyblockEvent::compareTo);
        return tmp.get(0);
    }

    private static SkyblockEvent getNextTravelingZooEvent() {
        return TRAVELING_ZOO_1.getSecondsToNextOccurrence() < TRAVELING_ZOO_2.getSecondsToNextOccurrence() ? TRAVELING_ZOO_1 : TRAVELING_ZOO_2;
    }


    public SkyblockEvent(String name, int color, SkyblockTime eventOffsetInSeconds, int durationInDays) {
        this.name = name;
        this.eventOffsetInSeconds = eventOffsetInSeconds;
        this.durationInDays = durationInDays;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return getName().replaceAll("[0-9]", "").hashCode();
    }

    public int getColor() {
        return color;
    }

    public SkyblockTime getEventOffset() {
        return eventOffsetInSeconds;
    }

    public SkyblockTime getNextEventOccurrence() {
        return validate(new SkyblockTime(0, 0, 0, (TimeConverter.getIGTime().getAgeInSeconds() + getSecondsToNextOccurrence()) / 50));
    }

    public int getDurationInIgDays() {
        return durationInDays;
    }

    public int getIgDaysToNextOccurrence() {
        return getSecondsToNextOccurrence() / (60 * 20);
    }

    public int getSecondsToNextOccurrence() {
        if (getName().equalsIgnoreCase("DARK AUCTION")) {
            int DATime = 54; // alarm always in the 53th minute
            int secondsSinceLastFullHour = (int) ((System.currentTimeMillis() / 1000) % 3600);
            int tmp_res = (DATime * 60 - secondsSinceLastFullHour);
            return (tmp_res <= 0) ? tmp_res + 3600 : tmp_res;
        }
        if (getName().equalsIgnoreCase("JACOB CONTEST")) {
            int DATime = 15; // alarm always in the 15th minute
            int secondsSinceLastFullHour = (int) ((System.currentTimeMillis() / 1000) % 3600);
            int tmp_res = (DATime * 60 - secondsSinceLastFullHour);
            return (tmp_res <= 0) ? tmp_res + 3600 : tmp_res;
        }
        long eventTimeInSeconds = TimeConverter.getTimestampInSeconds(getEventOffset())
                + (getEventOffset().getYear() <= 0 ? ((TimeConverter.getIGYear() - 1) * 12 * 31 * 60 * 20) : 0);
        while (eventTimeInSeconds + (durationInDays * 20 * 60) <= (System.currentTimeMillis() / 1000))
            eventTimeInSeconds += 12 * 31 * 60 * 20;
        return (int) ((eventTimeInSeconds - System.currentTimeMillis() / 1000));
    }

    @Override
    public int compareTo(SkyblockEvent o) {
        return getSecondsToNextOccurrence() - o.getSecondsToNextOccurrence();
    }
}
