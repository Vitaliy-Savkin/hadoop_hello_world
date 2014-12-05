import sbt._

object Dependencies {
  val resolutionRepos = Seq(
//    "Typesafe releases"   at "http://repo.typesafe.com/typesafe/releases/",
//    "Typesafe repository" at "http://repo.typesafe.com/typesafe/repo/",
//    "spray"               at "http://repo.spray.io/"
//    "Apache releases" at "https://repository.apache.org/content/repositories/releases"
  )

  def compile   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def provided  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
  def runtime   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
  def container (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

  object V {
    val hadoop = "2.6.0"
    val yarn   = "2.6.0"
    val mrunit = "1.1.0"
  }
  
  val hadoop_client                     = "org.apache.hadoop" % "hadoop-client"                     % V.hadoop
  val hadoop_mapreduce_client_jobclient = "org.apache.hadoop" % "hadoop-mapreduce-client-jobclient" % V.hadoop
  val hadoop_common                     = "org.apache.hadoop" % "hadoop-common"                     % V.hadoop
  val hadoop_yarn_api                   = "org.apache.hadoop" % "hadoop-yarn-api"                   % V.yarn
  val hadoop_yarn_common                = "org.apache.hadoop" % "hadoop-yarn-common"                % V.yarn
  val hadoop_yarn_client                = "org.apache.hadoop" % "hadoop-yarn-client"                % V.yarn
  val mr_unit                           = "org.apache.mrunit" % "mrunit"                            % V.mrunit classifier "hadoop2"
  
}
