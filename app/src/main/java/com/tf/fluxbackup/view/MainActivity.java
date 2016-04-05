package com.tf.fluxbackup.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.tf.fluxbackup.R;
import com.tf.fluxbackup.model.OptionsMenuFragment;
import com.tf.fluxbackup.util.BackupManager;

public class MainActivity extends AppCompatActivity {

    private Menu menu;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentFragment =  ActionChoiceFragment.newInstance();

        if (BackupManager.getAllBackedUpPackages().size() == 0) {
            currentFragment = BackupFragment.newInstance();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, currentFragment)
                .commit();
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
