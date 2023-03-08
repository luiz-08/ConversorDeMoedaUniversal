package com.example.currencytoday.notification

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class permission (private  val activity: Activity) {

    fun requestNotificationPermission(){
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            ACCESS_NOTIFICATION_CODE
        )
    }

    fun NotificationPermissionOK(): Boolean{
        val notificationPermission =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)

        return notificationPermission == PackageManager.PERMISSION_GRANTED
    }

    companion object{
        private const val ACCESS_NOTIFICATION_CODE = 1000
    }
}