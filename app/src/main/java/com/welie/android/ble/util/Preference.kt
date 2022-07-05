package com.welie.android.ble.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by 4z7l(7d4z7l@gmail.com) on 2020-12-15.
 *
 * Contents : Store shared preference about state of BluetoothService
 */

/**
 * Get shared preferences of Application
 */
private fun getPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("SHARED_PREFERENCE", 0)
}

/**
 * Return state of service
 */
fun isServiceStarted(context: Context): Boolean {
    return getServiceState(context)
}

/**
 * Set service state as Boolean
 */
fun setServiceState(context: Context, boolean: Boolean) {
    val sharedPrefs = getPreferences(context)
    sharedPrefs.edit().let {
        it.putBoolean("SERVICE_STATE", boolean)
        it.commit()
    }
}

/**
 * Get service state as Boolean.
 *
 * Default value is false.
 */
fun getServiceState(context: Context): Boolean {
    val sharedPrefs = getPreferences(context)
    return sharedPrefs.getBoolean("SERVICE_STATE", false)
}
