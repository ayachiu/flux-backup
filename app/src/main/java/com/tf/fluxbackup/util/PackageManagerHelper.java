package com.tf.fluxbackup.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * Created by kamran on 4/3/16.
 */
public class PackageManagerHelper {

    public static List<PackageInfo> getInstalledPackages(Context context) {
        return context.getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
    }
}
