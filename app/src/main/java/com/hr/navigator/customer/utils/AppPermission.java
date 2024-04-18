package com.hr.navigator.customer.utils;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

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
            permissionsList.add(Manifest.permission.READ_MEDIA_IMAGES);
            permissionsList.add(Manifest.permission.CAMERA);
        } else if (SDK_INT >= Build.VERSION_CODES.S) {
            //API Level (12) 32
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionsList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionsList.add(Manifest.permission.CAMERA);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            //API level 29
            permissionsList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionsList.add(Manifest.permission.CAMERA);
        } else {
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionsList.add(Manifest.permission.CAMERA);
        }
        /*Handle Permissions*/
        return permissionsList;
    }


    public boolean isAllPermissionGranted() {
        boolean isCheck = false;
        int count =0;
        ArrayList<String> permissionList = getPermissionList();
        String[] mStringArray = new String[permissionList.size()];
        for (int i = 0; i < permissionList.size(); i++) {
            mStringArray = permissionList.toArray(mStringArray);
            if (ContextCompat.checkSelfPermission(activity, permissionList.get(i)) != PackageManager.PERMISSION_GRANTED) {
                count++;
            }
        }

        if(count>0){
            isCheck = true;
        }
        return isCheck;
    }
}
