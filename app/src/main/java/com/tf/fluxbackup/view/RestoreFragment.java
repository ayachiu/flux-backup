package com.tf.fluxbackup.view;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tf.fluxbackup.R;
import com.tf.fluxbackup.model.OptionsMenuFragment;
import com.tf.fluxbackup.service.RestoreIntentService;
import com.tf.fluxbackup.util.BackupManager;

import java.util.ArrayList;
import java.util.List;

public class RestoreFragment extends OptionsMenuFragment {

    private static final String TAG = RestoreFragment.class.getSimpleName();

    private RecyclerView listBackups;
    private List<String> backedUpPackages;
    private List<String> selectedPackages = new ArrayList<>();

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

        listBackups = (RecyclerView) view.findViewById(R.id.list_backups);

        new BackupFetcher().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_restore) {
            restoreSelectedPackages();

            return true;
        } else if (item.getItemId() == R.id.action_select_all) {
            selectAllPackages();

            return true;
        } else if (item.getItemId() == R.id.action_select_none) {
            unselectAllPackages();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void unselectAllPackages() {
        selectedPackages.clear();

        listBackups.getAdapter().notifyDataSetChanged();
    }

    private void selectAllPackages() {
        for (String packageName : backedUpPackages) {
            if (!selectedPackages.contains(packageName)) {
                selectedPackages.add(packageName);
            }
        }

        listBackups.getAdapter().notifyDataSetChanged();
    }

    private void restoreSelectedPackages() {
        for (String packageName : selectedPackages) {
            RestoreIntentService.restore(getContext(), packageName);
        }
    }

    private class BackupFetcher extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getContext(), "Please Wait", "Gathering information about your backed up applications", true, false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            backedUpPackages = BackupManager.getAllBackedUpPackages();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            listBackups.setLayoutManager(new LinearLayoutManager(getContext()));

            listBackups.setAdapter(new BackupAdapter());

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }

    private class BackupAdapter extends RecyclerView.Adapter<ApplicationViewHolder> {

        public BackupAdapter() {

        }

        @Override
        public ApplicationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ApplicationViewHolder(LayoutInflater.from(getContext())
                    .inflate(R.layout.application_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ApplicationViewHolder holder, int position) {
            holder.lblName.setText(backedUpPackages.get(position));

            holder.chkSelect.setChecked(selectedPackages.contains(backedUpPackages.get(position)));
        }

        @Override
        public int getItemCount() {
            return backedUpPackages.size();
        }
    }

    private class ApplicationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        ImageView imgIcon;
        TextView lblName;
        CheckBox chkSelect;

        public ApplicationViewHolder(View itemView) {
            super(itemView);

            imgIcon = (ImageView) itemView.findViewById(R.id.img_icon);
            lblName = (TextView) itemView.findViewById(R.id.lbl_name);
            chkSelect = (CheckBox) itemView.findViewById(R.id.chk_select);

            itemView.setOnClickListener(this);
            chkSelect.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View v) {
            chkSelect.performClick();
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                selectedPackages.add(backedUpPackages.get(getLayoutPosition()));
            } else {
                selectedPackages.remove(backedUpPackages.get(getLayoutPosition()));
            }
        }
    }
}
