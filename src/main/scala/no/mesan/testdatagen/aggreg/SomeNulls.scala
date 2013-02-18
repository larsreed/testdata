package no.mesan.testdatagen.aggreg

import scala.collection.immutable.List

import no.mesan.testdatagen.{Generator, GeneratorImpl, Percentage}

class SomeNulls[T](gen: Generator[T]) extends GeneratorImpl[T] with Percentage {

  private var nullPct= 0
  /** insert null in n% of the cases. */
  def nullFactor(percent: Int): SomeNulls[T]= { nullPct= percent; this }

  private def kill = hit(nullPct)

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
