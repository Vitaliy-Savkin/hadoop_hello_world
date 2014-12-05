package com.epam.hadoop_training

import org.apache.hadoop.io.{Text, LongWritable}
import org.apache.hadoop.mapreduce.Mapper

import Counters.UserBrowsers._

class AnalysisMapper extends Mapper[LongWritable, Text, Text, TotalAndCount] {
  val result = TotalAndCount()
  val ipWritable = new Text

  override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, TotalAndCount]#Context): Unit = {
    try {
      value.toString.split(' ') match {
        // ip16 -  -  [24/Apr/2011:04:55:57 -0400] "GET /sunFAQ/ HTTP/1.0" 200 8342
        // case Array(ip, "-", "-", dt, gmt, method, url, version, code, bytes)
        case Array(ip, _, _, _, _, _, _, _, _, bytes, _, userAgent, rest @ _*) =>
          ipWritable.set(ip)
          val bytesCount = if (bytes == "-") 0 else bytes.toLong
          result.v1 = bytesCount
          result.v2 = 1L
          context.write(ipWritable, result)
          // Quick and dirty check of browser
          if(userAgent startsWith "\"Mozilla")
            if(rest.lift(1) == Some("MSIE")) // skip "(compatible;" and check for IE
              context.IECounter.increment(1)
            else
              context.MozillaCounter.increment(1)
          else
            context.OtherBrowsersCounter.increment(1)
      }
    }
    catch {
      case e: Exception => throw new Exception(s"${e.getMessage} occurred while processing line $value")
    }
  }
}