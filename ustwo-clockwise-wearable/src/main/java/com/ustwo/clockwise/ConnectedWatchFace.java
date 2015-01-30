/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 ustwo studio inc (www.ustwo.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.ustwo.clockwise;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.ustwo.clockwise.util.Logr;

/**
 * A {@link com.ustwo.clockwise.WatchFace} extension that supports data transfer between the watch
 * and a connected device. This watch face manages a {@link com.google.android.gms.common.api.GoogleApiClient}
 * connection to the {@link com.google.android.gms.wearable.Wearable#API}, exposing methods to send data
 * to connected nodes and callbacks to receive data from connected nodes.
 */
public abstract class ConnectedWatchFace extends WatchFace implements MessageApi.MessageListener,
        NodeApi.NodeListener, DataApi.DataListener {

    private WearableAPIHelper mWearableAPIHelper;
    private GoogleApiClient mApiClient;

    @Override
    public Engine onCreateEngine() {
        Engine engine = super.onCreateEngine();

        mWearableAPIHelper = new WearableAPIHelper(this, new WearableAPIHelper.WearableAPIHelperListener() {
            @Override
            public void onWearableAPIConnected(GoogleApiClient apiClient) {
                mApiClient = apiClient;
                Wearable.MessageApi.addListener(mApiClient, ConnectedWatchFace.this);
                Wearable.NodeApi.addListener(mApiClient, ConnectedWatchFace.this);
                Wearable.DataApi.addListener(mApiClient, ConnectedWatchFace.this);
            }

            @Override
            public void onWearableAPIConnectionSuspended(int cause) {
            }

            @Override
            public void onWearableAPIConnectionFailed(ConnectionResult result) {
            }
        });

        return engine;
    }

    @Override
    public void onDestroy() {
        if (mApiClient != null) {
            Wearable.MessageApi.removeListener(mApiClient, ConnectedWatchFace.this);
            Wearable.NodeApi.removeListener(mApiClient, ConnectedWatchFace.this);
            Wearable.DataApi.removeListener(mApiClient, ConnectedWatchFace.this);
        }
        mWearableAPIHelper.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onPeerConnected(Node node) {
        Logr.v("ConnectedWatchFace.onPeerConnected: " + node.getDisplayName());
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Logr.v("ConnectedWatchFace.onPeerDisconnected: " + node.getDisplayName());
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Logr.v("ConnectedWatchFace.onDataChanged: " + dataEvents);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Logr.v("ConnectedWatchFace.onMessageReceived: " + messageEvent);
    }

    /**
     * Put a data map on the data layer for all connected nodes to receive using the
     * {@link com.google.android.gms.wearable.DataApi}
     * <p/>
     * NOTE: onDataChanged() will only be called on the receiving end of this call if the data
     * has changed. If you put the same data into the {@link com.google.android.gms.wearable.DataApi}
     * multiple times, onDataChanged() is only called once until you write different data.
     *
     * @param path     identifier used to specify a particular endpoint at the receiving node
     * @param dataMap  map of data.
     * @param callback for put result. Can be null.
     */
    protected void putDataItem(String path, DataMap dataMap, ResultCallback<DataApi.DataItemResult> callback) {
        Logr.v("ConnectedWatchFace.putDataItem: " + path + " " + dataMap);

        mWearableAPIHelper.putDataMap(path, dataMap, callback);
    }

    /**
     * Put a small message payload on the data layer for all connected nodes to receive using the
     * {@link com.google.android.gms.wearable.MessageApi}
     *
     * @param path     identifier used to specify a particular endpoint at the receiving node
     * @param payload  payload for the message. Can be null.
     * @param callback for put result. Can be null.
     */
    protected void putMessage(String path, byte[] payload, ResultCallback<MessageApi.SendMessageResult> callback) {
        Logr.v("ConnectedWatchFace.putMessage: " + path);

        mWearableAPIHelper.putMessage(path, payload, callback);
    }
}
