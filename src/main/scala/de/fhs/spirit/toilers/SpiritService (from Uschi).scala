package de.fhs.spirit

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import android.content.{Context, Intent}

/**
 * Author: Illaz
 * Date: 01.07.11
 * Time: 00:13
 */
class SpiritService { //extends Service {
  /*
  override def onCreate: Unit = {
    mNM = getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager]
    showNotification
  }

  override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {
    Log.i("LocalService", "Received start id " + startId + ": " + intent)
    return 1 //START_STICKY
  }

  override def onDestroy: Unit = {
    mNM.cancel(2)
    Toast.makeText(this, "Spirit Service gestoppt", Toast.LENGTH_SHORT).show
  }

  def onBind(intent: Intent): IBinder = {
    return mBinder
  }

  /**
   * Show a notification while this service is running.
   */
  private def showNotification: Unit = {
    var text: CharSequence = "Spirit Service started"
    var notification: Notification = new Notification(R.drawable.logo_spirit_v03_ldpi, text, System.currentTimeMillis)
    var contentIntent: PendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, classOf[NewsMulti]), 0)
    notification.setLatestEventInfo(this, "Label", text, contentIntent)
    mNM.notify(2, notification)
  }

  private var mNM: NotificationManager = null
  private val mBinder: IBinder = new LocalBinder

  /**
   * Class for clients to access.  Because we know this service always
   * runs in the same process as its clients, we don't need to deal with
   * IPC.
   */
  class LocalBinder extends Binder {
    private[spirit] def getService: SpiritService = {
      return SpiritService.this
    }
  }
    */
}