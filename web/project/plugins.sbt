// Comment to get more information during initialization
logLevel := Level.Warn

//resolvers += "Secured Central Repository" at "https://repo1.maven.org/maven2"
resolvers += Resolver.url("my-test-repo", url("https://scala.jfrog.io/artifactory/ivy-releases/"))(Resolver.ivyStylePatterns)

resolvers += Resolver.url("my-test-repo2", url("https://scala.jfrog.io/artifactory/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)


resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
  "Typesafe Simple Repository" at "http://repo.typesafe.com/typesafe/simple/maven-releases/",
  "Secured Central Repository" at "https://repo1.maven.org/maven2"
)


externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = false)

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.10")

// web plugins

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")
 
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.0.0")
