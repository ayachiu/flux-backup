package com.tf.fluxbackup;

import android.app.Application;

import com.tf.fluxbackup.util.DatabaseLayer;

/**
 * Created by kamran on 4/2/16.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DatabaseLayer.initialize(MainApplication.this);
    }
}
