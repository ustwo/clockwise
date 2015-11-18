package com.ustwo.clockwise.common.permissions;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.wearable.DataMap;
import com.ustwo.clockwise.R;
import com.ustwo.clockwise.common.Constants;
import com.ustwo.clockwise.common.WearableAPIHelper;
import com.ustwo.clockwise.wearable.permissions.PermissionsRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PermissionRequestActivity extends Activity {
    public static final String EXTRA_COMPANION_JUST_CHECKING = "extra_companion_just_checking";
    public static final String EXTRA_PERMISSION_REQUEST = "extra_permission_request";

    private static final int REQUEST_CODE = 21;

    private WearableAPIHelper mWearableAPIHelper;

    private boolean mJustCheckingCompanion = false;
    private PermissionsRequest mPermissionsRequest;
    private PermissionRequestItem mWearablePermission = null;

    private HashMap<String, Boolean> mCompanionPermissionResults = new HashMap<>();
    private List<String> mCheckedCompanionPermissions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWearableAPIHelper = new WearableAPIHelper(this, null);

        mJustCheckingCompanion = getIntent().getBooleanExtra(EXTRA_COMPANION_JUST_CHECKING, false);
        mPermissionsRequest = PermissionsRequest.deserialize(getIntent().getByteArrayExtra(EXTRA_PERMISSION_REQUEST));
        if(mPermissionsRequest == null || mPermissionsRequest.getRequestItems().size() == 0) {
            throw new IllegalArgumentException();
        } else {
            if(mPermissionsRequest.getRequestItems().get(0).isWearable()) {
                mWearablePermission = mPermissionsRequest.getRequestItems().get(0);
            }

            if (mWearablePermission != null && mWearablePermission.getPermissions().size() > 0) {
                // WEARABLE
                String wearablePermission = mWearablePermission.getPermissions().get(0);
                if (ContextCompat.checkSelfPermission(this, wearablePermission) == PackageManager.PERMISSION_GRANTED) {
                    sendWearableResponse(true);
                } else {
                    ActivityCompat.requestPermissions(PermissionRequestActivity.this, new String[]{wearablePermission}, REQUEST_CODE);
                }
            } else {
                // COMPANION
                for (PermissionRequestItem requestItem : mPermissionsRequest.getRequestItems()) {
                    if(requestItem.isWearable()) continue;

                    for(String companionPermission : requestItem.getPermissions()) {
                        if (ContextCompat.checkSelfPermission(this, companionPermission) == PackageManager.PERMISSION_GRANTED) {
                            mCompanionPermissionResults.put(companionPermission, true);
                        } else {
                            mCompanionPermissionResults.put(companionPermission, false);
                        }
                    }
                }

                if (mJustCheckingCompanion) {
                    sendCompanionResponse();
                } else {
                    requestNextCompanionPermission();
                }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE) {
            if(mWearablePermission != null) {
                sendWearableResponse(grantResults[0] == PackageManager.PERMISSION_GRANTED);
            } else {
                for(int i=0; i<permissions.length; i++) {
                    mCheckedCompanionPermissions.add(permissions[i]);
                    mCompanionPermissionResults.put(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
                }
                requestNextCompanionPermission();
            }
        }
    }

    private void requestNextCompanionPermission() {
        boolean noMorePermissions = true;
        for(PermissionRequestItem requestItem : mPermissionsRequest.getRequestItems()) {
            if(requestItem.isWearable()) continue;

            boolean request = true;
            for(String companionPermission : requestItem.getPermissions()) {
                // grab next companion permission that hasn't been accepted or checked
                if(mCompanionPermissionResults.get(companionPermission) || mCheckedCompanionPermissions.contains(companionPermission)) {
                    request = false;
                    break;
                }
            }

            if(request) {
                noMorePermissions = false;
                showCompanionPermissionEducation(requestItem);
                break;
            }
        }
        if(noMorePermissions) {
            sendCompanionResponse();
        }
    }

    private void showCompanionPermissionEducation(final PermissionRequestItem requestItem) {
        setContentView(R.layout.educational_companion);
        findViewById(R.id.educational_companion_image_background).setBackgroundColor(requestItem.getEducationLightBackgroundColor());
        findViewById(R.id.educational_companion_text_background).setBackgroundColor(requestItem.getEducationDarkBackgroundColor());

        if(null != requestItem.getCompanionEducationImageResource()) {
            try {
                ((ImageView) findViewById(R.id.educational_companion_image)).setImageDrawable(getApplicationContext().getResources().getDrawable(
                        getApplicationContext().getResources().getIdentifier(requestItem.getCompanionEducationImageResource(),
                                "drawable", requestItem.getCompanionEducationImageResourcePackage())));
            } catch (Resources.NotFoundException nfe) {
                nfe.printStackTrace();
            }
        }
        TextView t1 = (TextView) findViewById(R.id.educational_companion_primary_text);
        t1.setTextColor(requestItem.getEducationTextColor());
        t1.setText(requestItem.getCompanionEducationPrimaryText());
        TextView t2 = (TextView) findViewById(R.id.educational_companion_secondary_text);
        t2.setTextColor(requestItem.getEducationTextColor());
        t2.setText(requestItem.getCompanionEducationSecondaryText());
        TextView positiveButton = (TextView)findViewById(R.id.educational_companion_positive_button);
        positiveButton.setTextColor(requestItem.getEducationTextColor());
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCompanionPermission(requestItem);
            }
        });
        TextView negativeButton = (TextView)findViewById(R.id.educational_companion_negative_button);
        negativeButton.setTextColor(requestItem.getEducationTextColor());
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(String companionPermission : requestItem.getPermissions()) {
                    mCheckedCompanionPermissions.add(companionPermission);
                    mCompanionPermissionResults.put(companionPermission, false);
                }
                requestNextCompanionPermission();
            }
        });
    }

    private void requestCompanionPermission(PermissionRequestItem requestItem) {
        String[] permissions = new String[requestItem.getPermissions().size()];
        ActivityCompat.requestPermissions(PermissionRequestActivity.this, requestItem.getPermissions().toArray(permissions), REQUEST_CODE);
    }

    private void sendWearableResponse(boolean granted) {
        DataMap dataMap = new DataMap();
        dataMap.putLong(Constants.DATA_KEY_TIMESTAMP, System.currentTimeMillis());
        dataMap.putString(Constants.DATA_KEY_WEARABLE_PERMISSION, mWearablePermission.getPermissions().get(0));
        dataMap.putBoolean(Constants.DATA_KEY_WEARABLE_PERMISSION_GRANTED, granted);
        if (mWearableAPIHelper != null) {
            mWearableAPIHelper.putMessageToNode(mWearableAPIHelper.getLocalNodeId(), Constants.DATA_PATH_WEARABLE_PERMISSION_RESPONSE, dataMap.toByteArray(), null);
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

