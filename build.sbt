import BuildKeys._
import Boilerplate._

import org.typelevel.scalacoptions.ScalacOptions
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}
import sbtcrossproject.CrossProject
import sbtcrossproject.Platform
import xerial.sbt.Sonatype.sonatypeCentralHost

// ---------------------------------------------------------------------------
// Versions

val Scala212  = "2.12.21"
val Scala213  = "2.13.18"
val Scala3    = "3.3.7"

val CatsVersion        = "2.13.0"
val CirceVersionV0_14  = "0.14.15"
val PureConfigV0_17    = "0.17.8"
val MUnitVersion       = "1.3.0"
val Shapeless2xVersion = "2.3.12"
val Shapeless3xVersion = "3.4.1"

def munitTestDependency = Def.setting {
  "org.scalameta" %%% "munit" % MUnitVersion % Test
}

// ---------------------------------------------------------------------------
// Commands

addCommandAlias("ci-test-all", "+ci-test")
addCommandAlias("ci-test",     ";clean;Test/compile;test;mimaReportBinaryIssues;package")
addCommandAlias("ci-doc",      s";project root ;++$Scala3! ;clean ;unidoc")
addCommandAlias("ci",          ";project root ;reload ;+ci-test ;ci-doc")
addCommandAlias("ci-release",  ";+publishSigned ;sonatypeBundleRelease")
addCommandAlias(
  "ci-publish-local",
  "+publishLocalSigned"
)

// ---------------------------------------------------------------------------

lazy val publishStableVersion =
  settingKey[Boolean]("If it should publish stable versions to Sonatype staging repository, instead of a snapshot")

/**
  * Defines common plugins between all projects.
  */
def defaultPlugins: Project ⇒ Project = pr => {
  pr.enablePlugins(AutomateHeaderPlugin)
    .enablePlugins(GitBranchPrompt)
}

// The version with which we must keep binary compatibility.
// https://github.com/typesafehub/migration-manager/wiki/Sbt-plugin
val majorProjectSeries = "0.2.1"

def mimaSettings(projectName: String) = Seq(
  mimaPreviousArtifacts := Set("io.monix" %% projectName % majorProjectSeries),
  //mimaBinaryIssueFilters ++= MimaFilters.changesFor_3_0_1,
)

lazy val sharedSettings = Seq(
  projectTitle := "Newtypes",
  projectWebsiteRootURL := "https://newtypes.monix.io/",
  projectWebsiteBasePath := "/",
  githubOwnerID := "monix",
  githubRelativeRepositoryID := "newtypes",

  organization := "io.monix",
  scalaVersion := Scala3,
  crossScalaVersions := Seq(Scala212, Scala213, Scala3),

  // https://www.scala-lang.org/blog/2021/02/16/preventing-version-conflicts-with-versionscheme.html
  versionScheme := Some("early-semver"),

  libraryDependencySchemes ++= Seq(
    "org.scala-native" % "test-interface_native0.5_3" % "early-semver",
    "org.scala-native" % "test-interface_native0.5_2.13" % "early-semver",
    "org.scala-native" % "test-interface_native0.5_2.12" % "early-semver",
  ),

  // Turning off fatal warnings for doc generation
  Compile / doc / tpolecatExcludeOptions ++= ScalacOptions.defaultConsoleExclude,

  // Turning off fatal warnings and certain annoyances during testing
  Test / tpolecatExcludeOptions ++= ScalacOptions.defaultConsoleExclude,

  // Disable tpolecat for Scala 2.12 only
  Compile / tpolecatExcludeOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) => ScalacOptions.defaultConsoleExclude
      case _ => Set.empty
    }
  },

  // ScalaDoc settings
  autoAPIMappings := true,
  scalacOptions ++= Seq(
    // Note, this is used by the doc-source-url feature to determine the
    // relative path of a given source file. If it's not a prefix of a the
    // absolute path of the source file, the absolute path of that file
    // will be put into the FILE_SOURCE variable, which is
    // definitely not what we want.
    "-sourcepath", file(".").getAbsolutePath.replaceAll("[.]$", ""),
    // Debug warnings
    "-Wconf:any:warning-verbose",
  ),

  // https://github.com/sbt/sbt/issues/2654
  incOptions := incOptions.value.withLogRecompileOnMacro(false),

  // ---------------------------------------------------------------------------
  // Options for testing

  Test / logBuffered := false,

  // ---------------------------------------------------------------------------
  // Options meant for publishing on Maven Central

  ThisBuild / publishTo := sonatypePublishToBundle.value,
  ThisBuild / isSnapshot := {
    !isVersionStable.value || !publishStableVersion.value
  },
  ThisBuild / dynverSonatypeSnapshots := !(isVersionStable.value && publishStableVersion.value),
  ThisBuild / sonatypeProfileName := organization.value,
  sonatypeSessionName := s"[sbt-sonatype] ${name.value}-${version.value}",

  Test / publishArtifact := false,
  pomIncludeRepository := { _ => false }, // removes optional dependencies

  licenses := List(License.Apache2),
  homepage := Some(url(projectWebsiteFullURL.value)),
  headerLicense := Some {
    val years = {
      val start = "2021"
      val current = java.time.LocalDate.now().getYear().toString()
      if (start != current) s"$start-$current"
      else start
    }
    HeaderLicense.Custom(
      s"""|Copyright (c) $years Alexandru Nedelcu.
          |See the project homepage at: ${projectWebsiteFullURL.value}
          |
          |Licensed under the Apache License, Version 2.0 (the "License");
          |you may not use this file except in compliance with the License.
          |You may obtain a copy of the License at
          |
          |    http://www.apache.org/licenses/LICENSE-2.0
          |
          |Unless required by applicable law or agreed to in writing, software
          |distributed under the License is distributed on an "AS IS" BASIS,
          |WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
          |See the License for the specific language governing permissions and
          |limitations under the License."""
      .stripMargin
    )
  },

  scmInfo := Some(
    ScmInfo(
      url(s"https://github.com/${githubFullRepositoryID.value}"),
      s"scm:git@github.com:${githubFullRepositoryID.value}.git"
    )),

  developers := List(
    Developer(
      id="alexelcu",
      name="Alexandru Nedelcu",
      email="noreply@alexn.org",
      url=url("https://alexn.org")
    )),

  // -- Settings meant for deployment on Sonatype
  sonatypeCredentialHost := sonatypeCentralHost,
  usePgpKeyHex(sys.env.getOrElse("PGP_KEY_HEX", "")),
)

