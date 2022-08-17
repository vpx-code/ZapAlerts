package com.xvlaze.zapalerts.receivers

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.huawei.hms.searchkit.bean.NewsItem
import com.xvlaze.zapalerts.model.*
import com.xvlaze.zapalerts.repository.CloudDBRepository
import com.xvlaze.zapalerts.repository.Repository
import com.xvlaze.zapalerts.ui.MainActivity
import com.xvlaze.zapalerts.util.Constants.InterestFrequency.*
import kotlin.random.Random

class AlertReceiver : BroadcastReceiver() {
    private var updatedNews = arrayListOf<NewsItem>()

    override fun onReceive(c: Context, intent: Intent) {
        Log.d("ZAP_TAG", "Alarm received!")

        // TODO: CloudDB = CloudDB() o como sea
        val cloudDBRepository = CloudDBRepository(c)

        // FIXME Descomentar a partir de aquí supone un problema porque no enciende el receiver o se mata. Creo que es porque no tengo la base de datos inicializada al reiniciar el teléfono.
        cloudDBRepository.getAll(object : IOnGetAllSuccessCallback {
            override fun onSuccess(res: MutableList<InterestCloudObject>) {
                val updatedNames = arrayListOf<String>()

                for (interest in res) {
                    NewsSearcher.search(
                        interest.name,
                        interest.language.toInt(),
                        interest.country.toInt(),
                        c,
                        object : OnNewsSearchPerformedCallback {
                            override fun onNewsSearchResult(result: ArrayList<NewsItem>) {
                                val lastUpdate = when (interest.frequency.toInt()) {
                                    DAILY.id -> {
                                        Daily.getSavedDate(c)
                                    }
                                    WEEKLY.id -> {
                                        Weekly.getSavedDate(c)
                                    }
                                    REALTIME.id -> {
                                        Realtime.getSavedDate(c)
                                    }
                                    else -> {
                                        Realtime.getSavedDate(c)
                                    }
                                }

                                updatedNews.addAll(
                                    result.filter {
                                        it.publishTime.toLong() < lastUpdate
                                    }
                                )

                                if (result.any { it.publishTime.toLong() * 1000 > lastUpdate }) {
                                    updatedNames.add(interest.name)
                                }
                            }
                        }
                    )
                }

                //Repository(c).overwriteInterest(Daily.getType())
                //Repository(c).overwriteInterest(Weekly.getType())
                Repository(c).saveInterest(Realtime.getType())

                if (updatedNames.isNotEmpty()) {
                    showNotification(
                        c,
                        "${updatedNews.random().title} and more.",
                        Random.nextInt()
                    )
                }
            }
        })
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
                .setContentText("And ${updatedNews.size} more updates.")
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
        Log.d("ZAP_TAG", "showNotification: $reqCode")
    }
}