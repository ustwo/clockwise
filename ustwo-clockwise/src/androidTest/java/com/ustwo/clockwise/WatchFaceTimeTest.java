package com.ustwo.clockwise;

import com.ustwo.clockwise.common.WatchFaceTime;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.TimeZone;

public class WatchFaceTimeTest extends TestCase {
    private WatchFaceTime mBeginWatchFaceTime;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //  Setting up the begin time for all tests
        mBeginWatchFaceTime = new WatchFaceTime();
        mBeginWatchFaceTime.set(0, 0, 0, 1, 1, 2015);
    }

    public void testWatchFaceTimeSecondChanged() throws Exception {
        WatchFaceTime endWatchFaceTime = new WatchFaceTime();
        endWatchFaceTime.set(1, 0, 0, 1, 1, 2015);

        assertTrue(mBeginWatchFaceTime.hasSecondChanged(endWatchFaceTime));
    }

    public void testWatchFaceTimeSecondChangedThrowsException() throws Exception {
        try {
            mBeginWatchFaceTime.hasSecondChanged(null);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException npe) {
            //success
        }
    }

    public void testWatchFaceTimeMinuteChanged() throws Exception {
        WatchFaceTime endWatchFaceTime = new WatchFaceTime();
        endWatchFaceTime.set(0, 1, 0, 1, 1, 2015);

        assertTrue(mBeginWatchFaceTime.hasMinuteChanged(endWatchFaceTime));
    }

    public void testWatchFaceTimeMinuteChangedThrowsException() throws Exception {
        try {
            mBeginWatchFaceTime.hasMinuteChanged(null);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException npe) {
            //success
        }
    }

    public void testWatchFaceTimeHourChanged() throws Exception {
        WatchFaceTime endWatchFaceTime = new WatchFaceTime();
        endWatchFaceTime.set(0, 0, 1, 1, 1, 2015);

        assertTrue(mBeginWatchFaceTime.hasHourChanged(endWatchFaceTime));
    }

    public void testWatchFaceTimeHourChangedThrowsException() throws Exception {
        try {
            mBeginWatchFaceTime.hasHourChanged(null);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException npe) {
            //success
        }
    }

    public void testWatchFaceTimeDateChanged() throws Exception {
        WatchFaceTime endWatchFaceTime = new WatchFaceTime();
        endWatchFaceTime.set(0, 0, 0, 2, 1, 2015);
        endWatchFaceTime.normalize(false);
        assertTrue(mBeginWatchFaceTime.hasDateChanged(endWatchFaceTime));
    }

    public void testWatchFaceTimeTimeZoneChanged() throws Exception {
        WatchFaceTime endWatchFaceTime = new WatchFaceTime();
        endWatchFaceTime.set(mBeginWatchFaceTime);
        endWatchFaceTime.timezone = TimeZone.getAvailableIDs()[0];
        assertTrue(mBeginWatchFaceTime.hasTimeZoneChanged(endWatchFaceTime));
    }
}
