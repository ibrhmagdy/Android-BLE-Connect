package com.example.android.ble.util

/**
 * Created by 4z7l(7d4z7l@gmail.com) on 2020-12-15.
 *
 * Contents : value list to send broadcast
 */

/**
 * action which means bluetooth is connected
 */
const val ACTION_GATT_CONNECTED = "com.example.android.ble.util.ACTION_GATT_CONNECTED"

/**
 * action which means bluetooth is disconnected
 */
const val ACTION_GATT_DISCONNECTED = "com.example.android.ble.util.ACTION_GATT_DISCONNECTED"

/**
 * action which means gatt service is discovered
 */
const val ACTION_GATT_SERVICES_DISCOVERED = "com.example.android.ble.util.ACTION_GATT_SERVICES_DISCOVERED"

/**
 * action which means data is existed to send
 */
const val ACTION_DATA_AVAILABLE = "com.example.android.ble.util.ACTION_DATA_AVAILABLE"

/**
 * intent key to send heart rate data
 */
const val HEART_RATE = "com.example.android.ble.util.HEART_RATE"