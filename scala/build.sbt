import Dependencies._

ThisBuild / scalaVersion     := "2.11.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

resolvers += "Artima Maven Repository" at "https://repo.artima.com/releases"

lazy val root: Project = (project in file("."))
  .settings(
    name := "XMLImporter",
    libraryDependencies ++= Seq(
      scalactic % Test,
      scalatest % Test,
      assertj % Test,
      jakartaXmlBindApi,
      jaxbRuntime,
      postgresql
    )
  )
