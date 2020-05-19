package com.dyddyd.sendmms.main.history.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.dyddyd.sendmms.R
import com.dyddyd.sendmms.databinding.FragmentHistoryBinding
import com.dyddyd.sendmms.main.history.adapter.HistoryAdapter
import com.dyddyd.sendmms.repository.Utils
import com.dyddyd.sendmms.repository.data.HistoryViewModel

class HistoryFragment : Fragment() {

    private lateinit var viewModel: HistoryViewModel
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)

        Utils.setStatusBarColor(activity!!, R.color.white)
        Utils.changeStatusBarTextColorLight(activity!!)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = this

        viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)

        historyAdapter = HistoryAdapter(viewModel)

        binding.historyRcv.apply {
            adapter = historyAdapter
            layoutManager = GridLayoutManager(context, 3)
        }

        viewModel.getAll().observe(viewLifecycleOwner, Observer {
            historyAdapter.getAllHistory(it)
        })
    }
}