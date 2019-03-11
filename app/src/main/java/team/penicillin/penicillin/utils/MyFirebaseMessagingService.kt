package team.penicillin.penicillin.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import team.penicillin.penicillin.BuildConfig
import team.penicillin.penicillin.HomeActivity
import team.penicillin.penicillin.R
import team.penicillin.penicillin.network.RetrofitService
import team.penicillin.penicillin.network.apis.UserApiService
import team.penicillin.penicillin.network.models.NotiData
import team.penicillin.penicillin.network.models.NotiModel

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.e(TAG, "From: " + remoteMessage!!.from!!)

        Log.e(TAG, remoteMessage.toString())

        if (remoteMessage.data.isNotEmpty()) {
            Log.e(TAG, "Message data payload: " + remoteMessage.data)

            val t = object : TypeToken<NotiModel>(){}.type
            var data = Gson().fromJson(JSONObject(remoteMessage.data).toString(), t) as NotiModel

            val t2 = object : TypeToken<NotiData>(){}.type
            var notiData = Gson().fromJson(JSONObject(data.noti).toString(), t2) as NotiData
            sendNotification(notiData)

        }

        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)
        }
    }

    override fun onNewToken(token: String?) {
//        Log.d(TAG, "Refreshed token: " + token!!)
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // Instance ID token to your app server.
//        sendRegistrationToServer(token)
    }

    @SuppressLint("CheckResult")
    private fun sendRegistrationToServer(token: String?) {
        RetrofitService.instance(applicationContext).create(UserApiService::class.java)
            .saveDeviceToken(token!!)
            .subscribeOn(Schedulers.newThread())
            .subscribe({
            },{
                it.printStackTrace()
            })
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(data: NotiData) {
        var notificationId = data.id
        val intent = Intent(applicationContext, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )


//        val largeBitmap = if(data.type == "order" || data.type == "order_edit")
//            BitmapFactory.decodeResource(resources, R.drawable.ic_notifications_black_24dp)
//        else
//            BitmapFactory.decodeResource(resources, R.drawable.ic_message_black_24dp)

        val largeBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

        val channelId = "default"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_logo_stat)
            .setContentTitle(data.title)
            .setContentText(data.body)
            .setAutoCancel(true)
            .setSubText(data.title)
            .setColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
            .setSound(defaultSoundUri)
            .setDefaults(Notification.DEFAULT_ALL)
            .setLargeIcon(largeBitmap)
            .setStyle(NotificationCompat.BigTextStyle().bigText(data.body))
            .setGroup(BuildConfig.APPLICATION_ID)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            notificationBuilder.priority = NotificationManager.IMPORTANCE_MAX
        }
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Chiti",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify( notificationId, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
