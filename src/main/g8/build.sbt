import Dependencies._

lazy val root = (project in file(".")).
  settings(

    inThisBuild(List(
      organization := "$organization$",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),

    name := "$name$",

    libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "3.0.1" % Test,
        "org.scalacheck" %% "scalacheck" % "1.13.4" % Test,
        "com.lightbend" %% "kafka-streams-scala" % "0.1.0",
        "org.slf4j" % "slf4j-simple" % "1.7.12"
    ),

    resolvers ++= Seq(
      "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/",
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      "Second Typesafe repo" at "http://repo.typesafe.com/typesafe/maven-releases/",
       Resolver.sonatypeRepo("public")
    )

  )

