package com.dyddyd.sendmms.splash

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import androidx.core.content.ContextCompat
import com.dyddyd.sendmms.R
import com.dyddyd.sendmms.launch.LaunchActivity
import com.dyddyd.sendmms.main.MainActivity
import com.dyddyd.sendmms.repository.Utils
import com.dyddyd.sendmms.repository.sharedpreference.MMSPreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class SplashActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 1000
    private val PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.SEND_SMS,
        android.Manifest.permission.READ_SMS,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onBackPressed() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Utils.setStatusBarColor(this, R.color.dark_blue)

        GlobalScope.launch {
            delay(2000L)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!hasPermission(PERMISSIONS)) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE)
                } else {
                    val intent = Intent(this@SplashActivity, LaunchActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun hasPermission(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_DENIED
            )
                return false
        }

        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    var accepted = true

                    for (result in grantResults) {
                        if (result == PackageManager.PERMISSION_DENIED) accepted = false
                    }

                    if (!accepted) {
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.")
                    } else {
                        val intent = Intent(this, LaunchActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun showDialogForPermission(msg: String) {
        val builder = MaterialAlertDialogBuilder(this, R.style.MyAlertDialogStyle)
        builder.setTitle("알림")
        builder.setMessage(msg)
        builder.setCancelable(false)
        builder.setPositiveButton("예") { dialog, which ->
            requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE)
        }
        builder.create().show()
    }

}