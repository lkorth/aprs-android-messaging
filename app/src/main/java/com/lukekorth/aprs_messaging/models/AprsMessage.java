package com.lukekorth.aprs_messaging.models;

import android.location.Location;

import com.lukekorth.aprs_messaging.exceptions.InvalidMessageException;

import java.text.DecimalFormat;

public class AprsMessage {

    private static final String MATCHER = "^[0-9]{14}[EW][0-9]{6}[NS].+$";
    private static final DecimalFormat DECIMAL_FORMATER = new DecimalFormat("#.0000");

    private Location mLocation;
    private String mMessage;

    public static AprsMessage parse(String rawMessage) throws InvalidMessageException {
        if (rawMessage == null || !rawMessage.matches(MATCHER)) {
            throw new InvalidMessageException();
        }

        AprsMessage message = new AprsMessage();

        message.mLocation = new Location("");
        message.mLocation.setTime(parseTimestamp(rawMessage));
        message.mLocation.setLongitude(parseLongitude(rawMessage));
        message.mLocation.setLatitude(parseLatitude(rawMessage));

        message.mMessage = rawMessage.substring(22);

        return message;
    }

    public long getTimestamp() {
        return mLocation.getTime();
    }

    public double getLongitude() {
        return Double.parseDouble(DECIMAL_FORMATER.format(mLocation.getLongitude()));
    }

    public double getLatitude() {
        return Double.parseDouble(DECIMAL_FORMATER.format(mLocation.getLatitude()));
    }

    public String getMessage() {
        return mMessage;
    }

    @Override
    public String toString() {
        String longitude;
        if (getLongitude() < 0) {
            longitude = Double.toString(getLongitude() * -1).replace(".", "") + "W";
        } else {
            longitude = Double.toString(getLongitude()).replace(".", "") + "E";
        }

        String latitude;
        if (getLatitude() < 0) {
            latitude = Double.toString(getLatitude() * -1).replace(".", "") + "S";
        } else {
            latitude = Double.toString(getLatitude()).replace(".", "") + "N";
        }

        return Long.toString(getTimestamp() / 10000).substring(2) + longitude + latitude +
                mMessage;
    }

    private static long parseTimestamp(String rawMessage) {
        return Long.parseLong(Long.toString(System.currentTimeMillis() / 1000).substring(0, 2) +
                rawMessage.substring(0, 7) + "0000");
    }

    private static double parseLongitude(String rawMessage) {
        double longitude = Double.parseDouble(rawMessage.substring(7, 10) + "." +
            rawMessage.substring(10, 14));

        if (rawMessage.substring(14, 15).equals("W")) {
            longitude = longitude * -1;
        }

        return longitude;
    }

    private static double parseLatitude(String rawMessage) {
        double latitude = Double.parseDouble(rawMessage.substring(15, 17) + "." +
                rawMessage.substring(17, 21));

        if (rawMessage.substring(21, 22).equals("S")) {
            latitude = latitude * -1;
        }

        return latitude;
    }
}
