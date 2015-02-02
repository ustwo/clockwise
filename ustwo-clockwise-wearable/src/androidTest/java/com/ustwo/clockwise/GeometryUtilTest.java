package com.ustwo.clockwise;

import android.graphics.PointF;

import com.ustwo.clockwise.util.GeometryUtil;

import junit.framework.Assert;
import junit.framework.TestCase;

public class GeometryUtilTest extends TestCase {
    private PointF mCenter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //  Setting up mCenter point for all tests
        mCenter = new PointF(160f, 160f);
    }

    public void testPointOnCircleHourNine() throws Exception {
        //  Getting point of circle for Nine Hour watch and 100px radius.
        PointF pointOnCircle = GeometryUtil.getPointOnCircle(100.0f, 270.0f, mCenter);

        assertTrue((pointOnCircle.x == 60.0f) && (pointOnCircle.y == 160.0f));
    }

    public void testPointOnCircleHourTwelve() throws Exception {
        //  Getting point of circle for Twelve Hour watch and 100px radius.
        PointF pointOnCircle = GeometryUtil.getPointOnCircle(100f, 0.0f, mCenter);

        assertTrue((pointOnCircle.x == 160.0f) && (pointOnCircle.y == 60.0f));
    }

    public void testDistanceInBetweenPointPoints() throws Exception {
        PointF pointOnCircle = new PointF(160f, 60f);

        float distance = GeometryUtil.getDistanceBetweenPoints(mCenter,pointOnCircle);

        assertEquals(100.0f, distance);
    }

    public void testPointOnCircleThrowsNullPointerException() throws Exception {
        try {
            GeometryUtil.getPointOnCircle(100f, 0.0f, null);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException npe) {
            //success
        }
    }

    public void testPointOnCircleThrowsIllegalArgumentException() throws Exception {
        try {
            GeometryUtil.getPointOnCircle(-100f, 0.0f, null);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            //success
        }
    }

    public void testGetLineIntersectionWithYThrowsNullPointerException() throws Exception {
        try {
            GeometryUtil.getLineIntersectionWithY(null, null, 0.0f);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException npe) {
            //success
        }
    }

    public void testGetLineIntersectionWithXThrowsNullPointerException() throws Exception {
        try {
            GeometryUtil.getLineIntersectionWithX(null, null, 0.0f);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException npe) {
            //success
        }
    }

    public void testGetDistanceBetweenPointsThrowsNullPointerException() throws Exception {
        try {
            GeometryUtil.getDistanceBetweenPoints(null, null);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException npe) {
            //success
        }
    }
}
