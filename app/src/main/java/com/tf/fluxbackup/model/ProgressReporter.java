package com.tf.fluxbackup.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by kamran on 4/5/16.
 */
public abstract class ProgressReporter extends BroadcastReceiver {

    public static final String ACTION_REPORT_PROGRESS = "ProgressReporter.ACTION_REPORT_PROGRESS";

    public static final String EXTRA_PROGRESS = "progress";
    public static final String EXTRA_TOTAL = "total";
    public static final String EXTRA_CURRENT = "current";
    private final Context mContext;

    public ProgressReporter(Context context) {
        mContext = context;

        LocalBroadcastManager.getInstance(mContext)
                .registerReceiver(this, new IntentFilter(ACTION_REPORT_PROGRESS));
    }

    public static void reportProgress(Context context, int progress, int total, String current) {
        Intent progressIntent = new Intent(ACTION_REPORT_PROGRESS);

        progressIntent.putExtra(EXTRA_PROGRESS, progress);
        progressIntent.putExtra(EXTRA_TOTAL, total);
        progressIntent.putExtra(EXTRA_CURRENT, current);

        LocalBroadcastManager.getInstance(context).sendBroadcast(progressIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        onProgress(intent.getIntExtra(EXTRA_PROGRESS, 0),
                intent.getIntExtra(EXTRA_TOTAL, 0),
                intent.getStringExtra(EXTRA_CURRENT));
    }

    public abstract void onProgress(int progress, int total, String current);

    public void unregister() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(this);
    }
}
