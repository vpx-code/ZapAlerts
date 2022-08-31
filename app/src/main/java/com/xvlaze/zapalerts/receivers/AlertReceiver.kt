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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class AlertReceiver : BroadcastReceiver() {
    private var updatedNews = arrayListOf<NewsItem>()

    // FIXME: Cuando no hay internet se rompe la app porque intenta acceder a una lista de intereses que no existe. Omitir alarma en ese caso.
    override fun onReceive(c: Context, intent: Intent) {
        Log.d("ZAP_TAG", "Alarm received!")

        val cloudDBRepository = CloudDBRepository(c)
        cloudDBRepository.getAll(object : IOnGetAllSuccessCallback {
            override fun onSuccess(res: MutableList<InterestCloudObject>) {
                var interestList = res
                if (interestList.isEmpty()) {
                    Log.d("ZAP_TAG", "Cannot access DB, using cache...")
                    interestList = InterestsManager().getFile()!!
                }
                val updatedNames = arrayListOf<String>()

                Log.d("ZAP_TAG", "Number of interests to process: ${interestList.size}")
                for (interest in interestList) {
                    Log.d("ZAP_TAG", "Processing interest ${interest.name}")
                    NewsSearcher.search(
                        interest.name,
                        interest.language.toInt(),
                        interest.country.toInt(),
                        c,
                        object : OnNewsSearchPerformedCallback {
                            override fun onNewsSearchResult(result: ArrayList<NewsItem>) {
                                val lastUpdate = when (interest.frequency.toInt()) {
                                    DAILY.id -> {
                                        Daily.getPreviouslySavedDate(c)
                                    }
                                    WEEKLY.id -> {
                                        Weekly.getPreviouslySavedDate(c)
                                    }
                                    REALTIME.id -> {
                                        Realtime.getPreviouslySavedDate(c)
                                    }
                                    else -> {
                                        Realtime.getPreviouslySavedDate(c)
                                    }
                                }


                                Log.d("ZAP_TAG", "Comparing news publishing date vs. saved date, must be >=): ${convertLongToTime(result.first().publishTime.toLong() * 1000)} vs. ${convertLongToTime(lastUpdate)}")
                                val recent = result.filter {
                                    it.publishTime.toLong() * 1000 >= lastUpdate
                                }
                                if (recent.isNotEmpty()) {
                                    Log.d("ZAP_TAG", "Found ${recent.size} recent news.")
                                    showNotification(
                                        c,
                                        recent.random().title,
                                        recent.size,
                                        Random.nextInt()
                                    )
                                }
                            }
                        }
                    )
                }

                //Repository(c).overwriteInterest(Daily.getType())
                //Repository(c).overwriteInterest(Weekly.getType())
                Repository(c).updateSavedDate(Realtime.getType())
            }
        })
    }


    private fun showNotification(
        context: Context,
        message: String?,
        updates: Int,
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
                .setContentTitle(message)
                .setContentText("And $updates more updates.")
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

    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
        return format.format(date)
    }
}