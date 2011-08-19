package de.fhs.spirit.toilers

import com.google.gson.Gson
import scala.collection.JavaConversions._
import android.util.Log
import com.codahale.jerkson.Json._
import scala.Collection


//needed for JavaList to ScalaList conversion

/**
 * Author: Illaz
 * Date: 29.06.11
 * Time: 18:37
 */

object JsonProcessor {

  def jsonStringToNewsList(jsonString: String) = {
    //TODO stream-parser statt tree-parser nutzen... für Batterieverbrauch.. jackson hat streamparser
    //TODO Ben: GZip nutzen für komprimierung vor übertragung
    Log.d("Zu parsender jsonString", jsonString.toString)
    var workJsonString = jsonString
    if (jsonString.equals("")) {
      workJsonString = """{
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
    Log.d("Zu parsender String", workJsonString.toString)
    //val newsScalaList = parse[NewsList](jsonString)

     val gson = new Gson().asInstanceOf[Gson]
var gsonObject = gson.fromJson(workJsonString, classOf[NewsList])

var newsJavaList = gsonObject.news.asInstanceOf[java.util.List[News]] //TODO orderBy
var newsScalaList = newsJavaList.toList //mutates the JavaList from GSON to Scala List

    Log.d("NEUER PARSER, NEUES GLÜCK", newsScalaList.toString)
    newsScalaList
  }

  def jsonStringToDegreeList(jsonString: String) = {
    //TODO stream-parser statt tree-parser nutzen... für Batterieverbrauch.. jackson hat streamparser
    //TODO Ben: GZip nutzen für komprimierung vor übertragung
    var workJsonString = jsonString
    Log.d("DEGREEJSONSTRING", jsonString)
    if (jsonString == "") {
      workJsonString = """{
                          "degreeClass":[{
                            "class_id":1,
                            "parent":null,
                            "title":"NoClassFound",
                            "classType":"RootClass",
                            "mail":"all@fh-sm.de",
                            "subClasses":[]
                              }]
                        }"""
    }

    val gson = new Gson().asInstanceOf[Gson]
    var gsonObject = gson.fromJson(workJsonString, classOf[DegreeClassList])

    var dcJavaList = gsonObject.degreeClass.asInstanceOf[java.util.List[DegreeClassExt]] //TODO orderBy
    var dcScalaList = dcJavaList.toList //mutates the JavaList from GSON to Scala List
    //val dcScalaList = parse[DegreeClassList](jsonString)
    dcScalaList
  }

  def jsonStringToSingleNews(jsonString: String) = {
    var workJsonString = jsonString
    /*    if (jsonString == "") {
      workJsonString = """{
                          "degreeClass":[{
                            "class_id":1,
                            "parent":null,
                            "title":"NoClassFound",
                            "classType":"RootClass",
                            "mail":"all@fh-sm.de",
                            "subClasses":[]
                              }]
                        }"""
    }*/

    val gson = new Gson().asInstanceOf[Gson]
    var gsonObject = gson.fromJson(workJsonString, classOf[NewsList])

    var newsJavaList = gsonObject.news.asInstanceOf[java.util.List[News]] //TODO orderBy
    var newsScalaList = newsJavaList.toList //mutates the JavaList from GSON to Scala List

    //val newsScalaList = parse[NewsList](jsonString)
    newsScalaList
  }

  //CASECLASSES NEWS


  //case class DegreeClass(class_id: Int, title: String, parent: Parent, classType: String, mail: String)

  case class NewsList(news: java.util.List[News])

  case class News(news_id: Int,
                  title: String,
                  content: String,
                  owner: Owner,
                  expireDate: String,
                  creationDate: String,
                  lastModified: String,
                  degreeClass: java.util.List[DegreeClassExt],
                  newsComment: java.util.List[NewsComment])

  case class Owner(fhs_id: String, displayedName: String, memberType: String)

  case class NewsComment(comment_id: Int, content: String, creationDate: String, owner: Owner)

  case class DegreeClassExt(class_id: Int, parent: Parent, title: String, classType: String, mail: String, subClasses: java.util.List[DegreeClassExt])



  //CASECLASSSES DEGREECLASS
  /*case class NewDegreeClass(class_id: Int, parent_id: Int, title: String, classType: String, mail: String)  */

  case class DegreeClassList(degreeClass: java.util.List[DegreeClassExt])

  case class Parent(class_id: Int, title: String)



}