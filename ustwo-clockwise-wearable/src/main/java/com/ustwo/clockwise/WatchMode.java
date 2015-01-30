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

/**
 * Modes in which the watch face can appear.
 * <p/>
 * <li>{@link #INTERACTIVE}</li>
 * <li>{@link #AMBIENT}</li>
 * <li>{@link #LOW_BIT}</li>
 * <li>{@link #BURN_IN}</li>
 * <li>{@link #LOW_BIT_BURN_IN}</li>
 */
public enum WatchMode {
    /**
     * When the user moves their wrist to glance at their watch, the watch face enters
     * Interactive mode. Full color and fluid animation is permitted.
     */
    INTERACTIVE,

    /**
     * Ambient mode helps the device conserve power. The watch face should only
     * display shades of gray, black, and white and update once per minute.
     */
    AMBIENT,

    /**
     * Low-Bit mode is a type of Ambient mode and is based on hardware considerations
     * (e.g. OLED, transflective LED screens). The watch face should only use black and white, avoid
     * grayscale colors, and disable anti-aliasing in paint styles.
     */
    LOW_BIT,

    /**
     * Burn-In Protection mode is a type of Ambient mode and is based on hardware considerations
     * (e.g. OLED screens). The watch face should not use large blocks of non-black pixels while keeping
     * ~95% of all pixels black.
     */
    BURN_IN,

    /**
     * Low-Bit + Burn-In Protection mode is a type of Ambient mode that is the combination of
     * Low-Bit and Burn-In Protection modes.
     */
    LOW_BIT_BURN_IN
}
