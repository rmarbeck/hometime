name := "web"

version := "0.9"

lazy val root = (project in file(".")).enablePlugins(PlayJava, SbtWeb)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.1",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "commons-io" % "commons-io" % "2.4",
  "com.mailjet" % "mailjet-client" % "4.1.1",
  "com.google.zxing" % "core" % "3.4.0",
  "com.google.zxing" % "javase" % "3.4.0",
  javaJdbc,
  javaEbean,
  filters,
  cache,
  javaWs
)


includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"

pipelineStages := Seq(digest, gzip)

LessKeys.compress in Assets := true

WebKeys.webTarget := target.value / "scala-web"

artifactPath in PlayKeys.playPackageAssets := WebKeys.webTarget.value / (artifactPath in PlayKeys.playPackageAssets).value.getName