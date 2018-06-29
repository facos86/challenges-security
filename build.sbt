name := """challenges-security"""
organization := "organization"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  guice,
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.36"
)
