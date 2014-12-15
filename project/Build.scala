import sbt.Project.Initialize
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
      deploy <<= deployTaskImpl,
      //tas ++= Seq(deploy),
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

  lazy val deploy = inputKey[Unit]("Runs job on cluster")

  lazy val deployTaskImpl: Def.Initialize[InputTask[Unit]] = Def.inputTask {
    val args = Def.spaceDelimited("<arg>").parsed
    def arg(pos: Int, default: String) = args.lift(pos).getOrElse(default)

    val user = arg(0, "root")
    val pass = arg(1, "hadoop")
    val host = arg(2, "127.0.0.1")
    val port = arg(3, "2222")
    val file = arg(4, "000000")

    val workingDir = new java.io.File(".").getCanonicalPath
    val currProjectBase = logsAnalysis.base.getName
    val scriptPath = s"$workingDir\\$currProjectBase\\src\\main\\scripts\\deploy.bat"
    val inputPath = s"$workingDir\\$currProjectBase\\src\\main\\input\\$file"
    val jarPath = assembly.value.getAbsolutePath

    val command = s"cmd /c start $scriptPath $user $pass $host $port $inputPath $jarPath"
    println("Executing command: " + command)
    java.lang.Runtime.getRuntime.exec(command)
  }
}
