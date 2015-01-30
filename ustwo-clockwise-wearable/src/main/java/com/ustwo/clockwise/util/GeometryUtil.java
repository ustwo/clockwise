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

import android.graphics.PointF;

/**
 * Auxiliary utilities related to standard geometry to help drawing on a watch face.
 */
public class GeometryUtil {
    public static final int DEGREES_IN_CIRCLE = 360;

    /**
     * Given a center point and radius of a circle, update a point on the circle at the given angle.
     * Due north of the center point (x=0) is 0 degrees and increasing the degree value will move
     * the point clockwise around the circle.<br><br>
     *
     * Throws IllegalArgumentException if radius < 0.
     *
     * @param pointToUpdate Point on circle updated with x and y values
     * @param radius        Radius of circle
     * @param angleDegrees  Angle from center (in degrees)
     * @param center        Center point of circle
     */
    public static void updatePointOnCircle(PointF pointToUpdate, float radius, double angleDegrees, PointF center) {
        if(radius < 0) {
            throw new IllegalArgumentException("Radius cannot be < 0.");
        }
        double angleRadians = Math.toRadians(angleDegrees);
        float x = (float) (radius * Math.sin(angleRadians) + center.x);
        float y = (float) (radius * -Math.cos(angleRadians) + center.y);
        pointToUpdate.set(x, y);
    }

    /**
     * Given a center point and radius of a circle, return a new point on the circle at the given angle.
     * Due north of the center point (x=0) is 0 degrees and increasing the degree value will move
     * the point clockwise around the circle.<br><br>
     *
     * Throws IllegalArgumentException if radius < 0.
     *
     * @param radius        Radius of circle
     * @param angleDegrees  Angle from center (in degrees)
     * @param center        Center point of circle
     * @return              New point on circle
     */
    public static PointF getPointOnCircle(float radius, double angleDegrees, PointF center) {
        PointF newPoint = new PointF();
        updatePointOnCircle(newPoint, radius, angleDegrees, center);
        return newPoint;
    }

    /**
     * Given two points, return the point at which this line will cross the given y-intercept
     * on a 2D coordinate system.
     *
     * @param linePoint1    First point on line
     * @param linePoint2    Second point on line
     * @param y             Y-intercept
     * @return              New point where line crosses y
     */
    public static PointF getLineIntersectionWithY(PointF linePoint1, PointF linePoint2, float y) {
        if ((linePoint2.y == linePoint1.y)) {
            return null;
        } else {
            float x = 0;
            if (linePoint2.x != linePoint1.x) {
                float m = (linePoint2.y - linePoint1.y) / (linePoint2.x - linePoint1.x);
                x = ((y - linePoint1.y) / m) + linePoint1.x;
            } else {
                x = linePoint2.x;
            }
            return new PointF(x, y);
        }
    }

    /**
     * Given two points, return the point at which this line will cross the given x-intercept
     * on a 2D coordinate system.
     *
     * @param linePoint1    First point on line
     * @param linePoint2    Second point on line
     * @param x             X-intercept
     * @return              New point where line crosses x
     */
    public static PointF getLineIntersectionWithX(PointF linePoint1, PointF linePoint2, float x) {
        if ((linePoint2.x == linePoint1.x)) {
            return null;
        } else {
            float y = 0;
            if (linePoint2.y != linePoint1.y) {
                float m = (linePoint2.y - linePoint1.y) / (linePoint2.x - linePoint1.x);
                y = (m * (x - linePoint1.x)) + linePoint1.y;
            } else {
                y = linePoint2.y;
            }
            return new PointF(x, y);
        }
    }

    /**
     * Given two points, return the distance between them on a 2D coordinate system.
     *
     * @param point1    First point on line
     * @param point2    Second point on line
     * @return          Distance between point1 and point2
     */
    public static float getDistanceBetweenPoints(PointF point1, PointF point2) {
        return (float) Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2));
    }
}
