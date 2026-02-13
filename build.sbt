ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "scala-presentation",
    version := "1.0.0",
    libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.13.1"
  )
