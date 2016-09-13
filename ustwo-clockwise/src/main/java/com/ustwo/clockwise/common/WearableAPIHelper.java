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

package com.ustwo.clockwise.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
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

    /** Max time we'll wait for the Google API to client to connect in seconds. */
    private static final int MAX_API_CONNECTION_TIME = 30;

    private GoogleApiClient mGoogleApiClient;

    private String mLocalNodeId = "";

    private WearableAPIHelperListener mListener;

    public WearableAPIHelper(Context context, WearableAPIHelperListener listener) {
        mListener = listener;

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Wearable.NodeApi.getLocalNode(mGoogleApiClient).setResultCallback(
                                new ResultCallback<NodeApi.GetLocalNodeResult>() {
                                    @Override
                                    public void onResult(NodeApi.GetLocalNodeResult getLocalNodeResult) {
                                        mLocalNodeId = getLocalNodeResult.getNode().getId();
                                        Log.v(TAG, "Wearable API connected to node: " + mLocalNodeId);

                                        if(mListener != null) {
                                            mListener.onWearableAPIConnected(mGoogleApiClient);
                                        }
                                    }
                                }
                        );
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.e(TAG, "Wearable API connection suspended. Cause: " + cause);
                        if(mListener != null) {
                            mListener.onWearableAPIConnectionSuspended(cause);
                        }
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.e(TAG, "Wearable API connection failed: Has resolution? " + result.hasResolution());
                        if(mListener != null) {
                            mListener.onWearableAPIConnectionFailed(result);
                        }
                    }
                })
                .addApi(Wearable.API)
                .addApi(LocationServices.API)
                .build();
        // Connect asynchronously.
        mGoogleApiClient.connect();
    }

    public WearableAPIHelper(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public String getLocalNodeId() {
        return mLocalNodeId;
    }

    /**
     * Connect to the Google API client. Blocks until we connect or fail to connect.
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
     * Put a message on the data layer for the watch to receive. This is sent to all connected nodes.
     *
     * @param path identifier used to specify a particular endpoint at the receiving node
     * @param payload payload for the message. Can be null.
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
     * Put a message on the data layer for a specific watch to receive. This is sent to a single given node.
     *
     * @param nodeId id of node to receive message
     * @param path identifier used to specify a particular endpoint at the receiving node
     * @param payload payload for the message. Can be null.
     * @param callback for put result. Can be null.
     */
    public void putMessageToNode(final String nodeId, final String path, final byte[] payload,
                                 final ResultCallback<MessageApi.SendMessageResult> callback) {
        if (mGoogleApiClient.isConnected()) {
            // Send immediately.
            doPutMessageToNode(nodeId, path, payload, callback);
        } else {
            // Wait for an api connection then send off the UI thread.
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (connectGoogleApiClient()) {
                        doPutMessageToNode(nodeId, path, payload, callback);
                    } // else, we failed to connect to the API.
                }
            }).start();
        }
    }

    private void doPutMessageToNode(final String nodeId, final String path, final byte[] payload,
                                    final ResultCallback<MessageApi.SendMessageResult> callback) {
        PendingResult<MessageApi.SendMessageResult> pendingResult =
                Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, path, payload);

        if (callback != null) {
            pendingResult.setResultCallback(callback);
        }
    }

    /**
     * Put a data map on the data layer for the watch to receive. This is sent to all connected wathces.
     *
     * @param path identifier used to specify a particular endpoint at the receiving node
     * @param dataMap map of data.
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
     * Gets the {@link com.google.android.gms.wearable.DataItem} at the given path on the node defined
     * in the given {@link android.net.Uri}.
     *
     * @param uri identifier used to specify a particular endpoint at the receiving node
     * @param callback for get result.
     */
    public void getDataItem(Uri uri, ResultCallback<DataApi.DataItemResult> callback) {
        Wearable.DataApi.getDataItem(mGoogleApiClient, uri).setResultCallback(callback);
    }

    /**
     * Gets the {@link com.google.android.gms.wearable.DataItem} at the given path on the node defined
     * in the given {@link android.net.Uri}.
     *
     * @param uri identifier used to specify a particular endpoint at the receiving node
     * @param callback for get result.
     */
    public void getDataItem(Uri uri, final FetchDataMapCallback callback) {
        Wearable.DataApi.getDataItem(mGoogleApiClient, uri).setResultCallback(new DataItemResultCallback(callback));
    }

    /**
     * Gets the {@link com.google.android.gms.wearable.DataItem} at the given path on the local node.
     *
     * @param path identifier used to specify a particular endpoint on the local node
     * @param callback for get result.
     */
    public void getLocalDataItem(final String path, final FetchDataMapCallback callback) {
        Wearable.NodeApi.getLocalNode(mGoogleApiClient).setResultCallback(
                new ResultCallback<NodeApi.GetLocalNodeResult>() {
                    @Override
                    public void onResult(NodeApi.GetLocalNodeResult getLocalNodeResult) {
                        if(getLocalNodeResult != null && getLocalNodeResult.getNode() != null) {
                            String localNode = getLocalNodeResult.getNode().getId();
                            Uri uri = new Uri.Builder()
                                    .scheme(PutDataRequest.WEAR_URI_SCHEME)
                                    .path(path)
                                    .authority(localNode)
                                    .build();
                            getDataItem(uri, new DataItemResultCallback(callback));
                        }
                    }
                }
        );
    }

    /**
     * Deletes the {@link com.google.android.gms.wearable.DataItem} at the given path on the local node.
     *
     * @param path identifier used to specify a particular endpoint on the local node
     */
    public void deleteLocalDataItem(final String path) {
        Wearable.NodeApi.getLocalNode(mGoogleApiClient).setResultCallback(
                new ResultCallback<NodeApi.GetLocalNodeResult>() {
                    @Override
                    public void onResult(NodeApi.GetLocalNodeResult getLocalNodeResult) {
                        if(getLocalNodeResult != null && getLocalNodeResult.getNode() != null) {
                            String localNode = getLocalNodeResult.getNode().getId();
                            Uri uri = new Uri.Builder()
                                    .scheme(PutDataRequest.WEAR_URI_SCHEME)
                                    .path(path)
                                    .authority(localNode)
                                    .build();
                            Wearable.DataApi.deleteDataItems(mGoogleApiClient, uri);
                        }
                    }
                }
        );
    }

    /**
     * Loads a bitmap from from a wearable data asset. Note, this is blocking. Do not call from the UI thread.
     * @param asset
     * @return bitmap, or null if the bitmap could not be loaded.
     */
    public Bitmap loadBitmap(Asset asset) {
        Bitmap bitmap = null;

        if(mGoogleApiClient.isConnected()) {
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
        void onWearableAPIConnected(GoogleApiClient apiClient);
        void onWearableAPIConnectionSuspended(int cause);
        void onWearableAPIConnectionFailed(ConnectionResult result);
    }

    public interface FetchDataMapCallback {
        void onDataMapFetched(DataMap dataMap);
    }

    private static class DataItemResultCallback implements ResultCallback<DataApi.DataItemResult> {

        private final FetchDataMapCallback mCallback;

        public DataItemResultCallback(FetchDataMapCallback callback) {
            mCallback = callback;
        }

        @Override
        public void onResult(DataApi.DataItemResult dataItemResult) {
            if (dataItemResult.getStatus().isSuccess()) {
                if (dataItemResult.getDataItem() != null) {
                    DataItem configDataItem = dataItemResult.getDataItem();
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(configDataItem);
                    DataMap dataMap = dataMapItem.getDataMap();
                    mCallback.onDataMapFetched(dataMap);
                } else {
                    mCallback.onDataMapFetched(new DataMap());
                }
            }
        }
    }
}