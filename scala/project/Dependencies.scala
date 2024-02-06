import sbt._

object Dependencies {
  lazy val scalactic: ModuleID = "org.scalactic" %% "scalactic" % "3.2.17"
  lazy val scalatest: ModuleID = "org.scalatest" %% "scalatest" % "3.2.17"
  lazy val assertj: ModuleID = "org.assertj" % "assertj-core" % "3.24.2"
  lazy val jakartaXmlBindApi: ModuleID = "jakarta.xml.bind" % "jakarta.xml.bind-api" % "2.3.3"
  lazy val jaxbRuntime: ModuleID = "org.glassfish.jaxb" % "jaxb-runtime" % "2.3.8"
  lazy val postgresql: ModuleID = "org.postgresql" % "postgresql" % "42.7.1"
}
