package com.ustwo.clockwise.companion.permissions;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.DataMap;
import com.ustwo.clockwise.common.Constants;
import com.ustwo.clockwise.common.MessageReceivedHandler;
import com.ustwo.clockwise.common.WearableAPIHelper;
import com.ustwo.clockwise.common.permissions.EducationalObject;
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
            String permission = map.getString(Constants.DATA_KEY_PERMISSION);
            boolean justChecking = map.getBoolean(Constants.DATA_KEY_JUST_CHECKING);
            String text1 = map.getString(Constants.DATA_KEY_EDUCATIONAL_TEXT1);
            String text2 = map.getString(Constants.DATA_KEY_EDUCATIONAL_TEXT2);

            Log.v(TAG, "requesting companion permission: " + permission);

            Intent i = new Intent(mContext.getApplicationContext(), PermissionRequestActivity.class);
            i.putExtra(PermissionRequestActivity.EXTRA_RESPONSE_DATA_PATH, Constants.DATA_PATH_COMPANION_PERMISSION_RESPONSE);
            i.putExtra(PermissionRequestActivity.EXTRA_PERMISSIONS, new String[]{permission});
            i.putExtra(PermissionRequestActivity.EXTRA_JUST_CHECKING, justChecking);
            if(null != text1 && null != text2) {

                System.out.println(">>>>> COMPANION PERMISSION REQUEST MESSAGE HANDLER COLOR: " +  map.getInt(Constants.DATA_KEY_EDUCATIONAL_BG_COLOR, 0));

                int bgColor = map.getInt(Constants.DATA_KEY_EDUCATIONAL_BG_COLOR, 0);
                int textColor = map.getInt(Constants.DATA_KEY_EDUCATIONAL_TEXT_COLOR, 0);
                int resourceId = map.getInt(Constants.DATA_KEY_EDUCATIONAL_RESOURCE_ID, -1);
                EducationalObject eo = new EducationalObject(null, text1, text2, bgColor, textColor, resourceId);
                i.putExtra(PermissionRequestActivity.EXTRA_EDUCATIONAL_OBJECT, eo);
            }
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.getApplicationContext().startActivity(i);
        }
    }

    @Override
    public List<String> getSupportedPaths() {
        return Arrays.asList(Constants.DATA_PATH_COMPANION_PERMISSION_REQUEST);
    }
}
