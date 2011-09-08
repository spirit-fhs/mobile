package org.unsane.spirit.tasks

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.app.Activity._
import org.unsane.spirit.R
import org.unsane.spirit.toilers.{ViewBuilder, SpiritConnect}

/**
 * Reloads news data
 *
 * @author Sebastian Stallenberger
 */
class NewsReloadTask(context: Context) extends AsyncTaskAdapter[Unit, Unit, Unit] {
  val progressDialog: ProgressDialog = ProgressDialog.show(context, "Bitte warten", "Die News werden abgerufen.", true, false)

  override protected def doInBackground(param: Unit) {
    SpiritConnect.getNews(context)
    SpiritConnect.getDegreeClasses(context)
  }

  override protected def onPostExecute(param: Unit) {
    progressDialog.dismiss
    var activity = context.asInstanceOf[Activity]
    activity.setContentView(R.layout.newsmulti)
    ViewBuilder.constructNewsMultiMainLay(context)
  }

  override protected def onPreExecute {
    progressDialog.show
  }
}