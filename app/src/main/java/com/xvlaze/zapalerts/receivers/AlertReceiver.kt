package com.xvlaze.zapalerts.receivers

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.huawei.hms.searchkit.bean.ImageItem
import com.huawei.hms.searchkit.bean.NewsItem
import com.huawei.hms.searchkit.bean.VideoItem
import com.huawei.hms.searchkit.bean.WebItem
import com.xvlaze.zapalerts.model.*
import com.xvlaze.zapalerts.ui.MainActivity
import com.xvlaze.zapalerts.util.Constants.AlertType.*
import kotlin.random.Random

class AlertReceiver : BroadcastReceiver() {
    private lateinit var updatedImages: ArrayList<ImageItem>
    private lateinit var updatedVideos: ArrayList<VideoItem>
    private lateinit var updatedNews: ArrayList<NewsItem>
    private lateinit var updatedWebsites: ArrayList<WebItem>

    override fun onReceive(c: Context, intent: Intent) {
        Log.d(ContentValues.TAG, "Alarm received!")

        val interests = InterestsManager.getSavedInterests(c)
        val updatedNames = arrayListOf<String>()

        for (interest in interests) {
            when (interest.type) {
                IMAGE.id -> {
                    ImageSearcher.search(
                        interest.name,
                        interest.language,
                        interest.country,
                        c,
                        object : OnImageSearchPerformedCallback {
                            override fun onImageSearchResult(result: ArrayList<ImageItem>) {
                                TODO()
                            }
                        }
                    )
                }
                NEWS.id -> {
                    NewsSearcher.search(
                        interest.name,
                        interest.language,
                        interest.country,
                        c,
                        object : OnNewsSearchPerformedCallback {
                            override fun onNewsSearchResult(result: ArrayList<NewsItem>) {
                                // TODO: Debug aquí para REALTIME.
                                updatedNews.addAll( // TODO: Se guarda bien, pero ¿ahora cómo lo pasamos? ¿Lo guardamos en un JSON o rehacemos la búsqueda al entrar?
                                    result.filter {
                                        it.publishTime.toLong() < interest.lastUpdate
                                    }
                                )
                                val lastUpdate = interest.lastUpdate
                                if (result.any { it.publishTime.toLong() * 1000 > lastUpdate }) {
                                    updatedNames.add(interest.name)
                                    interest.lastUpdate = System.currentTimeMillis()
                                }
                                InterestsManager.updateInterestInJSON(interest, c)
                            }
                        }
                    )
                }
                VIDEO.id -> {
                    VideoSearcher.search(
                        interest.name,
                        interest.language,
                        interest.country,
                        c,
                        object : OnVideoSearchPerformedCallback {
                            override fun onVideoSearchResult(result: ArrayList<VideoItem>) {
                                TODO()
                            }
                        }
                    )
                }
                WEBSITE.id -> {
                    WebSearcher.search(
                        interest.name,
                        interest.language,
                        interest.country,
                        c,
                        object : OnWebsiteSearchPerformedCallback {
                            override fun onWebsiteSearchResult(result: ArrayList<WebItem>) {
                                TODO()
                            }
                        }
                    )
                }
            }
        }

        if (updatedNames.isNotEmpty()) {
            showNotification(
                c,
                "${updatedNews.random().title} and more.",
                Random.nextInt()
            )
        }
    }

    private fun showNotification(
        context: Context,
        message: String?,
        reqCode: Int
    ) {
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                reqCode,
                Intent(
                    context,
                    MainActivity::class.java
                ), // TODO: Añadir extra con los updated o bien recalcular en Main pasando una flag.
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    }
                    else -> PendingIntent.FLAG_IMMUTABLE
                }
            )
        val channelID = "channel_name" // The id of the channel.
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.mipmap.sym_def_app_icon)
                .setContentTitle("${updatedNews.random().title} and more.")
                .setContentText("And $updatedNews.size() more updates.")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val name: CharSequence = "Channel Name" // The user-visible name of the channel.
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(channelID, name, importance)
        notificationManager.createNotificationChannel(mChannel)
        notificationManager.notify(
            reqCode,
            notificationBuilder.build()
        )
        Log.d("showNotification", "showNotification: $reqCode")
    }
}