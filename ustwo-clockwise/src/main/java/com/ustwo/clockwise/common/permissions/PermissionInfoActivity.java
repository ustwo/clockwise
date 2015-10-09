package com.ustwo.clockwise.common.permissions;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.view.View;
import android.widget.TextView;

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

    public static final String EXTRA_BG_COLOR = "extra_bg_color";
    public static final String EXTRA_TEXT_COLOR = "extra_text_color";
    public static final String EXTRA_MESSAGE = "extra_message";

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

        int bgColor = getIntent().getIntExtra(EXTRA_BG_COLOR, Color.BLACK);
        findViewById(R.id.permission_info_layout_root).setBackgroundColor(bgColor);

        int textColor = getIntent().getIntExtra(EXTRA_TEXT_COLOR, Color.WHITE);
        TextView tv = (TextView)findViewById(R.id.permission_info_textview_message);
        tv.setTextColor(textColor);
        String message = getIntent().getStringExtra(EXTRA_MESSAGE);
        if(null != message && !"".equals(message)) {
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
                    mWearableAPIHelper.putDataMap(Constants.DATA_PATH_PERMISSION_INFO_RESPONSE, dataMap, null);
                    mAccepted = true;
                }

                // Show the confirmation activity.
                Intent intent = new Intent(getApplicationContext(), ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
                startActivity(intent);

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
