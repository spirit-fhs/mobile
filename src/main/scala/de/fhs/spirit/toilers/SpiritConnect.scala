package de.fhs.spirit.toilers

import android.util.Log
import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import android.app.{Activity}
import java.lang.Boolean
import org.apache.http.util.EntityUtils
import org.apache.http.client.methods.{HttpPut, HttpGet}
import org.apache.http.entity.StringEntity
import org.apache.http.params.CoreProtocolPNames
import org.apache.http.protocol.{HTTP, BasicHttpContext}
import de.fhs.spirit.R
import de.fhs.spirit.toilers.JsonProcessor.News


//TODO mal alle Methoden komplett überarbeiten
/**
 * Author: Illaz
 * Date: 30.06.11
 * Time: 16:56
 */
object SpiritConnect {

  /**Connects to the Spirit REST service and gets a Json-String
   *
   * @author Sebastian Stallenberger
   * @param context The context
   * @param restUrl Url for the REST request
   * @return JSON String
   * @todo remove Log
   */
  def getJsonFromUrl(context: Context, restUrl: String): String = {
    var returnString = ""
    Log.d("getJsonFromUrl", restUrl)
    Log.d("getJsonFromUrl", "Standalone?" + SpiritHelpers.getBoolPrefs(context.asInstanceOf[Activity], "standalone").toString)
    if (SpiritHelpers.getBoolPrefs(context.asInstanceOf[Activity], "standalone")) {
      Log.d("getJsonFromUrl", "standalone active")
      try {
        returnString = DatabaseOps.getJsonFromUrl(context, restUrl)
        Log.d("getJsonFromUrl", "Returned String from DatabaseOps: " + returnString)
      } catch {
        case e: Exception =>
          Log.e("EXCEPTION", e.getMessage)
      }
    } else {
      Log.d("getJsonFromUrl", "standalone inactive")
      val basicHttpContext = new BasicHttpContext
      val spiritHttpClient = new SpiritHttpClient(context)
      val httpGet = new HttpGet(restUrl)
      httpGet.setHeader("Accept", "application/json") //also possible: application/xml
      try {
        val response = spiritHttpClient.execute(httpGet, basicHttpContext)
        val inputStream = response.getEntity.getContent
        returnString = SpiritHelpers.convertStreamToString(inputStream)
      } catch {
        case e: Exception =>
          Log.e("EXCEPTION", e.getMessage)
      }
    }


    Log.d("JSONFROMURLSTRING", returnString)
    returnString
  }

  /**Connects to the Spirit REST service, puts a Json-String
   *
   * @author Sebastian Stallenberger
   * @param context The context
   * @param restUrl Url for the REST request
   * @param putJson JSON String to be put
   * @return Answer of REST service as String
   */
  def putJsonToUrl(context: Context, restUrl: String, putJson: String): String = {
    var returnString = ""
    val basicHttpContext = new BasicHttpContext()

    val spiritHttpClient = new SpiritHttpClient(context)
    spiritHttpClient.getParams.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HTTP.UTF_8)
    val httpPut = new HttpPut(restUrl)
    val stringEntity = new StringEntity(putJson)

    httpPut.setEntity(stringEntity)
    httpPut.setHeader("Accept", "application/json")
    httpPut.setHeader("Content-Type", "application/json")

