package com.ilenlab.ilentt.twitterclient.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ADMIN on 4/5/2016.
 */
public class TimeFormat {

    public static String createTime(String createTime) {
        String tweetTime = "EEE MMM dd HH:mm:ss ZZZZ yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(tweetTime, Locale.ENGLISH);
        simpleDateFormat.setLenient(true);

        String timeString;
        long msDate = 0;

        try {
            msDate = simpleDateFormat.parse(createTime).getTime();
        }catch(ParseException e) {
            e.printStackTrace();
        }

        long dateString = Math.max(System.currentTimeMillis() - msDate, 0);
        if(dateString > 604800000L) {
            timeString = (dateString / 604800000L) + "w";
        } else if(dateString > 86400000L) {
            timeString = (dateString / 86400000L) + "d";
        } else if(dateString > 3600000L) {
            timeString = (dateString / 3600000L) + "h";
        } else if(dateString > 60000) {
            timeString = (dateString / 60000) + "m";
        } else {
            timeString = (dateString / 1000) + "s";
        }
        return timeString;
    }

    public static String tweetDetailDateFormat(String rawJsonDate) {
        // display the tweet's timestamp using the format that the Twitter app uses when you
        // tap a tweet to bring up a detailed view
        Date date;
        long dateMillis = 0;
        date = new Date(dateMillis);
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            dateMillis = sf.parse(rawJsonDate).getTime();
            date = new Date(dateMillis);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String twitterDetailDateFormat = "h:mm a • d MMM yy";
        SimpleDateFormat stdf = new SimpleDateFormat(twitterDetailDateFormat, Locale.ENGLISH);
        stdf.setLenient(true);

        return stdf.format(date);
    }
}
