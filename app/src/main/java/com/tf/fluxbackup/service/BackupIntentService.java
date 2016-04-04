package com.tf.fluxbackup.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import com.tf.fluxbackup.R;
import com.tf.fluxbackup.model.AdvancedIntentService;
import com.tf.fluxbackup.util.BackupManager;
import com.tf.fluxbackup.util.Constants;

public class BackupIntentService extends AdvancedIntentService {

    private static final String ACTION_BACKUP = "com.tf.fluxbackup.service.action.BACKUP";

    private static final String EXTRA_PACKAGE_NAME = "com.tf.fluxbackup.service.extra.PACKAGE_NAME";

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

        BackupManager.backupPackage(getBaseContext(), packageName);

        if (wasLastInQueue()) {
            showBackupCompleteNotification();
        }
    }

    private boolean wasLastInQueue() {
        return getQueueProgress() / getQueueSize() == 1;
    }

    private void showProgressNotification(String packageName) {
        String progressInPercent = (((getQueueProgress() - 1) * 100) / getQueueSize()) + "%";

        NotificationManagerCompat.from(getBaseContext())
                .notify(Constants.NOTIFICATION_BACKUP,
                        new NotificationCompat.Builder(getBaseContext())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Backing Up - " + progressInPercent)
                                .setContentText(packageName)
                                .setTicker("Backing Up - " + progressInPercent)
                                .setOngoing(true)
                                .build());
    }

    private void showBackupCompleteNotification() {
        NotificationManagerCompat.from(getBaseContext())
                .notify(Constants.NOTIFICATION_BACKUP,
                        new NotificationCompat.Builder(getBaseContext())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Backup Complete")
                                .setContentText("Backed up " + getQueueSize() + " applications")
                                .setTicker("Backup Complete")
                                .build());
    }
}
