package com.tf.fluxbackup.util;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tf.fluxbackup.R;
import com.tf.fluxbackup.model.SimpleKeyValuePair;

/**
 * Created by kamran on 4/23/16.
 */
public class AnalyticsHelper {

    private static Tracker mTracker;

    public static void initialize(Context context) {
        mTracker = getDefaultTracker(context);
    }

    public static void sendScreenView(String screenName) {
        if (mTracker != null) {
            mTracker.setScreenName(screenName);
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    public static void sendCustomEvent(String action, SimpleKeyValuePair... keyValuePairs) {
        if (mTracker != null) {
            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder()
                    .setAction(action);

            for (SimpleKeyValuePair keyValuePair : keyValuePairs) {
                eventBuilder.set(keyValuePair.getKey(), keyValuePair.getValue());
            }

            mTracker.send(eventBuilder.build());
        }
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized private static Tracker getDefaultTracker(Context context) {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }

        return mTracker;
    }

    public static void destroy() {
        mTracker = null;
    }
}
