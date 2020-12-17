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
 * Contents : 블루투스 콜백을 구현한 서비스
 *
 */

class BluetoothService : Service() {

    //속성을 지속적으로 받기위한 변수?
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

    // 서비스 시작, mac address를 통해 디바이스 정보 가져온후 콜백 연결,
    private fun startService() {
        Log.e(TAG, "startService")
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)
        if (bluetoothDevice != null) {
            bluetoothGatt = bluetoothDevice!!.connectGatt(this, true, gattCallback)
        }
        startHeartRate()
    }

    //서비스 종료 및 자원 해제
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

    // 1초마다 심박수 스캔
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

    // ble의 정보를 broadcast를 통해 전달
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
        }
        sendBroadcast(intent)
    }

    // 콜백 정의, 여러 속성에 대한 변화가 감지되면 broadcast를 통해 전달
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