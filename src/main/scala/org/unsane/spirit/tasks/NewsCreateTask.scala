package org.unsane.spirit.tasks

import android.app.Activity
import android.app.ProgressDialog
import android.util.Log
import android.widget.Toast
import scala.Array
import org.unsane.spirit.toilers.SpiritConnect
import android.content.{Intent, Context}
import org.unsane.spirit.activities.NewsMulti

/**
 * Creates new news
 *
 * @author Sebastian Stallenberger
 */
class NewsCreateTask(context: Context) extends AsyncTaskAdapter[Array[String], Unit, Boolean] {
  var progressDialog: ProgressDialog = null

  protected def doInBackground(params: Array[String]): Boolean = {
    return SpiritConnect.createNews(context, params(0), params(1), params(2), params(3), params(4))
  }

  protected override def onPostExecute(success: Boolean): Unit = {
    progressDialog.dismiss
    if (success) {
      val activity = context.asInstanceOf[Activity]
      Toast.makeText(context, "News erfolgreich eingetragen!", Toast.LENGTH_LONG).show

      //finish NewsCreate activity
      activity.overridePendingTransition(0, 0)
      activity.finish

      //show news
      val intent = new Intent(context, classOf[NewsMulti])
      intent.putExtra("load", true)
      context.startActivity(intent)
    }
    else {
      Toast.makeText(context, "Fehler beim Eintragen der News!", Toast.LENGTH_LONG).show
    }
  }

  protected override def onPreExecute: Unit = {
    progressDialog = ProgressDialog.show(context, "Bitte warten", "Die News wird veröffentlicht.", true, false)
    progressDialog.show
  }
}