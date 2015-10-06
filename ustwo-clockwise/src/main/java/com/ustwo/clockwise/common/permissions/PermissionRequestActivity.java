package com.ustwo.clockwise.common.permissions;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.ustwo.clockwise.common.Constants;
import com.ustwo.clockwise.common.WearableAPIHelper;

public class PermissionRequestActivity extends Activity {
    public static final String EXTRA_RESPONSE_DATA_PATH = "extra_response_data_path";
    public static final String EXTRA_PERMISSIONS = "extra_permissions";
    public static final String EXTRA_JUST_CHECKING = "extra_just_checking";

    private static final int REQUEST_CODE = 21;

    private WearableAPIHelper mWearableAPIHelper;
    private String mResponseDataPath;
    private String[] mPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWearableAPIHelper = new WearableAPIHelper(this, new WearableAPIHelper.WearableAPIHelperListener() {
            @Override
            public void onWearableAPIConnected(GoogleApiClient apiClient) {
            }

            @Override
            public void onWearableAPIConnectionSuspended(int cause) {
            }

            @Override
            public void onWearableAPIConnectionFailed(ConnectionResult result) {
            }
        });

        mResponseDataPath = getIntent().getStringExtra(EXTRA_RESPONSE_DATA_PATH);
        mPermissions = getIntent().getStringArrayExtra(EXTRA_PERMISSIONS);
        if(null != mResponseDataPath && null != mPermissions && mPermissions.length > 0) {
            if(ContextCompat.checkSelfPermission(this, mPermissions[0]) == PackageManager.PERMISSION_GRANTED) {
                // TODO: mWearableAPIHelper might not be connected yet.
                sendResponse(mResponseDataPath, true);
            } else if(getIntent().getBooleanExtra(EXTRA_JUST_CHECKING, true)) {
                // TODO: mWearableAPIHelper might not be connected yet.
                sendResponse(Constants.DATA_PATH_INSTANT_COMPANION_PERMISSION_RESPONSE, false);
            } else {
                ActivityCompat.requestPermissions(this, mPermissions, REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(null != mWearableAPIHelper) {
            mWearableAPIHelper.onDestroy();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == REQUEST_CODE) {
            sendResponse(mResponseDataPath, grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
    }

    private void sendResponse(String responsePath, boolean granted) {
        DataMap dataMap = new DataMap();
        dataMap.putBoolean(Constants.DATA_KEY_PERMISSION, granted);
        dataMap.putLong(Constants.DATA_KEY_TIMESTAMP, System.currentTimeMillis());
        if (mWearableAPIHelper != null) {
            mWearableAPIHelper.putDataMap(responsePath, dataMap, null);
        }
        finish();
    }
}

