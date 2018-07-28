name := "async-producer-consumer"

version := "0.1"

scalaVersion := "2.12.6"

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7")

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "utf-8",
  "-explaintypes",
  "-feature",
  "-language:existentials",
  "-language:experimental.macros",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xcheckinit",
  "-Xfatal-warnings",
  "-Xfuture",
  "-Xlint:adapted-args",
  "-Xlint:by-name-right-associative",
  "-Xlint:constant",
  "-Xlint:delayedinit-select",
  "-Xlint:doc-detached",
  "-Xlint:inaccessible",
  "-Xlint:infer-any",
  "-Xlint:missing-interpolator",
  "-Xlint:nullary-override",
  "-Xlint:nullary-unit",
  "-Xlint:option-implicit",
  "-Xlint:package-object-classes",
  "-Xlint:poly-implicit-overload",
  "-Xlint:private-shadow",
  "-Xlint:stars-align",
  "-Xlint:type-parameter-shadow",
  "-Xlint:unsound-match",
  "-Yno-adapted-args",
  "-Ypartial-unification",
  "-Ywarn-dead-code",
  "-Ywarn-extra-implicit",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Ywarn-nullary-override",
  "-Ywarn-nullary-unit",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused:implicits",
  "-Ywarn-unused:imports",
  "-Ywarn-unused:locals",
  "-Ywarn-unused:params",
  "-Ywarn-unused:patvars",
  "-Ywarn-unused:privates",
  "-Ywarn-value-discard"
)

val Http4sVersion = "0.19.0-M1"
val CirceVersion = "0.10.0-M1"
val Fs2Version = "1.0.0-M1"

libraryDependencies ++= Seq(
  "co.fs2" %% "fs2-core" % Fs2Version,
  "co.fs2" %% "fs2-io" % Fs2Version,
  "io.circe" %% "circe-core" % CirceVersion,
  "io.circe" %% "circe-parser" % CirceVersion,
  "org.http4s" %% "http4s-core" % Http4sVersion,
  "org.http4s" %% "http4s-dsl" % Http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
  "com.github.pureconfig" %% "pureconfig" % "0.9.1",
  "org.typelevel" %% "cats-effect" % "1.0.0-RC2",
  "org.scalacheck" %% "scalacheck" % "1.14.0" % Test,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)
