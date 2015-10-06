package com.ustwo.clockwise.common.permissions;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.ustwo.clockwise.R;
import com.ustwo.clockwise.common.Constants;
import com.ustwo.clockwise.common.WearableAPIHelper;

/**
 * Created by Toffer on 10/5/2015.
 */
public class PermissionInfoActivity extends Activity {

    private WearableAPIHelper mWearableAPIHelper;
    private boolean mAccepted = false;

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

        setContentView(R.layout.permission_info);
        findViewById(R.id.permission_info_imagebutton_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataMap dataMap = new DataMap();
                dataMap.putBoolean(Constants.DATA_KEY_OPEN_ON_PHONE, true);
                dataMap.putLong(Constants.DATA_KEY_TIMESTAMP, System.currentTimeMillis());
                if (mWearableAPIHelper != null) {
                    mWearableAPIHelper.putDataMap(Constants.DATA_PATH_PERMISSION_INFO_RESPONSE, dataMap, null);
                    mAccepted = true;
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!mAccepted) {
            DataMap dataMap = new DataMap();
            dataMap.putBoolean(Constants.DATA_KEY_OPEN_ON_PHONE, false);
            dataMap.putLong(Constants.DATA_KEY_TIMESTAMP, System.currentTimeMillis());
            if (mWearableAPIHelper != null) {
                mWearableAPIHelper.putDataMap(Constants.DATA_PATH_PERMISSION_INFO_RESPONSE, dataMap, null);
            }
        }

        if(null != mWearableAPIHelper) {
            mWearableAPIHelper.onDestroy();
        }
    }
}
