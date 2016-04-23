package com.tf.fluxbackup.view;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import com.tf.fluxbackup.R;
import com.tf.fluxbackup.model.PackageDetails;
import com.tf.fluxbackup.model.ProgressReporter;
import com.tf.fluxbackup.model.SimpleKeyValuePair;
import com.tf.fluxbackup.service.BackupIntentService;
import com.tf.fluxbackup.util.AnalyticsHelper;
import com.tf.fluxbackup.util.PackageManagerHelper;

import java.util.ArrayList;
import java.util.List;

public class BackupFragment extends OptionsMenuFragment {

    private static final String TAG = BackupFragment.class.getSimpleName();

    private RecyclerView listApplications;
    private List<PackageDetails> applicationInfos;
    private List<String> selectedPackages = new ArrayList<>();
    private BackupProgressReceiver progressReporter;

    public BackupFragment() {
        // Required empty public constructor
    }

    public static BackupFragment newInstance() {
        return new BackupFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_backup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).changeMenu(getMenuResource());

        listApplications = (RecyclerView) view.findViewById(R.id.list_applications);

        new ApplicationFetcher().execute();
    }

    @Override
    public void onStart() {
        super.onStart();

        progressReporter = new BackupProgressReceiver(getActivity());

        AnalyticsHelper.sendScreenView(TAG);
    }

    @Override
    public void onDestroy() {
        if (progressReporter != null) {
            progressReporter.unregister();
        }

        super.onDestroy();
    }

    @Override
    public int getMenuResource() {
        return R.menu.menu_backup;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_backup) {
            backupSelectedPackages();

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

        listApplications.getAdapter().notifyDataSetChanged();
    }

    private void selectAllPackages() {
        for (PackageDetails packageDetails : applicationInfos) {
            if (!selectedPackages.contains(packageDetails.getPackageName())) {
                selectedPackages.add(packageDetails.getPackageName());
            }
        }

        listApplications.getAdapter().notifyDataSetChanged();
    }

    private void backupSelectedPackages() {
        for (String packageName : selectedPackages) {
            BackupIntentService.backup(getContext(), packageName);
        }

        AnalyticsHelper.sendCustomEvent("Backup Queued",
                new SimpleKeyValuePair("numberOfSelectedApplications",
                        String.valueOf(selectedPackages.size())));
    }

    private class ApplicationFetcher extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getContext(), "Please Wait", "Gathering information about your installed applications", true, false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            applicationInfos = PackageManagerHelper.getInstalledPackages(getContext());

            AnalyticsHelper.sendCustomEvent("Applications Fetched",
                    new SimpleKeyValuePair("numberOfInstalledApplications",
                            String.valueOf(applicationInfos.size())));

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            listApplications.setLayoutManager(new LinearLayoutManager(getContext()));

            listApplications.setAdapter(new ApplicationAdapter());

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }

    private class ApplicationAdapter extends RecyclerView.Adapter<ApplicationViewHolder> {

        private PackageManager packageManager;

        public ApplicationAdapter() {
            packageManager = getContext().getPackageManager();
        }

        @Override
        public ApplicationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ApplicationViewHolder(LayoutInflater.from(getContext())
                    .inflate(R.layout.application_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ApplicationViewHolder holder, int position) {
            try {
                holder.imgIcon.setImageDrawable(packageManager
                        .getApplicationIcon(applicationInfos.get(position).getPackageName()));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            holder.lblName.setText(applicationInfos.get(position).getLabel());

            holder.chkSelect.setChecked(selectedPackages.contains(applicationInfos.get(position).getPackageName()));
        }

        @Override
        public int getItemCount() {
            return applicationInfos.size();
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
                selectedPackages.add(applicationInfos.get(getLayoutPosition()).getPackageName());
            } else {
                selectedPackages.remove(applicationInfos.get(getLayoutPosition()).getPackageName());
            }
        }
    }

    private class BackupProgressReceiver extends ProgressReporter {

        private ProgressDialog progressDialog;

        public BackupProgressReceiver(Context context) {
            super(context);
        }

        @Override
        public void onProgress(int progress, int total, String current) {
            if (progressDialog == null || !progressDialog.isShowing()) {
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
            }

            progressDialog.setMessage(String.format(getString(R.string.backing_up), current));
            progressDialog.setProgress(progress);
            progressDialog.setMax(total);

            if (total == progress) {
                progressDialog.dismiss();

                Toast.makeText(getContext(), R.string.backup_complete, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
