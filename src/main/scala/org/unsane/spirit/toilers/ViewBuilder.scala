package org.unsane.spirit.toilers

import android.util.Log
import android.content.{Intent, Context}
import android.app._
import android.view.View._
import android.view.View
import android.widget._
import org.unsane.spirit.R
import android.graphics.Color
import android.graphics.PorterDuff.Mode
import android.text.TextUtils.TruncateAt
import java.text.SimpleDateFormat
import scala.collection.JavaConversions._
import org.unsane.spirit.activities.NewsSingle
import android.graphics.drawable.Drawable
import org.unsane.spirit.toilers.JsonProcessor.{NewsComment, DegreeClassExt, News}
import org.unsane.spirit.tasks.NewsCommentCreateTask

/**Builder functions for some Views used in Spirit
 * @author Sebastian Stallenberger
 */
object ViewBuilder {

  /**Returns a RelativeLayout containing a passphrase mask for trial phase
   *
   * @author Sebastian Stallenberger
   * @param context The context
   * @return Trial RelativeLayout
   * @todo refactorize
   */
  def getTrialView(context: Context): RelativeLayout = {
    val activity = context.asInstanceOf[Activity]

    val mainlay = new RelativeLayout(context) {
      val id_logo =   2020201
      val id_text =   2020202
      val id_edit =   2020203
      val id_button = 2020204

      val logo = new ImageView(context) {
        setImageDrawable(getResources.getDrawable(R.drawable.logo_spirit_transp))
        setAdjustViewBounds(true)
        setPadding(0, 20, 0, 30)
        setId(id_logo)
      }

      val tv = new TextView(context) {
        setText("Beta Passphrase")
        setId(id_text)
      }

      val edit = new EditText(context) {
        setMinimumWidth(200)
        setId(id_edit)
      }

      val button = new Button(context) {
        setText("Unlock")
        setId(id_button)
        setOnClickListener(new OnClickListener {
          def onClick(p1: View) {
            SpiritHelpers.setPrefs(activity, "trialPassphrase", edit.getText.toString, false)

            var intent: Intent = activity.getIntent
            activity.finish
            activity.overridePendingTransition(0, 0)
            activity.startActivity(intent)
          }
        })
      }

      var lp_iv = new RelativeLayout.LayoutParams(-2,-2).asInstanceOf[RelativeLayout.LayoutParams]
      lp_iv.addRule(RelativeLayout.CENTER_HORIZONTAL)
      lp_iv.addRule(RelativeLayout.ALIGN_PARENT_TOP)

      var lp_tv = new RelativeLayout.LayoutParams(-2, -2).asInstanceOf[RelativeLayout.LayoutParams]
      lp_tv.addRule(RelativeLayout.BELOW, id_logo)
      lp_tv.addRule(RelativeLayout.CENTER_HORIZONTAL)

      var lp_edit = new RelativeLayout.LayoutParams(-2, -2).asInstanceOf[RelativeLayout.LayoutParams]
      lp_edit.addRule(RelativeLayout.BELOW, id_text)
      lp_edit.addRule(RelativeLayout.CENTER_HORIZONTAL)

      var lp_button = new RelativeLayout.LayoutParams(-2, -2).asInstanceOf[RelativeLayout.LayoutParams]
      lp_button.addRule(RelativeLayout.BELOW, id_edit)
      lp_button.addRule(RelativeLayout.CENTER_HORIZONTAL)

      addView(logo, lp_iv)
      addView(tv, lp_tv)
      addView(edit, lp_edit)
      addView(button, lp_button)
    }
    mainlay
  }

