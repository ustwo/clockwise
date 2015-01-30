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

package com.ustwo.clockwise.util;

import android.text.format.DateUtils;

import com.ustwo.clockwise.WatchFaceTime;

/**
 * Auxiliary utilities related to time to help drawing on a watch face.
 */
public class TimeUtil {
    public static final int DAY_IN_HOURS = 24;
    public static final int HALF_DAY_IN_HOURS = DAY_IN_HOURS / 2;
    public static final int HOUR_IN_MINUTES = 60;
    public static final int MINUTE_IN_SECONDS = 60;

    /**
     * Builds second value float from {@link com.ustwo.clockwise.WatchFaceTime}'s second and
     * millisecond values and passes to
     * {@link com.ustwo.clockwise.util.TimeUtil#getSecondDegrees(float)}.
     *
     * @param time  Point in time
     * @return      Number of degrees
     */
    public static float getSecondDegrees(WatchFaceTime time) {
        long totalMs = convertSecondsToMilliseconds(time.second) +
                time.millis;
        float seconds = (float) totalMs / (float) DateUtils.SECOND_IN_MILLIS;
        return getSecondDegrees(seconds);
    }

    /**
     * Builds minute value float from {@link com.ustwo.clockwise.WatchFaceTime}'s minute,
     * second, and millisecond values and passes to
     * {@link com.ustwo.clockwise.util.TimeUtil#getMinuteDegrees(float)}.
     *
     * @param time  Point in time
     * @return      Number of degrees
     */
    public static float getMinuteDegrees(WatchFaceTime time) {
        long totalMs = convertMinuteToMilliseconds(time.minute) +
                convertSecondsToMilliseconds(time.second) +
                time.millis;
        float minutes = (float) totalMs / (float) DateUtils.MINUTE_IN_MILLIS;
        return getMinuteDegrees(minutes);
    }

    /**
     * Builds hour value float from {@link com.ustwo.clockwise.WatchFaceTime}'s hour, minute,
     * second, and millisecond values and passes to
     * {@link com.ustwo.clockwise.util.TimeUtil#getHourDegrees(float)}.
     *
     * @param time  Point in time
     * @return      Number of degrees
     */
    public static float getHourDegrees(WatchFaceTime time) {
        long totalMs = convertHourToMilliseconds(time.hour12) +
                convertMinuteToMilliseconds(time.minute) +
                convertSecondsToMilliseconds(time.second) +
                time.millis;
        float hours = (float) totalMs / (float) DateUtils.HOUR_IN_MILLIS;
        return getHourDegrees(hours);
    }

    /**
     * Returns number of degrees calculated from the given number of seconds
     * within a minute. E.g 0 seconds = 0 degrees, 60 seconds = 360 degrees.
     *
     * @param seconds   Number of seconds
     * @return          Number of degrees
     */
    public static float getSecondDegrees(float seconds) {
        return seconds / MINUTE_IN_SECONDS * GeometryUtil.DEGREES_IN_CIRCLE;
    }

    /**
     * Returns number of degrees calculated from the given number of minutes
     * within an hour. E.g 0 minutes = 0 degrees, 60 minutes = 360 degrees.
     *
     * @param minutes   Number of minutes
     * @return          Number of degrees
     */
    public static float getMinuteDegrees(float minutes) {
        return minutes / HOUR_IN_MINUTES * GeometryUtil.DEGREES_IN_CIRCLE;
    }

    /**
     * Returns number of degrees calculated from the given number of hour
     * within a half day. E.g 0 hours = 0 degrees, 12 hours = 360 degrees.
     *
     * @param hours     Number of hours
     * @return          Number of degrees
     */
    public static float getHourDegrees(float hours) {
        return hours / HALF_DAY_IN_HOURS * GeometryUtil.DEGREES_IN_CIRCLE;
    }

    private static long convertSecondsToMilliseconds(int seconds) {
        return seconds * DateUtils.SECOND_IN_MILLIS;
    }

    private static long convertMinuteToMilliseconds(int minutes) {
        return minutes * DateUtils.MINUTE_IN_MILLIS;
    }

    private static long convertHourToMilliseconds(int hours) {
        return hours * DateUtils.HOUR_IN_MILLIS;
    }
}
