name := "web"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  filters,
  cache
)     

play.Project.playJavaSettings
