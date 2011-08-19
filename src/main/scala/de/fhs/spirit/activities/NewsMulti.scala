package de.fhs.spirit.activities

import android.app.Activity
import android.os.{Bundle}
import android.widget._
import android.view.{MenuItem, MenuInflater, Menu, View}
import de.fhs.spirit.R
import de.fhs.spirit.tasks.NewsReloadTask
import de.fhs.spirit.toilers.JsonProcessor.{News}
import android.util.Log
import scala.collection.JavaConversions._
import android.content.{Context, Intent}
import de.fhs.spirit.toilers._

//needed for JavaList to ScalaList conversion

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


  /* val extras: Bundle = getIntent.getExtras
 if (extras != null) {
    if (extras.getBoolean("load")) {
      new NewsReloadTask(NewsMulti.this).execute(null)
    }
  }*/

  /* override def onResume() {
    super.onResume()
    if (SpiritConnect.checkInternetConnection(NewsMulti.this)) {
      val extras: Bundle = getIntent.getExtras
      if (extras != null) {
        if (extras.getBoolean("load")) {
          new NewsReloadTask(NewsMulti.this).execute(null)
        }
      }
    } else {
      Toast.makeText(NewsMulti.this, getString(R.string.err_noInternet), Toast.LENGTH_LONG).show()
      ViewBuilder.constructNewsMultiMainLay(NewsMulti.this)
    }
  }*/


  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    super.onActivityResult(requestCode, resultCode, data)
    Log.d("ONACTIVITYRESULT",data.toString)
    Log.d("ONACTIVITYRESULT",data.getExtras.toString)
    Log.d("ONACTIVITYRESULT",data.getExtras.getBoolean("load").toString)
    Log.d("ONACTIVITYRESULT NEW NEWS",data.getExtras.containsKey("newSingleNews").toString)
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
          new NewsReloadTask(NewsMulti.this).execute(null) //TODO hier muss irgendetwas übergeben werden, sonst 2 Exceptions^^
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

//TODO General: when turning the display while a refresh the app crashes...