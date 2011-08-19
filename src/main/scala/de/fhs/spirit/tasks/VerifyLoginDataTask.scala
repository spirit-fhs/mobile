package de.fhs.spirit.tasks

import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast

/**
 * Author: Illaz
 * Date: 01.07.11
 * Time: 02:12
 */
class VerifyLoginDataTask(context: Context) extends AsyncTaskAdapter[Array[String], Unit, String] {
  val progressDialog: ProgressDialog = ProgressDialog.show(context, "Bitte warten", "Prüfe Logindaten...", true, false)

  override protected def doInBackground(param: Array[String]): String = {
    if (param(0).equals("braun") || param(0).equals("otto") || param(0).equals("knolle")) {
      "professor"
    }
    else if (param(0).equals("stallen3")) {
      "student"
    }
    else {
      ""
    }
  }

  override protected def onPostExecute(role: String): Unit = {
    progressDialog.dismiss
    if (!role.equals("")) {
      Toast.makeText(context, "Nutzerdaten verifiziert!", Toast.LENGTH_LONG).show
    }
    else {
      Toast.makeText(context, "Fehler beim Verifizieren der Nutzerdaten!", Toast.LENGTH_LONG).show
    }
  }

  override protected def onPreExecute: Unit = {
    progressDialog.show
  }
}