package de.fhs.spirit.activities

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import java.text.SimpleDateFormat
import android.widget._
import android.graphics.drawable.Drawable
import android.view.View
import scala.collection.JavaConversions._
import de.fhs.spirit.R
import de.fhs.spirit.tasks.NewsCommentCreateTask
import android.util.Log
import de.fhs.spirit.toilers.JsonProcessor.{News, DegreeClassExt, NewsComment}
import android.widget.TextView._
import de.fhs.spirit.toilers.{ViewBuilder, SpiritHelpers}
import android.content.Intent
import scala.collection.JavaConversions._

//needed for JavaList to ScalaList conversion

class NewsSingle extends Activity {
  var text: StringBuilder = new StringBuilder()

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.newssingle)
    val intent = getIntent
    val extras: Bundle = intent.getExtras().asInstanceOf[Bundle]

    if (extras == null) {
      Toast.makeText(NewsSingle.this, getString(R.string.err_erroroccured), Toast.LENGTH_LONG).show()
    } else {
      val news = if (extras.containsKey("newSingleNews")) {
        extras.get("newSingleNews").asInstanceOf[News]
      } else {
        extras.get("singleNewsJson").asInstanceOf[News]
      }

      setResult(Activity.RESULT_OK, intent)

      val newsMain = findViewById(R.id.newsSingleMain).asInstanceOf[LinearLayout]
      newsMain.setOrientation(LinearLayout.VERTICAL) //Layout with one column{
      newsMain.addView(ViewBuilder.buildNewsViewSingle(NewsSingle.this, news))
      val commList = news.newsComment.toList.asInstanceOf[List[NewsComment]]
      newsMain.addView(ViewBuilder.buildCommentsView(NewsSingle.this, commList, news.news_id))
    }
  }

  /*
  override def onDestroy() {
    super.onDestroy()
    val intent = getIntent
    intent.putExtra("load", true)
    setResult(Activity.RESULT_OK, intent)
    //finish()
  }*/
}