// Comment to get more information during initialization
logLevel := Level.Warn

resolvers += "Secured Central Repository" at "https://repo1.maven.org/maven2"

externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = false)

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.4")

// web plugins

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")
 
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.0.0")
