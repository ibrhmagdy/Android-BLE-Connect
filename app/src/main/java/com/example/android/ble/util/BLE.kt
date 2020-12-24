package com.example.android.ble.util

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt

/**
 * Created by 4z7l(7d4z7l@gmail.com) on 2020-12-15.
 *
 * Contents : Save information about BLE device such as BluetoothAdapter, BluetoothGatt, Device Address ..
 */

object BLE {
    val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var bluetoothGatt: BluetoothGatt? = null
    var bluetoothDevice: BluetoothDevice? = null
    var deviceAddress: String? = null

    /**
     * This is used to find device. Device name is preset as "MI Band 2" which I tested with.
     * To get other BLE device information, change device name like "Galaxy Watch3".
     * However, I have not tested on other devices, so I cannot guarantee that this code will work.
     */
    const val wearableDeviceName: String = "MI Band 2"
}