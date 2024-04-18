package com.hr.navigator.customer.dialogs

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import com.hr.navigator.customer.R


class LogoutDialog {

    fun openDialog(context: Activity) {
        val inflater = LayoutInflater.from(context)
        val dialogView: View = inflater.inflate(R.layout.layout_dialog_logout, null)
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setView(dialogView)
        val btnYes = dialogView.findViewById<AppCompatTextView>(R.id.btnYes)
        val btnCancel = dialogView.findViewById<AppCompatTextView>(R.id.btnCancel)
        btnYes.setOnClickListener {
            onClickListener?.onClick()
            alertDialog.dismiss()
        }
        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }


    interface OnClickListener {
        fun onClick()
    }

    private var onClickListener: OnClickListener? = null
    fun setOnDialogListener(mOnClickListener: OnClickListener?) {
        this.onClickListener = mOnClickListener
    }
}