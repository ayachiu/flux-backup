package com.tf.fluxbackup.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by kamran on 4/3/16.
 */
public class PackageManagerHelper {

    public static List<PackageInfo> getInstalledPackages(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

        Collections.sort(installedPackages, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo lhs, PackageInfo rhs) {
                return lhs.applicationInfo.loadLabel(packageManager).toString()
                        .compareTo(rhs.applicationInfo.loadLabel(packageManager).toString());
            }
        });

        return installedPackages;
    }
}
