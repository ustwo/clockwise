package com.ustwo.clockwise.companion.permissions;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.DataMap;
import com.ustwo.clockwise.common.Constants;
import com.ustwo.clockwise.common.MessageReceivedHandler;
import com.ustwo.clockwise.common.WearableAPIHelper;
import com.ustwo.clockwise.common.permissions.PermissionRequestActivity;

import java.util.Arrays;
import java.util.List;

public class CompanionPermissionRequestMessageHandler implements MessageReceivedHandler {
    private static final String TAG = CompanionPermissionRequestMessageHandler.class.getSimpleName();

    private Context mContext;

    public CompanionPermissionRequestMessageHandler(Context context) {
        mContext = context;
    }

    @Override
    public void onMessageReceived(String path, DataMap map, WearableAPIHelper apiHelper) {
        Log.v(TAG, "onMessageReceived: " + path);

        if(path.equals(Constants.DATA_PATH_COMPANION_PERMISSION_REQUEST)) {

            boolean justChecking = map.getBoolean(Constants.DATA_KEY_JUST_CHECKING);
            byte[] request = map.getByteArray(Constants.DATA_KEY_PERMISSION_REQUEST);

            Intent i = new Intent(mContext.getApplicationContext(), PermissionRequestActivity.class);
            i.putExtra(PermissionRequestActivity.EXTRA_COMPANION_JUST_CHECKING, justChecking);
            i.putExtra(PermissionRequestActivity.EXTRA_PERMISSION_REQUEST, request);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.getApplicationContext().startActivity(i);
        }
    }

    @Override
    public List<String> getSupportedPaths() {
        return Arrays.asList(Constants.DATA_PATH_COMPANION_PERMISSION_REQUEST);
    }
}
