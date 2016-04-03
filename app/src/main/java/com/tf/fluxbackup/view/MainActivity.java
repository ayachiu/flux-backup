package com.tf.fluxbackup.view;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tf.fluxbackup.BackupManager;
import com.tf.fluxbackup.R;

public class MainActivity extends AppCompatActivity {

    private Button btnBackup;
    private Button btnRestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBackup = (Button) findViewById(R.id.btn_backup);
        btnBackup.setOnClickListener(btnBackupOnClickListener);

        btnRestore = (Button) findViewById(R.id.btn_restore);
        btnRestore.setOnClickListener(btnRestoreOnClickListener);
    }

    private View.OnClickListener btnBackupOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Handler mainHandler = new Handler(Looper.getMainLooper());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    BackupManager.backupPackage(MainActivity.this, "com.tf.SoundEmUp");

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Backup complete", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        }
    };

    private View.OnClickListener btnRestoreOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Handler mainHandler = new Handler(Looper.getMainLooper());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    BackupManager.restorePackage(MainActivity.this, "com.tf.SoundEmUp");

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Backup complete", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        }
    };
}
