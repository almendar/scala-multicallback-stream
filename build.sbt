scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked"
)

scalaVersion := "2.13.1"
scalacOptions += "-language:higherKinds"
addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)

libraryDependencies ++= Seq(
  "co.fs2" %% "fs2-core" % "2.0.1",
  "dev.zio" %% "zio-streams" % "1.0.0-RC15",
)
turbo := true
// useSuperShell := false

fork in run := true