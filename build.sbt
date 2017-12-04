name := "skill_tag"

version := "1.0"

lazy val `skill_tag` = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  evolutions,
  javaWs,
  guice,
  "org.postgresql" % "postgresql" % "9.4-1206-jdbc42",
  "org.mindrot" % "jbcrypt" % "0.3m"
)

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

routesGenerator := InjectedRoutesGenerator