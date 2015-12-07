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
import android.support.v4.content.ContextCompat;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.ustwo.clockwise.common.Constants;
import com.ustwo.clockwise.common.WearableAPIHelper;
import com.ustwo.clockwise.common.permissions.PermissionRequestActivity;
import com.ustwo.clockwise.common.permissions.PermissionRequestItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PermissionsRequestor implements MessageApi.MessageListener {
    private static final String TAG = PermissionsRequestor.class.getSimpleName();

    private Context mContext;
    private WearableAPIHelper mWearableAPIHelper;
    private PermissionsRequest mRequest;
    private PermissionsResponse mResponse;
    private PermissionRequestListener mListener;

    private List<PermissionRequestItem> mWearablePermissionsToRequest = new ArrayList<>();
    private List<PermissionRequestItem> mCompanionPermissionsToRequest = new ArrayList<>();

    private HashMap<String, Boolean> mWearablePermissionResults = new HashMap<>();
    private HashMap<String, Boolean> mCompanionPermissionResults = new HashMap<>();

    public PermissionsRequestor(Context context) {
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

    public void request(PermissionsRequest request, PermissionRequestListener listener) {
        mRequest = request;
        mListener = listener;

        for(PermissionRequestItem requestItem : request.getRequestItems()) {
            if(requestItem.isWearable()) {
                mWearablePermissionsToRequest.add(requestItem);
            }
            else {
                mCompanionPermissionsToRequest.add(requestItem);
            }
        }
        mResponse = new PermissionsResponse();

        requestNextPermission();
    }

    private void requestNextPermission() {
        if(mWearablePermissionsToRequest.size() > 0) {
            requestNextWearablePermission();
        } else if(mCompanionPermissionsToRequest.size() > 0) {
            requestCompanionPermissions(true);
        } else {
            checkComplete();
        }
    }

    private void requestNextWearablePermission() {
        if(mWearablePermissionsToRequest.size() > 0) {
            PermissionRequestItem request = mWearablePermissionsToRequest.get(0);
            String permission = request.getPermissions().get(0);
            mWearablePermissionsToRequest.remove(request);

            if (mRequest.shouldRequestSilently()) {
                if (ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED) {
                    handleWearablePermissionGranted(permission);

                } else {
                    handleWearablePermissionDenied(permission);
                }
            } else {
                if (ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED) {
                    handleWearablePermissionGranted(permission);
                } else {
                    Intent i = new Intent(mContext.getApplicationContext(), PermissionRequestActivity.class);
                    i.putExtra(PermissionRequestActivity.EXTRA_PERMISSION_REQUEST, mRequest.serialize());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                }
            }
        } else if(mCompanionPermissionsToRequest.size() > 0) {
            requestCompanionPermissions(true);
        } else {
            checkComplete();
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
        Intent i = new Intent(mContext.getApplicationContext(), WearablePermissionEducationActivity.class);
        i.putExtra(WearablePermissionEducationActivity.EXTRA_PERMISSION_REQUEST, mRequest.serialize());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
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
        requestNextWearablePermission();
    }

    private void handleWearablePermissionDenied(String permission) {
        mResponse.getWearablePermissionResults().put(permission, false);
        requestNextWearablePermission();
    }

    private void handleCompanionPermissionGranted(String permission) {
        mResponse.getCompanionPermissionResults().put(permission, true);
        if(checkReceivedAllResponsesForPermissionRequestItem(permission)) {
            mCompanionPermissionsToRequest.remove(getPermissionRequestItemForPermission(permission));
            checkComplete();
        }
    }

    private void handleCompanionPermissionDenied(String permission) {
        mResponse.getCompanionPermissionResults().put(permission, false);
        if(checkReceivedAllResponsesForPermissionRequestItem(permission)) {
            mCompanionPermissionsToRequest.remove(getPermissionRequestItemForPermission(permission));
            checkComplete();
        }
    }

    private boolean checkReceivedAllResponsesForPermissionRequestItem(String permission) {
        PermissionRequestItem requestForResponse = getPermissionRequestItemForPermission(permission);
        boolean receivedAllRequestResponses = true;
        if(requestForResponse != null) {
            for (String requestPermission : requestForResponse.getPermissions()) {
                if (!mResponse.getCompanionPermissionResults().containsKey(requestPermission)) {
                    receivedAllRequestResponses = false;
                    break;
                }
            }
        } else {
            receivedAllRequestResponses = false;
        }
        return (requestForResponse != null && receivedAllRequestResponses);
    }

    private PermissionRequestItem getPermissionRequestItemForPermission(String permission) {
        PermissionRequestItem requestForResponse = null;
        for(PermissionRequestItem requestItem : mCompanionPermissionsToRequest) {
            if (requestItem.getPermissions().contains(permission)) {
                requestForResponse = requestItem;
                break;
            }
        }
        return requestForResponse;
    }

    private void acceptAllCompanionPermissionsAndComplete() {
        for(PermissionRequestItem requestItem : mCompanionPermissionsToRequest) {
            for(String companionPermission : requestItem.getPermissions()) {
                if (!mResponse.getCompanionPermissionResults().containsKey(companionPermission)) {
                    mResponse.getCompanionPermissionResults().put(companionPermission, true);
                }
            }
        }
        mCompanionPermissionsToRequest.clear();
        checkComplete();
    }

    private void denyAllCompanionPermissionsAndComplete() {
        for(PermissionRequestItem requestItem : mCompanionPermissionsToRequest) {
            for(String companionPermission : requestItem.getPermissions()) {
                if (!mResponse.getCompanionPermissionResults().containsKey(companionPermission)) {
                    mResponse.getCompanionPermissionResults().put(companionPermission, false);
                }
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
        void onCompleted(PermissionsResponse response);
    }
}
