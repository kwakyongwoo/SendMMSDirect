package com.dyddyd.sendmms.repository.data

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bumptech.glide.Glide

@Entity(tableName = "mms_history")
data class History(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String?,
    val phoneNum: String?,
    val content: String?,
    val uriString: String,
    val dateString: String
) {
    companion object {
        @BindingAdapter("imgSrc")
        @JvmStatic
        fun loadImage(view: ImageView, uriString: String) {
            Glide.with(view.context)
                .load(Uri.parse(uriString))
                .into(view)
        }
    }
}