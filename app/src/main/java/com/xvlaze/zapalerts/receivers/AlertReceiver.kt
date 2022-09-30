package com.xvlaze.zapalerts.receivers

import android.app.Notification
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
import androidx.core.app.NotificationCompat.GROUP_ALERT_SUMMARY
import com.huawei.hms.searchkit.bean.NewsItem
import com.xvlaze.zapalerts.BuildConfig
import com.xvlaze.zapalerts.R
import com.xvlaze.zapalerts.model.*
import com.xvlaze.zapalerts.model.MyApplication.Companion.appContext
import com.xvlaze.zapalerts.repository.CloudDBRepository
import com.xvlaze.zapalerts.ui.InterestDetailActivity
import com.xvlaze.zapalerts.util.Constants
import com.xvlaze.zapalerts.util.Constants.InterestFrequency.*
import com.xvlaze.zapalerts.util.Extensions.toTimeStamp
import kotlin.random.Random.Default.nextInt

class AlertReceiver : BroadcastReceiver() {
    private val summaryID = 0
    private val groupKey = "com.xvlaze.zapalerts.ALERT_GROUP"
    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelID = "channel_name"

    private val notificationsList = ArrayList<Notification>()

    init {
        val name: CharSequence = "Zap Alerts Notification Channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(channelID, name, importance)
        notificationManager.createNotificationChannel(mChannel)

        CloudDB.initAGConnectCloudDB(appContext.applicationContext)
        MyApplication.cloudDB = CloudDB(appContext.applicationContext)
        MyApplication.cloudDB.createObjectType()
        MyApplication.cloudDB.openCloudDbZone()
    }

    override fun onReceive(c: Context, intent: Intent) {
        Log.d("ZAP_TAG", "Alarm received!")

        val lastDailyDate = Daily.getPreviouslySavedDate()
        val lastWeeklyDate = Weekly.getPreviouslySavedDate()
        val lastRealtimeDate = Realtime.getPreviouslySavedDate()

        val savedDateCandidates = mutableListOf<Long>()


        // SOLO DEBUG
        if (BuildConfig.DEBUG && Constants.DEBUG) {
            createAlertNotification(
                c,
                "Apple",
                "This is a test notification about Apple",
                99,
                1
            )
            createAlertNotification(
                c,
                "Tesla",
                "This is a test notification about Tesla",
                99,
                2
            )
            createAlertNotification(
                c,
                "Amazon",
                "This is a test notification about Amazon",
                99,
                3
            )

            var i = 1
            for (not in notificationsList) {
                notificationManager.notify(i, not)
                i++
            }
            notifySummaryNotification(c)
        } else {
            val cloudDBRepository = CloudDBRepository()
            cloudDBRepository.getAll(object : IOnGetAllSuccessCallback {
                override fun onSuccess(res: MutableList<InterestCloudObject>) {
                    var interestList = res
                    if (interestList.isEmpty()) {
                        Log.d("ZAP_TAG", "Cannot access DB, using cache...")
                        interestList = InterestsManager().getFile()
                            ?: mutableListOf() // If this is null, we are in big trouble.
                    }

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
                                    if (result.isNotEmpty()) {
                                        val lastUpdate = when (interest.frequency.toInt()) {
                                            DAILY.id -> {
                                                lastDailyDate
                                            }
                                            WEEKLY.id -> {
                                                lastWeeklyDate
                                            }
                                            REALTIME.id -> {
                                                lastRealtimeDate
                                            }
                                            else -> {
                                                lastRealtimeDate
                                            }
                                        }

                                        Log.d(
                                            "ZAP_TAG",
                                            "Comparing news publishing date vs. last saved date, must be >=): ${
                                                (result.first().publishTime.toLong() * 1000).toTimeStamp()
                                            } >=? ${lastUpdate.toTimeStamp()}"
                                        )
                                        var recent = result.filter {
                                            it.publishTime != ""
                                        }
                                        recent = recent.filter {
                                            it.publishTime.toLong() * 1000 >= lastUpdate
                                        }

                                        if (recent.isNotEmpty()) {
                                            Log.d("ZAP_TAG", "Found ${recent.size} recent news.")

                                            savedDateCandidates.add(recent.map { it.publishTime }
                                                .max().toLong() * 1000)

                                            createAlertNotification(
                                                c,
                                                interest.name,
                                                recent.random().title,
                                                recent.size,
                                                nextInt()
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }

                    if (notificationsList.isNotEmpty()) {
                        notificationsList.forEach { not ->
                            notificationManager.notify(nextInt(), not)
                        }
                        notifySummaryNotification(c)
                        notificationsList.clear()
                    }

                    if (savedDateCandidates.isNotEmpty()) {
                        Realtime.setSavedDate(savedDateCandidates.max())
                        savedDateCandidates.clear()
                    }
                }
            })
        }
    }


    private fun notifySummaryNotification(context: Context) {
        Log.d("ZAP_TAG", "Notifying summary...")
        val summaryNotification = NotificationCompat.Builder(context, channelID)
            .setContentTitle(context.getString(R.string.summary_title))
            .setContentText(context.getString(R.string.summary_subtitle))
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setGroup(groupKey)
            .setGroupSummary(true)
            .build()

        notificationManager.notify(summaryID, summaryNotification) // id must be constant!
    }

    private fun createAlertNotification(
        context: Context,
        interestName: String,
        message: String?,
        updates: Int,
        reqCode: Int
    ) {
        val intent = Intent(
            context,
            InterestDetailActivity::class.java
        )
        intent.putExtra("name", interestName)
        intent.putExtra("fromNotification", true)

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                reqCode,
                intent,
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    }
                    else -> PendingIntent.FLAG_IMMUTABLE
                }
            )

        val updatesStringNumber = updates - 1
        val notificationDescription = if (updatesStringNumber == 0) {
            context.getString(R.string.summary_subtitle)
        } else {
            context.getString(R.string.more_updates_1) + updatesStringNumber + context.getString(R.string.more_updates_2)
        }

        val alertNotification: Notification =
            NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(context.getString(R.string.new_update_on) + interestName + "!")
                .setContentText(notificationDescription)
                .setAutoCancel(true)
                .setGroup(groupKey)
                .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("$message.")
                )
                .setContentIntent(pendingIntent)
                .build()

        notificationsList.add(alertNotification)
    }
}