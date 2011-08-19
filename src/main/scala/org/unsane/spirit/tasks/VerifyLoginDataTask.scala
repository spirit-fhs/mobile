package org.unsane.spirit.tasks

import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast

/**
 * Verifies a login/password combination
 *
 * @author Sebastian Stallenberger
 */
class VerifyLoginDataTask(context: Context) extends AsyncTaskAdapter[Array[String], Unit, String] {
  val progressDialog: ProgressDialog = ProgressDialog.show(context, "Bitte warten", "Prüfe Logindaten...", true, false)

  override protected def doInBackground(param: Array[String]): String = {
    val username = param(0)
    if (username.equals("braun") || username.equals("otto") || username.equals("knolle")) {
      "professor"
    }
    else if (username.equals("stallen3")) {
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