package de.fhs.spirit.tasks

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import de.fhs.spirit.activities.NewsMulti
import de.fhs.spirit.toilers.SpiritConnect

/**
 * Author: Illaz
 * Date: 01.07.11
 * Time: 02:12
 */
class NewsLoadTask(context: Context) extends AsyncTaskAdapter[Unit, Unit, Unit] {
  val progressDialog: ProgressDialog = ProgressDialog.show(context, "Bitte warten", "Die News werden abgerufen.", true, false)

  override protected def doInBackground(param: Unit) {
    SpiritConnect.getNews(context)
    SpiritConnect.getDegreeClasses(context)
  }

  override protected def onPostExecute(param: Unit) {
    progressDialog.dismiss
    var intent: Intent = new Intent(context, classOf[NewsMulti])
    intent.putExtra("noInternet", false)
    context.startActivity(intent)
  }

  override protected def onPreExecute {
    progressDialog.show
  }
}