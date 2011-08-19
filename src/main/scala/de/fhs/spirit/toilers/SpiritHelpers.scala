package de.fhs.spirit.toilers

import java.io._
import android.content.SharedPreferences.Editor
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import android.content.{Intent, SharedPreferences, Context}
import android.app._
import android.widget._
import android.preference.PreferenceManager
import de.fhs.spirit.activities.NewsMulti
import de.fhs.spirit.R
import java.lang.{Exception, String, Boolean}
import javax.crypto.{Cipher, SecretKey, KeyGenerator}
import de.fhs.spirit.toilers.JsonProcessor.DegreeClassExt
import collection.immutable.ListMap
import scala.collection.JavaConversions._

/**Helpers for the SpiritMobile Android application
 * @author Sebastian Stallenberger
 * @date: 30.06.11
 * @time: 16:12
 */
object SpiritHelpers {
  var secretKey: SecretKey = null //TODO better way?

  /**Shows multiple toasts one by one.
   *
   * @author Sebastian Stallenberger
   * @param context The Context
   * @param stringList A List[(String, Int)] containing (content, duration)
   */
  def showToasts(context: Context, stringList: List[(String, Int)]) {
    stringList.foreach(
      element => Toast.makeText(context, element._1, element._2).show()
    )
  }

  /**Converts InputStream to String.
   *
   * @author Sebastian Stallenberger
   * @param is Inputstream to be converted
   * @return String
   */
  def convertStreamToString(is: InputStream): String = {
    scala.io.Source.fromInputStream(is).getLines.mkString
  }

  /**Loads a text file from Android filesystem into a String.
   *
   * @author Sebastian Stallenberger
   * @param context The context
   * @param filename name of the file
   * @return String
   * @todo remove Log
   */
  def loadString(context: Activity, filename: String): String = {
    var string = ""
    try {
      string = convertStreamToString(context.openFileInput(filename))
    } catch {
      case e: Exception => Log.e("EXCEPTION", e.getMessage.toString)
    }

    Log.d("loadStringRETURNSTRING", string)
    string
  }

  /**Saves a String to a textfile in the Android filesystem.
   *
   * @author Sebastian Stallenberger
   * @param context The context
   * @param filename name of the file
   * @param string String to be saved
   * @return String
   * @todo remove Log
   */
  def saveString(context: Context, filename: String, string: String) {
    var fos: FileOutputStream = null
    try {
      fos = context.openFileOutput(filename, Context.MODE_PRIVATE)
      fos.write(string.getBytes)
      Log.d("SAVEDSTRING", filename + " = " + string)
    } catch {
      case e: Exception =>
        Log.e("EXCEPTION", e.getMessage)
    } finally {
      if (fos != null) {
        fos.close()
      }
    }
  }

  /**Saves a String to DefaultSharedPreferences.
   *
   * @author Sebastian Stallenberger
   * @param activity The activity
   * @param key The preference key
   * @param paravalue The value to be stored
   * @param encrypt Encrypt value before saving?
   */
  def setPrefs(activity: Activity, key: String, paravalue: String, encrypt: Boolean) {
    val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
    val edit: Editor = pref.edit()
    var value = paravalue
    if (encrypt) {
      value = encryptString(value)
    }
    edit.putString(key, value)
    edit.commit()
  }

  /**Loads a String Preference from DefaultSharedPreferences into String.
   * Returns "N/A" if key not found.
   *
   * @author Sebastian Stallenberger
   * @param activity The activity
   * @param key The preference key
   * @param decrypt Decrypt the loaded value?
   * @return String
   */
  def getStringPrefs(activity: Activity, key: String, decrypt: Boolean): String = {
    val context = activity.asInstanceOf[Context]
    PreferenceManager.setDefaultValues(context, R.xml.preferences, false)
    val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)

