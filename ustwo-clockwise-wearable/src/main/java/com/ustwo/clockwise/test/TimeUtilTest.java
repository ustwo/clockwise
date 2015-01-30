package com.ustwo.clockwise.test;

import com.ustwo.clockwise.WatchFaceTime;
import com.ustwo.clockwise.util.TimeUtil;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TimeUtilTest extends TestCase {
    public void testSecondDegreesWithTime() throws Exception {
        // 30s, 500ms
        WatchFaceTime time = new WatchFaceTime();
        time.set(30, 10, 10, 1, 1, 2015);
        time.millis = 500;

        float degrees = TimeUtil.getSecondDegrees(time);
        assertEquals(183.0f, degrees, Math.ulp(degrees));
    }

    public void testSecondDegreesWithSeconds() throws Exception {
        // 30s, 500ms
        float degrees = TimeUtil.getSecondDegrees(30.5f);
        assertEquals(183.0f, degrees, Math.ulp(degrees));
    }

    public void testNegativeSecondDegrees() throws Exception {
        // 30s, 500ms
        float degrees = TimeUtil.getSecondDegrees(-30.5f);
        assertEquals(-183.0f, degrees, Math.ulp(degrees));
    }

    public void testHighSecondDegrees() throws Exception {
        // 90s, 500ms
        float degrees = TimeUtil.getSecondDegrees(60f + 30.5f);
        assertEquals(360f + 183.0f, degrees, Math.ulp(degrees));
    }

    public void testSecondDegreesThrowsException() throws Exception {
        try {
            TimeUtil.getSecondDegrees(null);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException npe) {
            //success
        }
    }


    public void testMinuteDegreesWithTime() throws Exception {
        // 10m, 30s
        WatchFaceTime time = new WatchFaceTime();
        time.set(30, 10, 10, 1, 1, 2015);

        float degrees = TimeUtil.getMinuteDegrees(time);
        assertEquals(63.0f, degrees, Math.ulp(degrees));
    }

    public void testMinuteDegreesWithMinute() throws Exception {
        // 10m, 30s
        float degrees = TimeUtil.getMinuteDegrees(10.5f);
        assertEquals(63.0f, degrees, Math.ulp(degrees));
    }

    public void testNegativeMinuteDegrees() throws Exception {
        // 10m, 30s
        float degrees = TimeUtil.getMinuteDegrees(-10.5f);
        assertEquals(-63.0f, degrees, Math.ulp(degrees));
    }

    public void testHighMinuteDegrees() throws Exception {
        // 70m, 30s
        float degrees = TimeUtil.getMinuteDegrees(60f + 10.5f);
        assertEquals(360f + 63.0f, degrees, Math.ulp(degrees));
    }

    public void testMinuteDegreesThrowsException() throws Exception {
        try {
            TimeUtil.getMinuteDegrees(null);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException npe) {
            //success
        }
    }


    public void testHourDegreesWithTime() throws Exception {
        // 10h, 30m
        WatchFaceTime time = new WatchFaceTime();
        time.set(0, 30, 10, 1, 1, 2015);

        float degrees = TimeUtil.getHourDegrees(time);
        assertEquals(315.0f, degrees, Math.ulp(degrees));
    }

    public void testHourDegreesWithHours() throws Exception {
        // 10h, 30m
        float degrees = TimeUtil.getHourDegrees(10.5f);
        assertEquals(315.0f, degrees, Math.ulp(degrees));
    }

    public void testNegativeHourDegrees() throws Exception {
        // 10h, 30m
        float degrees = TimeUtil.getHourDegrees(-10.5f);
        assertEquals(-315.0f, degrees, Math.ulp(degrees));
    }

    public void testHighHourDegrees() throws Exception {
        // 22h, 30m
        float degrees = TimeUtil.getHourDegrees(12f + 10.5f);
        assertEquals(360f + 315.0f, degrees, Math.ulp(degrees));
    }

    public void testHourDegreesThrowsException() throws Exception {
        try {
            TimeUtil.getHourDegrees(null);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException npe) {
            //success
        }
    }
}
