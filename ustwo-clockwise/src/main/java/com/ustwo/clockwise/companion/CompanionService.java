package com.ustwo.clockwise.companion;

import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.ustwo.clockwise.common.DataChangedHandler;
import com.ustwo.clockwise.common.MessageReceivedHandler;
import com.ustwo.clockwise.common.WearableAPIHelper;
import com.ustwo.clockwise.companion.permissions.CompanionPermissionRequestMessageHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that responds to all com.google.android.gms.wearable.BIND_LISTENER events from the wearable and routes the
 * incoming messages and data paths to the required handlers.
 * <p/>
 * Note that this is required because each application may only have one Service for the BIND_LISTENER action.
 *
 * @author mark@ustwo.com
 */
public class CompanionService extends WearableListenerService {
    private static final String TAG = CompanionService.class.getSimpleName();

    protected WearableAPIHelper mWearableAPIHelper;

    protected final List<DataChangedHandler> mDataChangedHandlers = new ArrayList<>();

    protected final List<MessageReceivedHandler> mMessageReceivedHandlers = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        mMessageReceivedHandlers.add(new CompanionPermissionRequestMessageHandler(this));

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
    }

    @Override
    public void onDestroy() {
        mWearableAPIHelper.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        // Route the changed data event to the correct handler.
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);

        for (DataEvent event : events) {
            String path = event.getDataItem().getUri().getPath();

            for (DataChangedHandler handler : mDataChangedHandlers) {
                for(String supportedPath : handler.getSupportedPaths()) {
                    // allow handler to define just the first part of the entire URI.
                    if(path.startsWith(supportedPath)) {
                        handler.onDataChanged(path, event, mWearableAPIHelper);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String path = messageEvent.getPath();
        Log.v(TAG, "onMessageReceived: " + path);


        for (MessageReceivedHandler handler : mMessageReceivedHandlers) {
            if (null != handler.getSupportedPaths() && handler.getSupportedPaths().contains(path)) {
                byte[] rawData = messageEvent.getData();
                DataMap dataMap = DataMap.fromByteArray(rawData);

                handler.onMessageReceived(path, dataMap, mWearableAPIHelper);
            }
        }
    }
}