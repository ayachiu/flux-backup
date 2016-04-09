package com.tf.fluxbackup.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.ContactsContract;

import com.tf.fluxbackup.model.PackageDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by kamran on 4/3/16.
 */
public class PackageManagerHelper {

    public static List<PackageDetails> getInstalledPackages(Context context) {
        ArrayList<PackageDetails> packages = new ArrayList<>();

        packages.addAll(DatabaseLayer.getAllPackages());

        if (packages.size() == 0) {
            final PackageManager packageManager = context.getPackageManager();

            List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

            for (PackageInfo installedPackage : installedPackages) {
                DatabaseLayer.addPackage(new PackageDetails(installedPackage.packageName,
                        installedPackage.applicationInfo.loadLabel(packageManager).toString()));
            }

            packages.addAll(DatabaseLayer.getAllPackages());
        }

        return packages;
    }
}
