package de.fhs.spirit.toilers

import android.content.Context
import org.apache.http.util.EntityUtils
import org.apache.http.client.methods.{HttpPut}
import org.apache.http.entity.StringEntity
import org.apache.http.params.CoreProtocolPNames
import org.apache.http.protocol.{HTTP, BasicHttpContext}
import de.fhs.spirit.R
import android.database.sqlite.SQLiteDatabase
import android.util.Log

//TODO mal alle Methoden komplett überarbeiten
/**
 * Author: Illaz
 * Date: 30.06.11
 * Time: 16:56
 */
object DatabaseOps {

  /**Gets a Json-String
   *
   * @author Sebastian Stallenberger
   * @param context The context
   * @param restUrl Url for the REST request
   * @return JSON String
   * @todo remove Log
   */
  def getJsonFromUrl(context: Context, restUrl: String): String = {
    var returnString = ""
    Log.d("getJsonFromUrl(DB)", restUrl)
    val baseString = "https://" + context.getString(R.string.ipadress) + ":" + context.getString(R.string.port) + "/"
    Log.d("getJsonFromUrl(DB)", "BaseString: " + baseString)
    val baseStringLenght = baseString.length()
    Log.d("getJsonFromUrl(DB)", "BaseStringLength: " + baseStringLenght.toString)

    val decisionString = restUrl.substring(baseStringLenght)
    Log.d("getJsonFromUrl(DB)", "decisionString: " + decisionString)
    if (decisionString.equals("fhs-spirit/news")) {
      Log.d("getJsonFromUrl(DB)", "choosenObject: News")
      returnString = getNewsString
    } else if (decisionString.equals("fhs-spirit/degreeClass")) {
      Log.d("getJsonFromUrl(DB)", "choosenObject: Degrees")
      returnString = getDegreeString
    } else {
      Log.d("getJsonFromUrl(DB)", "choosenObject: NOTHING!!!")
    }
    Log.d("getJsonFromUrl(DB)", "returnString: " + returnString)
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
    initDbIfNecessary(context)
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

  def getNewsString: String = {
    """{
       "news":[
          {
             "news_id":-1,
             "title":"Fehler",
             "content":"Leider keine Nachrichten vorhanden.",
             "owner":{
                "fhs_id":"system",
                "displayedName":"System",
                "memberType":"Lecturer"
             },
             "expireDate":"3011-12-24 12:00:00",
             "creationDate":"3011-06-15 10:18:00",
             "lastModified":"3011-06-15 17:28:03",
             "degreeClass":[
                {
                   "class_id":1,
                   "title":"AllClasses",
                   "parent":{
                      "class_id":0,
                      "title":"leer"
                   },
                   "classType":"Course",
                   "mail":""
                }
             ],
             "newsComment":[

             ]
          }
       ]
    }"""
  }

  def getDegreeString: String = {
    """{
       "degreeClass":[
          {
             "class_id":1,
             "parent":null,
             "title":"NoClassFound",
             "classType":"RootClass",
             "mail":"all@fh-sm.de",
             "subClasses":[

             ]
          }
       ]
    }"""
  }

  def initDbIfNecessary(context: Context) {
    Log.d("initDbIfNecessary", "INIT DATABASE (seems to be necessary :P)")
    val MODE_PRIVATE = 0
    var spiritDB: SQLiteDatabase = null
    val dbName = "SpiritDatabase"
    try {
      spiritDB = context.openOrCreateDatabase(dbName, MODE_PRIVATE, null)
      //CREATE TABLES
      val createNewsTableString = """CREATE TABLE IF NOT EXISTS news
                                    ( news_id integer primary key autoincrement,
                                      title varchar(100),
                                      content varchar(400),
                                      owner varchar(100),
                                      expireDate varchar(20),
                                      creationDate varchar(20),
                                      lastModified varchar(20)
                                      );""""
      spiritDB.execSQL(createNewsTableString)
      val createCommentTableString = """CREATE TABLE IF NOT EXISTS comments
                                    ( comment_id integer primary key autoincrement,
                                      title varchar(100),
                                      content varchar(400),
                                      owner integer
                                      );""""
      spiritDB.execSQL(createCommentTableString)
      val createNewsCommentTableString = """CREATE TABLE IF NOT EXISTS news_comments
                                    ( news_id integer,
                                      comment_id integer
                                      );""""
      spiritDB.execSQL(createNewsCommentTableString)

      //INSERT DATA
      //insert News
      val baseprefix = "INSERT INTO news (title, content, owner, expireDate, creationDate, lastModified) VALUES ("
      val basesuffix = ");"
      val listOfInsertQuerys = List(
        "'Stundenausfall', 'Wegen erhöhten Bahn-Preisen muss mein Unterricht für MAI3 vorübergehen ausfallen.', 'braun', '2011-10-24 12:00:00', '2011-10-24 12:00:00', '2011-12-24 12:00:00'",
        "'Stromausfall', 'Wegen erhöhten Strom-Preisen muss der Unterricht vorübergehen ausfallen.', 'otto', '2011-12-24 12:00:00', '2011-12-24 12:00:00', '2011-12-24 12:00:00'",
        "'Wieder da!', 'Hab mir schnell ein Auto gekauft. Ist billiger als Bahn fahren. Der Unterricht findet ab jetzt wieder statt.', 'braun', '2011-11-12 12:00:00', '2011-11-12 12:00:00', '2011-12-24 12:00:00'")
      listOfInsertQuerys.foreach(
        element => {
          spiritDB.execSQL(baseprefix + element + basesuffix)
          Log.d("INSERT IN DB", element)
        }
      )

    } finally {
      if (spiritDB != null) {
        spiritDB.close()
      }
    }
  }
}