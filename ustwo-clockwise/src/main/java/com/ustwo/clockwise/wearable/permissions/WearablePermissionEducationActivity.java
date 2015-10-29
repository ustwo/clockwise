package com.ustwo.clockwise.wearable.permissions;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.wearable.DataMap;
import com.ustwo.clockwise.R;
import com.ustwo.clockwise.common.Constants;
import com.ustwo.clockwise.common.WearableAPIHelper;

public class WearablePermissionEducationActivity extends Activity {

    public static final String EXTRA_PERMISSION_REQUEST = "extra_permission_request";

    private PermissionRequest mPermissionRequest;

    private WearableAPIHelper mWearableAPIHelper;
    private boolean mAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWearableAPIHelper = new WearableAPIHelper(this, null);

        setContentView(R.layout.permission_info);

        mPermissionRequest = PermissionRequest.deserialize(getIntent().getByteArrayExtra(EXTRA_PERMISSION_REQUEST));

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.permission_info_stub);
        // Update the UI in a listener because the layout is using a stub.
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {

            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                findViewById(R.id.permission_info_layout_root).setBackgroundColor(mPermissionRequest.getEducationBackgroundColor());

                TextView tv = (TextView) findViewById(R.id.permission_info_textview_message);
                tv.setTextColor(mPermissionRequest.getEducationTextColor());
                String message = mPermissionRequest.getWearableEducationText();
                if (null != message && !"".equals(message)) {
                    tv.setText(message);
                }

                findViewById(R.id.permission_info_imagebutton_confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Send message back to the PermissionRequestor class
                        DataMap dataMap = new DataMap();
                        dataMap.putBoolean(Constants.DATA_KEY_OPEN_ON_PHONE, true);
                        dataMap.putLong(Constants.DATA_KEY_TIMESTAMP, System.currentTimeMillis());
                        if (mWearableAPIHelper != null) {
                            mWearableAPIHelper.putMessageToNode(mWearableAPIHelper.getLocalNodeId(), Constants.DATA_PATH_PERMISSION_INFO_RESPONSE, dataMap.toByteArray(), null);
                            //mWearableAPIHelper.putDataMap(Constants.DATA_PATH_PERMISSION_INFO_RESPONSE, dataMap, null);
                            mAccepted = true;
                        }

                        // Show the confirmation activity.
                        Intent intent = new Intent(getApplicationContext(), ConfirmationActivity.class);
                        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
                        startActivity(intent);

                        finish();
                    }
                });

                ImageButton button = (ImageButton)findViewById(R.id.permission_info_imagebutton_confirm);
                if(mPermissionRequest.getEducationTextColor() == Color.WHITE) {
                    button.setColorFilter(mPermissionRequest.getEducationBackgroundColor(), PorterDuff.Mode.MULTIPLY);
                } else {
                    button.setColorFilter(mPermissionRequest.getEducationTextColor(), PorterDuff.Mode.MULTIPLY);
                }

                ((TextView) findViewById(R.id.permission_info_textview_confirm)).setTextColor(mPermissionRequest.getEducationTextColor());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mAccepted) {
            DataMap dataMap = new DataMap();
            dataMap.putBoolean(Constants.DATA_KEY_OPEN_ON_PHONE, false);
            dataMap.putLong(Constants.DATA_KEY_TIMESTAMP, System.currentTimeMillis());
            if (mWearableAPIHelper != null) {
                mWearableAPIHelper.putMessageToNode(mWearableAPIHelper.getLocalNodeId(), Constants.DATA_PATH_PERMISSION_INFO_RESPONSE, dataMap.toByteArray(), null);
                //mWearableAPIHelper.putDataMap(Constants.DATA_PATH_PERMISSION_INFO_RESPONSE, dataMap, null);
            }
        }

        if (null != mWearableAPIHelper) {
            mWearableAPIHelper.onDestroy();
        }
    }
}
