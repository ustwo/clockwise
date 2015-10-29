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

import com.google.android.gms.wearable.DataMap;
import com.ustwo.clockwise.R;
import com.ustwo.clockwise.common.Constants;
import com.ustwo.clockwise.common.WearableAPIHelper;
import com.ustwo.clockwise.wearable.permissions.PermissionRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PermissionRequestActivity extends Activity {
    public static final String EXTRA_WEARABLE_PERMISSION = "extra_wearable_permission";
    public static final String EXTRA_COMPANION_JUST_CHECKING = "extra_companion_just_checking";
    public static final String EXTRA_PERMISSION_REQUEST = "extra_permission_request";

    private static final int REQUEST_CODE = 21;

    private WearableAPIHelper mWearableAPIHelper;
    private PermissionRequest mPermissionRequest;
    private boolean mIsWearableMode = false;
    private String mWearablePermission;

    private boolean mJustCheckingCompanion = false;
    private HashMap<String, Boolean> mCompanionPermissionResults = new HashMap<>();
    private List<String> mCheckedCompanionPermissions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWearableAPIHelper = new WearableAPIHelper(this, null);

        mWearablePermission = getIntent().getStringExtra(EXTRA_WEARABLE_PERMISSION);
        mPermissionRequest = PermissionRequest.deserialize(getIntent().getByteArrayExtra(EXTRA_PERMISSION_REQUEST));
        mIsWearableMode = (null != mWearablePermission && !mWearablePermission.isEmpty());
        mJustCheckingCompanion = getIntent().getBooleanExtra(EXTRA_COMPANION_JUST_CHECKING, false);

        if(mIsWearableMode) {
            // WEARABLE
            if(ContextCompat.checkSelfPermission(this, mWearablePermission) == PackageManager.PERMISSION_GRANTED) {
                sendWearableResponse(true);
            } else {
                ActivityCompat.requestPermissions(PermissionRequestActivity.this, new String[]{mWearablePermission}, REQUEST_CODE);
            }
        } else if(mPermissionRequest != null) {
            for(String companionPermission : mPermissionRequest.getCompanionPermissions()) {
                if(ContextCompat.checkSelfPermission(this, companionPermission) == PackageManager.PERMISSION_GRANTED) {
                    mCompanionPermissionResults.put(companionPermission, true);
                } else {
                    mCompanionPermissionResults.put(companionPermission, false);
                }
            }

            // COMPANION
            if(mJustCheckingCompanion) {
                sendCompanionResponse();
            } else {
                showEducationalScreen();
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

    private void showEducationalScreen() {
        setContentView(R.layout.educational_companion);

        findViewById(R.id.educational_companion_layout_root).setBackgroundColor(mPermissionRequest.getEducationBackgroundColor());

        if(null != mPermissionRequest.getCompanionEducationImageResource()) {
            try {
                ((ImageView) findViewById(R.id.educational_companion_image)).setImageDrawable(getApplicationContext().getResources().getDrawable(
                        getApplicationContext().getResources().getIdentifier(mPermissionRequest.getCompanionEducationImageResource(),
                                "drawable", mPermissionRequest.getCompanionEducationImageResourcePackage())));
            } catch (Resources.NotFoundException nfe) {
                nfe.printStackTrace();
            }
        }

        TextView t1 = (TextView) findViewById(R.id.educational_companion_textview1);
        t1.setTextColor(mPermissionRequest.getEducationTextColor());
        t1.setText(mPermissionRequest.getCompanionEducationPrimaryText());

        TextView t2 = (TextView) findViewById(R.id.educational_companion_textview2);
        t2.setTextColor(mPermissionRequest.getEducationTextColor());
        t2.setText(mPermissionRequest.getCompanionEducationSecondaryText());

        TextView positiveButton = (TextView)findViewById(R.id.educational_companion_positive_button);
        positiveButton.setTextColor(mPermissionRequest.getEducationTextColor());
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNextCompanionPermission();
            }
        });

        TextView negativeButton = (TextView)findViewById(R.id.educational_companion_negative_button);
        negativeButton.setTextColor(mPermissionRequest.getEducationTextColor());
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCompanionResponse();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        String permission = permissions[0];
        boolean granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

        if(requestCode == REQUEST_CODE) {
            if(mIsWearableMode) {
                sendWearableResponse(granted);
            } else {
                mCheckedCompanionPermissions.add(permission);
                mCompanionPermissionResults.put(permission, granted);
                requestNextCompanionPermission();
            }
        }
    }

    private void requestNextCompanionPermission() {
        boolean noMorePermissions = true;
        for(String companionPermission : mCompanionPermissionResults.keySet()) {
            // grab next companion permission that hasn't been accepted or checked
            if(!mCompanionPermissionResults.get(companionPermission) && !mCheckedCompanionPermissions.contains(companionPermission)) {
                noMorePermissions = false;
                ActivityCompat.requestPermissions(PermissionRequestActivity.this, new String[]{companionPermission}, REQUEST_CODE);
                break;
            }
        }
        if(noMorePermissions) {
            sendCompanionResponse();
        }
    }

    private void sendWearableResponse(boolean granted) {
        DataMap dataMap = new DataMap();
        dataMap.putLong(Constants.DATA_KEY_TIMESTAMP, System.currentTimeMillis());
        dataMap.putString(Constants.DATA_KEY_WEARABLE_PERMISSION, mWearablePermission);
        dataMap.putBoolean(Constants.DATA_KEY_WEARABLE_PERMISSION_GRANTED, granted);
        if (mWearableAPIHelper != null) {
            mWearableAPIHelper.putMessage(Constants.DATA_PATH_WEARABLE_PERMISSION_RESPONSE, dataMap.toByteArray(), null);
            //mWearableAPIHelper.putDataMap(Constants.DATA_PATH_WEARABLE_PERMISSION_RESPONSE, dataMap, null);
        }
        finish();
    }

    private void sendCompanionResponse() {
        DataMap dataMap = new DataMap();
        dataMap.putLong(Constants.DATA_KEY_TIMESTAMP, System.currentTimeMillis());
        ArrayList<DataMap> results = new ArrayList<>();
        for(String permission : mCompanionPermissionResults.keySet()) {
            DataMap resultMap = new DataMap();
            resultMap.putString(Constants.DATA_KEY_COMPANION_PERMISSION, permission);
            resultMap.putBoolean(Constants.DATA_KEY_COMPANION_PERMISSION_GRANTED, mCompanionPermissionResults.get(permission));
            results.add(resultMap);
        }
        dataMap.putDataMapArrayList(Constants.DATA_KEY_COMPANION_PERMISSION_RESULTS, results);
        if (mWearableAPIHelper != null) {
            String path = mJustCheckingCompanion ? Constants.DATA_PATH_INSTANT_COMPANION_PERMISSION_RESPONSE : Constants.DATA_PATH_COMPANION_PERMISSION_RESPONSE;
            mWearableAPIHelper.putMessage(path, dataMap.toByteArray(), null);
            //mWearableAPIHelper.putDataMap(path, dataMap, null);
        }
        finish();
    }
}

