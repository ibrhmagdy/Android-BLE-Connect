package com.example.android.ble.ui

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.example.android.ble.R
import com.example.android.ble.databinding.FragmentBluetoothBinding
import com.example.android.ble.service.BluetoothService
import com.example.android.ble.util.*

/**
 * Created by 4z7l(7d4z7l@gmail.com) on 2020-12-15.
 *
 * Contents :
 *
 * Show information get from BLE device such as heart rate
 *
 * BluetoothFragment starts BluetoothService as created
 */

class BluetoothFragment : Fragment() {

    private lateinit var binding: FragmentBluetoothBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBluetoothBinding.inflate(inflater, container, false)

        initBLEObject()
        initProgressDialog()
        initView()

        return binding.root
    }

    /**
     * Register Broadcast receiver as started
     */
    override fun onStart() {
        super.onStart()

        registerReceiver()
    }

    /**
     * Reclaim resources and stop service as stopped
     */
    override fun onStop() {
        super.onStop()

        requireActivity().run {
            unregisterReceiver(broadcastReceiver)
            stopService(Intent(this, BluetoothService::class.java))
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ConnectFragment())
                .commitAllowingStateLoss()
        }
    }

    /**
     * Register Broadcast receiver with actions
     *
     * See Also : util.Action
     */
    private fun registerReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                when (intent.action) {
                    ACTION_GATT_CONNECTED -> stateConnected()
                    ACTION_GATT_DISCONNECTED -> stateDisConnected()
                    ACTION_DATA_AVAILABLE -> displayData(intent)
                }
            }
        }

        val filter = IntentFilter()
        filter.apply {
            addAction(ACTION_GATT_CONNECTED)
            addAction(ACTION_GATT_DISCONNECTED)
            addAction(ACTION_DATA_AVAILABLE)
        }
        requireActivity().registerReceiver(broadcastReceiver, filter)
    }

    /**
     * Show heart rate in animation as if the heart is beating
     */
    private fun initView() {
        if(!isServiceStarted(requireContext()))
            stateDisConnected()

        binding.apply {
            txtDeviceName.text = BLE.wearableDeviceName
            ivHeart.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.heartbeat))
        }
    }

    private fun initBLEObject() {
        requireActivity().startService(Intent(requireContext(), BluetoothService::class.java))
    }

    private fun initProgressDialog() {
        progressDialog = ProgressDialog(requireContext())
        progressDialog.apply {
            setTitle("Bluetooth Low Energy")
            setMessage("Connecting...")
            setCancelable(false)
            create()
        }
    }

    private fun stateConnected() {
        progressDialog.dismiss()
        setServiceState(requireContext(), true)
    }

    private fun stateDisConnected() {
        progressDialog.show()
        setServiceState(requireContext(), false)
    }

    /**
     * Display heart rate data received from BluetoothService though broadcast receiver
     */
    private fun displayData(intent: Intent) {
        val heartRate = intent.getIntExtra(HEART_RATE, 0)

        binding.txtHeartRate.text = heartRate.toString()
    }
}