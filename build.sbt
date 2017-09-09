import _root_.play.sbt.PlayImport._

import sbt.Keys._
import sbt._

name := "tapi-api-definition"

version := "1.0"

lazy val `tapi_api_definition` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq( ws, guice )
libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.12.6-play26"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test,it"
libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19" % "test,it"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

lazy val microservice = (project in file("."))
  .enablePlugins(Seq(play.sbt.PlayScala) : _*)
  .configs(IntTest)
  .settings(inConfig(IntTest)(Defaults.testSettings): _*)
  .settings(
    Keys.fork in IntTest := false,
    unmanagedSourceDirectories in IntTest <<= (baseDirectory in IntTest) (base => Seq(base / "it"))
  )

lazy val IntTest = config("it") extend Test
