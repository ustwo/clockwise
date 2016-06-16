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
package com.ustwo.clockwise.wearable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.ustwo.clockwise.common.WatchFaceTime;
import com.ustwo.clockwise.common.WatchMode;
import com.ustwo.clockwise.common.WatchShape;
import com.ustwo.clockwise.common.util.Logr;

import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Base class for all watch faces that is an extension of the
 * {@link android.support.wearable.watchface.WatchFaceService}. Manages the current time, provides
 * information about the size and shape of the watch face, handles watch face system actions and
 * propagates appropriately. Also manages changes between interactive and ambient modes, as well as
 * the various hardware considerations in ambient mode.
 */
public abstract class WatchFace extends WatchFaceService {
    private static final String TAG = WatchFace.class.getSimpleName();

    private final WatchFaceEngine mWatchFaceEngine = new WatchFaceEngine();
    private final Rect mFaceRect = new Rect();
    private boolean mIs24HourFormat = false;
    private final WatchFaceTime mPreviousTime = new WatchFaceTime();
    private final WatchFaceTime mLatestTime = new WatchFaceTime();
    private boolean mIsAmbient = false;
    private boolean mLowBitAmbient = false;
    private boolean mBurnInProtection = false;
    private WatchShape mWatchShape = WatchShape.UNKNOWN;
    private WindowInsets mFaceInsets;
    private boolean mLayoutComplete;
    private ContentObserver mFormatChangeObserver;

    /**
     * Returns the width of the watch face.
     *
     * @return value of the watch face {@link android.graphics.Rect}s width
     */
    public final int getWidth() {
        return mFaceRect.width();
    }

    /**
     * Returns the height of the watch face.
     *
     * @return value of the watch face {@link android.graphics.Rect}s height
     */
    public final int getHeight() {
        return mFaceRect.height();
    }

    /**
     * Returns the shape of the watch face (e.g. round, square)
     *
     * @return watch face {@link WatchShape}.
     */
    public final WatchShape getWatchShape() {
        return mWatchShape;
    }

    /**
     * Returns true if user preference is set to 24-hour format.
     *
     * @return true if 24 hour time format is selected, false otherwise.
     */
    public final boolean is24HourFormat() {
        return mIs24HourFormat;
    }

    /**
     * Gets the latest {@link WatchFaceTime} that was updated the last time
     * onTimeChanged was called.
     *
     * @return latest {@link WatchFaceTime}
     */
    public final WatchFaceTime getTime() {
        return mLatestTime;
    }

    /**
     * Returns the current {@link WatchMode}.
     *
     * @return the current {@link WatchMode}.
     */
    protected final WatchMode getCurrentWatchMode() {
        WatchMode watchMode;
        if (mIsAmbient) {
            if (mBurnInProtection) {
                if (mLowBitAmbient) {
                    watchMode = WatchMode.LOW_BIT_BURN_IN;
                } else {
                    watchMode = WatchMode.BURN_IN;
                }
            } else if (mLowBitAmbient) {
                watchMode = WatchMode.LOW_BIT;
            } else {
                watchMode = WatchMode.AMBIENT;
            }
        } else {
            watchMode = WatchMode.INTERACTIVE;
        }
        return watchMode;
    }

    /**
     * Override to provide a custom {@link android.support.wearable.watchface.WatchFaceStyle} for the
     * watch face.
     *
     * @return {@link android.support.wearable.watchface.WatchFaceStyle} for watch face.
     */
    protected WatchFaceStyle getWatchFaceStyle() {
        return null;
    }

    /**
     * Returns the {@link WatchMode#INTERACTIVE} mode update rate in millis.
     * This will tell the {@link WatchFace} base class the period to call
     * {@link #onTimeChanged(WatchFaceTime, WatchFaceTime)} and
     * {@link #onDraw(android.graphics.Canvas)}.
     * <br><br>DEFAULT={@link android.text.format.DateUtils#MINUTE_IN_MILLIS}
     *
     * @return number of millis to wait before calling onTimeChanged and onDraw.
     */
    protected long getInteractiveModeUpdateRate() {
        return DateUtils.MINUTE_IN_MILLIS;
    }

    /**
     * Called when the size and shape of the watch face are first realized, and then every time they
     * are changed.
     *
     * @param shape        the watch screen shape.
     * @param screenBounds the raw screen size.
     * @param screenInsets the screen's window insets.
     */
    protected void onLayout(WatchShape shape, Rect screenBounds, WindowInsets screenInsets) {
        Logr.v(String.format("WatchFace.onLayout: Shape=%s; Bounds=%s; Insets=%s", shape.name(),
                screenBounds, screenInsets));
    }

    /**
     * Lifecycle event guaranteed to be called once after {@link #onLayout(WatchShape, Rect, WindowInsets)}
     * has been called for the first time.
     */
    protected void onLayoutCompleted() { }

