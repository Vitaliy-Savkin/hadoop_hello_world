package com.epam.hadoop_training

import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.Job

object Counters{
  object UserBrowsers {
    private val groupName = "UserBrowsers"

    implicit def pimpContext[T1, T2, T3, T4](context: Mapper[T1, T2, T3, T4]#Context) = new {
      def MozillaCounter = context.getCounter(groupName, "Mozilla")

      def IECounter = context.getCounter(groupName, "IE")

      def OtherBrowsersCounter = context.getCounter(groupName, "Other")
    }

    implicit def pimpJob(job: Job) = new {
      def MozillaCounter = Option(job.getCounters.findCounter(groupName, "Mozilla"))

      def IECounter = Option(job.getCounters.findCounter(groupName, "IE"))

      def OtherBrowsersCounter = Option(job.getCounters.findCounter(groupName, "Other"))
    }
  }
}