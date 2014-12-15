package com.epam.hadoop_training

import org.apache.hadoop.conf.{Configuration, Configured}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.SequenceFile.CompressionType
import org.apache.hadoop.io.Text
import org.apache.hadoop.io.compress.{SnappyCodec, CompressionCodec}
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.{TextInputFormat, FileInputFormat}
import org.apache.hadoop.mapreduce.lib.output.{SequenceFileOutputFormat, FileOutputFormat}
import org.apache.hadoop.util.{Tool, ToolRunner}

import Counters.UserBrowsers._

//hadoop fs -rm -r /test/access_log/output &&
//hadoop jar logs_analysis-assembly-0.1-SNAPSHOT.jar -Ddir.input=/test/access_log/input/ -Ddir.output=/test/access_log/output/ -Dcompress=true
class AnalysisDriver extends Configured with Tool{
  override def run(strings: Array[String]): Int = {
    val inputDir = getConf.get("dir.input")
    val outputDir = getConf.get("dir.output")
    val compress = getConf.getBoolean("compress", false)

    implicit val job = Job.getInstance(getConf)
    job.setJarByClass(this.getClass)
    job.setJobName(this.getClass.getName)
    job.setNumReduceTasks(2)

    FileInputFormat.addInputPath(job, new Path(inputDir))
    FileOutputFormat.setOutputPath(job, new Path(outputDir))

    if(compress) {
      //hadoop fs -libjars logs_analysis-assembly-0.1-SNAPSHOT.jar -text /test/access_log/output/part-r-00000
      job.setOutputFormatClass(classOf[SequenceFileOutputFormat[_, _]])
      job.setInputFormatClass(classOf[TextInputFormat])

      getConf.set     ("mapreduce.map.output.compress",                    false.toString)
      getConf.set     ("mapreduce.output.fileoutputformat.compress",       true.toString)
      getConf.set     ("mapreduce.output.fileoutputformat.compress.type",  CompressionType.BLOCK.toString)
      getConf.setClass("mapreduce.output.fileoutputformat.compress.codec", classOf[SnappyCodec], classOf[CompressionCodec])
    } else {
      //hadoop fs -cat /test/access_log/output/part-r-00000
    }

    job.setMapperClass(classOf[AnalysisMapper])
    job.setCombinerClass(classOf[AnalysisCombiner])
    job.setReducerClass(classOf[AnalysisReducer])


    job.setMapOutputKeyClass(classOf[Text])
    job.setMapOutputValueClass(classOf[TotalAndCount])

    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[TotalAndAverage])

    val res = if (job.waitForCompletion(true)) 0 else 1

    job.MozillaCounter       foreach { c => println(s"Mozilla users count: ${c.getValue}")}
    job.IECounter            foreach { c => println(s"IE users count: ${c.getValue}")}
    job.OtherBrowsersCounter foreach { c => println(s"Other browsers users count: ${c.getValue}")}

    res
  }
}

object AnalysisDriver{
  def main(args: Array[String]): Unit ={
    val res = ToolRunner.run(new Configuration, new AnalysisDriver, args)
    System.exit(res)
  }
}