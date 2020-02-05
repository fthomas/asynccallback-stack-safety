import sbtcrossproject.CrossPlugin.autoImport.crossProject

name := "asynccallback-stack-safety"

lazy val root = project
  .in(file("."))
  .aggregate(client.js)

lazy val client = crossProject(JSPlatform)
  .in(file("client"))
  .settings(
    scalaVersion := "2.12.10",
    libraryDependencies ++= Seq(
      "com.github.japgolly.scalajs-react" %%% "ext-cats-effect" % "1.6.0",
      "org.scalatest" %%% "scalatest" % "3.1.0"
    )
  )
