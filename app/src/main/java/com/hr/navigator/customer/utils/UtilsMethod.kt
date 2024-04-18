package com.hr.navigator.customer.utils

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.location.Address
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsControllerCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hr.navigator.customer.ui.profile.UserModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


open class UtilsMethod {

    companion object {

        fun generateCompanyId(): String {
            val uniqueKeys = mutableSetOf<String>()
            val random = java.util.Random()

            while (uniqueKeys.size < 1000) {
                val key = String.format("%03d", random.nextInt(1000))
                uniqueKeys.add(key)
            }

            return uniqueKeys.random()
        }

        fun generateAddress(address: Address): String {
            var addressLine = ""
            val city: String = address.locality
            val state: String = address.adminArea
            val country: String = address.countryName
            val postalCode: String = address.postalCode
            addressLine= "$city, $state, $country, $postalCode"
            return addressLine
        }


        fun generateShortAddress(address: Address): String {
            var addressLine = ""
            val city: String = address.locality
            val postalCode: String = address.postalCode
            addressLine= "$city, $postalCode"
            return addressLine
        }


        fun changeStatusBarColor(colorValue: Int, activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val window: Window = activity.window
                val decorView: View = window.decorView
                val wic = WindowInsetsControllerCompat(window, decorView)
                wic.isAppearanceLightStatusBars = true
                window.statusBarColor = colorValue
            }
        }

        fun getCurrentVersionName(context: Context): String {
            var versionName = ""
            try {
                val pInfo: PackageInfo =
                    context.packageManager.getPackageInfo(context.packageName, 0)
                versionName = pInfo.versionName.toString()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return versionName
        }

        fun convertSeconds(input: Long): String {
            return if (input >= 10L) {
                input.toString()
            } else {
                "0$input"
            }
        }

        fun timeStampFormatter(value: Long): String {
            val formatter = SimpleDateFormat("dd MMM, yyyy hh:mm")
            return formatter.format(Date(value))
        }

        fun getCurrentTimeDate(): String {
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
            return df.format(Calendar.getInstance().time)
        }

        fun bitmapToFile(mBitmap: Bitmap, context: Context): File {
            val f = File(context.cacheDir, System.currentTimeMillis().toString())
            f.createNewFile()

            val bitmap: Bitmap = mBitmap
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
            val bitmapdata: ByteArray = bos.toByteArray()

            val fos = FileOutputStream(f)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            return f
        }


        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        var storage_permissions_33 = arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
        )

        var storage_permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        fun permissions(): Array<String> {
            val p: Array<String>
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                p = storage_permissions_33
            } else {
                p = storage_permissions
            }
            return p
        }

        fun appInForeground(context: Context): Boolean {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningAppProcesses = activityManager.runningAppProcesses ?: return false
            return runningAppProcesses.any { it.processName == context.packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND }
        }

        fun appIsRunning(ctx: Context): Boolean {
            val activityManager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val tasks = activityManager.getRunningTasks(Int.MAX_VALUE)
            for (task in tasks) {
                if (ctx.packageName.equals(
                        task.baseActivity!!.packageName,
                        ignoreCase = true
                    )
                ) return true
            }
            return false
        }


        fun getUPIHandle(qrValue: String): String {
            var upiHandle = ""
            var originalUpiHandle = ""
            try {
                if (qrValue.isNotEmpty()) {
                    Log.e("qrValue: ", "===> $qrValue")

                    val paValueList: Array<String> = qrValue.split("?").toTypedArray()
                    for (pValue in paValueList) {
                        Log.e("pValue: ", "===> $pValue")
                    }

                    if (paValueList.isNotEmpty()) {
                        val qrValueList: Array<String> = paValueList[1].split("&").toTypedArray()
                        for (qValue in qrValueList) {
                            Log.e("qValue: ", "===> $qValue")
                            if (qValue.startsWith("pa=")) {
                                upiHandle = qValue
                            }
                        }
                        Log.e("upiHandle: ", "===> $upiHandle")
                        val sub = "pa="
                        originalUpiHandle = upiHandle.substring(upiHandle.indexOf(sub) + sub.length)
                        Log.e("originalUpiHandle: ", "===> $originalUpiHandle")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return originalUpiHandle
        }

        fun rotateBitmap(bitmap: Bitmap): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(90F)
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)
            return Bitmap.createBitmap(
                scaledBitmap,
                0,
                0,
                scaledBitmap.width,
                scaledBitmap.height,
                matrix,
                true
            )
        }

        fun convertStringToUserModel(mContext: Context): UserModel {
            val userJson = PrefUtil.getStringPref(PrefUtil.PREF_USER_MODEL, mContext)
            val tokenType = object : TypeToken<UserModel>() {}.type
            val userModel: UserModel = Gson().fromJson(userJson, tokenType)
            return userModel
        }

         fun dateFormatterCurrentDate(): String{
            var returnDate = ""
            try {
                val sdfTime = SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa", Locale.getDefault())
                returnDate = sdfTime.format(Date())
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return returnDate
        }

    }



}