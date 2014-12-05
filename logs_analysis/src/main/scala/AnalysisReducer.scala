package com.epam.hadoop_training

import java.lang.Iterable

import org.apache.hadoop.io.{ArrayPrimitiveWritable, Text}
import org.apache.hadoop.mapreduce.Reducer

class AnalysisReducer extends Reducer[Text, TotalAndCount, Text, TotalAndAverage] {
  val result = TotalAndAverage()

  override def reduce(key: Text, values: Iterable[TotalAndCount], context: Reducer[Text, TotalAndCount, Text, TotalAndAverage]#Context): Unit = {
    var count = 0L
    var total = 0L
    val itr = values.iterator
    while (itr.hasNext){
      itr.next() match {
        case TotalAndCount(t, c) =>
          total += t
          count += c
      }
    }
    result.v1 = total
    result.v2 = if (count == 0) 0 else total / count
    context.write(key, result)
  }
}