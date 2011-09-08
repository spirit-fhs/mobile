package org.unsane.spirit.activities

import android.app.Activity
import android.os.{Bundle}
import android.widget._
import android.view.{MenuItem, MenuInflater, Menu}
import org.unsane.spirit.R
import org.unsane.spirit.tasks.NewsReloadTask
import scala.collection.JavaConversions._
import android.content.{Intent}
import org.unsane.spirit.toilers._

class NewsMulti extends Activity {

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    if (SpiritConnect.checkInternetConnection(NewsMulti.this)) {
      new NewsReloadTask(NewsMulti.this).execute(null)
    } else {
      Toast.makeText(NewsMulti.this, getString(R.string.err_noInternet), Toast.LENGTH_LONG).show()
      ViewBuilder.constructNewsMultiMainLay(NewsMulti.this)
    }
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    super.onActivityResult(requestCode, resultCode, data)

    val extras = data.getExtras

    if (extras != null) {
      if (extras.containsKey("newSingleNews")) {
        if (SpiritConnect.checkInternetConnection(NewsMulti.this)) {
          new NewsReloadTask(NewsMulti.this).execute(null)
        } else {
          Toast.makeText(NewsMulti.this, getString(R.string.err_noInternet), Toast.LENGTH_LONG).show()
          ViewBuilder.constructNewsMultiMainLay(NewsMulti.this)
        }
      }
    }
  }

  override def onCreateOptionsMenu(menu: Menu) = {
    val inflater: MenuInflater = getMenuInflater
    inflater.inflate(R.menu.newsmulti, menu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem) = {
    item.getItemId match {
      case R.id.reload =>
        if (SpiritConnect.checkInternetConnection(NewsMulti.this)) {
          new NewsReloadTask(NewsMulti.this).execute(null)
        } else {
          Toast.makeText(NewsMulti.this, getString(R.string.err_noInternet), Toast.LENGTH_LONG).show()
        }
        true
      case _ => super.onOptionsItemSelected(item)
    }
  }

  def reload() {
    val intent: Intent = getIntent()
    overridePendingTransition(0, 0)
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    finish()

    overridePendingTransition(0, 0)
    startActivity(intent)
  }
}