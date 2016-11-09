name := "SpellChecker"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.5.0"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.0"
libraryDependencies += "org.scalatest" % "scalatest_2.11" % "3.0.0" % "test"

lazy val commonSettings = Seq(
  version := "0.1",
  organization := "",
  scalaVersion := "2.11.7",
  test in assembly := {}
)

lazy val app = (project in file("app")).
  settings(commonSettings: _*).
  settings(mainClass in assembly := Some("SpellChecker"))

