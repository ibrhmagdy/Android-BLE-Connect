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
 *
 * Contents :
 */

class BluetoothService : Service() {

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

    private fun startService() {
        Log.e(TAG, "startService")
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)
        if (bluetoothDevice != null) {
            bluetoothGatt = bluetoothDevice!!.connectGatt(this, true, gattCallback)
        }
        startHeartRate()
    }

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

    private fun startHeartRate() {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                scanHeartRate()
                getBatteryStatus()
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

    private fun getBatteryStatus() {
        if (bluetoothGatt != null) {
            val bluetoothGattCharacteristic = bluetoothGatt?.getService(UUIDs.BASIC_SERVICE)
                ?.getCharacteristic(UUIDs.BASIC_BATTERY_CHARACTERISTIC)
            if (bluetoothGattCharacteristic != null) {
                bluetoothGatt!!.readCharacteristic(bluetoothGattCharacteristic)
            }
        }
    }

    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic) {
        val intent = Intent(action)
        when (characteristic.uuid) {
            UUIDs.HEART_RATE_MEASUREMENT_CHARACTERISTIC -> {
                val heartRate: Int = characteristic.value[1].toInt()
                intent.putExtra(HEART_RATE, heartRate)
            }
            UUIDs.BASIC_BATTERY_CHARACTERISTIC -> {
                val lastIndex = characteristic.value.size - 1

                val data = characteristic.value
                Log.e("SEULGI", Arrays.toString(data))
                val heartRate: Int = characteristic.value[lastIndex].toInt()
                intent.putExtra(BATTERY_STATE, heartRate)
            }
        }
        sendBroadcast(intent)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    bluetoothGatt?.discoverServices()
                    Log.e(TAG, "STATE_CONNECTED")
                    broadcastUpdate(ACTION_GATT_CONNECTED)
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.e(TAG, "STATE_DISCONNECTED")
                    broadcastUpdate(ACTION_GATT_DISCONNECTED)
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

            val data = characteristic.value
            Log.e("SEULGI", "onCharacteristicRead/setValue: " + Arrays.toString(data))
            /*when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
                }
            }*/
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