package com.dyddyd.sendmms.launch

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.telephony.PhoneNumberFormattingTextWatcher
import android.telephony.PhoneNumberUtils
import android.text.InputFilter
import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.dyddyd.sendmms.main.MainActivity
import com.dyddyd.sendmms.R
import com.dyddyd.sendmms.databinding.ActivityLaunchBinding
import com.dyddyd.sendmms.repository.Utils
import com.dyddyd.sendmms.repository.sharedpreference.MMSPreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*


class LaunchActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 1000
    private val PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.SEND_SMS,
        android.Manifest.permission.READ_SMS,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private val CONTACT_INTENT_CODE = 2000
    private var doubleBackToExitPressedOnce = false

    private lateinit var binding: ActivityLaunchBinding

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "\"뒤로가기\"를 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_launch)

        Utils.setStatusBarColor(this, R.color.dark_blue)

        binding.apply {
            confirmButton.isClickable = true
            contactButton.isClickable = true
        }

        MMSPreferenceManager.setInstance(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermission(PERMISSIONS)) {
                requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE)
            } else {
                if (MMSPreferenceManager.phoneNum == null) {
                    binding.apply {
                        phoneNum.apply {
                            inputType = InputType.TYPE_CLASS_PHONE
                            addTextChangedListener(PhoneNumberFormattingTextWatcher())
                            filters = arrayOf(InputFilter.LengthFilter(13))
                        }

                        confirmButton.setOnClickListener {
                            if(name.text.trim().isEmpty()) name.error = "필수 입력란입니다."
                            else name.error = null

                            if(phoneNum.text.trim().length != 13) phoneNum.error = "필수 입력란입니다."
                            else phoneNum.error = null

                            if(name.text.trim().isNotEmpty() && phoneNum.text.trim().length == 13) {
                                MMSPreferenceManager.name = name.text.trim().toString()
                                MMSPreferenceManager.phoneNum = phoneNum.text.trim().toString()
                                if(content.text.trim().isNotEmpty()) MMSPreferenceManager.content = content.text.trim().toString()

                                val intent = Intent(this@LaunchActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }

                        contactButton.setOnClickListener {
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.data = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                            startActivityForResult(intent, CONTACT_INTENT_CODE)
                        }
                    }
                } else {
                    val intent = Intent(this, MainActivity::class.java)
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
                        if (MMSPreferenceManager.phoneNum == null) {
                            val intent = Intent(this, LaunchActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == CONTACT_INTENT_CODE) {
            val cursor = contentResolver.query(
                data?.data!!,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                null,
                null,
                null
            )
            cursor?.apply {
                moveToFirst()
                binding.phoneNum.setText(PhoneNumberUtils.formatNumber(cursor.getString(1), Locale.getDefault().country))
                binding.name.setText(cursor.getString(0))
                close()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showPhoneNumDialog() {
        val phoneNum = EditText(this@LaunchActivity).apply {
            inputType = InputType.TYPE_CLASS_PHONE
            addTextChangedListener(PhoneNumberFormattingTextWatcher())
            background = null
            hint = "010-1234-5678"
            filters = arrayOf(InputFilter.LengthFilter(13))
        }

        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            leftMargin = 50
            rightMargin = 50
        }
        phoneNum.layoutParams = params
        container.addView(phoneNum)

//        val dialog = MaterialAlertDialogBuilder(this, R.style.MyAlertDialogStyle).apply {
//            setTitle("번호 입력")
//            setMessage("받는 사람 번호를 입력해주세요.\n(하이폰(-)은 자동 입력 됩니다.)")
//            setView(container)
//            setCancelable(false)
//            setPositiveButton("확인", null)
//            setNeutralButton("전화번호부에서 찾기") { dialog, which ->
//                val intent = Intent(Intent.ACTION_PICK)
//                intent.data = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
//                startActivityForResult(intent, CONTACT_INTENT_CODE)
//            }
//        }.create()
//        dialog.show()
//
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
//            if (phoneNum.text.length < 13) {
//                phoneNum.error = "전화번호를 확인해주세요."
//            } else {
//                MMSPreferenceManager.phoneNum = phoneNum.text.toString()
//                val intent = Intent(this@LaunchActivity, MainActivity::class.java)
//                startActivity(intent)
//
//                finish()
//                dialog.dismiss()
//            }
//        }
    }
}

