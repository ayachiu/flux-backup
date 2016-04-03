package com.tf.fluxbackup.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.tf.fluxbackup.R;
import com.tf.fluxbackup.model.OptionsMenuFragment;

public class RestoreFragment extends OptionsMenuFragment {

    public RestoreFragment() {
        // Required empty public constructor
    }

    public static RestoreFragment newInstance() {
        return new RestoreFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restore, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).changeMenu(R.menu.menu_restore);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
