package com.epam.hadoop_training

import java.io.{DataOutput, DataInput}

import scala.reflect.ClassTag

import org.apache.hadoop.io.Writable

// Experiment failed:
// generic classes must not be used with as hadoop writables because of
// Writable requires no-args constructor, while ClassTag's ARE parameters anyway.
abstract class PairWritable[T1: ClassTag, T2: ClassTag] extends Writable{
  var v1: T1
  var v2: T2

  private def writeMethod[T: ClassTag]: (DataOutput, Any) => Unit =
    implicitly[ClassTag[T]] match {
      case ClassTag.Int   => (o, v) => o.writeInt(v.asInstanceOf[Int])
      case ClassTag.Long  => (o, v) => o.writeLong(v.asInstanceOf[Long])
      case ClassTag.Float => (o, v) => o.writeFloat(v.asInstanceOf[Float])
      //case _ => etc...
    }

  private val wm1 = writeMethod[T1]
  private val wm2 = writeMethod[T2]

  override def write(dataOutput: DataOutput): Unit = {
    wm1(dataOutput, v1)
    wm2(dataOutput, v2)
  }

  private def readMethod[T: ClassTag]: DataInput => T =
    implicitly[ClassTag[T]] match {
      case ClassTag.Int   => i => i.readInt.asInstanceOf[T]
      case ClassTag.Long  => i => i.readLong.asInstanceOf[T]
      case ClassTag.Float => i => i.readFloat.asInstanceOf[T]
      //case _ => etc...
    }

  private val rm1 = readMethod[T1]
  private val rm2 = readMethod[T2]

  override def readFields(dataInput: DataInput): Unit = {
    v1 = rm1(dataInput)
    v2 = rm2(dataInput)
  }
}

case class TotalAndCount  (var v1: Long = 0, var v2: Long  = 0) extends PairWritable[Long, Long]  { def this() = this(0, 0) }
case class TotalAndAverage(var v1: Long = 0, var v2: Float = 0) extends PairWritable[Long, Float] { def this() = this(0, 0) }