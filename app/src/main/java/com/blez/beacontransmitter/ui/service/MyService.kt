package com.blez.beacontransmitter.ui.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.blez.beacontransmitter.R
import com.blez.beacontransmitter.ui.SetupActivity
import com.blez.beacontransmitter.utils.Constants
import com.blez.beacontransmitter.utils.Constants.NOTIFICATION_ID
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter

class MyService : Service() {
    private var mBinder = MyBinder()
    private lateinit var context : Context
      var beacon : Beacon?= null
       var beaconParser : BeaconParser?= null
      var beaconTransmitter : BeaconTransmitter?=null
    override fun onBind(intent: Intent?): IBinder? {
       return mBinder
    }

    override fun onCreate() {
        super.onCreate()

    }

    inner class MyBinder : Binder(){
        fun currentService(): MyService {
            return this@MyService
        }
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("TAG","onStartCommand is called from MyService")
        return super.onStartCommand(intent, flags, startId)

    }


     fun startForeground_Service() {
        val notificationManager = getSystemService(baseContext, NotificationManager::class.java) as NotificationManager
        createNotificationChannel(notificationManager)

        val notification = Notification.Builder(baseContext, Constants.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.tower)
            .setContentTitle("Beacon Emission Service")
            .setContentText("Beacon emission service is running")
            .setContentIntent(BeaconPendingIntent())
            .build()
        startForeground(NOTIFICATION_ID,notification)


    }
    fun stopforegroundservice(){
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

    private fun BeaconPendingIntent() = if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S)PendingIntent.getActivity(this,0,Intent(this,
        SetupActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT) else PendingIntent.getActivity(this,0,Intent(this,
        SetupActivity::class.java), PendingIntent.FLAG_IMMUTABLE)








}

