/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 ustwo studio inc (www.ustwo.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.ustwo.clockwise;

import android.text.format.Time;

import java.util.TimeZone;

/**
 * A point in time.
 */
public class WatchFaceTime extends Time {
    /**
     * The number of milliseconds passed within the current second. From 0 to 999.
     */
    public int millis;

    /**
     * Hour of the morning or afternoon. [0-11]
     */
    public int hour12;

    public WatchFaceTime() {
        super();

        reset();
    }

    @Override
    public void setToNow() {
        super.setToNow();
        setMillis(System.currentTimeMillis());
        setHour12();
    }

    public void set(WatchFaceTime that) {
        super.set(that);
        millis = that.millis;
        setHour12();
    }

    @Override
    public void set(int second, int minute, int hour, int monthDay, int month, int year) {
        super.set(second, minute, hour, monthDay, month, year);
        setMillis(0l);
        setHour12();
    }

    @Override
    public void set(int monthDay, int month, int year) {
        super.set(monthDay, month, year);
        setMillis(0l);
        setHour12();
    }

    @Override
    public void set(long millis) {
        super.set(millis);
        setMillis(millis);
        setHour12();
    }

    private void setMillis(long millis) {
        this.millis = (int)millis % 1000;
    }

    private void setHour12() {
        hour12 = (hour % 12);
    }

    /**
     * Set to now and set the time zone to the default.
     */
    protected void reset() {
        clear(TimeZone.getDefault().getID());
        setToNow();
    }

    /**
     * Determine if the hour value has changed.
     *
     * @param otherTime a WatchFaceTime to compare to.
     * @return true if it changed, otherwise false.
     */
    public boolean hasHourChanged(WatchFaceTime otherTime) {
        return hour != otherTime.hour;
    }

    /**
     * Determine if the minute value has changed.
     *
     * @param otherTime a WatchFaceTime to compare to.
     * @return true if it changed, otherwise false.
     */
    public boolean hasMinuteChanged(WatchFaceTime otherTime) {
        return minute != otherTime.minute;
    }

    /**
     * Determine if the second value has changed.
     *
     * @param otherTime a WatchFaceTime to compare to.
     * @return true if it changed, otherwise false.
     */
    public boolean hasSecondChanged(WatchFaceTime otherTime) {
        return second != otherTime.second;
    }

    /**
     * Determine if the day of the year or the year changed.
     *
     * @param otherTime a WatchFaceTime to compare to.
     * @return true if it changed, otherwise false.
     */
    public boolean hasDateChanged(WatchFaceTime otherTime) {
        return (monthDay != otherTime.monthDay) || (month != otherTime.month) || (year != otherTime.year);
    }

    /**
     * Determine if the time zone changed.
     *
     * @param otherTime a WatchFaceTime to compare to.
     * @return true if it changed, otherwise false.
     */
    public boolean hasTimeZoneChanged(WatchFaceTime otherTime) {
        return (timezone == null ? otherTime.timezone != null : !timezone.equals(otherTime.timezone));
    }

}
