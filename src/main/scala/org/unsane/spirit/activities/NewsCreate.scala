package org.unsane.spirit.activities

import java.util.Calendar
import android.os.Bundle
import org.unsane.spirit.R
import org.unsane.spirit.toilers.JsonProcessor.DegreeClassExt
import android.view.View.OnClickListener
import android.view.View
import android.util.Log
import org.unsane.spirit.tasks.NewsCreateTask
import android.content.DialogInterface
import android.app.{DatePickerDialog, AlertDialog, Dialog, Activity}
import android.widget._
import scala.collection.JavaConversions._
import android.text.Html
import collection.immutable.ListMap
import org.unsane.spirit.toilers.{SpiritHelpers, JsonProcessor}

/**Activity for creating News
 * @author Sebastian Stallenberger
 **/
class NewsCreate extends Activity {

  val DEGREEDIALOGID = 0
  val DATEPICKERDIALOGID = 1

  var selArrForDialog = new Array[Boolean](0)
  var degreeDropDownArr = new Array[CharSequence](0)

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.newscreate)

    val degreeJsonString = SpiritHelpers.loadString(NewsCreate.this, "degreeJsonString")
    val getAllDegreesObject = JsonProcessor.jsonStringToDegreeList(degreeJsonString).asInstanceOf[List[DegreeClassExt]]

    val degreeMap = SpiritHelpers.getDegreeMap(NewsCreate.this, getAllDegreesObject, "")
    degreeDropDownArr = SpiritHelpers.getDegreeTitleArr(degreeMap)
    selArrForDialog = new Array[Boolean](degreeDropDownArr.length)

    val selectedDegreesString = SpiritHelpers.getStringPrefs(NewsCreate.this, "selectedCreationDegrees", false)
    if (selectedDegreesString.equals("N/A")) {
      SpiritHelpers.setPrefs(NewsCreate.this, "selectedCreationDegrees", "AllClasses", false)
    }

    val selectedDegreesDiffArr = selectedDegreesString.split("<;>")

    for (i <- 0 until degreeDropDownArr.length) {
      val degreeClass = degreeDropDownArr(i)
      if (selectedDegreesDiffArr.contains(degreeClass)) {
        selArrForDialog(i) = true
      } else {
        selArrForDialog(i) = false
      }
    }

    val degreeTv = findViewById(R.id.degreeText).asInstanceOf[TextView]
    degreeTv.setOnClickListener(new OnClickListener {
      def onClick(p1: View) {
        showDialog(DEGREEDIALOGID)
      }
    })

    val expireDateTv = findViewById(R.id.datePexpiredegreeText).asInstanceOf[TextView]
    expireDateTv.setOnClickListener(new OnClickListener {
      def onClick(p1: View) {
        showDialog(DATEPICKERDIALOGID)
      }
    })

    val sendButton = findViewById(R.id.createButton).asInstanceOf[Button]
    var fhsId = SpiritHelpers.getStringPrefs(NewsCreate.this, "editFhsId", false)
    var expireDate = SpiritHelpers.getStringPrefs(NewsCreate.this, "createNewsExpireDate", false)
    var degreeclass = SpiritHelpers.getStringPrefs(NewsCreate.this, "selectedCreationDegrees", false)
    val subject = findViewById(R.id.editSubject).asInstanceOf[EditText].getText
    val message = findViewById(R.id.editMessage).asInstanceOf[EditText].getText

    sendButton.setText(getString(R.string.string_send_as) + fhsId + ")")
    sendButton.setOnClickListener(new OnClickListener {
      def onClick(p1: View) {
        fhsId = SpiritHelpers.getStringPrefs(NewsCreate.this, "editFhsId", false)
        expireDate = SpiritHelpers.getStringPrefs(NewsCreate.this, "createNewsExpireDate", false)
        degreeclass = SpiritHelpers.getStringPrefs(NewsCreate.this, "selectedCreationDegrees", false)

        if (subject.toString.equals("") || message.toString.equals("")) {
          Toast.makeText(NewsCreate.this, R.string.string_subjMess_must, Toast.LENGTH_SHORT).show()
        } else {
          new NewsCreateTask(NewsCreate.this).execute(Array(fhsId, subject.toString, message.toString, expireDate, translateDegreeNameToId(degreeclass, degreeMap)))
        }
      }
    })

    updateDisplay()
  }

  override def onCreateDialog(id: Int): Dialog = {
    val c: Calendar = Calendar.getInstance()
    val nowYear = c.get(Calendar.YEAR)
    val nowMonth = c.get(Calendar.MONTH)
    val nextMonth = nowMonth + 1
    val nowDay = c.get(Calendar.DAY_OF_MONTH)

    id match {
      case DEGREEDIALOGID =>
        new AlertDialog.Builder(this).setTitle(getString(R.string.string_regardingDegrees)).setMultiChoiceItems(
          degreeDropDownArr,
          selArrForDialog,
          new DialogInterface.OnMultiChoiceClickListener {
            def onClick(dialog: DialogInterface, clicked: Int, selected: Boolean): Unit = {
              //TODO if parent element selected, deactivate child elements and vice versa
            }
          }).setPositiveButton(
          "OK",
          new DialogInterface.OnClickListener {
            def onClick(dialog: DialogInterface, clicked: Int): Unit = {
              clicked match {
                case DialogInterface.BUTTON_POSITIVE =>
                  returnSelectedDegrees
              }
            }
          }).create

      case DATEPICKERDIALOGID =>
        new DatePickerDialog(this,
          mDateSetListener,
          nowYear, nextMonth, nowDay)
    }
  }

  val mDateSetListener: DatePickerDialog.OnDateSetListener =
    new DatePickerDialog.OnDateSetListener() {
      def onDateSet(view: DatePicker, year: Int,
                    monthOfYear: Int, dayOfMonth: Int) {
        val month = monthOfYear + 1 //starts at 0                       2011-08-10 12:00:00
        SpiritHelpers.setPrefs(NewsCreate.this, "createNewsExpireDate", year.toString + "-" + fillStringWithNull(month.toString) + "-" + fillStringWithNull(dayOfMonth.toString) + " 00:00:01", false)
        updateDisplay()
      }
    }

  def returnSelectedDegrees: Unit = {
    {
      var i: Int = 0
      var selectedDegreesString = ""

      while (i < degreeDropDownArr.size) {
        if (selArrForDialog(i)) {
          selectedDegreesString = selectedDegreesString + degreeDropDownArr(i) + "<;>"
        }
        i += 1
      }
      if (selectedDegreesString.equals("")) {
        selectedDegreesString = getString(R.string.string_all) + "<;>"
      }
      selectedDegreesString = selectedDegreesString.substring(0, selectedDegreesString.length() - 3)
      SpiritHelpers.setPrefs(NewsCreate.this, "selectedCreationDegrees", selectedDegreesString, false)
      updateDisplay()
    }
  }

  def updateDisplay() {
    val degreeString = SpiritHelpers.getStringPrefs(NewsCreate.this, "selectedCreationDegrees", false)
    val expdateString = SpiritHelpers.getStringPrefs(NewsCreate.this, "createNewsExpireDate", false)

    NewsCreate.this.findViewById(R.id.degreeText).asInstanceOf[TextView].setText(Html.fromHtml("<b>" + getString(R.string.string_regardingDegrees) + "</b> ") + degreeString.replace("<;>", ", ")) //TODO Fat Font
    NewsCreate.this.findViewById(R.id.datePexpiredegreeText).asInstanceOf[TextView].setText(Html.fromHtml("<b>" + getString(R.string.string_valid_till) + "</b>") + expdateString.substring(0, 10))
  }

  def translateDegreeNameToId(degreeName: String, map: ListMap[String, Int]): String = {
    val degreeNameArr = degreeName.split("<;>")
    var returnString = ""
    for (singleDegreeName <- degreeNameArr) {
      if (singleDegreeName.equals(degreeNameArr.last)) {
        returnString = returnString + map.getOrElse(singleDegreeName, "").toString
      } else {
        returnString = returnString + map.getOrElse(singleDegreeName, "").toString + "<;>"
      }
    }
    returnString
  }

  def fillStringWithNull(monthOrDay: String): String = {
    var output = monthOrDay
    if (output.length() == 1) {
      output = "0" + output
    }
    output
  }


}
