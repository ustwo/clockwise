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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Helper class for sending data to the Google Play Services Wearable API.
 */
public class WearableAPIHelper {

    private static final String TAG = WearableAPIHelper.class.getSimpleName();

    /**
     * Max time we'll wait for the Google API to client to connect in seconds.
     */
    private static final int MAX_API_CONNECTION_TIME = 30;

    private GoogleApiClient mGoogleApiClient;

    private WearableAPIHelperListener mListener;

    public WearableAPIHelper(Context context, WearableAPIHelperListener listener) {
        mListener = listener;

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.v(TAG, "Wearable API connected");
                        mListener.onWearableAPIConnected(mGoogleApiClient);
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.e(TAG, "Wearable API connection suspended. Cause: " + cause);
                        mListener.onWearableAPIConnectionSuspended(cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.e(TAG, "Wearable API connection failed: Has resolution? " + result.hasResolution());
                        mListener.onWearableAPIConnectionFailed(result);
                    }
                })
                .addApi(Wearable.API)
                .build();
        // Connect asynchronously.
        mGoogleApiClient.connect();
    }

    /**
     * Connect to the Google API client. Blocks until we connect or fail to connect.
     *
     * @return true if connected, otherwise false.
     */
    private boolean connectGoogleApiClient() {
        boolean connected = true;

        if (!mGoogleApiClient.isConnected()) {
            // Connect synchronously.
            connected = mGoogleApiClient.blockingConnect(MAX_API_CONNECTION_TIME, TimeUnit.SECONDS).isSuccess();

            if (!connected) {
                Log.e(TAG, "Failed to connect to GoogleApiClient.");
            }
        }

        return connected;
    }

    /**
     * Put a message on the data layer for all connected nodes to receive using the
     * {@link com.google.android.gms.wearable.MessageApi}.
     *
     * @param path     identifier used to specify a particular endpoint at the receiving node
     * @param payload  payload for the message. Can be null.
     * @param callback for put result. Can be null.
     */
    public void putMessage(final String path, final byte[] payload,
                           final ResultCallback<MessageApi.SendMessageResult> callback) {
        if (mGoogleApiClient.isConnected()) {
            // Send immediately.
            doPutMessage(path, payload, callback);
        } else {
            // Wait for an api connection then send off the UI thread.
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (connectGoogleApiClient()) {
                        doPutMessage(path, payload, callback);
                    } // else, we failed to connect to the API.
                }
            }).start();
        }
    }

    private void doPutMessage(final String path, final byte[] payload,
                              final ResultCallback<MessageApi.SendMessageResult> callback) {
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(
                new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult result) {
                        for (Node node : result.getNodes()) {
                            PendingResult<MessageApi.SendMessageResult> pendingResult =
                                    Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, payload);

                            if (callback != null) {
                                pendingResult.setResultCallback(callback);
                            }
                        }
                    }
                });
    }

    /**
     * Put a data map on the data layer for all connected nodes to receive using the
     * {@link com.google.android.gms.wearable.DataApi}.
     *
     * @param path     identifier used to specify a particular endpoint at the receiving node
     * @param dataMap  map of data.
     * @param callback for put result. Can be null.
     */
    public void putDataMap(final String path, final DataMap dataMap, final ResultCallback<DataApi.DataItemResult> callback) {
        if (mGoogleApiClient.isConnected()) {
            // Send immediately.
            doPutDataMap(path, dataMap, callback);
        } else {
            // Wait for an api connection then send off the UI thread.
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (connectGoogleApiClient()) {
                        doPutDataMap(path, dataMap, callback);
                    } // else, we failed to connect to the API.
                }
            }).start();
        }
    }

    private void doPutDataMap(String path, DataMap dataMap, ResultCallback<DataApi.DataItemResult> callback) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path);

        if (dataMap != null) {
            putDataMapRequest.getDataMap().putAll(dataMap);
        }

        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);

        if (callback != null) {
            pendingResult.setResultCallback(callback);
        }
    }

    /**
     * Loads a bitmap from from a wearable data asset. Note, this is blocking. Do not call from the UI thread.
     *
     * @param asset Bitmap asset
     * @return      Bitmap, or null if the bitmap could not be loaded.
     */
    public Bitmap loadBitmap(Asset asset) {
        Bitmap bitmap = null;

        if (mGoogleApiClient.isConnected()) {
            InputStream assetInputStream = Wearable.DataApi.getFdForAsset(mGoogleApiClient, asset).await().getInputStream();
            bitmap = BitmapFactory.decodeStream(assetInputStream);
        }

        return bitmap;
    }

    /**
     * Disconnects the Google Api client
     */
    public void onDestroy() {
        mGoogleApiClient.disconnect();
    }

    public interface WearableAPIHelperListener {
        public void onWearableAPIConnected(GoogleApiClient apiClient);

        public void onWearableAPIConnectionSuspended(int cause);

        public void onWearableAPIConnectionFailed(ConnectionResult result);
    }
}
