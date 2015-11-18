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

package com.ustwo.clockwise.common.util;

import android.util.Log;

import com.ustwo.clockwise.BuildConfig;

/**
 * A logging class that wraps the Android Log class.
 */
public class Logr {
    private static final String TAG = "UsTwoWatchFaceFramework";

    private static final boolean LOGD = BuildConfig.DEBUG;

    private Logr() {
    }

    /**
     * Send a VERBOSE log message with the '{@value #TAG}' tag.
     *
     * @param message The message you would like logged.
     */
    public static void v(String message) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (LOGD) {
            log(message, Log.VERBOSE);
        }
    }

    /**
     * Send a DEBUG log message with the '{@value #TAG}' tag.
     *
     * @param message The message you would like logged.
     */
    public static void d(String message) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (LOGD) {
            log(message, Log.DEBUG);
        }
    }

    /**
     * Send an INFO log message with the '{@value #TAG}' tag.
     *
     * @param message The message you would like logged.
     */
    public static void i(String message) {
        log(message, Log.INFO);
    }

    /**
     * Send a WARN log message with the '{@value #TAG}' tag.
     *
     * @param message The message you would like logged.
     */
    public static void w(String message) {
        log(message, Log.WARN);
    }

    /**
     * Send an ERROR log message with the '{@value #TAG}' tag.
     *
     * @param message The message you would like logged.
     */
    public static void e(String message) {
        log(message, Log.ERROR);
    }

    private static void log(String message, int level) {
        if (Log.isLoggable(TAG, level)) {
            switch (level) {
                case Log.VERBOSE:
                    Log.v(TAG, message);
                    break;
                case Log.DEBUG:
                    Log.d(TAG, message);
                    break;
                case Log.INFO:
                    Log.i(TAG, message);
                    break;
                case Log.WARN:
                    Log.w(TAG, message);
                    break;
                case Log.ERROR:
                    Log.e(TAG, message);
                    break;
            }
        }
    }
}