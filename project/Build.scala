import sbt._
import Keys._

import sbtassembly.Plugin._
import AssemblyKeys._

object Build extends Build{
  import sbtassembly.Plugin._
  import Dependencies._

  val SCALA_MAJOR_VERSION = "2.11"
  val SCALA_MINOR_VERSION = "4"
  val SCALA_VERSION = SCALA_MAJOR_VERSION + "." + SCALA_MINOR_VERSION

  lazy val warpCommonSettings = Defaults.defaultSettings ++ Seq(
    organization  := "com.epam",
    scalaVersion  := SCALA_VERSION,
    scalacOptions := Seq("-deprecation", "-unchecked", "-encoding", "utf8"),
    resolvers     ++= Dependencies.resolutionRepos
  )

  lazy val logsAnalysis = Project("logs_analysis", file("./logs_analysis"), settings = warpCommonSettings ++ baseAssemblySettings)
    .settings(
      libraryDependencies ++=
        test(mr_unit) ++
          provided(
            hadoop_client,
            hadoop_mapreduce_client_jobclient,
            hadoop_common,
            hadoop_yarn_api,
            hadoop_yarn_common,
            hadoop_yarn_client),
      mergeStrategy in assembly <<= (mergeStrategy in assembly) { old => {
          case s if s contains "org\\apache" => MergeStrategy.last
          case x => old(x)
        }
      }
    )
}
