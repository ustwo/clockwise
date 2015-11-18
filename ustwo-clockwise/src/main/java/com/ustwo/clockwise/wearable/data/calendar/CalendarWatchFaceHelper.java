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

package com.ustwo.clockwise.wearable.data.calendar;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.support.wearable.provider.WearableCalendarContract;

import com.ustwo.clockwise.common.permissions.PermissionRequestItem;
import com.ustwo.clockwise.wearable.permissions.PermissionsRequest;
import com.ustwo.clockwise.wearable.permissions.PermissionsRequestor;
import com.ustwo.clockwise.wearable.permissions.PermissionsResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Helper class to create watch faces that integrate calendar data from the WearableCalendarContract.
 */
public class CalendarWatchFaceHelper {

    private static final String[] INSTANCE_PROJECTION = new String[]{
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.ALL_DAY,
            CalendarContract.Instances.DISPLAY_COLOR,
            CalendarContract.Instances.EVENT_LOCATION,
            CalendarContract.Instances.SELF_ATTENDEE_STATUS,
    };
    private final BroadcastReceiver mCalendarProviderChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // only update on event if we have the permission granted on the wearable
            if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                doUpdateCalendarEvents();
            } else if(mListener != null) {
                mListener.onPermissionDenied();
            }
        }
    };
    private final BroadcastReceiver mDateTimeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // only update on event if we have the permission granted on the wearable
            if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                doUpdateCalendarEvents();
            } else if(mListener != null) {
                mListener.onPermissionDenied();
            }
        }
    };

    private long mTimeLimitMillis;
    private Context mContext;
    private OnCalendarDataChangedListener mListener;

    public CalendarWatchFaceHelper(long timeLimitMillis) {
        mTimeLimitMillis = timeLimitMillis;
    }

    public void onCreate(Context context, OnCalendarDataChangedListener listener) {
        mContext = context;
        mListener = listener;

        // Using the same broadcast receiver for both filters does not work!
        context.registerReceiver(mCalendarProviderChangedReceiver, getCalendarEventsIntentFilter());
        context.registerReceiver(mDateTimeChangedReceiver, getDateTimeChangedIntentFilter());
    }

    public void onDestroy() {
        mContext.unregisterReceiver(mCalendarProviderChangedReceiver);
        mContext.unregisterReceiver(mDateTimeChangedReceiver);
    }

    /**
     * Change the time limit from now for which we will retrieve calendar events. If the time limit
     * differs to the currently set value we will perform a new query of the events.
     *
     * @param timeLimitMillis milliseconds from now for which to retrieve calendar events.
     */
    public void setTimeLimitMillis(int timeLimitMillis) {
        if (mTimeLimitMillis != timeLimitMillis) {
            mTimeLimitMillis = timeLimitMillis;
            doUpdateCalendarEvents();
        }
    }

    /**
     * Builds an intent filter to listen out for calendar events being added or removed
     *
     * @return the intent filter
     */
    private IntentFilter getCalendarEventsIntentFilter() {
        IntentFilter filter = new IntentFilter("android.intent.action.PROVIDER_CHANGED");
        filter.addDataScheme("content");
        filter.addDataAuthority("com.google.android.wearable.provider.calendar", null);
        return filter;
    }

    /**
     * Builds an intent filter to listen out for the date, time or timezone being changed
     *
     * @return the intent filter
     */
    private IntentFilter getDateTimeChangedIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        return filter;
    }

    public void updateCalendarEvents() {
        final String permission = Manifest.permission.READ_CALENDAR;

        PermissionRequestItem requestItem = new PermissionRequestItem();
        requestItem.setIsWearable(true);
        requestItem.getPermissions().add(permission);
        PermissionsRequest request = new PermissionsRequest();
        request.setContext(mContext);
        request.getRequestItems().add(requestItem);

        final PermissionsRequestor requestor = new PermissionsRequestor(mContext);
        requestor.request(request, new PermissionsRequestor.PermissionRequestListener() {
            @Override
            public void onCompleted(PermissionsResponse response) {

                if(response.getWearablePermissionResults().get(permission)) {
                    if(mListener != null) {
                        mListener.onPermissionGranted();
                    }
                    doUpdateCalendarEvents();
                } else {
                    if(mListener != null) {
                        mListener.onPermissionDenied();
                    }
                }
            }
        });
    }

    private void doUpdateCalendarEvents() {
        List<CalendarEvent> events = new ArrayList<CalendarEvent>();

        Uri.Builder builder = WearableCalendarContract.Instances.CONTENT_URI.buildUpon();
        long begin = System.currentTimeMillis();

        ContentUris.appendId(builder, begin);
        ContentUris.appendId(builder, begin + mTimeLimitMillis);

        final Cursor cursor = mContext.getContentResolver().query(builder.build(), INSTANCE_PROJECTION, null, null, null);
        if (cursor == null) {
            return;
        }

        Calendar cal = Calendar.getInstance();
        while (cursor.moveToNext()) {
            long beginVal = cursor.getLong(cursor.getColumnIndex(CalendarContract.Instances.BEGIN));
            long endVal = cursor.getLong(cursor.getColumnIndex(CalendarContract.Instances.END));
            String title = cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.TITLE));
            Boolean isAllDay = !cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.ALL_DAY)).equals("0");
            String eventColor = cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.DISPLAY_COLOR));
            String eventLocation = cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.EVENT_LOCATION));
            int myAttendeeStatus = cursor.getInt(cursor.getColumnIndex(CalendarContract.Instances.SELF_ATTENDEE_STATUS));

            if(myAttendeeStatus != CalendarContract.Attendees.ATTENDEE_STATUS_DECLINED) {
                CalendarEvent newEvent = new CalendarEvent();
                newEvent.setTitle(title);
                cal.setTimeInMillis(beginVal);
                newEvent.setStart(cal.getTime());
                cal.setTimeInMillis(endVal);
                newEvent.setEnd(cal.getTime());
                newEvent.setAllDay(isAllDay);
                newEvent.setDisplayColor(eventColor);
                newEvent.setLocation(eventLocation);
                newEvent.setMyAttendeeStatus(myAttendeeStatus);
                events.add(newEvent);
            }
        }
        cursor.close();

        Collections.sort(events, new CalendarEventComparator());

        mListener.onCalendarDataChanged(events);
    }

}
