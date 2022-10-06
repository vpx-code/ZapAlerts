package com.xvlaze.zapalerts.receivers

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationManagerCompat
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
    private val channelID = "cyan"
    private val notificationsList = ArrayList<Notification>()

    init {
        CloudDB.initAGConnectCloudDB(appContext.applicationContext)
        MyApplication.cloudDB = CloudDB(appContext.applicationContext)
        MyApplication.cloudDB.createObjectType()
        MyApplication.cloudDB.openCloudDbZone()
    }

    override fun onReceive(c: Context, intent: Intent) {
        val alarmFrequency = intent.getIntExtra("alarmFrequency", -1)
        Log.d("ZAP_TAG", "Alarm received! Type = $alarmFrequency")

        val lastUpdate = when (alarmFrequency) {
            DAILY.id -> {
                Daily.getPreviouslySavedDate()
            }
            WEEKLY.id -> {
                Weekly.getPreviouslySavedDate()
            }
            REALTIME.id -> {
                Realtime.getPreviouslySavedDate()
            }
            else -> {
                Realtime.getPreviouslySavedDate()
            }
        }

        val savedDateCandidates = mutableListOf<Long>()

        // SOLO DEBUG
        if (BuildConfig.DEBUG && Constants.DEBUG) {
            createAlertNotification(
                c,
                "Apple",
                "This is a test notification about Apple",
                99
            )
            createAlertNotification(
                c,
                "Tesla",
                "This is a test notification about Tesla",
                99
            )
            createAlertNotification(
                c,
                "Amazon",
                "This is a test notification about Amazon",
                99
            )

            var i = 1
            for (not in notificationsList) {
                with(NotificationManagerCompat.from(appContext)) {
                    notify(i, not)
                }
                i++
            }
            playRingtone()
            performWakeLock()
            //notifySummaryNotification(c)
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

                    Log.d(
                        "ZAP_TAG",
                        "Number of interests to process: ${interestList.filter { it.frequency.toInt() == alarmFrequency }}"
                    )

                    for (interest in interestList.filter { it.frequency.toInt() == alarmFrequency }) {
                        Log.d("ZAP_TAG", "Processing interest ${interest.name}")
                        NewsSearcher.search(
                            interest.name,
                            interest.language.toInt(),
                            interest.country.toInt(),
                            c,
                            object : OnNewsSearchPerformedCallback {
                                override fun onNewsSearchResult(result: ArrayList<NewsItem>) {
                                    if (result.isNotEmpty()) {
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
                                                recent.size
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }

                    if (notificationsList.isNotEmpty()) {
                        notificationsList.forEach { not ->
                            MyApplication.notificationManager.notify(nextInt(), not)
                        }
                        // notifySummaryNotification(c)
                        playRingtone()
                        performWakeLock()
                        notificationsList.clear()
                    }

                    if (savedDateCandidates.isNotEmpty()) {
                        when (alarmFrequency) {
                            DAILY.id -> {
                                Daily.updateSavedDate(savedDateCandidates.max())
                            }
                            WEEKLY.id -> {
                                Weekly.updateSavedDate(savedDateCandidates.max())
                            }
                            REALTIME.id -> {
                                Realtime.updateSavedDate(savedDateCandidates.max())
                            }
                        }
                        savedDateCandidates.clear()
                    }
                }
            })
        }
    }

    private fun performWakeLock() {
        val screenLock = (appContext.getSystemService(POWER_SERVICE) as PowerManager).newWakeLock(
            PowerManager.FULL_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE, "CYAN:Tag"
        )
        screenLock.acquire(3000)

        val screenLock2 = (appContext.getSystemService(POWER_SERVICE) as PowerManager).newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK, "CYAN:Tag2"
        )

        screenLock2.acquire(3000)
    }

    private fun playRingtone() {
        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(
                appContext,
                notification
            )
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // TODO: Lo vamos a dejar porque no entiendo lo de que de repente se muestre esta notificación sola y no todos los intereses se actualizan a la vez.
    private fun notifySummaryNotification(context: Context) {
        Log.d("ZAP_TAG", "Notifying summary...")
        val summaryNotification = Notification.Builder(context, channelID)
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
        message: String,
        updates: Int
    ) {
        val intent = Intent(
            context,
            InterestDetailActivity::class.java
        )

        val b = Bundle()
        b.putString("fromNotificationName", interestName)
        b.putBoolean("fromNotification", true)
        intent.putExtras(b)

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                nextInt(),
                intent,
                FLAG_UPDATE_CURRENT
            )
        val updatesStringNumber = updates - 1
        val notificationDescription = if (updatesStringNumber == 0) {
            context.getString(R.string.summary_subtitle)
        } else {
            context.getString(R.string.more_updates_1) + " " + updatesStringNumber + context.getString(
                R.string.more_updates_2
            )
        }

        val alertNotification: Notification =
            Notification.Builder(appContext, channelID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(context.getString(R.string.new_update_on) + interestName + "!")
                .setContentText(notificationDescription)
                .setAutoCancel(true)
                .setGroup(groupKey)
                .setGroupAlertBehavior(
                    Notification.GROUP_ALERT_SUMMARY
                )
                .setStyle(
                    Notification.BigTextStyle()
                        .bigText("${message.replace("&#39;", "'")}.")
                )
                .setContentIntent(pendingIntent)
                .build()

        notificationsList.add(alertNotification)
    }
}