import Dependencies._
import java.time.{LocalDateTime, ZoneId}

Global / onChangedBuildSource := ReloadOnSourceChanges

Global / excludeLintKeys := Set(idePackagePrefix)

ThisBuild / scalafixDependencies ++= Seq(
  "io.github.ghostbuster91.scalafix-unified" %% "unified" % "0.0.9",
  "net.pixiv" %% "scalafix-pixiv-rule" % "4.5.1"
)

ThisBuild / idePackagePrefix := Some("io.github.cakelier")

ThisBuild / name := "tuples-space-core"
ThisBuild / organization := "io.github.cake-lier"
ThisBuild / organizationName := "cake_lier"
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/cake-lier/tuples-space-core"),
    "scm:git@github.com:cake-lier/tuples-space-core.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id = "cake_lier",
    name = "Matteo Castellucci",
    email = "matteo.castellucci@outlook.com",
    url = url("https://github.com/cake-lier")
  )
)
ThisBuild / description := "A scala library for the implementation of tuples and templates, based on the JSON specification."
ThisBuild / licenses := List(
  "MIT" -> new URL("https://opensource.org/licenses/MIT")
)
ThisBuild / homepage := Some(
  url("https://github.com/cake-lier/tuples-space-core")
)
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishMavenStyle := true
ThisBuild / versionScheme := Some("semver-spec")
ThisBuild / publishTo := sonatypePublishToBundle.value
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

lazy val root = project
  .in(file("."))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(
    name := "tuples-space-core",
    scalaVersion := "3.3.0",
    scalacOptions ++= Seq(
      "-deprecation",
      "-Xfatal-warnings"
    ),
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    libraryDependencies ++= Seq(
      scalactic,
      scalatest,
      circeCore,
      circeGeneric,
      circeParser
    ),
    wartremoverErrors ++= Warts.allBut(Wart.ImplicitParameter),
    version := "1.0.1",
    coverageMinimumStmtTotal := 80,
    coverageMinimumBranchTotal := 80,
    headerLicense := Some(HeaderLicense.MIT(
      LocalDateTime.now(ZoneId.of("UTC+1")).getYear.toString,
      "Matteo Castellucci"
    )),
  )
