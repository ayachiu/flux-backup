package com.tf.fluxbackup.util;

import android.os.Environment;

/**
 * Created by kamran on 4/4/16.
 */
public class Constants {

    public static final int NOTIFICATION_BACKUP = 49;
    public static final int NOTIFICATION_RESTORE = 50;

    public static String APP_DATA_LOCATION_SHELL = System.getenv("EXTERNAL_STORAGE") + "/team.fluxion/";
    public static String APP_DATA_LOCATION = Environment.getExternalStorageDirectory().getAbsolutePath() + "/team.fluxion/";

    public static String BACKUP_LOCATION_SHELL = APP_DATA_LOCATION_SHELL + "FluxBackup/backups/";
    public static String BACKUP_LOCATION = APP_DATA_LOCATION + "FluxBackup/backups/";

    public static String CACHE_LOCATION_SHELL = APP_DATA_LOCATION_SHELL + "FluxBackup/cache/";
    public static String CACHE_LOCATION = APP_DATA_LOCATION + "FluxBackup/cache/";

}
