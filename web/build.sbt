name := "web"

version := "0.8"

libraryDependencies ++= Seq(
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  javaJdbc,
  javaEbean,
  filters,
  cache
)     

play.Project.playJavaSettings
