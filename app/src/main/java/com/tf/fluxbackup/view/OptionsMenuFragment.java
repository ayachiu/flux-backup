package com.tf.fluxbackup.view;

import android.support.v4.app.Fragment;
import android.view.MenuItem;

/**
 * Created by kamran on 4/3/16.
 */
public abstract class OptionsMenuFragment extends Fragment {

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public abstract int getMenuResource();
}
