package com.ustwo.clockwise.wearable.permissions;

import android.content.Context;
import android.graphics.Color;

import com.ustwo.clockwise.common.permissions.PermissionRequestItem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PermissionsRequest implements Serializable {
    private static final long serialVersionUID = 0L;

    private transient Context mContext;
    private List<PermissionRequestItem> mRequestItems = new ArrayList<>();
    private boolean mRequestSilently;

    public PermissionsRequest() {
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public List<PermissionRequestItem> getRequestItems() {
        return mRequestItems;
    }

    public void setRequestItems(List<PermissionRequestItem> requestItems) {
        mRequestItems = requestItems;
    }

    public boolean shouldRequestSilently() {
        return mRequestSilently;
    }

    public void setRequestSilently(boolean requestSilently) {
        mRequestSilently = requestSilently;
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

    public static PermissionsRequest deserialize(byte[] serialized) {
        PermissionsRequest deserialized = null;
        try {
            ByteArrayInputStream bi = new ByteArrayInputStream(serialized);
            ObjectInputStream si = new ObjectInputStream(bi);
            deserialized = (PermissionsRequest) si.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deserialized;
    }
}
