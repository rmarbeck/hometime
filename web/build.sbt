name := "web"

version := "0.9"

lazy val root = (project in file(".")).enablePlugins(PlayJava, SbtWeb)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.0",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  javaJdbc,
  javaEbean,
  filters,
  cache,
  javaWs
)


includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"

pipelineStages := Seq(rjs, digest, gzip)

LessKeys.compress in Assets := true