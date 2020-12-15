package com.example.android.ble.ui

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.ble.R
import com.example.android.ble.databinding.FragmentConnectBinding
import com.example.android.ble.util.BLE.bluetoothAdapter
import com.example.android.ble.util.BLE.deviceAddress
import com.example.android.ble.util.BLE.wearableDeviceName


class ConnectFragment : Fragment() {

    private lateinit var binding: FragmentConnectBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConnectBinding.inflate(inflater, container, false)

        initBoundedDevice()
        initEvent()

        return binding.root
    }

    private fun initBoundedDevice() {
        val devices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
        for (device in devices) {
            if (device.name.contains(wearableDeviceName)) {
                device.address.also {
                    deviceAddress = it
                    binding.txtPhysicalAddress.setText(it)
                }
            }
        }
    }

    private fun initEvent() {
        binding.btnConnect.setOnClickListener {
            if (deviceAddress != null) {
                deviceAddress = binding.txtPhysicalAddress.text.toString()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, BluetoothFragment())
                    .commit()
            }
        }
    }
}