/**
  * Shared configuration across all sub-projects with actual code to be published.
  */
def defaultCrossProjectConfiguration(
  platforms: Platform*
)(
  pr: CrossProject,
) = {
  val sharedJavascriptSettings = Seq(
    // Use globally accessible (rather than local) source paths in JS source maps
    scalacOptions += {
      val tagOrHash = {
        val ver = s"v${version.value}"
        if (isSnapshot.value)
          git.gitHeadCommit.value.getOrElse(ver)
        else
          ver
      }
      val l = (LocalRootProject / baseDirectory).value.toURI.toString
      val g = s"https://raw.githubusercontent.com/${githubFullRepositoryID.value}/$tagOrHash/"
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, _)) =>
          s"-P:scalajs:mapSourceURI:$l->$g"
        case _ =>
          s"-scalajs-mapSourceURI:$l->$g"
      }
    },
  )
  val sharedJVMSettings = Seq(
    // https://github.com/lightbend/mima/pull/289
    ThisBuild / mimaFailOnNoPrevious := false,
  )
  val cross = pr
    .configure(defaultPlugins)
    .settings(sharedSettings)
    .settings(crossVersionSharedSources)

  platforms.foldLeft(cross) { (acc, p) =>
    p match {
      case JSPlatform =>
        acc.jsSettings(sharedJavascriptSettings)
      case _ =>
        acc.jvmSettings(sharedJVMSettings)
    }
  }
}

lazy val root = project.in(file("."))
  .enablePlugins(ScalaUnidocPlugin)
  .disablePlugins(MimaPlugin)
  .aggregate(
    coreJVM,
    coreJS,
    coreNative,
    integrationCatsV2JVM,
    integrationCatsV2JS,
    integrationCatsV2Native,
    integrationCirceV014JVM,
    integrationCirceV014JS,
    integrationCirceV014Native,
    integrationPureConfigV017JVM,
  )
  .configure(defaultPlugins)
  .settings(sharedSettings)
  .settings(doNotPublishArtifact)
  .settings(unidocSettings(coreJVM, integrationCatsV2JVM, integrationCirceV014JVM, integrationPureConfigV017JVM))
  .settings(
    ScalaUnidoc / unidoc / scalacOptions ++= Seq(
      "-siteroot",
      (baseDirectory.value / "docs").getAbsolutePath,
      "-snippet-compiler:nocompile",
      "-doc-root-content",
      file("./docs/index.md").getAbsolutePath,
    ),

    // Try really hard to not execute tasks in parallel ffs
    Global / concurrentRestrictions := Tags.limitAll(1) :: Nil,
    // Reloads build.sbt changes whenever detected
    Global / onChangedBuildSource := ReloadOnSourceChanges,
    // Deactivate sbt's linter for some temporarily unused keys
    Global / excludeLintKeys ++= Set( githubRelativeRepositoryID,
    ),
    // Use Node.js in tests
    Global / scalaJSStage := FastOptStage,
    // Used in CI when publishing artifacts to Sonatype
    Global / publishStableVersion := {
      sys.env.get("PUBLISH_STABLE_VERSION")
        .exists(v => v == "true" || v == "1" || v == "yes")
    },
  )

