package com.ustwo.clockwise.common.permissions;

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
    private int mResourceId;

    public EducationalObject(String educationalTextWearable, String educationalText1Companion,
            String educationalText2Companion, int bgColor, int textColor, int resourceId) {

        mEducationalTextWearable = educationalTextWearable;
        mEducationalText1Companion = educationalText1Companion;
        mEducationalText2Companion = educationalText2Companion;
        mBackgroundColor = bgColor;
        mTextColor = textColor;
        mResourceId = resourceId;
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

    public int getResourceId() {
        return mResourceId;
    }
}

