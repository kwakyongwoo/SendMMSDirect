package com.dyddyd.sendmms.main.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.dyddyd.sendmms.R
import com.dyddyd.sendmms.databinding.FragmentProfileBinding
import com.dyddyd.sendmms.repository.Utils
import com.dyddyd.sendmms.repository.sharedpreference.MMSPreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val CONTACT_INTENT_CODE = 2000

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        Utils.setStatusBarColor(activity!!, R.color.dark_blue)
        Utils.changeStatusBarTextColorDark(activity!!)

        binding.pref = MMSPreferenceManager.Companion
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        binding.apply {
            block1.setOnClickListener {
                onClick(0)
            }
            block2.setOnClickListener {
                onClick(1)
            }
            block3.setOnClickListener {
                onClick(2)
            }
            contact.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.data = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                startActivityForResult(intent, CONTACT_INTENT_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == CONTACT_INTENT_CODE) {
            val cursor = activity!!.contentResolver.query(
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
                MMSPreferenceManager.phoneNum = cursor.getString(1)
                MMSPreferenceManager.name = cursor.getString(0)
                close()
            }
            val transaction = fragmentManager?.beginTransaction()
            transaction?.detach(this)?.attach(this)?.commit()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onClick(request: Int) {
        val titles = arrayOf("수신인 이름", "수신인 번호", "MMS 내용")
        val editText = EditText(context).apply {
            background = null
            when (request) {
                0 -> {
                    hint = "수신인 이름"
                    filters = arrayOf(InputFilter.LengthFilter(20))
                }

                1 -> {
                    inputType = InputType.TYPE_CLASS_PHONE
                    addTextChangedListener(PhoneNumberFormattingTextWatcher())
                    hint = "010-1234-5678"
                    filters = arrayOf(InputFilter.LengthFilter(13))
                }

                2 -> {
                    hint = "MMS 내용"
                }
            }
        }

        val container = FrameLayout(context!!)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            leftMargin = 50
            rightMargin = 50
        }
        editText.layoutParams = params
        container.addView(editText)

        val dialog = MaterialAlertDialogBuilder(context, R.style.MyAlertDialogStyle).apply {
            setTitle("${titles[request]} 입력")
            if (request == 1) setMessage("받는 사람 번호를 입력해주세요.\n(하이폰(-)은 자동 입력 됩니다.)")
            setView(container)
            setPositiveButton("확인", null)
            setNegativeButton("취소", null)
        }.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (editText.text.length < 13 && request == 1) {
                editText.error = "전화번호를 확인해주세요."
            } else {
                if (request == 0) {
                    MMSPreferenceManager.name =
                        if(editText.text.toString().trim().isEmpty()) null
                        else editText.text.toString().trim()
                }
                if (request == 1) MMSPreferenceManager.phoneNum = editText.text.toString()
                if (request == 2) {
                    MMSPreferenceManager.content =
                        if(editText.text.toString().trim().isEmpty()) null
                        else editText.text.toString().trim()
                }
                binding.invalidateAll()
                dialog.dismiss()
            }
        }
    }
}