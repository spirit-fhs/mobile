package org.unsane.spirit.tasks

import android.content.Context
import android.content.Intent
import org.unsane.spirit.activities.NewsMulti
import org.unsane.spirit.toilers.SpiritConnect
import android.app.{Activity, ProgressDialog}
import org.unsane.spirit.toilers.JsonProcessor.News

/**
 * Loads single news data
 *
 * @author Sebastian Stallenberger
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