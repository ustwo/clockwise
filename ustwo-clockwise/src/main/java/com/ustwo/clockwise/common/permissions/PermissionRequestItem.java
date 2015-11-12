package com.ustwo.clockwise.common.permissions;

import android.graphics.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PermissionRequestItem implements Serializable {
    private static final long serialVersionUID = 0L;

    private List<String> mPermissions = new ArrayList<>();
    private boolean mIsWearable = true;
    private int mEducationDarkBackgroundColor = Color.BLACK;
    private int mEducationLightBackgroundColor = Color.BLACK;
    private int mEducationTextColor = Color.WHITE;
    private String mWearableEducationText = "";
    private String mCompanionEducationPrimaryText = "";
    private String mCompanionEducationSecondaryText = "";
    private String mCompanionEducationImageResource = "";
    private String mCompanionEducationImageResourcePackage = "";

    public PermissionRequestItem() {
    }

    public List<String> getPermissions() {
        return mPermissions;
    }

    public void setPermissions(List<String> permissions) {
        mPermissions = permissions;
    }

    public boolean isWearable() {
        return mIsWearable;
    }

    public void setIsWearable(boolean isWearable) {
        mIsWearable = isWearable;
    }

    public int getEducationDarkBackgroundColor() {
        return mEducationDarkBackgroundColor;
    }

    public void setEducationDarkBackgroundColor(int educationDarkBackgroundColor) {
        mEducationDarkBackgroundColor = educationDarkBackgroundColor;
    }

    public int getEducationLightBackgroundColor() {
        return mEducationLightBackgroundColor;
    }

    public void setEducationLightBackgroundColor(int educationLightBackgroundColor) {
        mEducationLightBackgroundColor = educationLightBackgroundColor;
    }

    public int getEducationTextColor() {
        return mEducationTextColor;
    }

    public void setEducationTextColor(int educationTextColor) {
        mEducationTextColor = educationTextColor;
    }

    public String getWearableEducationText() {
        return mWearableEducationText;
    }

    public void setWearableEducationText(String wearableEducationText) {
        mWearableEducationText = wearableEducationText;
    }

    public String getCompanionEducationPrimaryText() {
        return mCompanionEducationPrimaryText;
    }

    public void setCompanionEducationPrimaryText(String companionEducationPrimaryText) {
        mCompanionEducationPrimaryText = companionEducationPrimaryText;
    }

    public String getCompanionEducationSecondaryText() {
        return mCompanionEducationSecondaryText;
    }

    public void setCompanionEducationSecondaryText(String companionEducationSecondaryText) {
        mCompanionEducationSecondaryText = companionEducationSecondaryText;
    }

    public String getCompanionEducationImageResource() {
        return mCompanionEducationImageResource;
    }

    public void setCompanionEducationImageResource(String companionEducationImageResource) {
        mCompanionEducationImageResource = companionEducationImageResource;
    }

    public String getCompanionEducationImageResourcePackage() {
        return mCompanionEducationImageResourcePackage;
    }

    public void setCompanionEducationImageResourcePackage(String companionEducationImageResourcePackage) {
        mCompanionEducationImageResourcePackage = companionEducationImageResourcePackage;
    }
}
