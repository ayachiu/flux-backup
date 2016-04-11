package com.tf.fluxbackup.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.tf.fluxbackup.model.PackageDetails;
import com.tf.fluxbackup.util.DatabaseLayer;

public class PackageChangeReceiver extends BroadcastReceiver {

    private static final String TAG = PackageChangeReceiver.class.getSimpleName();

    public PackageChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getData().getEncodedSchemeSpecificPart();

        if (packageName.equals(context.getApplicationContext().getPackageName())
                || DatabaseLayer.getAllPackages().size() == 0) {
            return;
        }

        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            Log.d(TAG, "Removed " + packageName);

            DatabaseLayer.deletePackage(packageName);
        } else {
            Log.d(TAG, intent.getAction() + " - " + packageName);

            try {
                PackageManager packageManager = context.getPackageManager();

                PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA);

                PackageDetails packageDetails = new PackageDetails(packageName,
                        packageInfo.applicationInfo.loadLabel(packageManager).toString());

                if (DatabaseLayer.updatePackage(packageDetails) == 0) {
                    DatabaseLayer.addPackage(packageDetails);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
