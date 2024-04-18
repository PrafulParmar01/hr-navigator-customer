package com.hr.navigator.customer.utils

import android.os.Handler
import android.os.Looper
import android.util.Log

class HandlerTimer {

    val timeHandler: Handler = Handler(Looper.getMainLooper())
    var isRunning = false
    var stopHandler = false

    private var mSeconds = 1000L
    private var mTimerTickListener: TimerTickListener? = null
    private var totalMinutesDelayed = 21000L // 20 seconds delayed timer...

    val timeRunnable: Runnable = object : Runnable {
        override fun run() {
            if (!stopHandler) {
                try {
                    isRunning = true
                    if (mTimerTickListener != null) {
                        mTimerTickListener?.onTickListener(totalMinutesDelayed - mSeconds)
                    }
                    mSeconds += 1000L

                    timeHandler.postDelayed(this, UPDATE_INTERVAL)
                } catch (e: Exception) {
                    e.printStackTrace()
                    isRunning = false
                }
            }
        }
    }

    fun removeTimerCallbacks() {
        timeHandler.removeCallbacksAndMessages(null)
        isRunning = false
        stopHandler = true
        totalMinutesDelayed = 21000L
        mSeconds = 1000L
        Log.e("removeTimerCallbacks : ", "===> cleared $stopHandler")
    }

    interface TimerTickListener {
        fun onTickListener(milliSeconds: Long)
    }

    fun setOnTimeListener(timerTickListener: TimerTickListener) {
        mTimerTickListener = timerTickListener
    }

    companion object {
        private const val UPDATE_INTERVAL = 1000L
    }
}