  /**Returns a RelativeLayout containing a news
   *
   * @author Sebastian Stallenberger
   * @param context The context
   * @param news The news Object
   * @return News RelativeLayout
   * @todo refactoring, outsourcing style, ...
   */
  def buildNewsView(context: Context, news: News): RelativeLayout = {
    val activity = context.asInstanceOf[Activity]
    val fill_parent = -1
    val wrap_content = -2
    val idbase = 19830615
    val id_title = idbase + 1
    val id_date = idbase + 2
    val id_content = idbase + 3
    val id_author = idbase + 4
    val id_comments = idbase + 5
    val id_header = idbase + 6
    val id_degreeclass = idbase + 7

    val newsRelativeLayout = new RelativeLayout(context) {
      var degreeclassbackground = this.getResources.getDrawable(R.drawable.newsbackgroundheader)
      var newsbackgroundheader = this.getResources.getDrawable(R.drawable.newsbackgroundheader)
      var footerbackground = this.getResources.getDrawable(R.drawable.newsbackgroundheader)
      var newsbackgroundsmall2 = this.getResources.getDrawable(R.drawable.newsbackgroundsmall2)

      if (SpiritHelpers.getStringPrefs(activity, "editFhsId", false).equals(news.owner.fhs_id)) {
        val filterColor = Color.rgb(199, 21, 133)
        newsbackgroundheader.mutate().setColorFilter(filterColor, Mode.MULTIPLY)
      }

      val headerRelativeLayout = new RelativeLayout(context) {
        val titleTextView = new TextView(context) {
          setText(news.title)

          setTextAppearance(context, android.R.style.TextAppearance_Medium)
          var lp_title = new RelativeLayout.LayoutParams(
            wrap_content,
            wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
          lp_title.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
          lp_title.addRule(RelativeLayout.LEFT_OF, id_date)
          setLayoutParams(lp_title)
          setLines(1)
          setId(id_title)
          setTextColor(Color.rgb(226, 234, 239))
          val ta: TruncateAt = TruncateAt.MIDDLE
          setEllipsize(ta)
        }

        val dateTextView = new TextView(context) {
          val date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(news.creationDate)
          val newFormat: SimpleDateFormat = new SimpleDateFormat("dd.MM.yyyy")
          setText(newFormat.format(date).toString)
          setTextAppearance(context, android.R.style.TextAppearance_Small)
          var lp_date = new RelativeLayout.LayoutParams(
            wrap_content,
            wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
          lp_date.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)

          setLayoutParams(lp_date)
          setLines(1)
          setId(id_date)
          setMaxWidth(180)
          setTextColor(Color.rgb(226, 234, 239))
        }

        setBackgroundDrawable(newsbackgroundheader)
        setId(id_header)

        addView(titleTextView)
        addView(dateTextView)
      }

      val degreeclassTextView = new TextView(context) {
        var degreeString = ""
        Log.v("DEGREECLASSELEMENT", news.degreeClass.toString)
        val newsDegreeClassList = news.degreeClass.toList

        degreeString = SpiritHelpers.getDegreeStringforNews(context, newsDegreeClassList, true)
        setText(context.getString(R.string.string_regardingDegrees) + degreeString)

        var lp_dclass = new RelativeLayout.LayoutParams(
          fill_parent,
          wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
        lp_dclass.addRule(RelativeLayout.BELOW, id_header)
        setLayoutParams(lp_dclass)
        setLines(1)
        setId(id_degreeclass)
        setTextColor(Color.rgb(3, 66, 118))
        degreeclassbackground.setAlpha(10)
        setBackgroundDrawable(degreeclassbackground)
      }

      val contentTextView = new TextView(context) {
        setText(news.content)
        var lp_content = new RelativeLayout.LayoutParams(
          fill_parent,
          wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
        lp_content.addRule(RelativeLayout.BELOW, id_degreeclass)
        setLayoutParams(lp_content)
        setId(id_content)
        val ta: TruncateAt = TruncateAt.END
        setEllipsize(ta)
        setSingleLine
      }


      val footerRelativeLayout = new RelativeLayout(context) {
        val authorTextView = new TextView(context) {
          setText(news.owner.displayedName)
          setTextAppearance(context, android.R.style.TextAppearance_Small)
          var lp_author = new RelativeLayout.LayoutParams(
            wrap_content,
            wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
          lp_author.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
          setLayoutParams(lp_author)
          setLines(1)
          setId(id_author)
          setTextColor(Color.rgb(3, 66, 118))
        }

        val countCommentsTextView = new TextView(context) {
          setText(context.getString(R.string.string_commentsCount) + news.newsComment.size().toString)
          setTextAppearance(context, android.R.style.TextAppearance_Small)
          var lp_comment = new RelativeLayout.LayoutParams(
            wrap_content,
            wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
          lp_comment.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
          setLayoutParams(lp_comment)
          setLines(1)
          setId(id_comments)
          setTextColor(Color.rgb(3, 66, 118))
        }

        var lp_footer = new RelativeLayout.LayoutParams(
          wrap_content,
          wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
        lp_footer.addRule(RelativeLayout.BELOW, id_content)
        setLayoutParams(lp_footer)

        addView(authorTextView)
        addView(countCommentsTextView)

        footerbackground.setAlpha(30)
        setBackgroundDrawable(footerbackground)
      }

      addView(headerRelativeLayout)
      addView(degreeclassTextView)
      addView(contentTextView)
      addView(footerRelativeLayout)

      var lp_main = new RelativeLayout.LayoutParams(
        fill_parent,
        wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
      setLayoutParams(lp_main)

      setBackgroundDrawable(newsbackgroundsmall2)
      if (news.news_id != -1) {
        setOnClickListener(new View.OnClickListener() {
          override def onClick(v: View) {
            val newsIntent = new Intent(context, classOf[NewsSingle])
            newsIntent.putExtra("singleNewsJson", news)
            activity.startActivityForResult(newsIntent, 198300)
          }
        })
      }

      if (SpiritHelpers.getStringPrefs(activity, "editFhsId", false).equals(news.owner.fhs_id)) {
        setOnLongClickListener(new OnLongClickListener {
          def onLongClick(p1: View) = {
            Toast.makeText(context, "DELETE", Toast.LENGTH_LONG).show()
            true
          }
        })
      }

    }

    newsRelativeLayout
  }

  def buildNewsViewSingle(context: Context, news: News): RelativeLayout = {
    val fill_parent = -1 //TODO Don't get them from Context. Why?
    val wrap_content = -2
    val activity = context.asInstanceOf[Activity]

    //TODO: correct layout for single news (more space for all fields a.s.o.)
    val idbase = 19830709
    val id_title = idbase + 1
    val id_date = idbase + 2
    val id_content = idbase + 3
    val id_author = idbase + 4
    val id_comments = idbase + 5
    val id_header = idbase + 6
    val id_degreeclass = idbase + 7

    val newsRelativeLayout = new RelativeLayout(context) {
      val degreeclassbackground = this.getResources.getDrawable(R.drawable.newsbackgroundheader)
      val newsbackgroundheader = this.getResources.getDrawable(R.drawable.newsbackgroundheader)
      val footerbackground = this.getResources.getDrawable(R.drawable.newsbackgroundheader)
      val newsbackgroundsmall2 = this.getResources.getDrawable(R.drawable.newsbackgroundsmall2)


      Log.d("FHSID_PREF", SpiritHelpers.getStringPrefs(activity, "editFhsId", false))
      Log.d("FHSID_OBJ", news.owner.fhs_id)

      if (SpiritHelpers.getStringPrefs(activity, "editFhsId", false).equals(news.owner.fhs_id)) {
        val filterColor = Color.rgb(199, 21, 133)
        newsbackgroundheader.mutate().setColorFilter(filterColor, Mode.MULTIPLY)
      }

      val headerRelativeLayout = new RelativeLayout(context) {
        val titleTextView = new TextView(context) {
          setText(news.title)
          setTextAppearance(context, android.R.style.TextAppearance_Medium)
          var lp_title = new RelativeLayout.LayoutParams(
            wrap_content,
            wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
          lp_title.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
          lp_title.addRule(RelativeLayout.LEFT_OF, id_date)
          setLayoutParams(lp_title)
          setId(id_title)
          setTextColor(Color.rgb(226, 234, 239))
        }

        val dateTextView = new TextView(context) {
          val date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(news.creationDate)
          val newFormat: SimpleDateFormat = new SimpleDateFormat("dd.MM.yyyy")
          setText(newFormat.format(date).toString)
          setTextAppearance(context, android.R.style.TextAppearance_Small)
          var lp_date = new RelativeLayout.LayoutParams(
            wrap_content,
            wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
          lp_date.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
          setLayoutParams(lp_date)
          setLines(1)
          setId(id_date)
          setMaxWidth(180)
          setTextColor(Color.rgb(226, 234, 239))
        }

        setBackgroundDrawable(newsbackgroundheader)
        setId(id_header)

        addView(titleTextView)
        addView(dateTextView)
      }

      val degreeclassTextView = new TextView(context) {
        var degreeString = ""
        Log.v("DEGREECLASSELEMENT", news.degreeClass.toString)
        val newsDegreeClassList = news.degreeClass.toList

        degreeString = SpiritHelpers.getDegreeStringforNews(context, newsDegreeClassList, false)
        setText(context.getString(R.string.string_regardingDegrees) + degreeString)

        var lp_dclass = new RelativeLayout.LayoutParams(
          fill_parent,
          wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
        lp_dclass.addRule(RelativeLayout.BELOW, id_header)
        setLayoutParams(lp_dclass)
        setLines(1)
        setId(id_degreeclass)
        setTextColor(Color.rgb(3, 66, 118))

        degreeclassbackground.setAlpha(10)
        setBackgroundDrawable(degreeclassbackground)
      }

      val contentTextView = new TextView(context) {
        setText(news.content)
        var lp_content = new RelativeLayout.LayoutParams(
          fill_parent,
          wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
        lp_content.addRule(RelativeLayout.BELOW, id_degreeclass)
        setLayoutParams(lp_content)
        setId(id_content)
      }


      val footerRelativeLayout = new RelativeLayout(context) {
        val authorTextView = new TextView(context) {
          setText(news.owner.displayedName)
          setTextAppearance(context, android.R.style.TextAppearance_Small)
          var lp_author = new RelativeLayout.LayoutParams(
            wrap_content,
            wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
          lp_author.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
          setLayoutParams(lp_author)
          setLines(1)
          setId(id_author)
          setTextColor(Color.rgb(3, 66, 118))
        }

        val countCommentsTextView = new TextView(context) {
          setText(context.getString(R.string.string_commentsCount) + news.newsComment.size().toString)
          setTextAppearance(context, android.R.style.TextAppearance_Small)
          var lp_comment = new RelativeLayout.LayoutParams(
            wrap_content,
            wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
          lp_comment.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
          setLayoutParams(lp_comment)
          setLines(1)
          setId(id_comments)
          setTextColor(Color.rgb(3, 66, 118))
        }

        var lp_footer = new RelativeLayout.LayoutParams(
          wrap_content,
          wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
        lp_footer.addRule(RelativeLayout.BELOW, id_content)
        setLayoutParams(lp_footer)

        addView(authorTextView)
        addView(countCommentsTextView)

        footerbackground.setAlpha(30)
        setBackgroundDrawable(footerbackground)
      }

      addView(headerRelativeLayout)
      addView(degreeclassTextView)
      addView(contentTextView)
      addView(footerRelativeLayout)

      var lp_main = new RelativeLayout.LayoutParams(
        fill_parent,
        wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
      setLayoutParams(lp_main)

      setBackgroundDrawable(newsbackgroundsmall2)
    }
    newsRelativeLayout
  }


  def buildCommentsView(context: Context, commentList: List[NewsComment], newsId: Int): LinearLayout = {
    val fill_parent = -1 //TODO Didn't get them from Context yet
    val wrap_content = -2

    val linLay = new LinearLayout(context)
    linLay.setPadding(10, 0, 10, 0)
    linLay.setOrientation(LinearLayout.VERTICAL)

    commentList.foreach(
      element => linLay.addView(singleCommentView(context, element))
    )

    val newCommentView = new RelativeLayout(context) {
      val newCommEdit: EditText = new EditText(context) {
        setHint(context.getString(R.string.string_newComment))
        var lp = new RelativeLayout.LayoutParams(
          fill_parent,
          wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL)
        setLayoutParams(lp)

        setId(29830710)

        setOnClickListener(new View.OnClickListener() {
          override def onClick(v: View) {
            newCommButton.setVisibility(View.VISIBLE)
          }
        })
      }
      val newCommButton = new Button(context) {
        setText(context.getString(R.string.string_sendComment))
        var lp = new RelativeLayout.LayoutParams(
          wrap_content,
          wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL)
        lp.addRule(RelativeLayout.BELOW, 29830710)
        setLayoutParams(lp)
        setVisibility(View.GONE)

        setOnClickListener(new View.OnClickListener() {
          override def onClick(v: View) {
            if (newCommEdit.getText.toString.equals("")) {
              Toast.makeText(context, R.string.string_commess_must, Toast.LENGTH_SHORT).show()
            } else {
              val fhsId = SpiritHelpers.getStringPrefs(context.asInstanceOf[Activity], "editFhsId", false)
              new NewsCommentCreateTask(context).execute(Array(newsId.toString, fhsId, newCommEdit.getText.toString))
            }
          }
        })
      }

      var commbackground = this.getResources.getDrawable(R.drawable.newsbackgroundsmall2).asInstanceOf[Drawable]
      commbackground.setAlpha(70)
      setBackgroundDrawable(commbackground)

      setPadding(10, 10, 10, 10)

      addView(newCommEdit)
      addView(newCommButton)
    }

    val role = SpiritHelpers.getStringPrefs(context.asInstanceOf[Activity], "role", false)
    if (role.equals("student") || role.equals("professor")) {
      linLay.addView(newCommentView)
    }

    linLay
  }

  def singleCommentView(context: Context, newsComment: NewsComment): RelativeLayout = {
    val fill_parent = -1 //TODO Didn't get them from Context yet
    val wrap_content = -2
    val activity = context.asInstanceOf[Activity]

    val headbackground = context.getResources.getDrawable(R.drawable.newsbackgroundheader)
    val commbackground = context.getResources.getDrawable(R.drawable.newsbackgroundsmall2)

    if (SpiritHelpers.getStringPrefs(activity, "editFhsId", false).equals(newsComment.owner.fhs_id)) {
      val filterColor = Color.rgb(199, 21, 133)
      headbackground.mutate().setColorFilter(filterColor, Mode.MULTIPLY)
    }

    val relSingleComm = new RelativeLayout(context) {
      val commHeadView = new RelativeLayout(context) {
        headbackground.setAlpha(20)
        setBackgroundDrawable(headbackground)
        addView(new TextView(context) {
          setText(newsComment.owner.displayedName)
          var lp_title = new RelativeLayout.LayoutParams(
            wrap_content,
            wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
          lp_title.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
          setLayoutParams(lp_title)
          setTextColor(Color.rgb(3, 66, 118))
        })
        addView(new TextView(context) {
          val date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(newsComment.creationDate)
          val newFormat: SimpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm")
          setText(newFormat.format(date).toString + context.getString(R.string.string_oclock))
          var lp_date = new RelativeLayout.LayoutParams(
            wrap_content,
            wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
          lp_date.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
          setLayoutParams(lp_date)
          setTextColor(Color.rgb(3, 66, 118))
        })

        var lp = new RelativeLayout.LayoutParams(
          fill_parent,
          wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
        setLayoutParams(lp)
        setId(29830709)

      }

      addView(commHeadView)
      addView(new TextView(context) {
        setText(newsComment.content)

        var lp = new RelativeLayout.LayoutParams(
          fill_parent,
          wrap_content).asInstanceOf[RelativeLayout.LayoutParams]
        lp.addRule(RelativeLayout.BELOW, 29830709)
        setLayoutParams(lp)
      })


      if (SpiritHelpers.getStringPrefs(activity, "editFhsId", false).equals(newsComment.owner.fhs_id)) {
        setOnLongClickListener(new OnLongClickListener {
          def onLongClick(p1: View) = {
            Toast.makeText(context, "DELETE", Toast.LENGTH_LONG).show()
            true
          }
        })
      }

      commbackground.setAlpha(70)

      setBackgroundDrawable(commbackground)
    }

    relSingleComm
  }

  def constructNewsMultiMainLay(context: Context) {
    val activity = context.asInstanceOf[Activity]
    activity.setContentView(R.layout.newsmulti)

    val loadedJsonString = SpiritHelpers.loadString(activity, "newsJsonString")
    val newsList = JsonProcessor.jsonStringToNewsList(loadedJsonString).asInstanceOf[List[News]]
    val newsMainLinLay = activity.findViewById(R.id.newsMain).asInstanceOf[LinearLayout]
    val lastNewsDateTV = activity.findViewById(R.id.lastNewsDate).asInstanceOf[TextView]

    lastNewsDateTV.setText(activity.getString(R.string.string_lastNewsFetch) +
      SpiritHelpers.getStringPrefs(activity, "LastNewsDate", false))
    newsList.foreach(
      element => {
        newsMainLinLay.addView(ViewBuilder.buildNewsView(context, element.asInstanceOf[News]))
        newsMainLinLay.addView(new FrameLayout(context) {
          setMinimumHeight(12)
        })
      })
  }
}