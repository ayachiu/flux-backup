package com.tf.fluxbackup.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import com.tf.fluxbackup.R;
import com.tf.fluxbackup.model.AdvancedIntentService;
import com.tf.fluxbackup.model.ProgressReporter;
import com.tf.fluxbackup.util.BackupManager;
import com.tf.fluxbackup.util.Constants;

public class RestoreIntentService extends AdvancedIntentService {

    private static final String ACTION_RESTORE = "com.tf.fluxbackup.service.action.RESTORE";

    private static final String EXTRA_PACKAGE_NAME = "com.tf.fluxbackup.service.extra.PACKAGE_NAME";
    private static int restoreFailureNotificationId = 2000;

    public RestoreIntentService() {
        super("BackupIntentService");
    }

    /**
     * Starts this service to perform backup of the given package. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void restore(Context context, String packageName) {
        Intent intent = new Intent(context, RestoreIntentService.class);
        intent.setAction(ACTION_RESTORE);
        intent.putExtra(EXTRA_PACKAGE_NAME, packageName);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RESTORE.equals(action)) {
                final String packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME);
                handleActionRestore(packageName);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRestore(String packageName) {
        showProgressNotification(packageName);

        ProgressReporter.reportProgress(getBaseContext(), getQueueProgress(), getQueueSize(), packageName);

        boolean restoreSuccess = BackupManager.restorePackage(getBaseContext(), packageName);

        if (!restoreSuccess) {
            showRestoreFailureNotification(packageName);
        }

        if (wasLastInQueue()) {
            showRestoreCompleteNotification();
        }
    }

    private boolean wasLastInQueue() {
        return getQueueProgress() / getQueueSize() == 1;
    }

    private void showProgressNotification(String packageName) {
        String progressInPercent = (((getQueueProgress() - 1) * 100) / getQueueSize()) + "%";

        NotificationManagerCompat.from(getBaseContext())
                .notify(Constants.NOTIFICATION_RESTORE,
                        new NotificationCompat.Builder(getBaseContext())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Restoring - " + progressInPercent)
                                .setContentText(packageName)
                                .setTicker("Restoring - " + progressInPercent)
                                .setOngoing(true)
                                .build());
    }

    private void showRestoreFailureNotification(String packageName) {
        NotificationManagerCompat.from(getBaseContext())
                .notify(restoreFailureNotificationId++,
                        new NotificationCompat.Builder(getBaseContext())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Restore Failed")
                                .setContentText("Restore failed for " + packageName)
                                .setTicker("Restore Failed")
                                .build());
    }

    private void showRestoreCompleteNotification() {
        NotificationManagerCompat.from(getBaseContext())
                .notify(Constants.NOTIFICATION_RESTORE,
                        new NotificationCompat.Builder(getBaseContext())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Restore Complete")
                                .setContentText("Restored " + getQueueSize() + " applications")
                                .setTicker("Restore Complete")
                                .build());
    }
}
