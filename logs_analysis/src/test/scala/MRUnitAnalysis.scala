package com.epam.hadoop_training

import org.apache.hadoop.io.{Text, LongWritable}
import org.apache.hadoop.mrunit.mapreduce.{MapDriver, ReduceDriver}
import org.junit.{Before, Test}

import scala.collection.JavaConversions._

class MRUnitAnalysis{
  val mapDriver = new MapDriver[LongWritable, Text, Text, TotalAndCount]
  val combinerDriver = new ReduceDriver[Text, TotalAndCount, Text, TotalAndCount]
  val reducerDriver = new ReduceDriver[Text, TotalAndCount, Text, TotalAndAverage]

  @Before
  def setUp(): Unit = {
    mapDriver setMapper new AnalysisMapper
    combinerDriver setReducer new AnalysisCombiner
    reducerDriver setReducer new AnalysisReducer
  }

  @Test
  def testMapperOnCorrectInput(): Unit = {
    mapDriver.withInput(new LongWritable(0),
      new Text("ip1 - - [24/Apr/2011:04:06:01 -0400] \"GET /~strabal/grease/photo9/927-3.jpg HTTP/1.1\" 200 40028 \"-\" \"Mozilla/5.0 (compatible; YandexImages/3.0; +http://yandex.com/bots)\""))

    mapDriver.withOutput(new Text("ip1"), TotalAndCount(40028, 1))
    mapDriver.runTest()
  }

  @Test
  def testMapperOnIncorrectInput(): Unit = {
    mapDriver.withInput(new LongWritable(0),
      new Text("ip1 - - [24/Apr/2011:04:06:01 -0400] \"GET /~strabal/grease/photo9/927-3.jpg HTTP/1.1\" 200 - \"-\" \"Mozilla/5.0 (compatible; YandexImages/3.0; +http://yandex.com/bots)\""))

    mapDriver.withOutput(new Text("ip1"), TotalAndCount(0, 1))
    mapDriver.runTest()
  }

  @Test
  def testCombiner(): Unit = {
    combinerDriver.withInput(new Text("ip1"), List(TotalAndCount(6000, 1), TotalAndCount(2000, 3)))
    combinerDriver.withOutput(new Text("ip1"), TotalAndCount(8000, 4))
    combinerDriver.runTest()
  }

  @Test
  def testReducerOnCorrectInput(): Unit = {
    reducerDriver.withInput(new Text("ip1"), List(TotalAndCount(6000, 1), TotalAndCount(2000, 3)))
    reducerDriver.withOutput(new Text("ip1"), TotalAndAverage(8000, 2000))
    reducerDriver.runTest()
  }

  @Test
  def testReducerOnIncorrectInput(): Unit = {
    reducerDriver.withInput(new Text("ip1"), List(TotalAndCount(0, 1)))
    reducerDriver.withOutput(new Text("ip1"), TotalAndAverage(0, 0))
    reducerDriver.runTest()
  }
}