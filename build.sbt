name := """ebookey"""

version := "1.0-SNAPSHOT"
EclipseKeys.preTasks := Seq(compile in Compile)
EclipseKeys.projectFlavor := EclipseProjectFlavor.Java
EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.jsoup" % "jsoup" % "1.8.3",
  "org.slf4j" % "slf4j-api"       % "1.7.7",
  "org.slf4j" % "jcl-over-slf4j"  % "1.7.7"	,
  "nl.siegmann.epublib" % "epublib-core" % "3.1"
).map(_.force())

//libraryDependencies ~= { _.map(_.exclude("org.slf4j", "slf4j-jdk14")) }



resolvers := Seq(Resolver.mavenLocal,"Maven Central Server" at "http://repo1.maven.org/maven2","Psiegman-Maven-Repo" at "https://github.com/psiegman/mvn-repo/raw/master/releases")

checksums := Nil
// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


