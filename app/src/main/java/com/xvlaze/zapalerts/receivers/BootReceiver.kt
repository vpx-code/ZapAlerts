package com.xvlaze.zapalerts.receivers

import android.content.BroadcastReceiver

import android.content.Context
import android.content.Intent
import android.util.Log
import com.xvlaze.zapalerts.model.Daily
import com.xvlaze.zapalerts.model.MyAlarmManager
import com.xvlaze.zapalerts.model.Realtime
import com.xvlaze.zapalerts.model.Weekly

// TODO: Revisar si realmente actualiza bien la fecha: cuando pasa, cuando no pasa... etc
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context, intent: Intent) {
        Log.d("ZAP_TAG", "Boot receiver on!")
        MyAlarmManager.enableReceivers()

        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            Log.d("ZAP_TAG", "Boot receiver on!")
            Log.d("ZAP_TAG", "[DAILY]")
            if (Daily.doesSavedDateExist(c)) {
                Log.d("ZAP_TAG", "Saved date exists")
                if (Daily.hasDatePassed(c)) {
                    Log.d("ZAP_TAG", "Saved date has passed")
                    MyAlarmManager.scheduleAlarm(Daily, c)
                    Daily.updateSavedDate(c)
                } else {
                    Log.d("ZAP_TAG", "Saved date has not passed")
                }
                MyAlarmManager.scheduleAlarm(Daily, c)
            } else {
                Log.d("ZAP_TAG", "Saved date does not exist")
                Daily.updateSavedDate(c)
                MyAlarmManager.scheduleAlarm(Daily, c)
            }

            Log.d("ZAP_TAG", "[WEEKLY]")
            if (Weekly.doesSavedDateExist(c)) {
                Log.d("ZAP_TAG", "Saved date exists")
                if (Weekly.hasDatePassed(c)) {
                    Log.d("ZAP_TAG", "Saved date has passed")
                    MyAlarmManager.scheduleAlarm(Weekly, c)
                    Weekly.updateSavedDate(c)
                } else {
                    Log.d("ZAP_TAG", "Saved date has not passed")
                }
                MyAlarmManager.scheduleAlarm(Weekly, c)
            } else {
                Log.d("ZAP_TAG", "Saved date does not exist")
                Weekly.updateSavedDate(c)
                MyAlarmManager.scheduleAlarm(Weekly, c)
            }

            /*
            TODO:
                - La alarma ha pasado. Creo que va bien.
                - La alarma no ha pasado.
             */

            // En Realtime nos da igual que haya ya una alarma pasada o no: es cada minuto.
            Log.d("ZAP_TAG", "[REALTIME]")
            /*if (Realtime.doesSavedDateExist(c)) {
                Log.d("ZAP_TAG", "Saved date exists")
                if (Realtime.hasDatePassed(c)) {
                    Log.d("ZAP_TAG", "Saved date has passed")
                    MyAlarmManager.scheduleAlarm(Realtime, c)
                    Realtime.updateSavedDate(c)
                } else {
                    Log.d("ZAP_TAG", "Saved date has not passed")
                }
                MyAlarmManager.scheduleAlarm(Realtime, c)
            } else {
                Log.d("ZAP_TAG", "Saved date does not exist")
                Realtime.updateSavedDate(c)
                MyAlarmManager.scheduleAlarm(Realtime, c)
            }*/

            /*
            TODO: Añadido el 7/6/22. Cuidado, porque creo que ya lo hacemos con Interests y su lastUpdate.
            if (Realtime.getFirstRingTime() == 0.toLong()) {
                Realtime.setFirstRingTine(System.currentTimeMillis())
            }*/

            Realtime.updateSavedDate(c)
            MyAlarmManager.scheduleAlarm(Realtime, c)
        }
    }
}