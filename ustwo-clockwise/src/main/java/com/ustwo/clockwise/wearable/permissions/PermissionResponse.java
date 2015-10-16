package com.ustwo.clockwise.wearable.permissions;

import java.util.HashMap;

public class PermissionResponse {
    private HashMap<String, Boolean> mWearablePermissionResults = new HashMap<>();
    private HashMap<String, Boolean> mCompanionPermissionResults = new HashMap<>();

    public HashMap<String, Boolean> getWearablePermissionResults() {
        return mWearablePermissionResults;
    }

    public HashMap<String, Boolean> getCompanionPermissionResults() {
        return mCompanionPermissionResults;
    }
}
