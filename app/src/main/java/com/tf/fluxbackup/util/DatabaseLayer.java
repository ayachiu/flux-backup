package com.tf.fluxbackup.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import com.tf.fluxbackup.model.PackageDetails;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by kamran on 20/8/15.
 */
public class DatabaseLayer {

    private static final String TAG = "DatabaseLayer";

    private static final int VERSION = 1;

    private static SQLiteDatabase database;

    private static class TablePackage {
        public static final String TABLE_NAME = "package";

        public static final String ID = "id";
        public static final String PACKAGE = "package_name";
        public static final String LABEL = "label";

        public static void createTable() {
            executeUpdate("CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + PACKAGE + " TEXT, " + LABEL + " TEXT)");
        }
    }

    public static void initialize(Context context) {
        database = SQLiteDatabase.openOrCreateDatabase(context.getFilesDir().getAbsolutePath()
                + "/database.db", null);

        if (!isTablePresent(TablePackage.TABLE_NAME))
            TablePackage.createTable();

        database.setVersion(VERSION);
    }

    public static void addPackage(PackageDetails packageDetails) {
        executeUpdate("INSERT INTO " + TablePackage.TABLE_NAME + " (" + TablePackage.PACKAGE + ", "
                + TablePackage.LABEL + ") VALUES ('"
                + packageDetails.getPackageName() + "', '"
                + new String(Base64.encode(packageDetails.getLabel().getBytes(), Base64.NO_WRAP)) + "')");
    }

    public static int updatePackage(PackageDetails packageDetails) {
        ContentValues values = new ContentValues();
        values.put(TablePackage.PACKAGE, packageDetails.getPackageName());
        values.put(TablePackage.LABEL,
                new String(Base64.encode(packageDetails.getLabel().getBytes(), Base64.NO_WRAP)));

        return database.update(TablePackage.TABLE_NAME, values,
                TablePackage.PACKAGE  + " = '" + packageDetails.getPackageName() + "'",null);
    }

    public static ArrayList<PackageDetails> getAllPackages() {
        ArrayList<PackageDetails> packages = new ArrayList<>();

        Cursor cursor = executeQuery("SELECT * FROM " + TablePackage.TABLE_NAME
                + " ORDER BY " + TablePackage.LABEL);

        while (cursor.moveToNext()) {
            packages.add(new PackageDetails(cursor.getString(cursor.getColumnIndex(TablePackage.PACKAGE)),
                    new String(Base64.decode(cursor.getString(cursor.getColumnIndex(TablePackage.LABEL)).getBytes(),
                            Base64.NO_WRAP))));
        }

        Collections.sort(packages, new Comparator<PackageDetails>() {
            @Override
            public int compare(PackageDetails lhs, PackageDetails rhs) {
                return lhs.getLabel().compareTo(rhs.getLabel());
            }
        });

        cursor.close();

        return packages;
    }

    public static String getLabel(String packageName) {
        String label = null;

        Cursor cursor = executeQuery("SELECT " + TablePackage.LABEL + " FROM " + TablePackage.TABLE_NAME + " WHERE " + TablePackage.PACKAGE + " = '" + packageName + "'");

        if (cursor.moveToFirst())
            label = new String(Base64.decode(cursor.getString(cursor.getColumnIndex(TablePackage.LABEL)).getBytes(),
                            Base64.NO_WRAP));

        cursor.close();

        return label;
    }

    public static  void deletePackage(String packageName) {
        executeUpdate("DELETE FROM " + TablePackage.TABLE_NAME
                + " WHERE " + TablePackage.PACKAGE + " = '" + packageName + "'");
    }

    private static boolean isTablePresent(String tableName) {
        Cursor cursor = executeQuery("SELECT name FROM sqlite_master WHERE name='" + tableName + "'");

        boolean isPresent = cursor.getCount() > 0;
        cursor.close();

        return isPresent;
    }

    private static Cursor executeQuery(String query) {
        return database.rawQuery(query, null);
    }

    private static void executeUpdate(String statement) {
        database.beginTransaction();

        database.execSQL(statement);

        database.setTransactionSuccessful();
        database.endTransaction();
    }
}
