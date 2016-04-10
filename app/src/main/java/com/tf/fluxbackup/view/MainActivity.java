package com.tf.fluxbackup.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.tf.fluxbackup.R;
import com.tf.fluxbackup.util.BackupManager;
import com.tf.fluxbackup.util.ShellScriptHelper;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Menu menu;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            String output = ShellScriptHelper.executeShell("ls /data/data");

            if (output.equals("") || output.contains("denied")) {
                showNoRootMessage();

                return;
            }
        } catch (IOException | InterruptedException e) {
            showNoRootMessage();

            return;
        }

        currentFragment = ActionChoiceFragment.newInstance();

        if (BackupManager.getAllBackedUpPackages().size() == 0) {
            currentFragment = BackupFragment.newInstance();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, currentFragment)
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .commit();
    }

    private void showNoRootMessage() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Requires Root Access")
                .setMessage("Sorry, but this application works only with root access. :(")
                .setPositiveButton("I understand", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        MainActivity.this.finish();
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        if (currentFragment instanceof OptionsMenuFragment) {
            getMenuInflater().inflate(((OptionsMenuFragment) currentFragment).getMenuResource(), menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (currentFragment != null && currentFragment instanceof OptionsMenuFragment) {
            return currentFragment.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            super.onBackPressed();
        }
    }

    public void changeFragment(Fragment fragment) {
        currentFragment = fragment;

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out,
                        R.anim.slide_left_in, R.anim.slide_right_out)
                .replace(R.id.root_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void changeMenu(int menuResource) {
        clearMenu();

        if (menu != null) {
            getMenuInflater().inflate(menuResource, menu);
        }
    }

    public void clearMenu() {
        if (menu != null) {
            menu.clear();
        }
    }
}
