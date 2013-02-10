package net.kalars.testgen.aggreg

import scala.collection.immutable.List
import scala.util.Random

import net.kalars.testgen.{Generator, GeneratorImpl}

class SomeNulls[T](gen: Generator[T]) extends GeneratorImpl[T] {

  private var nullFact= 0
  def nullFactor(nth: Int): SomeNulls[T]= { nullFact= nth; this }

  private def kill = ( nullFact>0  && Random.nextInt(nullFact)== 0)

  override def get(n: Int): List[T] = {
    val org= gen.get(n)
    org.map(x=> if (kill) null.asInstanceOf[T] else x)
  }

  override def getStrings(n: Int): List[String] = {
    val org= gen.getStrings(n)
    org.map(x=> if (kill) null else x)
  }
}

object SomeNulls {
  def apply[T](n:Int, gen: Generator[T]): SomeNulls[T] = new SomeNulls(gen).nullFactor(n)
}
