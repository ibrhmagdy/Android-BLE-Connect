package com.welie.android.ble.ui

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.welie.android.ble.R
import com.welie.android.ble.databinding.FragmentConnectBinding
import com.welie.android.ble.util.BLE.bluetoothAdapter
import com.welie.android.ble.util.BLE.deviceAddress
import com.welie.android.ble.util.BLE.wearableDeviceName

/**
 * Created by 4z7l(7d4z7l@gmail.com) on 2020-12-15.
 *
 * Contents :
 *
 * Initiate paired device information and connect
 */

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

    /**
     * Get Mac address through device name
     *
     * See also : util.BLE.wearableDeviceName
     */
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

    /**
     * When click connect button, start BluetoothFragment
     */
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