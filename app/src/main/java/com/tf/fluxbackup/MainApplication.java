package com.tf.fluxbackup;

import android.app.Application;

import com.tf.fluxbackup.util.AnalyticsHelper;
import com.tf.fluxbackup.util.DatabaseLayer;
import com.tf.fluxbackup.util.Logger;

/**
 * Created by kamran on 4/2/16.
 */
public class MainApplication extends Application {

    public static Thread.UncaughtExceptionHandler defaultExceptionHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        AnalyticsHelper.initialize(this);

        defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(Logger.uncaughtExceptionHandler);

        DatabaseLayer.initialize(MainApplication.this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        AnalyticsHelper.destroy();
    }
}
