package com.tf.fluxbackup.model;

/**
 * Created by kamran on 4/9/16.
 */
public class PackageDetails {

    private String packageName;
    private String label;

    public PackageDetails(String packageName, String label) {
        this.packageName = packageName;
        this.label = label;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
