package com.hr.navigator.customer.utils;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AlertDialog;

import com.hr.navigator.customer.R;


public class JSDialogUtils {

    private Activity mContext;
    private AlertDialog dialog;

    public JSDialogUtils(Activity mContext) {
        this.mContext = mContext;
    }

    public void showProgressDialog() {
        LayoutInflater factory = LayoutInflater.from(mContext);
        View dialogView = factory.inflate(R.layout.layout_progress_bar, null);
        dialog = new AlertDialog.Builder(mContext).create();
        dialog.setCancelable(false);
        dialog.setView(dialogView);
        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
