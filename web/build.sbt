name := "web"

version := "0.8"

libraryDependencies ++= Seq(
  "com.typesafe" %% "play-plugins-mailer" % "2.2.0",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  javaJdbc,
  javaEbean,
  filters,
  cache
)     

play.Project.playJavaSettings
