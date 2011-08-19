package de.fhs.spirit.tasks

import android.content.Context
import android.content.Intent
import de.fhs.spirit.activities.NewsMulti
import de.fhs.spirit.toilers.SpiritConnect
import android.app.{Activity, ProgressDialog}
import de.fhs.spirit.toilers.JsonProcessor.News

/**
 * Author: Illaz
 * Date: 01.07.11
 * Time: 02:12
 */
class NewsLoadSingleTask(context: Context) extends AsyncTaskAdapter[String, Unit, News] {
  val progressDialog: ProgressDialog = ProgressDialog.show(context, "Bitte warten", "Die News wird aktualisiert.", true, false)

  override protected def doInBackground(param: String): News = {
    SpiritConnect.getSingleNews(context, param)
  }

  override protected def onPostExecute(param: News) {
    progressDialog.dismiss
    val act = context.asInstanceOf[Activity]
    val intent = act.getIntent
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    intent.putExtra("newSingleNews", param)
    act.overridePendingTransition(0, 0)
    act.finish
    act.startActivity(intent)
  }

  override protected def onPreExecute {
    progressDialog.show
  }
}