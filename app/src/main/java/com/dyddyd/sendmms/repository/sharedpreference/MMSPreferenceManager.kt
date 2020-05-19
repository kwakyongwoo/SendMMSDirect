package com.dyddyd.sendmms.repository.sharedpreference

import android.content.Context
import android.content.SharedPreferences

class MMSPreferenceManager {
    companion object {
        // constant
        private const val PREFERENCE_NAME = "mms_format"
        private const val NAME_KEY = "mms_name"
        private const val PHONE_NUM_KEY = "mms_phone_num"
        private const val CONTENT_KEY = "mms_content"
        private const val HISTORY_KEY = "history_num"
        private const val DEFAULT_VALUE = "정보 없음"

        var INSTANCE: SharedPreferences? = null

        fun setInstance(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
            }
        }

        var name: String?
            get() = INSTANCE!!.getString(NAME_KEY, null)
            set(value) {
                val editor = INSTANCE!!.edit()
                editor.putString(NAME_KEY, value)
                editor.apply()
            }

        var phoneNum: String?
            get() = INSTANCE!!.getString(PHONE_NUM_KEY, null)
            set(value) {
                val editor = INSTANCE!!.edit()
                editor.putString(PHONE_NUM_KEY, value)
                editor.apply()
            }

        var content: String?
            get() = INSTANCE!!.getString(CONTENT_KEY, null)
            set(value) {
                val editor = INSTANCE!!.edit()
                editor.putString(CONTENT_KEY, value)
                editor.apply()
            }

        var historyNum: Int
            get() = INSTANCE!!.getInt(HISTORY_KEY, 3)
            set(value) {
                val editor = INSTANCE!!.edit()
                editor.putInt(HISTORY_KEY, value)
                editor.apply()
            }
    }
}
