package com.hr.navigator.customer.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.hr.navigator.customer.ui.profile.ProfileActivity
import com.hr.navigator.customer.R
import com.hr.navigator.customer.base.BaseActivity
import com.hr.navigator.customer.base.extentions.addOnBackPressedDispatcher
import com.hr.navigator.customer.base.extentions.hideKeyboard
import com.hr.navigator.customer.base.extentions.startActivityWithFadeInAnimation
import com.hr.navigator.customer.base.extentions.toastShort
import com.hr.navigator.customer.databinding.ActivityMainBinding
import com.hr.navigator.customer.ui.home.DashboardActivity
import com.hr.navigator.customer.ui.profile.UserModel
import com.hr.navigator.customer.utils.HandlerTimer
import com.hr.navigator.customer.utils.PrefUtil
import com.hr.navigator.customer.utils.UtilsMethod
import java.util.concurrent.TimeUnit

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private var phoneNumber = ""
    private var otpValue = ""
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    private lateinit var handlerTimer: HandlerTimer
    private lateinit var timeHandler: Handler
    private lateinit var timeRunnable: Runnable

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        initClicks()
    }

    private fun initViews() {
        auth = FirebaseAuth.getInstance()
        handlerTimer = HandlerTimer()
        timeHandler = handlerTimer.timeHandler
        timeRunnable = handlerTimer.timeRunnable


        addOnBackPressedDispatcher {
            finish()
        }

        handlerTimer.setOnTimeListener(object : HandlerTimer.TimerTickListener {
            override fun onTickListener(milliSeconds: Long) {
                if (milliSeconds == 0L) {
                    onTimerStop()
                } else {
                    val second = milliSeconds / 1000
                    val convertSeconds = UtilsMethod.convertSeconds(second)
                    val text = String.format(
                        resources.getString(R.string.label_otp_time_out),
                        convertSeconds
                    )
                    binding.otpControls.btnResendCode.visibility = View.GONE
                    binding.otpControls.txtResendCount.visibility = View.VISIBLE
                    binding.otpControls.txtResendCount.text = text
                }
            }
        })

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.e("Auth", "onVerificationCompleted:$credential")
                progressDialogs.dismissDialog()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressDialogs.dismissDialog()
                Log.e("Auth", "onVerificationFailed", e)
                toastShort("Verification failed")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken, ) {
                toastShort("Verification code sent successfully")
                Log.e("Auth", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
                binding.layoutLogin.visibility = View.GONE
                binding.layoutOTP.visibility = View.VISIBLE
                progressDialogs.dismissDialog()
                onTimerStart()
            }
        }
    }

    private fun initClicks() {
        binding.lToolbar.btnBack.setOnClickListener {
            finish()
        }
        binding.loginControls.btnGetStarted.setOnClickListener {
            if (!isCheckValidationPhone()) {
                hideKeyboard()
                val mPhoneNumber = "+91$phoneNumber"
                startPhoneNumberVerification(mPhoneNumber)
            }
        }

        binding.otpControls.btnNext.setOnClickListener {
            if (!isCheckValidationOTP()) {
                hideKeyboard()
                verifyPhoneNumberWithCode(storedVerificationId, otpValue)
            }
        }

        binding.otpControls.btnResendCode.setOnClickListener {
            val mPhoneNumber = "+91$phoneNumber"
            resendVerificationCode(mPhoneNumber, resendToken)
        }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        progressDialogs.showProgressDialog()
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }



    private fun isCheckValidationPhone(): Boolean {
        var isCheck = false
        phoneNumber = binding.loginControls.editLogin.text.toString().trim()
        if (phoneNumber.isEmpty()) {
            isCheck = true
            toastShort(getString(R.string.label_error_enter_mob_num))
        }
        return isCheck
    }


    private fun isCheckValidationOTP(): Boolean {
        var isCheck = false
        otpValue = binding.otpControls.otpView.otp.toString()
        if (otpValue.isEmpty()) {
            isCheck = true
            toastShort(getString(R.string.label_enter_otp))
        }
        return isCheck
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        progressDialogs.showProgressDialog()
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?,
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
        if (token != null) {
            optionsBuilder.setForceResendingToken(token)
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    private fun onTimerStart() {
        if (!handlerTimer.isRunning) {
            handlerTimer.stopHandler = false
            timeHandler.postDelayed(timeRunnable, 0L)
        }
    }

    private fun onTimerStop() {
        binding.otpControls.btnResendCode.visibility = View.VISIBLE
        binding.otpControls.txtResendCount.visibility = View.GONE
        handlerTimer.removeTimerCallbacks()
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result.user
                    val getNumber = user?.phoneNumber
                    PrefUtil.putStringPref(PrefUtil.PREF_PHONE_NUMBER,getNumber,applicationContext)
                    PrefUtil.putBooleanPref(PrefUtil.PRF_IS_LOGIN,true,applicationContext)
                    onTimerStop()
                    progressDialogs.dismissDialog()
                    toastShort("Login successfully")
                    isCheckAlreadyData()
                } else {
                    progressDialogs.dismissDialog()
                    Log.e("Auth", "onCodeSent:${task.exception.toString()}")
                    toastShort("Something went wrong. Please try again later.")
                }
            }
    }


    private fun isCheckAlreadyData() {
        progressDialogs.showProgressDialog()
        val mPhoneNumber = "+91$phoneNumber"
        val postRef = FirebaseDatabase.getInstance().reference.child("Users")
        postRef.orderByChild("phone").equalTo(mPhoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    progressDialogs.dismissDialog()
                    if (dataSnapshot.exists()) {
                        for (snapshot in dataSnapshot.children) {
                            val userModel = snapshot.getValue(UserModel::class.java)
                            PrefUtil.putBooleanPref(PrefUtil.PREF_IS_PROFILE_FILLED, true, applicationContext)
                            PrefUtil.putStringPref(PrefUtil.PREF_USER_MODEL, Gson().toJson(userModel),applicationContext)
                            startActivityWithFadeInAnimation(DashboardActivity.getIntent(applicationContext))
                            Log.e("Auth", "Data exists:${Gson().toJson(userModel)}")
                        }
                    } else {
                        startActivityWithFadeInAnimation(ProfileActivity.getIntent(applicationContext))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    progressDialogs.dismissDialog()
                    toastShort("Something went wrong. Please try again later.")
                }
            })
    }
}