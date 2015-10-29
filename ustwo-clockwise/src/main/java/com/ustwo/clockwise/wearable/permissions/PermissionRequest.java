package com.ustwo.clockwise.wearable.permissions;

import android.content.Context;
import android.graphics.Color;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PermissionRequest implements Serializable {
    private static final long serialVersionUID = 0L;

    private final transient Context context;
    private final List<String> wearablePermissions;
    private final List<String> companionPermissions;
    private final int educationBackgroundColor;
    private final int educationTextColor;
    private final String wearableEducationText;
    private final String companionEducationPrimaryText;
    private final String companionEducationSecondaryText;
    private final String companionEducationImageResource;
    private final String companionEducationImageResourcePackage;
    private final boolean requestSilently;

    public static class PermissionRequestBuilder {
        private transient Context context = null;
        private List<String> wearablePermissions = new ArrayList<>();
        private List<String> companionPermissions = new ArrayList<>();
        private String wearableEducationText = null;
        private int educationBackgroundColor = Color.BLACK;
        private int educationTextColor = Color.WHITE;
        private String companionEducationPrimaryText = null;
        private String companionEducationSecondaryText = null;
        private String companionEducationImageResource = null;
        private String companionEducationImageResourcePackage = null;
        private boolean requestSilently = false;

        public PermissionRequestBuilder(Context context) {
            this.context = context;
        }

        public PermissionRequestBuilder addWearablePermission(String permission) {
            this.wearablePermissions.add(permission);
            return this;
        }

        public PermissionRequestBuilder addCompanionPermission(String permission) {
            this.companionPermissions.add(permission);
            return this;
        }

        public PermissionRequestBuilder setWearableEducationText(String wearableEducationText) {
            this.wearableEducationText = wearableEducationText;
            return this;
        }

        public PermissionRequestBuilder setEducationBackgroundColor(int wearableEducationBackgroundColor) {
            this.educationBackgroundColor = wearableEducationBackgroundColor;
            return this;
        }

        public PermissionRequestBuilder setEducationTextColor(int educationTextColor) {
            this.educationTextColor = educationTextColor;
            return this;
        }

        public PermissionRequestBuilder setCompanionEducationPrimaryText(String companionEducationPrimaryText) {
            this.companionEducationPrimaryText = companionEducationPrimaryText;
            return this;
        }

        public PermissionRequestBuilder setCompanionEducationSecondaryText(String companionEducationSecondaryText) {
            this.companionEducationSecondaryText = companionEducationSecondaryText;
            return this;
        }

        public PermissionRequestBuilder setCompanionEducationImageResource(String resourceName, String packageName) {
            this.companionEducationImageResource = resourceName;
            this.companionEducationImageResourcePackage = packageName;
            return this;
        }

        public PermissionRequestBuilder setRequestSilently(boolean requestSilently) {
            this.requestSilently = requestSilently;
            return this;
        }

        public PermissionRequest build() {
            return new PermissionRequest(this);
        }
    }

    private PermissionRequest(PermissionRequestBuilder builder) {
        this.context = builder.context;
        this.wearablePermissions = builder.wearablePermissions;
        this.companionPermissions = builder.companionPermissions;
        this.wearableEducationText = builder.wearableEducationText;
        this.educationBackgroundColor = builder.educationBackgroundColor;
        this.educationTextColor = builder.educationTextColor;
        this.companionEducationPrimaryText = builder.companionEducationPrimaryText;
        this.companionEducationSecondaryText = builder.companionEducationSecondaryText;
        this.companionEducationImageResource = builder.companionEducationImageResource;
        this.companionEducationImageResourcePackage = builder.companionEducationImageResourcePackage;
        this.requestSilently = builder.requestSilently;
    }

    public Context getContext() {
        return context;
    }

    public List<String> getWearablePermissions() {
        return wearablePermissions;
    }

    public List<String> getCompanionPermissions() {
        return companionPermissions;
    }

    public String getWearableEducationText() {
        return wearableEducationText;
    }

    public int getEducationBackgroundColor() {
        return educationBackgroundColor;
    }

    public int getEducationTextColor() {
        return educationTextColor;
    }

    public String getCompanionEducationPrimaryText() {
        return companionEducationPrimaryText;
    }

    public String getCompanionEducationSecondaryText() {
        return companionEducationSecondaryText;
    }

    public String getCompanionEducationImageResource() {
        return companionEducationImageResource;
    }

    public String getCompanionEducationImageResourcePackage() {
        return companionEducationImageResourcePackage;
    }

    public boolean shouldRequestSilently() {
        return requestSilently;
    }

    public byte[] serialize() {
        byte[] serialized = null;
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(this);
            so.flush();
            serialized = bo.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serialized;
    }

    public static PermissionRequest deserialize(byte[] serialized) {
        PermissionRequest deserialized = null;
        try {
            ByteArrayInputStream bi = new ByteArrayInputStream(serialized);
            ObjectInputStream si = new ObjectInputStream(bi);
            deserialized = (PermissionRequest) si.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deserialized;
    }
}
