libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.10.0"
libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.2.1"

lazy val root = (project in file(".")).
settings(
  name := "healpay-java",
  version := "0.1"
  )
