package com.example.android.ble.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by 4z7l(7d4z7l@gmail.com) on 2020-12-15.
 *
 * Contents : 서비스의 상태 (시작 여부) 저장하고 있느 preference
 */

private fun getPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("SHARED_PREFERENCE", 0)
}

fun isServiceStarted(context: Context): Boolean {
    return getServiceState(context)
}

fun setServiceState(context: Context, boolean: Boolean) {
    val sharedPrefs = getPreferences(context)
    sharedPrefs.edit().let {
        it.putBoolean("SERVICE_STATE", boolean)
        it.commit()
    }
}

fun getServiceState(context: Context): Boolean {
    val sharedPrefs = getPreferences(context)
    return sharedPrefs.getBoolean("SERVICE_STATE", false)
}
