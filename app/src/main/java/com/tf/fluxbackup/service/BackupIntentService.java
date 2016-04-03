package com.tf.fluxbackup.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.tf.fluxbackup.util.BackupManager;

public class BackupIntentService extends IntentService {

    private static final String ACTION_PACKAGE = "com.tf.fluxbackup.service.action.BACKUP";

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
        intent.setAction(ACTION_PACKAGE);
        intent.putExtra(EXTRA_PACKAGE_NAME, packageName);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PACKAGE.equals(action)) {
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
        BackupManager.backupPackage(getBaseContext(), packageName);
    }
}
