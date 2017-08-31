package com.lukekorth.aprs_messaging.models;

import com.lukekorth.aprs_messaging.exceptions.InvalidMessageException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class AprsMessageTest {

    @Test(expected = InvalidMessageException.class)
    public void parse_throwsForNullMessage() throws InvalidMessageException {
        AprsMessage.parse("");
    }

    @Test(expected = InvalidMessageException.class)
    public void parse_throwsForEmptyMessage() throws InvalidMessageException {
        AprsMessage.parse("");
    }

    @Test(expected = InvalidMessageException.class)
    public void parse_throwsForInvalidMessageFormat() throws InvalidMessageException {
        AprsMessage.parse("This is only the message");
    }

    @Test
    public void parse_parsesMessage() throws InvalidMessageException {
        AprsMessage message = AprsMessage.parse("04059251231234E121234NThis is the text message");

        assertEquals(1504059250000L, message.getTimestamp());
        assertEquals(123.1234, message.getLongitude());
        assertEquals(12.1234, message.getLatitude());
        assertEquals("This is the text message", message.getMessage());
    }

    @Test
    public void parse_parsesMessageWithWestLongitude() throws InvalidMessageException {
        AprsMessage message = AprsMessage.parse("04059251231234W121234NThis is the text message");

        assertEquals(1504059250000L, message.getTimestamp());
        assertEquals(-123.1234, message.getLongitude());
        assertEquals(12.1234, message.getLatitude());
        assertEquals("This is the text message", message.getMessage());
    }

    @Test
    public void parse_parsesMessageWithSouthLatitude() throws InvalidMessageException {
        AprsMessage message = AprsMessage.parse("04059251231234E121234SThis is the text message");

        assertEquals(1504059250000L, message.getTimestamp());
        assertEquals(123.1234, message.getLongitude());
        assertEquals(-12.1234, message.getLatitude());
        assertEquals("This is the text message", message.getMessage());
    }

    @Test
    public void toString_createsStringRepresentationOfMessage() throws InvalidMessageException {
        String actualMessage = "04059251231234E121234NThis is the text message";
        AprsMessage message = AprsMessage.parse(actualMessage);

        assertEquals(actualMessage, message.toString());
    }

    @Test
    public void toString_handlesWestLongitude() throws InvalidMessageException {
        String actualMessage = "04059251231234W121234NThis is the text message";
        AprsMessage message = AprsMessage.parse(actualMessage);

        assertEquals(actualMessage, message.toString());
    }

    @Test
    public void toString_handlesSouthLatitude() throws InvalidMessageException {
        String actualMessage = "04059251231234E121234SThis is the text message";
        AprsMessage message = AprsMessage.parse(actualMessage);

        assertEquals(actualMessage, message.toString());
    }
}
