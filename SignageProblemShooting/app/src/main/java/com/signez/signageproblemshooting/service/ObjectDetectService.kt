package com.signez.signageproblemshooting.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.signez.signageproblemshooting.ErrorDetectActivity
import com.signez.signageproblemshooting.R
import org.pytorch.Module


class ObjectDetectService : Service() {
    companion object {
        private val REQUEST_DETECT_VIDEO: Int = 100
        private val REQUEST_DETECT_PHOTO: Int = 101
        private val REQUEST_CODE_ERROR_DETECT_ACTIVITY = 999
        private val REQUEST_CODE_ERROR_DETECT_FAIL_ACTIVITY = 998

        private val REQUEST_TYPE: String = "REQUEST_TYPE"
        private val REQUEST_SIGNAGE_ID: String = "REQUEST_SIGNAGE_ID"

        private val signageDetectModuleFileName: String = "signage_detect.torchscript.pt"
        private val errorDetectModuleFileName: String = "error_detect.torchscript.pt"

        private const val RESIZE_SIZE: Int = 640
        private val NO_MEAN_RGB = floatArrayOf(0.0f, 0.0f, 0.0f)
        private val NO_STD_RGB = floatArrayOf(1.0f, 1.0f, 1.0f)

        // model output is of size 25200*(num_of_class+5)
        private val mOutputRow =
            25200 // as decided by the YOLOv5 model for input image of size 640*640
        private val mOutputColumn = 6 // left, top, right, bottom, score and class probability
        private val scoreThreshold = 0.20f // score above which a detection is generated

        private val rectThreshold = 5.0f
    }

    private lateinit var signageDetectModule: Module
    private lateinit var errorDetectModule: Module

    private var startMode: Int = 0             // indicates how to behave if the service is killed
    private var binder: IBinder? = null        // interface for clients that bind
    private var allowRebind: Boolean = false   // indicates whether onRebind should be used

    override fun onCreate() {
        // The service is being created
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // A client is binding to the service with bindService()
        return null
    }


    override fun onRebind(intent: Intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    override fun onDestroy() {
        // The service is no longer used and is being destroyed
    }

    //Notififcation for ON-going
    private var iconNotification: Bitmap? = null
    private var notification: Notification? = null
    var mNotificationManager: NotificationManager? = null
    private val mNotificationId = 123
    private fun generateForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intentMainLanding = Intent(this, ErrorDetectActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, intentMainLanding, 0)
            iconNotification = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            if (mNotificationManager == null) {
                mNotificationManager =
                    this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert(mNotificationManager != null)
                mNotificationManager?.createNotificationChannelGroup(
                    NotificationChannelGroup("chats_group", "Chats")
                )
                val notificationChannel =
                    NotificationChannel(
                        "service_channel", "Service Notifications",
                        NotificationManager.IMPORTANCE_MIN
                    )
                notificationChannel.enableLights(false)
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
                mNotificationManager?.createNotificationChannel(notificationChannel)
            }
            val builder = NotificationCompat.Builder(this, "service_channel")

            builder.setContentTitle(
                StringBuilder(resources.getString(R.string.app_name)).append(" service is running")
                    .toString()
            )
                .setTicker(
                    StringBuilder(resources.getString(R.string.app_name)).append("service is running")
                        .toString()
                )
                .setContentText("Touch to open") //                    , swipe down for more options.
//                .setSmallIcon(
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setWhen(0)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
            if (iconNotification != null) {
                builder.setLargeIcon(Bitmap.createScaledBitmap(iconNotification!!, 128, 128, false))
            }
            builder.color = resources.getColor(R.color.purple_200)
            notification = builder.build()
            startForeground(mNotificationId, notification)
        }

    }
}