    /**
     * Override to perform view and logic updates. This will be called once per minute
     * ({@link WatchFace.WatchFaceEngine#onTimeTick()}) in
     * {@link WatchMode#AMBIENT} modes and once per {@link #getInteractiveModeUpdateRate()} in
     * {@link WatchMode#INTERACTIVE} mode. This is also called when the date,
     * time, and/or time zone (ACTION_DATE_CHANGED, ACTION_TIME_CHANGED, and ACTION_TIMEZONE_CHANGED
     * intents, respectively) is changed on the watch.
     *
     * @param oldTime {@link WatchFaceTime} last time this method was called.
     * @param newTime updated {@link WatchFaceTime}
     */
    protected void onTimeChanged(WatchFaceTime oldTime, WatchFaceTime newTime) {
        Logr.v(String.format("WatchFace.onTimeChanged: oldTime=%d; newTime=%d",
                oldTime.millis, newTime.millis));
    }

    /**
     * Override to render watch face on Canvas. This will be called once per minute
     * ({@link WatchFace.WatchFaceEngine#onTimeTick()}) in
     * {@link WatchMode#AMBIENT} modes and once per {@link #getInteractiveModeUpdateRate()} in
     * {@link WatchMode#INTERACTIVE} mode.
     *
     * @param canvas canvas on which to draw watch face.
     */
    protected abstract void onDraw(Canvas canvas);

    /**
     * Called when the system tells us the current watch mode has changed (e.g.
     * {@link WatchFace.WatchFaceEngine#onAmbientModeChanged}).
     *
     * @param watchMode the current {@link WatchMode}
     */
    protected void onWatchModeChanged(WatchMode watchMode) {
        Logr.v(String.format("WatchFace.onWatchModeChanged: watchMode=%s", watchMode.name()));
    }

    /**
     * Called when the "Use 24-hour format" user setting is modified.
     *
     * @param is24HourFormat
     */
    protected void on24HourFormatChanged(boolean is24HourFormat) {

    }

    /**
     * Override to be informed of card peek events.
     *
     * @param rect size of the peeking card.
     */
    protected void onCardPeek(Rect rect) {
        Logr.v("WatchFace.onCardPeek: " + rect);
    }

    protected void onTapCommand(@TapType int tapType, int x, int y, long eventTime) {
        Logr.v("WatchFace.onTapCommand: " + tapType);
    }

    /**
     * Invalidates the entire canvas and forces {@link #onDraw(android.graphics.Canvas)} to be called.
     */
    public final void invalidate() {
        Canvas canvas = mWatchFaceEngine.getSurfaceHolder().lockCanvas();
        if (canvas == null) {
            Logr.d("Cannot execute invalidate - WatchFaceService Engine Canvas is null.");
            return;
        }

        try {
            onDraw(canvas);
        } catch(Exception e) {
            Log.e(TAG, "Exception in WatchFace onDraw", e);
        } finally {
            mWatchFaceEngine.getSurfaceHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void updateTimeAndInvalidate() {
        mPreviousTime.set(mLatestTime);
        mLatestTime.setToNow();
        mLatestTime.timezone = TimeZone.getDefault().getID();

        onTimeChanged(mPreviousTime, mLatestTime);

        boolean is24Hour = DateFormat.is24HourFormat(this);
        if(is24Hour != mIs24HourFormat) {
            mIs24HourFormat = is24Hour;
            on24HourFormatChanged(mIs24HourFormat);
        }

        invalidate();
    }

    /**
     * Change the {@link WatchMode#INTERACTIVE} mode update rate for the
     * duration of the current interactive mode. The mode will change back to one specified by
     * {@link #getInteractiveModeUpdateRate()} once the watch returns to interactive mode.
     * May be useful for creating animations when a higher-than-normal update rate is desired for a
     * short period of time.
     * This will tell the {@link WatchFace} base class the period to call
     * {@link #onTimeChanged(WatchFaceTime, WatchFaceTime)} and
     * {@link #onDraw(android.graphics.Canvas)}.
     *
     * @param updateRateMillis The new update rate, expressed in milliseconds between updates
     * @param delayUntilWholeSecond Whether the first update should start on a whole second (i.e. when milliseconds are 0)
     */
    public void startPresentingWithUpdateRate(long updateRateMillis, boolean delayUntilWholeSecond) {
        mWatchFaceEngine.checkTimeUpdater(updateRateMillis, delayUntilWholeSecond);
    }

    public void setActiveComplications(int[] ids) {
        mWatchFaceEngine.setActiveComplications(ids);
    }

    protected void onComplicationDataUpdate(int id, ComplicationData data) {
    }

    @Override
    public WatchFaceService.Engine onCreateEngine() {
        Logr.v("WatchFace.onCreateEngine");

        return mWatchFaceEngine;
    }

    public class WatchFaceEngine extends WatchFaceService.Engine {
        private final ScheduledExecutorService mScheduledTimeUpdaterPool = Executors.newScheduledThreadPool(2);
        private ScheduledFuture<?> mScheduledTimeUpdater;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            filter.addAction(Intent.ACTION_DATE_CHANGED);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            registerReceiver(mDateTimeChangedReceiver, filter);

            if (mFormatChangeObserver == null) {
                mFormatChangeObserver = new TimeFormatObserver(new Handler());
                getContentResolver().registerContentObserver(
                        Settings.System.getUriFor(Settings.System.TIME_12_24), true, mFormatChangeObserver);
            }

        }

        @Override
        public void onDestroy() {
            unregisterReceiver(mDateTimeChangedReceiver);

            if (mFormatChangeObserver != null) {
                getContentResolver().unregisterContentObserver(
                        mFormatChangeObserver);
                mFormatChangeObserver = null;
            }

            cancelTimeUpdater();
            mScheduledTimeUpdaterPool.shutdown();

            super.onDestroy();
        }

        private final BroadcastReceiver mDateTimeChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateTimeAndInvalidate();
            }
        };

