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
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MultiplePermissionRequestor {
    private static final String TAG = MultiplePermissionRequestor.class.getSimpleName();

    private Context mContext;
    private MultiplePermissionRequestListener mListener;

    private List<String> mWearablePermissionsToRequest = new ArrayList<>();
    private HashMap<String, Boolean> mWearablePermissionResults = new HashMap<>();
    private List<String> mCompanionPermissionsToRequest = new ArrayList<>();
    private HashMap<String, Boolean> mCompanionPermissionResults = new HashMap<>();

    public MultiplePermissionRequestor(Context context, MultiplePermissionRequestListener listener) {

        mContext = context;
        mListener = listener;
    }

    public void request(List<String> wearablePermissionsToRequest, List<String> companionPermissionsToRequest) {
        mWearablePermissionsToRequest = wearablePermissionsToRequest;
        mCompanionPermissionsToRequest = companionPermissionsToRequest;

        requestNextPermission();
    }

    private void requestNextPermission() {
        if(mWearablePermissionsToRequest.size() > 0) {
            final String permission = mWearablePermissionsToRequest.get(0);
            PermissionRequestor pr = new PermissionRequestor(mContext, new PermissionRequestor.PermissionRequestListener() {
                @Override
                public void onPermissionGranted() {
                    Log.v(TAG, "wearable permission granted: " + permission);
                    mWearablePermissionResults.put(permission, true);
                    mWearablePermissionsToRequest.remove(0);
                    requestNextPermission();
                }

                @Override
                public void onPermissionDenied() {
                    Log.v(TAG, "wearable permission denied: " + permission);
                    mWearablePermissionResults.put(permission, false);
                    mWearablePermissionsToRequest.remove(0);
                    requestNextPermission();
                }
            });
            pr.requestPermission(permission, false);
        } else if(mCompanionPermissionsToRequest.size() > 0) {
            final String permission = mCompanionPermissionsToRequest.get(0);
            PermissionRequestor pr = new PermissionRequestor(mContext, new PermissionRequestor.PermissionRequestListener() {
                @Override
                public void onPermissionGranted() {
                    Log.v(TAG, "companion permission granted: " + permission);
                    mCompanionPermissionResults.put(permission, true);
                    mCompanionPermissionsToRequest.remove(0);
                    requestNextPermission();
                }

                @Override
                public void onPermissionDenied() {
                    Log.v(TAG, "companion permission denied: " + permission);
                    mCompanionPermissionResults.put(permission, false);
                    mCompanionPermissionsToRequest.remove(0);
                    requestNextPermission();
                }
            });
            pr.requestPermission(permission, true);
        } else {
            if(mListener != null) {
                Log.v(TAG, "onCompleted");
                mListener.onCompleted(mWearablePermissionResults, mCompanionPermissionResults);
            }
        }
    }

    public interface MultiplePermissionRequestListener {
        void onCompleted(HashMap<String, Boolean> wearableResults, HashMap<String, Boolean> companionResults);
    }
}
