resolvers += "Akka library repository".at("https://repo.akka.io/maven")

val scala3Version = "3.4.0"
val AkkaVersion = "2.9.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "kouse",
    version := "0.1.0",
    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "com.1stleg" % "jnativehook" % "2.1.0",
      "ch.qos.logback" % "logback-classic" % "1.5.3",

      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
    )
  )