        private void checkTimeUpdater() {
            checkTimeUpdater(getInteractiveModeUpdateRate(), true);
        }

        private void checkTimeUpdater(long updateRate, boolean delayStart) {
            cancelTimeUpdater();
            // Note that when we're ambient or invisible, we rely on timeTick to update instead of a scheduled future
            if (!mIsAmbient && isVisible()) {
                // start updater on next second (millis = 0) when delayed start is requested
                long initialDelay = (delayStart ? DateUtils.SECOND_IN_MILLIS - (System.currentTimeMillis() % 1000) : 0);
                mScheduledTimeUpdater = mScheduledTimeUpdaterPool.scheduleAtFixedRate(mTimeUpdater,
                        initialDelay, updateRate, TimeUnit.MILLISECONDS);
            }
        }

        private void cancelTimeUpdater() {
            if(mScheduledTimeUpdater != null) {
                mScheduledTimeUpdater.cancel(true);
            }
        }

        private boolean isTimeUpdaterRunning() {
            return (mScheduledTimeUpdater != null && !mScheduledTimeUpdater.isCancelled());
        }

        private final Runnable mTimeUpdater = new Runnable() {
            @Override
            public void run() {
                updateTimeAndInvalidate();
            }
        };

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            if (mFaceRect.width() != width || mFaceRect.height() != height) {
                mFaceRect.set(0, 0, width, height);

                if (mLayoutComplete) {
                    // A size change has occurred after the first layout. The subclass must re-layout.
                    onLayout(mWatchShape, mFaceRect, mFaceInsets);
                } // else, we wait for onApplyWindowInsets to perform the first layout.
            }
        }

        @Override
        public void onPeekCardPositionUpdate(Rect rect) {
            super.onPeekCardPositionUpdate(rect);
            onCardPeek(rect);
            updateTimeAndInvalidate();
        }

        @Override
        public void onTapCommand(@TapType int tapType, int x, int y, long eventTime) {
            super.onTapCommand(tapType, x, y, eventTime);
            WatchFace.this.onTapCommand(tapType, x, y, eventTime);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);

            Logr.d("WatchFace.onPropertiesChanged: " + "LowBit=" + Boolean.toString(mLowBitAmbient) +
                    ", BurnIn=" + Boolean.toString(mBurnInProtection));
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            Logr.v("WatchFace.onAmbientModeChanged: " + Boolean.toString(inAmbientMode));

            if (mIsAmbient != inAmbientMode) {
                mIsAmbient = inAmbientMode;

                onWatchModeChanged(getCurrentWatchMode());
                updateTimeAndInvalidate();
                checkTimeUpdater();
            }
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);
            Logr.d("WatchFace.onApplyWindowInsets: " + "isRound=" + Boolean.toString(insets.isRound()));

            mFaceInsets = insets;
            mWatchShape = mFaceInsets.isRound() ? WatchShape.CIRCLE : WatchShape.SQUARE;

            WatchFaceStyle watchFaceStyle = getWatchFaceStyle();
            if(watchFaceStyle != null) {
                setWatchFaceStyle(watchFaceStyle);
            }

            onLayout(mWatchShape, mFaceRect, mFaceInsets);

            // Start the time updater after the first layout is complete.
            if (!mLayoutComplete) {
                mLayoutComplete = true;
                onLayoutCompleted();
                updateTimeAndInvalidate();
                checkTimeUpdater();
            }
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            Logr.v("WatchFace.onTimeTick");

            // only update if layout has completed and time updater not running
            if (mLayoutComplete && !isTimeUpdaterRunning()) {
                updateTimeAndInvalidate();
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            // need to call super, otherwise onPropertiesChanged will not be called
            super.onVisibilityChanged(visible);
            Logr.v("WatchFace.onVisibilityChanged: " + visible);

            if (visible) {
                updateTimeAndInvalidate();
            }
            checkTimeUpdater();
        }

        @Override
        public void onComplicationDataUpdate(int id, ComplicationData data) {
            WatchFace.this.onComplicationDataUpdate(id, data);
        }

        private class TimeFormatObserver extends ContentObserver {
            public TimeFormatObserver(Handler handler) {
                super(handler);
            }

            @Override
            public void onChange(boolean selfChange) {
                updateTimeAndInvalidate();
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                updateTimeAndInvalidate();
            }
        }
    }
}
