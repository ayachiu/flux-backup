package com.tf.fluxbackup.util;

/**
 * Created by kamran on 4/4/16.
 */
public class Constants {

    public static final int NOTIFICATION_BACKUP = 49;
    public static final int NOTIFICATION_RESTORE = 50;

    public static final String APP_DATA_LOCATION = System.getenv("EXTERNAL_STORAGE") + "/team.fluxion/";
    public static final String BACKUP_LOCATION = APP_DATA_LOCATION + "FluxBackup/backups/";
    public static final String CACHE_LOCATION = APP_DATA_LOCATION + "FluxBackup/cache/";

}
