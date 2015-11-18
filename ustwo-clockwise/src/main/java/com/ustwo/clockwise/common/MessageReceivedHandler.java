package com.ustwo.clockwise.common;

import com.google.android.gms.wearable.DataMap;
import java.util.List;

/**
 * Interface for classes that are able to handle message received events between the wearable and companion devices.
 *
 * @author mark@ustwo.com
 */
public interface MessageReceivedHandler {

    /**
     * Called when message has been received for a path the handler supports.
     *
     * @param path
     * @param map
     * @param apiHelper
     */
    void onMessageReceived(String path, DataMap map, WearableAPIHelper apiHelper);

    /**
     * Handlers must return a list of paths they support. The content of this will be matched against incoming events.
     *
     * @return list of paths.
     */
    List<String> getSupportedPaths();
}
