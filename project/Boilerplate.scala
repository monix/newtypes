import BuildKeys._

import com.github.tkawachi.doctest.DoctestPlugin.DoctestTestFramework
import com.github.tkawachi.doctest.DoctestPlugin.autoImport._
import sbt._
import sbt.Keys._
import sbtunidoc.BaseUnidocPlugin.autoImport.{unidoc, unidocProjectFilter}
import sbtunidoc.ScalaUnidocPlugin.autoImport.ScalaUnidoc

import scala.util.matching.Regex
import scala.xml.Elem
import scala.xml.transform.{RewriteRule, RuleTransformer}

object Boilerplate {
  /**
    * Applies [[filterOutDependencyFromGeneratedPomXml]] to a list of multiple dependencies.
    */
  def filterOutMultipleDependenciesFromGeneratedPomXml(list: List[(String, Regex)]*) =
    list.foldLeft(List.empty[Def.Setting[_]]) { (acc, elem) =>
      acc ++ filterOutDependencyFromGeneratedPomXml(elem:_*)
    }

  /**
    * Filter out dependencies from the generated `pom.xml`.
    *
    * E.g. to exclude Scoverage:
    * {{{
    *   filterOutDependencyFromGeneratedPomXml("groupId" -> "org.scoverage")
    * }}}
    *
    * Or to exclude based on both `groupId` and `artifactId`:
    * {{{
    *   filterOutDependencyFromGeneratedPomXml("groupId" -> "io\.estatico".r, "artifactId" -> "newtype".r)
    * }}}
    */
  def filterOutDependencyFromGeneratedPomXml(conditions: (String, Regex)*) = {
    def shouldExclude(e: Elem) =
      e.label == "dependency" && {
        conditions.forall { case (key, regex) =>
          e.child.exists(child => child.label == key && regex.findFirstIn(child.text).isDefined)
        }
      }

    if (conditions.isEmpty) Nil else {
      Seq(
        // For evicting Scoverage out of the generated POM
        // See: https://github.com/scoverage/sbt-scoverage/issues/153
        pomPostProcess := { (node: xml.Node) =>
          new RuleTransformer(new RewriteRule {
            override def transform(node: xml.Node): Seq[xml.Node] = node match {
              case e: Elem if shouldExclude(e) => Nil
              case _ => Seq(node)
            }
          }).transform(node).head
        },
      )
    }
  }

  /**
    * For working with Scala version-specific source files, allowing us to
    * use 2.12 or 2.13 specific APIs.
    */
  lazy val crossVersionSharedSources: Seq[Setting[_]] = {
    def scalaPartV = Def setting (CrossVersion partialVersion scalaVersion.value)
    Seq(Compile, Test).map { sc =>
      (sc / unmanagedSourceDirectories) ++= {
        (sc / unmanagedSourceDirectories).value.flatMap { dir =>
          Seq(
            scalaPartV.value match {
              case Some((2, y)) if y == 11 => Seq(new File(dir.getPath + "-2.11"))
              case Some((2, y)) if y == 12 => Seq(new File(dir.getPath + "-2.12"))
              case Some((2, y)) if y == 13 => Seq(new File(dir.getPath + "-2.13"))
              case _                       => Nil
            },

            scalaPartV.value match {
              case Some((2, n)) if n > 12  => Seq(new File(dir.getPath + "-2.12+"))
              case Some((2, n)) if n == 12 => Seq(new File(dir.getPath + "-2.12+"), new File(dir.getPath + "-2.12-"))
              case Some((2, n)) if n < 12  => Seq(new File(dir.getPath + "-2.12-"))
              case _                       => Nil
            },

            scalaPartV.value match {
              case Some((2, n)) if n > 13  => Seq(new File(dir.getPath + "-2.13+"))
              case Some((2, n)) if n == 13 => Seq(new File(dir.getPath + "-2.13+"), new File(dir.getPath + "-2.13-"))
              case Some((2, n)) if n < 13  => Seq(new File(dir.getPath + "-2.13-"))
              case _                       => Nil
            },
          ).flatten
        }
      }
    }
  }

  /**
    * Skip publishing artifact for this project.
    */
  lazy val doNotPublishArtifact = Seq(
    publish / skip := true,
    publish := (()),
    publishLocal := (()),
    publishArtifact := false,
    publishTo := None
  )

  /**
    * Configures generated API documentation website.
    */
  def unidocSettings(projects: ProjectReference*) = Seq(
    // Only include JVM sub-projects, exclude JS or Native sub-projects
    ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(projects:_*),

    ScalaUnidoc / unidoc / scalacOptions +=
      "-Xfatal-warnings",
    ScalaUnidoc / unidoc / scalacOptions --=
      Seq("-Ywarn-unused-import", "-Ywarn-unused:imports"),
    ScalaUnidoc / unidoc / scalacOptions ++=
      Opts.doc.title(projectTitle.value),
    ScalaUnidoc / unidoc / scalacOptions ++=
      Opts.doc.sourceUrl(s"https://github.com/${githubFullRepositoryID.value}/tree/v${version.value}€{FILE_PATH}.scala"),
    ScalaUnidoc / unidoc / scalacOptions ++=
      Seq("-doc-root-content", file("rootdoc.txt").getAbsolutePath),
    ScalaUnidoc / unidoc / scalacOptions ++=
      Opts.doc.version(version.value)
  )

  /**
    * Settings for `sbt-doctest`, for unit testing ScalaDoc.
    */
  def doctestTestSettings(tf: DoctestTestFramework) = Seq(
    doctestTestFramework := tf,
    doctestIgnoreRegex := Some(s".*(internal).*"),
    doctestOnlyCodeBlocksMode := true
  )
}