lazy val core = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("core"))
  .configureCross(defaultCrossProjectConfiguration(JSPlatform, JVMPlatform))
  .jsConfigure(_.disablePlugins(MimaPlugin))
  .jvmConfigure(_.settings(mimaSettings("newtypes-core")))
  .settings(
    name := "newtypes-core",
    libraryDependencies ++= Seq(
      // https://typelevel.org/cats/
      "org.typelevel" %%% "cats-core" % CatsVersion % Test,
      // https://scalameta.org/munit/
      munitTestDependency.value,
    ),
    // Version specific dependencies
    libraryDependencies ++= (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, _)) =>
        Seq(
          // https://github.com/milessabin/shapeless
          "com.chuusai" %% "shapeless" % Shapeless2xVersion % Test,
        )
      case _ =>
        Seq(
          // https://github.com/typelevel/shapeless-3
          "org.typelevel" %% "shapeless3-test" % Shapeless3xVersion % Test
        )
    }),
  )

lazy val coreJVM = core.jvm
lazy val coreJS  = core.js
lazy val coreNative = core.native

// ---
def integrationSharedSettings(other: Setting[_]*) =
  other ++ Seq(
    libraryDependencies ++= Seq(
      munitTestDependency.value,
    ),
  ) ++ Seq(Compile, Test).map { sc =>
    (sc / unmanagedSourceDirectories) ++= {
      val base = baseDirectory.value
      val jvmOrJs = base.getName
      val mainOrTest = sc match { case Compile => "main"; case Test => "test" }
      val rootDir = baseDirectory.value.getParentFile().getParentFile
      Seq(
        rootDir / "all" / "shared" / "src" / mainOrTest / "scala",
        rootDir / "all" / jvmOrJs / "src" / mainOrTest / "scala",
      )
    }
  }

// ---
def catsSharedSettings(ver: String) =
  integrationSharedSettings(
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-core" % ver,
    )
  )

lazy val integrationCatsV2 = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("integration-cats/v2"))
  .jsConfigure(_.disablePlugins(MimaPlugin))
  .configureCross(defaultCrossProjectConfiguration(JSPlatform, JVMPlatform))
  .dependsOn(core)
  .settings(catsSharedSettings(CatsVersion))
  .settings(name := "newtypes-cats-v2")

lazy val integrationCatsV2JVM = integrationCatsV2.jvm
lazy val integrationCatsV2JS  = integrationCatsV2.js
lazy val integrationCatsV2Native = integrationCatsV2.native

// ---
def circeSharedSettings(ver: String) =
  integrationSharedSettings(
    libraryDependencies ++= Seq(
      // https://circe.github.io/circe/
      "io.circe" %%% "circe-core" % ver,
      "io.circe" %%% "circe-parser" % ver % Test,
    )
  )

lazy val integrationCirceV014 = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("integration-circe/v0.14"))
  .jsConfigure(_.disablePlugins(MimaPlugin))
  .configureCross(defaultCrossProjectConfiguration(JSPlatform, JVMPlatform))
  .dependsOn(core)
  .settings(circeSharedSettings(CirceVersionV0_14))
  .settings(name := "newtypes-circe-v0-14")
  .jvmSettings(mimaSettings("newtypes-circe-v0-14"))

lazy val integrationCirceV014JVM = integrationCirceV014.jvm
lazy val integrationCirceV014JS  = integrationCirceV014.js
lazy val integrationCirceV014Native = integrationCirceV014.native

// -----
def pureConfigSharedSettings(ver: String) =
  integrationSharedSettings(
    libraryDependencies ++= Seq(
      "com.github.pureconfig" %%% "pureconfig-core" % ver,
    ),
  )

lazy val integrationPureConfigV017 = crossProject(JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("integration-pureconfig/v0.17"))
  .configureCross(defaultCrossProjectConfiguration(JVMPlatform))
  .dependsOn(core)
  .settings(pureConfigSharedSettings(PureConfigV0_17))
  .settings(
    name := "newtypes-pureconfig-v0-17",
  )

// Does not support Javascript, only JVM
lazy val integrationPureConfigV017JVM = integrationPureConfigV017.jvm
