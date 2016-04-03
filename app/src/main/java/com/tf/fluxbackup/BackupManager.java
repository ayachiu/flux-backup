package com.tf.fluxbackup;

import android.content.Context;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by kamran on 4/2/16.
 */
public class BackupManager {

    private static final String BACKUP_LOCATION = "/sdcard/team.fluxion/FluxBackup/backups/";

    public static boolean backupPackage(Context context, String packageName) {
        boolean success = true;

        try {
            new File(BACKUP_LOCATION + packageName).mkdirs();

            String command = "";

            // Copy APK
            command += "cp -r /data/app/" + packageName + "* "
                    + context.getFilesDir().getAbsolutePath() + "/app\n";

            // Package APK into an archive
            command += "tar cz -C " + context.getFilesDir().getAbsolutePath() + " app"
                    + " -f " + context.getFilesDir().getAbsolutePath() + "/app.tar.gz\n";

            // Copy application data
            command += "cp -r /data/data/" + packageName + " "
                    + context.getFilesDir().getAbsolutePath() + "/data\n";

            // Package application data into an archive
            command += "tar cz -C " + context.getFilesDir().getAbsolutePath() + " data"
                    + " -f " + context.getFilesDir().getAbsolutePath() + "/data.tar.gz\n";

            // Copy application archives to backup location
            command += "cp " + context.getFilesDir().getAbsolutePath() + "/app.tar.gz"
                    + " " + BACKUP_LOCATION + packageName + "/\n";
            command += "cp " + context.getFilesDir().getAbsolutePath() + "/data.tar.gz"
                    + " " + BACKUP_LOCATION + packageName + "/\n";

            // Clean up
            command += "rm -rd " + context.getFilesDir().getAbsolutePath() + "/*\n";

            executeShell(command);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();

            success = false;
        }

        return success;
    }

    public static boolean restorePackage(Context context, String packageName) {
        boolean success = true;

        try {
            new File(BACKUP_LOCATION + packageName).mkdirs();

            String command = "";

            // Copy application archives from backup location
            command += "cp " + BACKUP_LOCATION + packageName + "/*"
                    + " " + context.getFilesDir().getAbsolutePath() + "/\n";

            // Extract and install APK from the archive
            command += "mkdir " + context.getFilesDir().getAbsolutePath() + "/app\n";
            command += "tar xz -C " + context.getFilesDir().getAbsolutePath()
                    + " -f " + context.getFilesDir().getAbsolutePath() + "/app.tar.gz\n";
            command += "pm install " + context.getFilesDir().getAbsolutePath() + "/app/base.apk\n";

            // Extract application data
            command += "mkdir " + context.getFilesDir().getAbsolutePath() + "/data\n";
            command += "tar xz -C " + context.getFilesDir().getAbsolutePath()
                    + " -f " + context.getFilesDir().getAbsolutePath() + "/data.tar.gz\n";

            // Copy application data
            command += "cp -r " + context.getFilesDir().getAbsolutePath() + "/data/*"
                    + " /data/data/" + packageName + "/\n";

            // Clean up
            command += "rm -rd " + context.getFilesDir().getAbsolutePath() + "/*\n";

            executeShell(command);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();

            success = false;
        }

        return success;
    }

    private static void executeShell(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("su");
        DataOutputStream os = new DataOutputStream(process.getOutputStream());
        os.writeBytes(command);
//        os.writeBytes("\nexit\n");
        os.flush();
        os.close();
        process.waitFor();
    }
}
