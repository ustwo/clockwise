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

package com.ustwo.clockwise.wearable.permissions;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.ustwo.clockwise.common.Constants;
import com.ustwo.clockwise.common.WearableAPIHelper;
import com.ustwo.clockwise.common.permissions.EducationalObject;
import com.ustwo.clockwise.common.permissions.PermissionInfoActivity;
import com.ustwo.clockwise.common.permissions.PermissionRequestActivity;

public class PermissionRequestor implements DataApi.DataListener {
    private WearableAPIHelper mWearableAPIHelper;
    private Context mContext;
    private PermissionRequestListener mListener;
    private String mPermission;
    private EducationalObject mEducationalObject;

    public PermissionRequestor(Context context, PermissionRequestListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void requestPermission(final String permission, final boolean onPhone) {
        requestPermission(permission, onPhone, null);
    }

    public void requestPermission(final String permission, final boolean onPhone, EducationalObject eo) {
        mPermission = permission;
        mEducationalObject = eo;

        mWearableAPIHelper = new WearableAPIHelper(mContext, new WearableAPIHelper.WearableAPIHelperListener() {
            @Override
            public void onWearableAPIConnected(GoogleApiClient apiClient) {
                if(onPhone) {
                    doRequestCompanionPermission(mPermission, true);
                } else {
                    doRequestWearablePermission(mPermission);
                }
            }

            @Override
            public void onWearableAPIConnectionSuspended(int cause) {
            }

            @Override
            public void onWearableAPIConnectionFailed(ConnectionResult result) {
            }
        });

        Wearable.DataApi.addListener(mWearableAPIHelper.getGoogleApiClient(), this);
    }

    private void showWearableEducationalScreen() {
        Intent i = new Intent(mContext.getApplicationContext(), PermissionInfoActivity.class);
        i.putExtra(PermissionInfoActivity.EXTRA_BG_COLOR, mEducationalObject.getBackgroundColor());
        i.putExtra(PermissionInfoActivity.EXTRA_TEXT_COLOR, mEducationalObject.getTextColor());
        i.putExtra(PermissionInfoActivity.EXTRA_MESSAGE, mEducationalObject.getEducationalTextWearable());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.getApplicationContext().startActivity(i);
    }

    private void doRequestCompanionPermission(final String permission, final boolean justChecking) {
        DataMap dataMap = new DataMap();
        dataMap.putString(Constants.DATA_KEY_PERMISSION, permission);
        dataMap.putBoolean(Constants.DATA_KEY_JUST_CHECKING, justChecking);
        if(null != mEducationalObject) {
            dataMap.putString(Constants.DATA_KEY_EDUCATIONAL_TEXT1, mEducationalObject.getEducationalText1Companion());
            dataMap.putString(Constants.DATA_KEY_EDUCATIONAL_TEXT2, mEducationalObject.getEducationalText2Companion());
            dataMap.putInt(Constants.DATA_KEY_EDUCATIONAL_BG_COLOR, mEducationalObject.getBackgroundColor());
            dataMap.putInt(Constants.DATA_KEY_EDUCATIONAL_TEXT_COLOR, mEducationalObject.getTextColor());
            dataMap.putStringArray(Constants.DATA_KEY_EDUCATIONAL_RESOURCE_ID, mEducationalObject.getResource());
        }
        dataMap.putLong(Constants.DATA_KEY_TIMESTAMP, System.currentTimeMillis());
        mWearableAPIHelper.putMessage(Constants.DATA_PATH_COMPANION_PERMISSION_REQUEST, dataMap.toByteArray(), null);
    }

    private void doRequestWearablePermission(final String permission) {
        if(ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED) {
            handlePermissionGranted();
        } else {
            Intent i = new Intent(mContext.getApplicationContext(), PermissionRequestActivity.class);
            i.putExtra(PermissionRequestActivity.EXTRA_RESPONSE_DATA_PATH, Constants.DATA_PATH_WEARABLE_PERMISSION_RESPONSE);
            i.putExtra(PermissionRequestActivity.EXTRA_PERMISSIONS, new String[] { permission });
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.getApplicationContext().startActivity(i);
        }
    }

    private void killApiClient() {
        if(mWearableAPIHelper != null) {
            Wearable.DataApi.removeListener(mWearableAPIHelper.getGoogleApiClient(), PermissionRequestor.this);
            mWearableAPIHelper.onDestroy();
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            Uri uri = event.getDataItem().getUri();
            boolean wearableResponse = uri.getPath().endsWith(Constants.DATA_PATH_WEARABLE_PERMISSION_RESPONSE);
            boolean companionResponse = uri.getPath().endsWith(Constants.DATA_PATH_COMPANION_PERMISSION_RESPONSE);
            if(wearableResponse || companionResponse) {
                DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                boolean granted = dataMap.getBoolean(Constants.DATA_KEY_PERMISSION, false);
                System.out.println(">>>>> PERMISSION RESPONSE - " + granted);
                if(granted) {
                    handlePermissionGranted();
                } else {
                    handlePermissionDenied();
                }
                killApiClient();
            } else if(uri.getPath().endsWith(Constants.DATA_PATH_INSTANT_COMPANION_PERMISSION_RESPONSE)) {
                DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                boolean granted = dataMap.getBoolean(Constants.DATA_KEY_PERMISSION, false);
                System.out.println(">>>>> INSTANT PERMISSION RESPONSE - " + granted);
                if(!granted) {
                    showWearableEducationalScreen();
                }
            } else if(uri.getPath().endsWith(Constants.DATA_PATH_PERMISSION_INFO_RESPONSE)) {
                DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                boolean openOnPhone = dataMap.getBoolean(Constants.DATA_KEY_OPEN_ON_PHONE, false);
                System.out.println(">>>>> PERMISSION INFO RESPONSE - " + openOnPhone);
                if(openOnPhone) {
                    doRequestCompanionPermission(mPermission, false);
                } else {
                    handlePermissionDenied();
                }
            }
        }
    }

    private void handlePermissionGranted() {
        if(mListener != null) {
            mListener.onPermissionGranted();
        }
    }

    private void handlePermissionDenied() {
        if(mListener != null) {
            mListener.onPermissionDenied();
        }
    }

    public interface PermissionRequestListener {
        void onPermissionGranted();
        void onPermissionDenied();
    }
}
