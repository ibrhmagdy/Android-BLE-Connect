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

/**
 * Created by 4z7l(7d4z7l@gmail.com) on 2020-12-15.
 *
 * Contents : 페어링된 ble 기기의 정보 확인 및 연결을 시작
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

    // 디바이스 이름을 통해 mac 주소 가져옴 link BLE.wearableDeviceName
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

    //클릭하면 fragment 이동
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