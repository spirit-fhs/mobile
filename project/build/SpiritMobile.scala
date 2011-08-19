import sbt._

trait Defaults {
  def androidPlatformName = "android-7"
}
class SpiritMobile(info: ProjectInfo) extends ParentProject(info) {
  override def shouldCheckOutputDirectories = false
  override def updateAction = task { None }
  
  def sources = "src".descendentsExcept("R.java", "java")

  lazy val main  = project(".", "SpiritMobile", new MainProject(_))
  lazy val tests = project("tests",  "tests", new TestProject(_), main)

  class MainProject(info: ProjectInfo) extends AndroidProject(info) with Defaults with MarketPublish with AndroidManifestGenerator {
	val jerksonRepo = "Coda Hales Repository" at "http://repo.codahale.com"
	
    val keyalias  = "spiritmobile"
    val scalatest = "org.scalatest" % "scalatest" % "1.2" % "test"
	//val lift_json = "net.liftweb" %% "lift-json" % "2.4-M1"
	//val jerkson =  "com.codahale" % "jerkson_2.9.0-1" % "0.3.3-SNAPSHOT"
  }

  class TestProject(info: ProjectInfo) extends AndroidTestProject(info) with Defaults
}