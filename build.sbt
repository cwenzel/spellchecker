name := "SpellChecker"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.5.0"

lazy val commonSettings = Seq(
  version := "0.1",
  organization := "",
  scalaVersion := "2.11.7",
  test in assembly := {}
)

lazy val app = (project in file("app")).
  settings(commonSettings: _*).
  settings(mainClass in assembly := Some("SpellChecker"))

