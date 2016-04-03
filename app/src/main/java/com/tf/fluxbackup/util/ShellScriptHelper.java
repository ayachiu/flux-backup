package com.tf.fluxbackup.util;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kamran on 4/3/16.
 */
public class ShellScriptHelper {
    
    static void executeShell(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("su");
        DataOutputStream os = new DataOutputStream(process.getOutputStream());
        os.writeBytes(command);
//        os.writeBytes("\nexit\n");
        os.flush();
        os.close();
        process.waitFor();
    }
}
