package com.hr.navigator.customer.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date


fun isInternetPresent(context: Context): Boolean {
    var result = false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
    }
    return result

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


fun getCurrentDate(): String {
    val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
    val date = Date()
    return dateFormat.format(date)
}

fun getPlanDetails(): MutableList<String> {
    val arrayList : MutableList<String> = mutableListOf()
    arrayList.add("₹70 Daily")
    arrayList.add("₹999 15 Days 1 Time")
    arrayList.add("₹1999 15 Days 2 Time")
    arrayList.add("₹1999 30 Days 1 Time")
    arrayList.add("₹3999 30 Days 2 Time")
    return arrayList
}