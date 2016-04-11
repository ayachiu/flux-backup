package com.tf.fluxbackup.util;

import android.util.Log;

import com.tf.fluxbackup.MainApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;

/**
 * Created by kamran on 16/12/14.
 */
public class Logger {
    private static final String APP_NAME = "FluxBackup";

    public static boolean ENABLED = true;

    public static void i(String tag, String message) {
        if (ENABLED)
            try {
                Log.i(tag, message);
            } catch (Exception e) {
                //e.printStackTrace();
            }
    }

    public static void d(String tag, String message) {
        if (ENABLED)
            try {
                Log.d(tag, message);
            } catch (Exception e) {
                //e.printStackTrace();
            }
    }

    public static void e(String tag, String message) {
        if (ENABLED)
            try {
                Log.e(tag, message);
            } catch (Exception e) {
                //e.printStackTrace();
            }
    }

    public static void v(String tag, String message) {
        if (ENABLED)
            try {
                Log.v(tag, message);
            } catch (Exception e) {
                //e.printStackTrace();
            }
    }

    public static void w(String tag, String message) {
        if (ENABLED)
            try {
                Log.w(tag, message);
            } catch (Exception e) {
                //e.printStackTrace();
            }
    }

    public static void wtf(String tag, String message) {
        if (ENABLED)
            try {
                Log.wtf(tag, message);
            } catch (Exception e) {
                //e.printStackTrace();
            }
    }

    public static void printStackTrace(Throwable e) {
        if (!ENABLED)
            return;

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        e.printStackTrace(printWriter);

        Log.wtf(e.getClass().getName(), stringWriter.toString());
    }

    public static Thread.UncaughtExceptionHandler uncaughtExceptionHandler
            = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            ex.printStackTrace();

            try {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                ex.printStackTrace(printWriter);

                String stackTrace = stringWriter.toString();

                File file = getLogFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                fileOutputStream.write(stackTrace.getBytes());
                fileOutputStream.close();

                if (ENABLED)
                    Log.e("Uncaught", stackTrace);

                MainApplication.defaultExceptionHandler.uncaughtException(thread, ex);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public static void logToFile(String log) {
        try {
            FileOutputStream outputStream = new FileOutputStream(getLogFile());

            outputStream.write(log.getBytes());

            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getLogFile() {
        File file = new File(Constants.CACHE_LOCATION
                + "log-" + Calendar.getInstance().getTime() + ".txt");

        file.getParentFile().mkdirs();

        return file;
    }
}
