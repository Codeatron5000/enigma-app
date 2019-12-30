name := "EnigmaMachine"

version := "0.1"

scalaVersion := "2.13.1"

val enumeratumVersion = "1.5.13"
libraryDependencies ++= Seq(
  "com.beachape" %% "enumeratum" % enumeratumVersion,
  "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)
