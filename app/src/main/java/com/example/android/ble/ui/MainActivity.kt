package com.example.android.ble.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.android.ble.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFragment()
    }

    private fun initFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ConnectFragment())
            .commit()
    }
}