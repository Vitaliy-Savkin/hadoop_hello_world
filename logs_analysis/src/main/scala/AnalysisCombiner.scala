package com.epam.hadoop_training

import java.lang.Iterable

import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Reducer

class AnalysisCombiner extends Reducer[Text, TotalAndCount, Text, TotalAndCount] {
  val result = TotalAndCount(0, 0)

  override def reduce(key: Text, values: Iterable[TotalAndCount], context: Reducer[Text, TotalAndCount, Text, TotalAndCount]#Context): Unit = {
    val itr = values.iterator()
    while (itr.hasNext){
      itr.next() match{
        case TotalAndCount(total, count) =>
          result.v1 += total
          result.v2 += count
      }
    }
    context.write(key, result)
  }
}