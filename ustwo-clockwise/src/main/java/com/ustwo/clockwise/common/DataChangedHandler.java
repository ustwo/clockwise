package com.ustwo.clockwise.common;

import com.google.android.gms.wearable.DataEvent;

import java.util.List;

/**
 * Interface for classes that are able to handle data changed events between the wearable and companion devices.
 *
 * @author mark@ustwo.com
 */
public interface DataChangedHandler {

    /**
     * Called when data has changed for a path the handler supports.
     *
     * @param path
     * @param event
     * @param apiHelper
     */
    void onDataChanged(String path, DataEvent event, WearableAPIHelper apiHelper);

    /**
     * Handlers must return a list of paths they support. The content of this will be matched against incoming events.
     *
     * @return list of paths.
     */
    List<String> getSupportedPaths();
}
