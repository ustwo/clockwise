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

package com.ustwo.clockwise.data.calendar;

import java.util.Date;

/**
 * Class describing a standard calendar event. This class wraps the calendar events defined in
 * {@link android.provider.CalendarContract.Instances}
 */
public class CalendarEvent implements Cloneable {
    private String mTitle = "";
    private Date mStart;
    private Date mEnd;
    private boolean mIsAllDay;
    private String mDisplayColor;
    private String mLocation;

    /**
     * Returns the title of the calendar event (corresponds to
     * {@link android.provider.CalendarContract.Instances#TITLE} column)
     *
     * @return the title of the calendar event.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Sets a new value for the title of the calendar event.
     *
     * @param title new title for calendar event.
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Returns the start date/time of the calendar event (corresponds to
     * {@link android.provider.CalendarContract.Instances#BEGIN} column)
     *
     * @return the start date/time of the calendar event.
     */
    public Date getStart() {
        return mStart;
    }

    /**
     * Sets a new value for the start date of the calendar event.
     *
     * @param start new start date for calendar event.
     */
    public void setStart(Date start) {
        mStart = start;
    }

    /**
     * Returns the end date/time of the calendar event (corresponds to
     * {@link android.provider.CalendarContract.Instances#END} column)
     *
     * @return the end date/time of the calendar event.
     */
    public Date getEnd() {
        return mEnd;
    }

    /**
     * Sets a new value for the end date of the calendar event.
     *
     * @param end new end date for calendar event.
     */
    public void setEnd(Date end) {
        mEnd = end;
    }

    /**
     * Returns whether the event is an all-day calendar event (corresponds to
     * {@link android.provider.CalendarContract.Instances#ALL_DAY} column)
     *
     * @return is the event all-day.
     */
    public boolean isAllDay() {
        return mIsAllDay;
    }

    protected void setAllDay(boolean isAllDay) {
        mIsAllDay = isAllDay;
    }

    /**
     * Returns the display color of the calendar event (corresponds to
     * {@link android.provider.CalendarContract.Instances#DISPLAY_COLOR} column). The color is
     * an argb integer value.
     *
     * @return the display color of the calendar event.
     */
    public String getDisplayColor() {
        return mDisplayColor;
    }

    /**
     * Sets a new value for the display color of the calendar event.
     *
     * @param displayColor new argb integer value for the display color of calendar event.
     */
    public void setDisplayColor(String displayColor) {
        mDisplayColor = displayColor;
    }

    /**
     * Returns the location of the calendar event (corresponds to
     * {@link android.provider.CalendarContract.Instances#EVENT_LOCATION} column)
     *
     * @return the location of the calendar event.
     */
    public String getLocation() {
        return mLocation;
    }

    /**
     * Sets a new value for the location of the calendar event.
     *
     * @param location new location for calendar event.
     */
    public void setLocation(String location) {
        mLocation = location;
    }
}
