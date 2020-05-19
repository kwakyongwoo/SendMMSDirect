package com.dyddyd.sendmms.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.dyddyd.sendmms.R
import com.dyddyd.sendmms.databinding.ActivityMainBinding
import com.dyddyd.sendmms.main.history.adapter.HistoryAdapter
import com.dyddyd.sendmms.main.history.ui.HistoryFragment
import com.dyddyd.sendmms.main.profile.ProfileFragment
import com.dyddyd.sendmms.main.send.SendFragment
import com.dyddyd.sendmms.repository.data.HistoryViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: HistoryViewModel

    private var doubleBackToExitPressedOnce = false

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)

        val fragment1 = SendFragment()
        val fragment2 = HistoryFragment()
        val fragment3 = ProfileFragment()

        supportFragmentManager.beginTransaction().replace(R.id.main_frameLayout, fragment1).commitAllowingStateLoss()

        binding.mainBottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.main_navigation_menu1 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frameLayout, fragment1).commitAllowingStateLoss()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.main_navigation_menu2 -> {
                    if(viewModel.getCount() == 0) {
                        Toast.makeText(this, "보낸 기록이 없습니다.", Toast.LENGTH_SHORT).show()
                        return@setOnNavigationItemSelectedListener false
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frameLayout, fragment2).commitAllowingStateLoss()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.main_navigation_menu3 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frameLayout, fragment3).commitAllowingStateLoss()
                    return@setOnNavigationItemSelectedListener true
                }

                else -> {
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }
    }
}
