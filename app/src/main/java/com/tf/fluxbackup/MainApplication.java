package com.tf.fluxbackup;

import android.app.Application;
import android.os.StrictMode;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.tf.fluxbackup.util.DatabaseLayer;
import com.tf.fluxbackup.util.Logger;

/**
 * Created by kamran on 4/2/16.
 */
public class MainApplication extends Application {

    public static Thread.UncaughtExceptionHandler defaultExceptionHandler;
    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();

        defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(Logger.uncaughtExceptionHandler);

        DatabaseLayer.initialize(MainApplication.this);
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
