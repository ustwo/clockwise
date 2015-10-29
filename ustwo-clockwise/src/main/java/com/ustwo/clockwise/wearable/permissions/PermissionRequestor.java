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

import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.ustwo.clockwise.common.Constants;
import com.ustwo.clockwise.common.WearableAPIHelper;
import com.ustwo.clockwise.common.permissions.PermissionRequestActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PermissionRequestor implements MessageApi.MessageListener {
    private static final String TAG = PermissionRequestor.class.getSimpleName();

    private Context mContext;
    private WearableAPIHelper mWearableAPIHelper;
    private PermissionRequest mRequest;
    private PermissionResponse mResponse;
    private PermissionRequestListener mListener;

    private List<String> mWearablePermissionsToRequest = new ArrayList<>();
    private List<String> mCompanionPermissionsToRequest = new ArrayList<>();

    private HashMap<String, Boolean> mCompanionPermissionResults = new HashMap<>();

    public PermissionRequestor(Context context) {
        mContext = context;

        mWearableAPIHelper = new WearableAPIHelper(mContext, null);
        Wearable.MessageApi.addListener(mWearableAPIHelper.getGoogleApiClient(), this);
    }

    private void killApiClient() {
        if(mWearableAPIHelper != null) {
            Wearable.MessageApi.removeListener(mWearableAPIHelper.getGoogleApiClient(), this);
            mWearableAPIHelper.onDestroy();
        }
    }

    public void request(PermissionRequest request, PermissionRequestListener listener) {
        mRequest = request;
        mListener = listener;

        mWearablePermissionsToRequest = new ArrayList<>(mRequest.getWearablePermissions());
        mCompanionPermissionsToRequest = new ArrayList<>(mRequest.getCompanionPermissions());
        mResponse = new PermissionResponse();

        requestNextPermission();
    }

    private void requestNextPermission() {
        if(mWearablePermissionsToRequest.size() > 0) {
            final String permission = mWearablePermissionsToRequest.get(0);
            requestWearablePermission(permission);
        } else if(mCompanionPermissionsToRequest.size() > 0) {
            requestCompanionPermissions(true);
        } else {
            checkComplete();
        }
    }

    private void requestWearablePermission(String permission) {
        if(mRequest.shouldRequestSilently()) {
            if(ContextCompat.checkSelfPermission(mRequest.getContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                handleWearablePermissionGranted(permission);
            } else {
                handleWearablePermissionDenied(permission);
            }
        } else {
            if(ContextCompat.checkSelfPermission(mRequest.getContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                handleWearablePermissionGranted(permission);
            } else {
                Intent i = new Intent(mRequest.getContext().getApplicationContext(), PermissionRequestActivity.class);
                i.putExtra(PermissionRequestActivity.EXTRA_WEARABLE_PERMISSION, permission);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mRequest.getContext().getApplicationContext().startActivity(i);
            }
        }
    }

    private void requestCompanionPermissions(final boolean justChecking) {
        if(mWearableAPIHelper != null) {
            DataMap dataMap = new DataMap();
            dataMap.putBoolean(Constants.DATA_KEY_JUST_CHECKING, justChecking);
            dataMap.putByteArray(Constants.DATA_KEY_PERMISSION_REQUEST, mRequest.serialize());

            dataMap.putLong(Constants.DATA_KEY_TIMESTAMP, System.currentTimeMillis());
            mWearableAPIHelper.putMessage(Constants.DATA_PATH_COMPANION_PERMISSION_REQUEST, dataMap.toByteArray(), null);
        }
    }


    private void showWearableEducationalScreen() {
        Intent i = new Intent(mRequest.getContext().getApplicationContext(), WearablePermissionEducationActivity.class);
        i.putExtra(WearablePermissionEducationActivity.EXTRA_PERMISSION_REQUEST, mRequest.serialize());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mRequest.getContext().getApplicationContext().startActivity(i);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        byte[] rawData = messageEvent.getData();
        DataMap dataMap = DataMap.fromByteArray(rawData);

        if(messageEvent.getPath().endsWith(Constants.DATA_PATH_WEARABLE_PERMISSION_RESPONSE)) {
            String permissionName = dataMap.getString(Constants.DATA_KEY_WEARABLE_PERMISSION);
            boolean granted = dataMap.getBoolean(Constants.DATA_KEY_WEARABLE_PERMISSION_GRANTED, false);
            if (granted)
                handleWearablePermissionGranted(permissionName);
            else
                handleWearablePermissionDenied(permissionName);
        } else if(messageEvent.getPath().endsWith(Constants.DATA_PATH_COMPANION_PERMISSION_RESPONSE)) {
            ArrayList<DataMap> resultMaps = dataMap.getDataMapArrayList(Constants.DATA_KEY_COMPANION_PERMISSION_RESULTS);
            mCompanionPermissionResults.clear();
            for(DataMap resultMap : resultMaps) {
                String permissionName = resultMap.getString(Constants.DATA_KEY_COMPANION_PERMISSION);
                boolean granted = resultMap.getBoolean(Constants.DATA_KEY_COMPANION_PERMISSION_GRANTED, false);
                mCompanionPermissionResults.put(permissionName, granted);
                if (granted)
                    handleCompanionPermissionGranted(permissionName);
                else
                    handleCompanionPermissionDenied(permissionName);
            }
        } else if(messageEvent.getPath().endsWith(Constants.DATA_PATH_INSTANT_COMPANION_PERMISSION_RESPONSE)) {
            ArrayList<DataMap> resultMaps = dataMap.getDataMapArrayList(Constants.DATA_KEY_COMPANION_PERMISSION_RESULTS);
            boolean requestCompanionPermissions = false;
            mCompanionPermissionResults.clear();
            for(DataMap resultMap : resultMaps) {
                String permissionName = resultMap.getString(Constants.DATA_KEY_COMPANION_PERMISSION);
                boolean granted = resultMap.getBoolean(Constants.DATA_KEY_COMPANION_PERMISSION_GRANTED, false);
                mCompanionPermissionResults.put(permissionName, granted);
                if (!granted) {
                    requestCompanionPermissions = true;
                }
            }
            if(requestCompanionPermissions) {
                showWearableEducationalScreen();
            } else {
                acceptAllCompanionPermissionsAndComplete();
            }
        } else if(messageEvent.getPath().endsWith(Constants.DATA_PATH_PERMISSION_INFO_RESPONSE)) {
            boolean openOnPhone = dataMap.getBoolean(Constants.DATA_KEY_OPEN_ON_PHONE, false);
            if(openOnPhone) {
                requestCompanionPermissions(false);
            } else {
                denyAllCompanionPermissionsAndComplete();
            }
        }
    }

    private void handleWearablePermissionGranted(String permission) {
        mResponse.getWearablePermissionResults().put(permission, true);
        mWearablePermissionsToRequest.remove(permission);
        requestNextPermission();
    }

    private void handleWearablePermissionDenied(String permission) {
        mResponse.getWearablePermissionResults().put(permission, false);
        mWearablePermissionsToRequest.remove(permission);
        requestNextPermission();
    }

    private void handleCompanionPermissionGranted(String permission) {
        mResponse.getCompanionPermissionResults().put(permission, true);
        mCompanionPermissionsToRequest.remove(permission);
        checkComplete();
    }

    private void handleCompanionPermissionDenied(String permission) {
        mResponse.getCompanionPermissionResults().put(permission, false);
        mCompanionPermissionsToRequest.remove(permission);
        checkComplete();
    }

    private void acceptAllCompanionPermissionsAndComplete() {
        for(String permission : mCompanionPermissionsToRequest) {
            if(!mResponse.getCompanionPermissionResults().containsKey(permission)) {
                mResponse.getCompanionPermissionResults().put(permission, true);
            }
        }
        mCompanionPermissionsToRequest.clear();
        checkComplete();
    }

    private void denyAllCompanionPermissionsAndComplete() {
        for(String permission : mCompanionPermissionsToRequest) {
            if(!mResponse.getCompanionPermissionResults().containsKey(permission)) {
                mResponse.getCompanionPermissionResults().put(permission, false);
            }
        }
        mCompanionPermissionsToRequest.clear();
        checkComplete();
    }

    private void checkComplete() {
        if(mWearablePermissionsToRequest.size() == 0 && mCompanionPermissionsToRequest.size() == 0) {
            if(mListener != null) {
                killApiClient();
                
                mListener.onCompleted(mResponse);
                mListener = null;
            }
        }
    }

    public interface PermissionRequestListener {
        void onCompleted(PermissionResponse response);
    }
}
