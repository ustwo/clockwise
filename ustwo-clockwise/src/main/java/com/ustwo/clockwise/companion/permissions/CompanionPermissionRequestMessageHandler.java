package com.ustwo.clockwise.companion.permissions;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.wearable.DataMap;
import com.ustwo.clockwise.common.Constants;
import com.ustwo.clockwise.common.MessageReceivedHandler;
import com.ustwo.clockwise.common.WearableAPIHelper;
import com.ustwo.clockwise.common.permissions.PermissionRequestActivity;
import com.ustwo.clockwise.common.permissions.PermissionRequestItem;
import com.ustwo.clockwise.wearable.permissions.PermissionsRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CompanionPermissionRequestMessageHandler implements MessageReceivedHandler {
    private static final String TAG = CompanionPermissionRequestMessageHandler.class.getSimpleName();

    private Context mContext;

    public CompanionPermissionRequestMessageHandler(Context context) {
        mContext = context;
    }

    @Override
    public void onMessageReceived(String path, DataMap map, WearableAPIHelper apiHelper) {
        if(path.equals(Constants.DATA_PATH_COMPANION_PERMISSION_REQUEST)) {
            boolean justChecking = map.getBoolean(Constants.DATA_KEY_JUST_CHECKING);
            byte[] requestBytes = map.getByteArray(Constants.DATA_KEY_PERMISSION_REQUEST);
            PermissionsRequest request = PermissionsRequest.deserialize(requestBytes);

            // if requesting silently, just get the results and return them instead of invoking the activity
            if(request.shouldRequestSilently()) {
                HashMap<String, Boolean> companionPermissionResults = new HashMap<>();
                for(PermissionRequestItem requestItem : request.getRequestItems()) {
                    if(requestItem.isWearable()) continue;

                    for(String companionPermission : requestItem.getPermissions()) {
                        if (ContextCompat.checkSelfPermission(mContext, companionPermission) == PackageManager.PERMISSION_GRANTED) {
                            companionPermissionResults.put(companionPermission, true);
                        } else {
                            companionPermissionResults.put(companionPermission, false);
                        }
                    }
                }

                DataMap dataMap = new DataMap();
                dataMap.putLong(Constants.DATA_KEY_TIMESTAMP, System.currentTimeMillis());
                ArrayList<DataMap> results = new ArrayList<>();
                for(String permission : companionPermissionResults.keySet()) {
                    DataMap resultMap = new DataMap();
                    resultMap.putString(Constants.DATA_KEY_COMPANION_PERMISSION, permission);
                    resultMap.putBoolean(Constants.DATA_KEY_COMPANION_PERMISSION_GRANTED, companionPermissionResults.get(permission));
                    results.add(resultMap);
                }
                dataMap.putDataMapArrayList(Constants.DATA_KEY_COMPANION_PERMISSION_RESULTS, results);
                apiHelper.putMessage(Constants.DATA_PATH_COMPANION_PERMISSION_RESPONSE, dataMap.toByteArray(), null);
                //apiHelper.putDataMap(Constants.DATA_PATH_COMPANION_PERMISSION_RESPONSE, dataMap, null);
            } else {
                Intent i = new Intent(mContext.getApplicationContext(), PermissionRequestActivity.class);
                i.putExtra(PermissionRequestActivity.EXTRA_COMPANION_JUST_CHECKING, justChecking);
                i.putExtra(PermissionRequestActivity.EXTRA_PERMISSION_REQUEST, requestBytes);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(i);
            }
        }
    }

    @Override
    public List<String> getSupportedPaths() {
        return Arrays.asList(Constants.DATA_PATH_COMPANION_PERMISSION_REQUEST);
    }
}
