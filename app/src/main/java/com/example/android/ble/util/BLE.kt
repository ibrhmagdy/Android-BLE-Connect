package com.example.android.ble.util

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt

/**
 * Created by 4z7l(7d4z7l@gmail.com) on 2020-12-15.
 *
 * Contents : ble 기기의 정보, 콜백 등등을 저장
 * 기본 세팅은 mi band 2
 */

object BLE {
    val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var bluetoothGatt: BluetoothGatt? = null
    var bluetoothDevice: BluetoothDevice? = null
    var deviceAddress: String? = null

    const val wearableDeviceName: String = "MI Band 2"
}