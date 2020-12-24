package com.example.android.ble.service

import android.app.Service
import android.bluetooth.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.android.ble.util.*
import com.example.android.ble.util.BLE.bluetoothAdapter
import com.example.android.ble.util.BLE.bluetoothDevice
import com.example.android.ble.util.BLE.bluetoothGatt
import com.example.android.ble.util.BLE.deviceAddress
import java.util.*

/**
 * Created by 4z7l(7d4z7l@gmail.com) on 2020-12-15.
 */

/**
 * This service implements bluetooth gatt callback to connect BLE device.
 *
 */
class BluetoothService : Service() {

    /**
     *
     */
    private var timer = Timer()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startService()
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
    }

    /**
     * ### Start service
     * Connect BLE device using Bluetooth information in com.example.android.ble.util.BLE
     *
     * Get paired device information by MAC address, and connect gatt callback
     */
    private fun startService() {
        Log.e(TAG, "startService")
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)
        if (bluetoothDevice != null) {
            bluetoothGatt = bluetoothDevice!!.connectGatt(this, true, gattCallback)
        }
        startHeartRate()
    }


    /**
     * ### Stop service
     * Stop service and reclaim resources
     */
    private fun stopService() {
        Log.e(TAG, "stopService")
        closeGatt()
        stopHeartRate()
        stopSelf()
    }

    private fun closeGatt() {
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    /**
     * Scan heart rate per a minute.
     * This allows you to get heart rate constantly
     */
    private fun startHeartRate() {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                scanHeartRate()
            }
        }, 0, 1000)
    }

    private fun stopHeartRate() {
        timer.cancel()
    }

    private fun scanHeartRate() {
        if (bluetoothGatt != null) {
            val bluetoothCharacteristic = bluetoothGatt?.getService(UUIDs.HEART_RATE_SERVICE)
                ?.getCharacteristic(UUIDs.HEART_RATE_CONTROL_CHARACTERISTIC)
            if (bluetoothCharacteristic != null) {
                bluetoothCharacteristic.value = byteArrayOf(21, 1, 1)
                bluetoothGatt!!.writeCharacteristic(bluetoothCharacteristic)
            }
        }
    }

    private fun listenHeartRate() {
        if (bluetoothGatt != null) {
            val bluetoothCharacteristic = bluetoothGatt!!.getService(UUIDs.HEART_RATE_SERVICE)
                .getCharacteristic(UUIDs.HEART_RATE_MEASUREMENT_CHARACTERISTIC)
            bluetoothGatt!!.setCharacteristicNotification(bluetoothCharacteristic, true)
            val descriptor = bluetoothCharacteristic.getDescriptor(UUIDs.HEART_RATE_DESCRIPTOR)
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            bluetoothGatt!!.writeDescriptor(descriptor)
        }
    }

    /**
     * Send BLE information through broadcast
     */
    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    /**
     * Send BLE information and data through broadcast
     */
    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic) {
        val intent = Intent(action)
        when (characteristic.uuid) {
            UUIDs.HEART_RATE_MEASUREMENT_CHARACTERISTIC -> {
                val heartRate: Int = characteristic.value[1].toInt()
                intent.putExtra(HEART_RATE, heartRate)
            }
        }
        sendBroadcast(intent)
    }

    /**
     * Define Gatt Callback
     *
     * When changes are detected, notify change through broadcast
     */
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    bluetoothGatt?.discoverServices()
                    broadcastUpdate(ACTION_GATT_CONNECTED)
                    Log.e(TAG, "STATE_CONNECTED")
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    broadcastUpdate(ACTION_GATT_DISCONNECTED)
                    Log.e(TAG, "STATE_DISCONNECTED")
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    listenHeartRate()
                    broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
                }
                else -> Log.e(TAG, "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic
        ) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
        }
    }

    companion object {
        private val TAG = BluetoothService::class.java.simpleName
    }

}