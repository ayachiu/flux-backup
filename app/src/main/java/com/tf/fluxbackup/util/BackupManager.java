package com.tf.fluxbackup.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by kamran on 4/2/16.
 */
public class BackupManager {

    public static boolean backupPackage(Context context, String packageName) {
        boolean success = true;

        File backupFolder = new File(Constants.BACKUP_LOCATION + packageName);

        try {
            backupFolder.mkdirs();

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
                    + " " + Constants.BACKUP_LOCATION + packageName + "/\n";
            command += "cp " + context.getFilesDir().getAbsolutePath() + "/data.tar.gz"
                    + " " + Constants.BACKUP_LOCATION + packageName + "/\n";

            // Clean up
            command += "rm -rd " + context.getFilesDir().getAbsolutePath() + "/*\n";

            ShellScriptHelper.executeShell(command);

            File[] backupFiles = backupFolder.listFiles();

            if (!backupFolder.exists()
                    || backupFolder.list().length < 2
                    || backupFolder.length() < 1000
                    || System.currentTimeMillis() - backupFiles[0].lastModified() > 5000
                    || System.currentTimeMillis() - backupFiles[1].lastModified() > 5000) {
                success = false;
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();

            success = false;
        }

        if (!success) {
            deleteDirectory(backupFolder);
        }

        return success;
    }

    public static boolean restorePackage(Context context, String packageName) {
        boolean success = true;

        try {
            new File(Constants.BACKUP_LOCATION + packageName).mkdirs();

            String command = "";

            // Copy application archives from backup location
            command += "cp " + Constants.BACKUP_LOCATION + packageName + "/*"
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

            ShellScriptHelper.executeShell(command);

            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);

            // TODO Handle multi user environment
            command = "chown -R u0_a" + String.valueOf(applicationInfo.uid).substring(2) + " /data/data/" + packageName;
            ShellScriptHelper.executeShell(command);
        } catch (InterruptedException | IOException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();

            success = false;
        }

        return success;
    }

    public static List<String> getAllBackedUpPackages() {
        ArrayList<String> backedUpPackages = new ArrayList<>();

        File backupDirectory = new File(Constants.BACKUP_LOCATION);

        if (backupDirectory.exists()) {
            File[] files = backupDirectory.listFiles();

            for (File file : files) {
                backedUpPackages.add(file.getName());
            }

            Collections.sort(backedUpPackages, new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return lhs.compareTo(rhs);
                }
            });
        }

        return backedUpPackages;
    }

    public static void deleteBackups(final List<String> packages) {
        File backupDirectory = new File(Constants.BACKUP_LOCATION);

        if (backupDirectory.exists()) {
            File[] filesToBeDeleted = backupDirectory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return packages.contains(filename);
                }
            });

            for (File file : filesToBeDeleted) {
                deleteDirectory(file);
            }
        }
    }

    public static void deleteDirectory(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] subFiles = file.listFiles();

                for (File subFile : subFiles) {
                    if (subFile.isDirectory()) {
                        deleteDirectory(subFile);
                    } else {
                        subFile.delete();
                    }
                }
            }

            file.delete();
        }
    }
}
