package org.unsane.spirit.activities

/**
 * An activity for building the app navigation respectively menu.
 *
 * @author Sebastian Stallenberger
 */

import android.widget.AdapterView.OnItemClickListener
import android.widget._
import android.view._
import android.graphics.{Color}
import android.os.{Bundle}
import android.app.{Activity}
import android.util.Log
import android.content.{Context, Intent}
import org.unsane.spirit.R
import org.unsane.spirit.toilers.{ViewBuilder, SpiritConnect, SpiritHelpers}
import android.graphics.PorterDuff.Mode

class MainActivity extends Activity {

  /**The overridden onCreate Method of MainActivity.
   * Checks if the user submitted the right passphrase for trial phase.
   * If yes it sets the mainactivity layout.
   * If not it shows a simple view for passphrase input.
   *
   * @author Sebastian Stallenberger
   * @param savedInstanceState
   */
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    if (SpiritHelpers.getStringPrefs(MainActivity.this, "trialPassphrase", false).equals(getString(R.string.trialPassphrase))) {
      setContentView(R.layout.mainactivity)
    } else {
      val mainlay = ViewBuilder.getTrialView(MainActivity.this)
      setContentView(mainlay)
    }
  }

  /**The overridden onResume Method of MainActivity.
   * Also checks if the user submitted the right passphrase for trial phase.
   * If yes it builds the menu.
   * If not it does nothing.
   *
   * @author Sebastian Stallenberger
   */
  override def onResume() {
    super.onResume()
    if (SpiritHelpers.getStringPrefs(MainActivity.this, "trialPassphrase", false) == getString(R.string.trialPassphrase)) {
      buildMenu
    }
  }

  /**Builds the main menu based on the GridView defined in mainactivity layout.
   *
   * @author Sebastian Stallenberger
   */
  def buildMenu() {
    val role = SpiritHelpers.getStringPrefs(MainActivity.this, "role", false)

    val dataArr = role match {
      case "professor" =>
        Log.d("BUILDMENUFOR", "professor")
        List(new MenuEntry(R.drawable.menu_news_bs, getString(R.string.menu_news), "readNews", true),
          new MenuEntry(R.drawable.menu_writenews_bs, getString(R.string.menu_createNews), "writeNews", true),
          new MenuEntry(R.drawable.menu_timetable_bs, getString(R.string.menu_timetable), "timeTable", false))
      case "student" =>
        Log.d("BUILDMENUFOR", "student")
        List(new MenuEntry(R.drawable.menu_news_bs, getString(R.string.menu_news), "readNews", true),
          new MenuEntry(R.drawable.menu_timetable_bs, getString(R.string.menu_timetable), "timeTable", false))
      case _ =>
        Log.d("BUILDMENUFOR", "everyoneelse")
        List(new MenuEntry(R.drawable.menu_news_bs, getString(R.string.menu_news), "readNews", true))
    }

    val grid = findViewById(R.id.grid).asInstanceOf[GridView]
    grid.setAdapter(new MainMenuAdapter(MainActivity.this, dataArr))

    grid.setOnItemClickListener(
      new OnItemClickListener {
        def onItemClick(parent: AdapterView[_], p2: View, itemId: Int, p4: Long) {
          val clickedItem = parent.getItemAtPosition(itemId).asInstanceOf[MenuEntry]

          if (!clickedItem.enabled) {
            Toast.makeText(MainActivity.this, getString(R.string.err_notImplementedYet), Toast.LENGTH_SHORT).show()
          } else {
            menuHandler(MainActivity.this, clickedItem.functionName)
          }
        }
      }
    )
  }

  /**Class that extends the BaseAdapter.
   * Used by function buildMenu.
   *
   * @author Sebastian Stallenberger
   */
  class MainMenuAdapter(context: Context, menuItems: List[MenuEntry]) extends BaseAdapter {

    override def getCount: Int = {
      menuItems.size
    }

    override def getItem(position: Int): Object = {
      menuItems(position)
    }

    override def getItemId(position: Int): Long = {
      position
    }

    override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
      var mev: MenuEntryView = null //TODO why is definition as null here necessary?

      if (convertView == null) {
        mev = new MenuEntryView(context, menuItems(position))
      } else {
        mev = convertView.asInstanceOf[MenuEntryView]
      }

      mev
    }
  }

  /**Executes task depending on the overgiven String.
   *
   * @author Sebastian Stallenberger
   * @param context The context
   * @param functionName The String on which depends what happens next
   */
  def menuHandler(context: Context, functionName: String) {
    functionName match {
      case "readNews" =>
        val intent = new Intent(context, classOf[NewsMulti])
        intent.putExtra("load", true)
        context.startActivity(intent)
      case "writeNews" => startActivity(new Intent(this, classOf[NewsCreate]))
      case "timeTable" => //TODO implement timetable activitiy
    }
  }

  /**Class representing a single menu entry View.
   * Extends RelativeLayout.
   *
   * @author Sebastian Stallenberger
   * @param context The context
   * @param menuEntry Single MenuEntry
   */
  class MenuEntryView(context: Context, menuEntry: MenuEntry) extends RelativeLayout(context) {

    //Create view elements
    var iv: ImageView = new ImageView(context)
    var tv: TextView = new TextView(context)

    //Get drawable
    var iconDraw = getResources.getDrawable(menuEntry.drawableId)
    var menuItemBackground = getResources.getDrawable(R.drawable.optmenubg)

    //Init settings
    menuItemBackground.setAlpha(60)
    tv.setTextColor(getResources.getColor(R.color.SpiritWhite))

    //Changes for enabled/disabled
    if (!menuEntry.enabled) {
      menuItemBackground.mutate().setColorFilter(0x77000000, Mode.SRC_ATOP)
      iconDraw.mutate().setColorFilter(0x77000000, Mode.SRC_ATOP)
      iconDraw.mutate().setAlpha(50)
      tv.setTextColor(getResources.getColor(R.color.SpiritLightBlue))
    }

    //Set drawables
    setBackgroundDrawable(menuItemBackground)
    iv.setImageDrawable(iconDraw)

    //Configure Imageview
    iv.setPadding(0, 20, 0, 0)

    //Configure TextView
    tv.setGravity(Gravity.CENTER)
    tv.setText(menuEntry.itemTitle)
    tv.setShadowLayer(1.3f, 1.3f, 1.0f, Color.rgb(50, 50, 100))
    tv.setTextSize(18)
    tv.setPadding(0, 0, 0, 15)

    //Set layout params
    var lp_iv = new RelativeLayout.LayoutParams(-2, -2).asInstanceOf[RelativeLayout.LayoutParams]
    lp_iv.addRule(RelativeLayout.CENTER_HORIZONTAL)
    lp_iv.addRule(RelativeLayout.ALIGN_PARENT_TOP)

    var lp_tv = new RelativeLayout.LayoutParams(-2, -2).asInstanceOf[RelativeLayout.LayoutParams]
    lp_tv.addRule(RelativeLayout.CENTER_HORIZONTAL)
    lp_tv.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

    //Add views
    addView(iv, lp_iv)
    addView(tv, lp_tv)
  }

  /**Represents single menu entry
   * @author Sebastian Stallenberger
   */
  case class MenuEntry(drawableId: Int,
                       itemTitle: String,
                       functionName: String,
                       enabled: Boolean)

  /**Overridden function onCreateOptionsMenu
   * @author Sebastian Stallenberger
   * @param menu
   */
  override def onCreateOptionsMenu(menu: Menu) = {
    val inflater: MenuInflater = getMenuInflater
    inflater.inflate(R.menu.options, menu)
    true
  }

  /**Overridden function onOptionsItemSelected.
   * Decides what happens when a menu button was pressed.
   *
   * @author Sebastian Stallenberger
   * @param item
   */
  override def onOptionsItemSelected(item: MenuItem) = {
    item.getItemId match {
      case R.id.userdata =>
        val optUserDataIntent = new Intent(this, classOf[Settings])
        startActivity(optUserDataIntent)
        true
      case R.id.help =>
        val optInfoIntent = new Intent(this, classOf[Info])
        startActivity(optInfoIntent)
        true
      case _ => super.onOptionsItemSelected(item)
    }
  }


}

