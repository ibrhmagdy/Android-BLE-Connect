package com.example.android.ble.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.android.ble.R

/**
 * Created by 4z7l(7d4z7l@gmail.com) on 2020-12-15.
 *
 * Contents :
 *
 * MainActivity has two fragments, BluetoothFragment and ConnectFragment.
 * When the application is started, MainActivity begins ConnectFragment first.
 */

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