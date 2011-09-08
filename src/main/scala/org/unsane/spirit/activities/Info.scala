package org.unsane.spirit.activities

import android.app.Activity
import android.os.Bundle
import org.unsane.spirit.R
import android.webkit.WebView
import java.io.{IOException, ByteArrayOutputStream}
import android.util.Log

/**
 * Author: Illaz
 * Date: 18.07.11
 * Time: 20:56
 */
class Info extends Activity {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.info)
    val webView = findViewById(R.id.infowebview).asInstanceOf[WebView]
    webView.loadData(readTextFromResource(R.raw.info), "text/html", "UTF-8")
  }

  /**Converts a raw resource to String
   *
   * @author Sebastian Stallenberger
   * @param ressourceID The id of the text resource (e.g. R.string.example)
   * @return Content of Resource as String
   */
  def readTextFromResource(resourceID: Int): String = {
    val raw = getResources().openRawResource(resourceID)
    val stream = new ByteArrayOutputStream()
    var i: Int = 0
    try {
      i = raw.read()
      while (i != -1) {
        stream.write(i)
        i = raw.read()
      }
      raw.close()
    }
    catch {
      case e: IOException => Log.e("ERROR", e.getMessage.toString)
    }
    stream.toString()
  }
}