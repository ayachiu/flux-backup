package com.tf.fluxbackup.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import com.tf.fluxbackup.R;
import com.tf.fluxbackup.model.ProgressReporter;
import com.tf.fluxbackup.util.BackupManager;
import com.tf.fluxbackup.util.Constants;

public class BackupIntentService extends AdvancedIntentService {

    private static final String ACTION_BACKUP = "com.tf.fluxbackup.service.action.BACKUP";

    private static final String EXTRA_PACKAGE_NAME = "com.tf.fluxbackup.service.extra.PACKAGE_NAME";

    private static int backupFailureNotificationId = 1000;

    public BackupIntentService() {
        super("BackupIntentService");
    }

    /**
     * Starts this service to perform backup of the given package. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void backup(Context context, String packageName) {
        Intent intent = new Intent(context, BackupIntentService.class);
        intent.setAction(ACTION_BACKUP);
        intent.putExtra(EXTRA_PACKAGE_NAME, packageName);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_BACKUP.equals(action)) {
                final String packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME);
                handleActionBackup(packageName);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBackup(String packageName) {
        showProgressNotification(packageName);

        ProgressReporter.reportProgress(getBaseContext(), getQueueProgress() - 1, getQueueSize(), packageName);

        boolean backupSuccess = BackupManager.backupPackage(getBaseContext(), packageName);

        if (!backupSuccess) {
            showBackupFailureNotification(packageName);
        }

        if (wasLastInQueue()) {
            showBackupCompleteNotification();

            ProgressReporter.reportProgress(getBaseContext(), getQueueSize(), getQueueSize(), packageName);
        }
    }

    private boolean wasLastInQueue() {
        return getQueueProgress() / getQueueSize() == 1;
    }

    private void showProgressNotification(String packageName) {
        int progressInPercent = (((getQueueProgress() - 1) * 100) / getQueueSize());

        NotificationManagerCompat.from(getBaseContext())
                .notify(Constants.NOTIFICATION_BACKUP,
                        new NotificationCompat.Builder(getBaseContext())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(String.format(getString(R.string.backing_up), progressInPercent) + "%")
                                .setContentText(packageName)
                                .setTicker(String.format(getString(R.string.backing_up), progressInPercent + "%"))
                                .setProgress(100, progressInPercent, false)
                                .setOngoing(true)
                                .build());
    }

    private void showBackupFailureNotification(String packageName) {
        NotificationManagerCompat.from(getBaseContext())
                .notify(backupFailureNotificationId++,
                        new NotificationCompat.Builder(getBaseContext())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(getString(R.string.backup_failed))
                                .setContentText("Backed failed for " + packageName)
                                .setTicker(getString(R.string.backup_failed))
                                .build());
    }

    private void showBackupCompleteNotification() {
        NotificationManagerCompat.from(getBaseContext())
                .notify(Constants.NOTIFICATION_BACKUP,
                        new NotificationCompat.Builder(getBaseContext())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(getString(R.string.backup_complete))
                                .setContentText("Backed up " + getQueueSize() + " applications")
                                .setTicker(getString(R.string.backup_complete))
                                .build());
    }
}
