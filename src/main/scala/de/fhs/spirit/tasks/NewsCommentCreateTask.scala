package de.fhs.spirit.tasks

import android.widget.Toast
import de.fhs.spirit.toilers.SpiritConnect
import android.app.{Activity, ProgressDialog}
import android.os.Bundle
import android.content.{Intent, Context}

/**
 * Author: Illaz
 * Date: 01.07.11
 * Time: 02:12
 */
class NewsCommentCreateTask(context: Context) extends AsyncTaskAdapter[Array[String], Unit, Boolean] {
  var progressDialog: ProgressDialog = ProgressDialog.show(context, "Bitte warten", "Der Kommentar wird veröffentlicht.", true, false)
  var newsId = 0

  override protected def doInBackground(params: Array[String]): Boolean = {
    newsId = params(0).toInt
    SpiritConnect.createNewsComment(context, newsId, params(1), params(2))
  }

  override protected def onPostExecute(success: Boolean): Unit = {
    val activity = context.asInstanceOf[Activity]
    progressDialog.dismiss
    if (success) {
      Toast.makeText(context, "Kommentar erfolgreich eingetragen!", Toast.LENGTH_LONG).show
      new NewsLoadSingleTask(context).execute(newsId.toString)
    }
    else {
      Toast.makeText(context, "Fehler beim Eintragen des Kommentars!", Toast.LENGTH_LONG).show
    }
  }

  override protected def onPreExecute: Unit = {
    progressDialog.show
  }


}