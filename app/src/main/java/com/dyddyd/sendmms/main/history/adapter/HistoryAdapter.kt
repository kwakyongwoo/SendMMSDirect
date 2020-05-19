package com.dyddyd.sendmms.main.history.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dyddyd.sendmms.R
import com.dyddyd.sendmms.main.history.viewholder.HistoryViewHolder
import com.dyddyd.sendmms.repository.data.History
import com.dyddyd.sendmms.repository.data.HistoryViewModel
import com.dyddyd.sendmms.repository.sharedpreference.MMSPreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_launch.view.*
import java.lang.Exception

class HistoryAdapter(private val viewModel: HistoryViewModel) :
    RecyclerView.Adapter<HistoryViewHolder>() {

    private var list = listOf<History>()

    fun getAllHistory(list: List<History>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val viewHolder = HistoryViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_history,
                parent,
                false
            )
        )

        viewHolder.itemView.setOnClickListener {
            val item = list[viewHolder.adapterPosition]

            AlertDialog.Builder(parent.context).apply {
                setItems(
                    arrayOf("이미지 열기", "상세정보", "삭제"),
                    DialogInterface.OnClickListener { dialog, which ->
                        when (which) {
                            0 -> {
                                try {
                                    val intent =
                                        Intent(Intent.ACTION_VIEW, Uri.parse(item.uriString))
                                    parent.context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "이 사진을 열 수 없습니다.\n파일이 삭제되었거나 위치가 변경되었습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            1 -> {
                                AlertDialog.Builder(parent.context).apply {
                                    setMessage(
                                        "수신인 이름: ${item.name}\n" +
                                                "수신인 번호: ${item.phoneNum}\n" +
                                                "문자 내용: ${item.content ?: "내용 없음"}\n" +
                                                "보낸 시간:\n${item.dateString}\n"
                                    )
                                    create()
                                }.show()
                            }

                            2 -> viewModel.delete(item)
                        }
                    })
                create()
            }.show()
        }

        viewHolder.itemView.setOnLongClickListener {
            val item = list[viewHolder.adapterPosition]
            val dialog = AlertDialog.Builder(parent.context).apply {
                setMessage(
                    "수신인 이름: ${item.name}\n" +
                            "수신인 번호: ${item.phoneNum}\n" +
                            "문자 내용: ${item.content ?: "내용 없음"}\n" +
                            "보낸 시간:\n${item.dateString}\n"
                )
                create()
            }.show()

            return@setOnLongClickListener true
        }

        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.onBind(list[position])
    }

}