    var value = pref.getString(key, "N/A")
    if (decrypt) {
      value = decryptString(value)
    }
    value
  }

  /**Loads a Boolean Preference from DefaultSharedPreferences into String.
   * Returns null if key not found.
   *
   * @author Sebastian Stallenberger
   * @param activity The activity
   * @param key The preference key
   * @param decrypt Decrypt the loaded value?
   * @return String
   */
  def getBoolPrefs(activity: Activity, key: String): Boolean = {
    val context = activity.asInstanceOf[Context]
    PreferenceManager.setDefaultValues(context, R.xml.preferences, false)
    val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)

    pref.getBoolean(key, true)
  }

  /**Sets the current Date to Preference "LastNewsDate".
   *
   * @author Sebastian Stallenberger
   * @param context The context
   */
  def setLastNewsDate(context: Context) {
    val df: SimpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
    SpiritHelpers.setPrefs(context.asInstanceOf[Activity], "LastNewsDate", df.format(new Date()).toString + context.getString(R.string.string_oclock), false)
  }

  /**Shows a notification in the Android taskbar.
   *
   * @author Sebastian Stallenberger
   * @param context The context
   * @param tickerText The text shown in ticker
   * @param title The title of the notification
   * @param content The content of the notification
   * @param notificationId The id representing the notification
   */
  def showNewsNotification(context: Context, tickerText: CharSequence, title: CharSequence, content: CharSequence, notificationId: Int) {
    val notificationService = Context.NOTIFICATION_SERVICE
    val notificationManager = context.getSystemService(notificationService).asInstanceOf[NotificationManager]

    val icon: Int = R.drawable.logo_spirit_v03_ldpi
    val when: Long = System.currentTimeMillis()

    val notification: Notification = new Notification(icon, tickerText, when)
    val notificationIntent: Intent = new Intent(context, classOf[NewsMulti])
    val contentIntent: PendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)

    notification.setLatestEventInfo(context, title, content, contentIntent)
    //notification.defaults |= Notification.DEFAULT_SOUND
    //notification.defaults |= Notification.DEFAULT_VIBRATE;  //Requires VIBRATE permission

    notificationManager.notify(notificationId, notification)
  }

  /**Encrypts a String.
   *
   * @author Sebastian Stallenberger
   * @param text String to be encrypted
   * @return Encrypted String
   * @todo implementation :P
   */
  def encryptString(text: String): String = {
    //val cryptKey = "sCaLaClaSsFiLeDeCoDeR"
    var encrypted = text
    /*try {
      val keyGen = KeyGenerator.getInstance("AES")
      keyGen.init(128)
      secretKey = keyGen.generateKey()

      val cipher = Cipher.getInstance("AES")
      cipher.init(Cipher.ENCRYPT_MODE, secretKey)

      encrypted = cipher.doFinal(text.getBytes).toString
      Log.v("ENCRYPTEDSTRING", encrypted)
      decryptString(encrypted)


    } catch {
      case e: Exception => Log.e("ERROR_IN_ENCRYPTION", e.getMessage.toString)
    }       */
    encrypted
  }

  /**Decrypts a String (if the String was previously encrypted with function "encryptString").
   *
   * @author Sebastian Stallenberger
   * @param text Encrypted String
   * @return Decrypted String
   * @todo implementation :P
   */
  def decryptString(text: String): String = {
    val cryptKey = "sCaLaClaSsFiLeDeCoDeR"
    var decrypted = text
    /*
    try {
      val cipher = Cipher.getInstance("AES")
      cipher.init(Cipher.DECRYPT_MODE, secretKey)

      decrypted = cipher.doFinal(text.getBytes).toString
      Log.v("ENCRYPTEDSTRING", decrypted)


    } catch {
      case e: Exception => Log.e("ERROR_IN_ENCRYPTION", e.getMessage.toString)
    }

    Log.v("DECRYPTEDSTRING", decrypted)    */
    decrypted
  }

  def getDegreeMap(context: Context, list: List[DegreeClassExt], prefix: String): ListMap[String, Int] = {
    var degreeMap = new ListMap[String, Int]

    list.foreach(
      element => {
        val degreeClassExt = element.asInstanceOf[DegreeClassExt]
        var title = degreeClassExt.title
        if (degreeClassExt.title.equals("AllClasses")) {
          title = context.getString(R.string.string_all)
        }

        if (degreeClassExt.classType.equals("Group")) {
          title = prefix + " - " + context.getString(R.string.string_group) + " " + title
        }

        degreeMap += title -> degreeClassExt.class_id
        if (degreeClassExt.subClasses != null) {
          degreeMap = degreeMap ++ getDegreeMap(context, degreeClassExt.subClasses.toList, title)
        }
      }
    )
    degreeMap = ListMap(degreeMap.toList.sortBy {
      _._1
    }: _*)

    degreeMap
  }

  def getDegreeStringforNews(context: Context, list: List[DegreeClassExt], shortForm: Boolean): String = {
    var degreeString = ""

    list.foreach(
      element => {
        val degreeClassExt = element.asInstanceOf[DegreeClassExt]
        var title = degreeClassExt.title
        if (degreeClassExt.title.equals("AllClasses")) {
          title = context.getString(R.string.string_all)
        }

        if (degreeClassExt.classType.equals("Group")) {
          if (shortForm) {
            title = degreeClassExt.parent.title + "(" + title + ")"
          } else {
            title = degreeClassExt.parent.title + " - " + context.getString(R.string.string_group) + " " + title
          }
        }

        if (element.equals(list.last)) {
          degreeString = degreeString + title
        } else {
          degreeString = degreeString + title + ", "
        }


      }
    )
    degreeString
  }

  def getDegreeTitleArr(listMap: ListMap[String, Int]): Array[CharSequence] = {
    var degreeTitleArr = new Array[CharSequence](listMap.size)
    var i = 0
    listMap.foreach(
      element => {
        degreeTitleArr(i) = element._1
        i = i + 1
      }
    )

    degreeTitleArr
  }
}