package org.unsane.spirit.activities

import android.app.Activity
import android.os.Bundle
import android.widget._
import org.unsane.spirit.R
import org.unsane.spirit.toilers.JsonProcessor.{News, NewsComment}
import org.unsane.spirit.toilers.{ViewBuilder}
import scala.collection.JavaConversions._

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
      newsMain.setOrientation(LinearLayout.VERTICAL)
      newsMain.addView(ViewBuilder.buildNewsViewSingle(NewsSingle.this, news))

      val commList = news.newsComment.toList.asInstanceOf[List[NewsComment]]
      newsMain.addView(ViewBuilder.buildCommentsView(NewsSingle.this, commList, news.news_id))
    }
  }
}