    try {
      val response = spiritHttpClient.execute(httpPut, basicHttpContext)
      returnString = EntityUtils.toString(response.getEntity)
    } catch {
      case e: Exception =>
        Log.e("EXCEPTION", e.getMessage)
    }
    returnString
  }

  /**Checks with Android methods if a connection to the internet exists.
   *
   * @author Sebastian Stallenberger
   * @param context The context
   * @return Connection exists (true/false)
   */
  def checkInternetConnection(context: Context): Boolean = {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE).asInstanceOf[ConnectivityManager]
    if (connectivityManager.getActiveNetworkInfo != null
      && connectivityManager.getActiveNetworkInfo.isAvailable
      && connectivityManager.getActiveNetworkInfo.isConnected) {
      true
    } else {
      false
    }
  }

  /**Gets the current JSON String of the news and saves it to file "newsJsonString"
   *
   * @author Sebastian Stallenberger
   * @param context The context
   * @return Is it a new String? (for service notfication etc.)
   * @todo delete Log.d things
   */
  def getNews(context: Context): Boolean = {
    var returnBoolean = false
    val newsUrl = "https://" + context.getString(R.string.ipadress) + ":" + context.getString(R.string.port) + "/fhs-spirit/news"
    val newsJsonString = SpiritConnect.getJsonFromUrl(context, newsUrl).asInstanceOf[String]
    Log.d("NEWSJSONSTRING", "getNews")
    Log.d("NEWSJSONSTRING", newsJsonString)

    if (newsJsonString == "" || newsJsonString == "Bad URI4" || newsJsonString == "Bad") {
      Log.d("JSON IS", "empty")
      Toast.makeText(context, "Fehler in den empfangenen Daten.", Toast.LENGTH_LONG).show()
    } else {
      Log.d("JSON IS", "full")
      Log.d("LOADSTRING", SpiritHelpers.loadString(context.asInstanceOf[Activity], "newsJsonString"))
      /*if (SpiritHelpers.loadString(context.asInstanceOf[Activity], "newsJsonString").asInstanceOf[String].equals(newsJsonString)) {
        returnBoolean = true
      }   */
      Log.d("STRINGCONTROL", "VOR SAVE")
      SpiritHelpers.saveString(context, "newsJsonString", newsJsonString)
      Log.d("STRINGCONTROL", "NACH SAVE")
      Log.d("SAVEDSTRINCONTROL", SpiritHelpers.loadString(context.asInstanceOf[Activity], "newsJsonString"))
      SpiritHelpers.setLastNewsDate(context.asInstanceOf[Activity])
    }
    returnBoolean
  }

  /**Gets the current JSON String of degreeClasses and saves it to file "degreeJsonString"
   *
   * @author Sebastian Stallenberger
   * @param context The context
   * @todo remove Log
   */
  def getDegreeClasses(context: Context) {
    val degreeUrl = "https://" + context.getString(R.string.ipadress) + ":" + context.getString(R.string.port) + "/fhs-spirit/degreeClass"
    val degreeJsonString = SpiritConnect.getJsonFromUrl(context, degreeUrl).asInstanceOf[String]
    Log.d("getDegreeClasses", degreeJsonString)
    if (degreeJsonString == "" || degreeJsonString == "Bad URI4" || degreeJsonString == "Bad") {
      //Toast.makeText(context, "Fehler in den empfangenen Daten.", Toast.LENGTH_LONG).show()   //TODO Can't create handler inside thread that has not called Looper.prepare()
    } else {
      Log.d("getDegreeClasses", "Save degreeJsonString")
      SpiritHelpers.saveString(context, "degreeJsonString", degreeJsonString)
    }
  }

  def getSingleNews(context: Context, id: String): News = {
    var returnNews: News = null
    val degreeUrl = "https://" + context.getString(R.string.ipadress) + ":" + context.getString(R.string.port) + "/fhs-spirit/news/" + id.toString
    val singleNewsJsonString = SpiritConnect.getJsonFromUrl(context, degreeUrl).asInstanceOf[String]
    Log.d("getSingleNewsString", singleNewsJsonString)
    if (singleNewsJsonString == "" || singleNewsJsonString == "Bad URI4" || singleNewsJsonString == "Bad") {
      //Toast.makeText(context, "Fehler in den empfangenen Daten.", Toast.LENGTH_LONG).show()   //TODO Can't create handler inside thread that has not called Looper.prepare()
    } else {
      val newsList = JsonProcessor.jsonStringToSingleNews(singleNewsJsonString).asInstanceOf[List[News]]
      returnNews = newsList.head
      Log.d("getSingleNewsReturnNews", returnNews.toString)
    }
    returnNews
  }

  /**Creates a news
   *
   * @author Sebastian Stallenberger
   * @param context The context
   * @param fhsId The fhsId as String
   * @param subject The news subject as String
   * @param message The news message as String
   * @param expireDate The news expire date as String
   * @param degreeClass A String of degreeClass ids seperated by <;>
   * @return Success?
   * @todo build and refector function
   */
  def createNews(context: Context, fhsId: String, subject: String, message: String, expireDate: String, degreeClass: String): Boolean = {
    val restUrl = "https://" + context.getString(R.string.ipadress) + ":" + context.getString(R.string.port) + "/fhs-spirit/news"
    var classids = ""
    val degreeClassArr = degreeClass.split("<;>")
    degreeClassArr.foreach(
      element => {
        if (element.equals(degreeClassArr.last)) {
          classids = classids + """{ "class_id":""" + element + """}"""
        } else {
          classids = classids + """{ "class_id":""" + element + """},"""
        }
      }
    )
    //TODO create Json with Gson or something like this
    val putString = """{"news":{
                          "title":"""" + subject + """",
                          "content":"""" + message + """",
                          "expireDate":"""" + expireDate + """",
                          "degreeClass":[""" + classids + """
                          ],
                          "owner":{
                            "fhs_id":"""" + fhsId + """"
                          }
                        }
                      } """
    /*val putString = """{
                  "news":{
                    "title":"neue news1000000",
                    "content":"das ist eine neue news",
                    "expireDate":"2011-12-24 12:00:00",
                    "degreeClass":[{
                      "class_id":2
                    }],
                    "owner":{
                      "fhs_id":"braun"
                    }
                  }
                }"""   */
    //TODO wait for Bens email and correct String
    val encodedString: String = new String(putString.getBytes("UTF-8"), "ISO8859-1")
    Log.d("ENCODEDSTRING", encodedString)
    val responseString = putJsonToUrl(context, restUrl, encodedString)
    Log.d("RESPONSESTRING", responseString)

    if (responseString.contains(subject)) {
      true
    } else {
      false
    }

  }

  /**Creates a comment for a news
   *
   * @author Sebastian Stallenberger
   * @param context The context
   * @param newsId The newsId as Int
   * @param ownerFhsId The fhsId as String
   * @param content The comment message as String
   * @return Success?
   * @todo build and refector function
   */
  def createNewsComment(context: Context, newsId: Int, ownerFhsId: String, content: String): Boolean = {
    //TODO parameter anpassen usw
    val restUrl = "https://" + context.getString(R.string.ipadress) + ":" + context.getString(R.string.port) + "/fhs-spirit/news/comment"

    //TODO Json richtig erzeugen
    val putString = """{ "newsComment":{
                            "content":"""" + content + """",
                            "news":{
                              "news_id":""" + newsId + """
                            },
                            "owner":{
                              "fhs_id":"""" + ownerFhsId + """"
                            }
                          }
                        } """
    //TODO string zusammensetzen

    val encodedString: String = new String(putString.getBytes("UTF-8"), "ISO8859-1")
    Log.d("sendString", encodedString)
    val responseString = putJsonToUrl(context, restUrl, encodedString)
    Log.d("responseString", responseString)
    if (responseString.contains(content)) {
      true
    } else {
      false
    }
  }
}