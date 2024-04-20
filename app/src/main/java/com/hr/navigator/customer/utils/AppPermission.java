package com.hr.navigator.customer.utils;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.content.Context;
import android.os.Build;

import java.util.ArrayList;

public class AppPermission {

    private ArrayList<String> permissionsList;

    private Context activity;

    public AppPermission(Context context) {
        this.activity = context;
    }

    public ArrayList<String> getPermissionList() {
        /*Handle Permissions*/
        permissionsList = new ArrayList<>();
        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //API level 33 or higher
            permissionsList.add(Manifest.permission.POST_NOTIFICATIONS);
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionsList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        } else if (SDK_INT >= Build.VERSION_CODES.S) {
            //API Level (12) 32
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionsList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            //API level 29
            permissionsList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        } else {
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        /*Handle Permissions*/
        return permissionsList;
    }
}
