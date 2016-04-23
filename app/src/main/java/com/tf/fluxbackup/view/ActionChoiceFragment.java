package com.tf.fluxbackup.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.tf.fluxbackup.R;
import com.tf.fluxbackup.util.AnalyticsHelper;

public class ActionChoiceFragment extends Fragment {

    private static final String TAG = ActionChoiceFragment.class.getSimpleName();

    private MainActivity mainActivity;
    private View btnActionBackup;
    private View btnActionRestore;

    public ActionChoiceFragment() {
        // Required empty public constructor
    }

    public static ActionChoiceFragment newInstance() {
        return new ActionChoiceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_action_choice, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = ((MainActivity) getActivity());
        mainActivity.clearMenu();

        btnActionBackup = view.findViewById(R.id.btn_action_backup);
        btnActionRestore = view.findViewById(R.id.btn_action_restore);

        btnActionBackup.setOnClickListener(btnActionBackupOnClickListener);
        btnActionRestore.setOnClickListener(btnActionRestoreOnClickListener);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getContext() != null) {
                    btnActionBackup.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.emphasize_button));
                }
            }
        }, 1000);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getContext() != null) {
                    btnActionRestore.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.emphasize_button));
                }
            }
        }, 1500);
    }

    @Override
    public void onStart() {
        super.onStart();

        AnalyticsHelper.sendScreenView(TAG);
    }

    private View.OnClickListener btnActionBackupOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mainActivity.changeFragment(BackupFragment.newInstance());
        }
    };

    private View.OnClickListener btnActionRestoreOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mainActivity.changeFragment(RestoreFragment.newInstance());
        }
    };
}
