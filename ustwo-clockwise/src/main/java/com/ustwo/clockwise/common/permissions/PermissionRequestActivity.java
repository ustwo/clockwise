package com.ustwo.clockwise.common.permissions;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.ustwo.clockwise.R;
import com.ustwo.clockwise.common.Constants;
import com.ustwo.clockwise.common.WearableAPIHelper;

public class PermissionRequestActivity extends Activity {
    public static final String EXTRA_RESPONSE_DATA_PATH = "extra_response_data_path";
    public static final String EXTRA_PERMISSIONS = "extra_permissions";
    public static final String EXTRA_JUST_CHECKING = "extra_just_checking";
    public static final String EXTRA_EDUCATIONAL_OBJECT = "extra_educational_object";

    private static final int REQUEST_CODE = 21;

    private WearableAPIHelper mWearableAPIHelper;
    private String mResponseDataPath;
    private String[] mPermissions;

    private boolean mResponseSent = false;

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
        EducationalObject eo = (EducationalObject)getIntent().getSerializableExtra(EXTRA_EDUCATIONAL_OBJECT);
        if(null != mResponseDataPath && null != mPermissions && mPermissions.length > 0) {
            if(ContextCompat.checkSelfPermission(this, mPermissions[0]) == PackageManager.PERMISSION_GRANTED) {
                // TODO: mWearableAPIHelper might not be connected yet.
                sendResponse(mResponseDataPath, true);
            } else if(getIntent().getBooleanExtra(EXTRA_JUST_CHECKING, false)) {
                // TODO: mWearableAPIHelper might not be connected yet.
                sendResponse(Constants.DATA_PATH_INSTANT_COMPANION_PERMISSION_RESPONSE, false);
            } else {
                if(null != eo) {
                    showEducationalScreen(eo);
                } else {
                    ActivityCompat.requestPermissions(PermissionRequestActivity.this, mPermissions, REQUEST_CODE);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!mResponseSent) {
            DataMap dataMap = new DataMap();
            dataMap.putBoolean(Constants.DATA_KEY_PERMISSION, false);
            dataMap.putLong(Constants.DATA_KEY_TIMESTAMP, System.currentTimeMillis());
            if (mWearableAPIHelper != null) {
                mWearableAPIHelper.putDataMap(mResponseDataPath, dataMap, null);
            }
        }

        if(null != mWearableAPIHelper) {
            mWearableAPIHelper.onDestroy();
        }
    }

    private void showEducationalScreen(EducationalObject eo) {
        setContentView(R.layout.educational_companion);

        findViewById(R.id.educational_companion_layout_root).setBackgroundColor(eo.getBackgroundColor());

        if(null != eo.getResource()) {
            try {
                ((ImageView) findViewById(R.id.educational_companion_image)).setImageDrawable(getApplicationContext().getResources().getDrawable(
                        getApplicationContext().getResources().getIdentifier(eo.getResource()[0], "drawable", eo.getResource()[1])));
            } catch (Resources.NotFoundException nfe) {
                nfe.printStackTrace();
            }
        }

        TextView t1 = (TextView) findViewById(R.id.educational_companion_textview1);
        t1.setTextColor(eo.getTextColor());
        t1.setText(eo.getEducationalText1Companion());

        TextView t2 = (TextView) findViewById(R.id.educational_companion_textview2);
        t2.setTextColor(eo.getTextColor());
        t2.setText(eo.getEducationalText2Companion());

        TextView positiveButton = (TextView)findViewById(R.id.educational_companion_positive_button);
        positiveButton.setTextColor(eo.getTextColor());
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(PermissionRequestActivity.this, mPermissions, REQUEST_CODE);
            }
        });

        TextView negativeButton = (TextView)findViewById(R.id.educational_companion_negative_button);
        negativeButton.setTextColor(eo.getTextColor());
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResponse(mResponseDataPath, false);
            }
        });
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
        mResponseSent = true;
        finish();
    }
}

