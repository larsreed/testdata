package no.mesan.testdatagen.aggreg

import scala.collection.immutable.List

import no.mesan.testdatagen.{GeneratorImpl, Generator}
import no.mesan.testdatagen.generators.FromList

/**
 * This generator takes one generator and a generator function as input; it draws values from the first
 * and uses that to generate values for the second.
 * Filters and formats are not supported.
 *
 * @author lre
 */
class TwoFromFunction[T, U](gen: Generator[T], genFun: T=>U) extends Generator[(T, U)]  {

  override def get(n: Int): List[(T, U)] = {
    val org= gen.get(n)
    org.map { v=> (v, genFun(v))}
  }

  override def getStrings(n: Int): List[String] = getFormatted(n).map{ _.toString }

  def getFormatted(n: Int): List[(String, String)] = {
    val org= gen.get(n)
    org.map { v=>
      val p1= gen.formatOne(v)
      val p2= genFun(v)
      (p1, if (p2==null) null else p2.toString)
    }
  }

  def asListGens(n: Int): (FromList[T], FromList[U]) = {
    val tuples = get(n)
    val gen1 = FromList(tuples map {_._1}).sequential
    val gen2 = FromList(tuples map {_._2}).sequential
    (gen1, gen2)
  }

  override def filter(f: ((T, U)) => Boolean) = throw new UnsupportedOperationException
  override def formatWith(f: ((T, U)) => String) = throw new UnsupportedOperationException
  override def formatOne[S >: (T, U)](v: S) = throw new UnsupportedOperationException
}
object TwoFromFunction {
  def apply[T, U](gen: Generator[T], genFun: T=>U): TwoFromFunction[T, U] = new TwoFromFunction(gen, genFun)
}
