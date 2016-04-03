package com.tf.fluxbackup.view;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import com.tf.fluxbackup.util.PackageManagerHelper;

import java.util.ArrayList;
import java.util.List;

public class BackupFragment extends OptionsMenuFragment {

    private static final String TAG = BackupFragment.class.getSimpleName();
    private RecyclerView listApplications;

    private List<PackageInfo> applicationInfos;
    private List<String> selectedPackages = new ArrayList<>();

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

        ((MainActivity) getActivity()).changeMenu(R.menu.menu_backup);

        listApplications = (RecyclerView) view.findViewById(R.id.list_applications);

        applicationInfos = PackageManagerHelper.getInstalledPackages(getContext());

        listApplications.setLayoutManager(new LinearLayoutManager(getContext()));
        listApplications.setAdapter(new ApplicationAdapter());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
            holder.imgIcon.setImageDrawable(applicationInfos.get(position).applicationInfo.loadIcon(packageManager));

            holder.lblName.setText(applicationInfos.get(position).applicationInfo.loadLabel(packageManager));

            holder.chkSelect.setChecked(selectedPackages.contains(applicationInfos.get(position).packageName));
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
                selectedPackages.add(applicationInfos.get(getLayoutPosition()).packageName);
            } else {
                selectedPackages.remove(applicationInfos.get(getLayoutPosition()).packageName);
            }
        }
    }
}
