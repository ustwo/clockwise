package com.ustwo.clockwise.common.permissions;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by Toffer on 10/8/2015.
 */
public class EducationalObject implements Serializable {
    private static final long serialVersionUID = 0L;

    private String mEducationalTextWearable;
    private String mEducationalText1Companion;
    private String mEducationalText2Companion;
    private int mBackgroundColor;
    private int mTextColor;
    private String[] mResource;

    public EducationalObject(String educationalTextWearable, String educationalText1Companion,
            String educationalText2Companion, int bgColor, int textColor, String[] resource) {

        mEducationalTextWearable = educationalTextWearable;
        mEducationalText1Companion = educationalText1Companion;
        mEducationalText2Companion = educationalText2Companion;
        mBackgroundColor = bgColor;
        mTextColor = textColor;
        if(null != resource && resource.length != 2) {
            mResource = null;
            Log.w(EducationalObject.class.getSimpleName(), "The resource argument must be either null or have lenght 2. First string is the resource name and the second the package where it's located.");
        } else {
            mResource = resource;
        }
    }

    public String getEducationalTextWearable() {
        return mEducationalTextWearable;
    }

    public String getEducationalText1Companion() {
        return mEducationalText1Companion;
    }

    public String getEducationalText2Companion() {
        return mEducationalText2Companion;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public String[] getResource() {
        return mResource;
    }
}

