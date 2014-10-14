package no.mesan.testdatagen.aggreg

import no.mesan.testdatagen.{GeneratorImpl, Generator}
import no.mesan.testdatagen.generators.FromList

/**
 * This generator takes one generator and a generator function as input; it draws values from the first
 * and uses that to generate values for the second.
 * Formats functions are not supported.
 *
 * @author lre
 */
class TwoFromFunction[T, U](gen: Generator[T], genFun: T=>U) extends GeneratorImpl[(T, U)]  {

  def getStream: Stream[(T, U)]= gen.gen map {v => (v, genFun(v))}

  override def genStrings: Stream[String]= genFormatted map { _.toString }

  /**
   * Returns pairs where both values are Strings, the first value formatted by the input
   * generator, the second by toString.
   */
  def genFormatted: Stream[(String, String)] = {
    gen.gen.map { v=>
      val p1= gen.formatOne(v)
      val p2= genFun(v)
      (p1, if (p2==null) null else p2.toString)
    }
  }

  /**
   * Return two ListGenerators corresponding to the parts of the generated tuples,
   * with a given sample size (number of occurrences read from the original).
   * TODO: Streams?
   */
  def asListGens(sampleSize: Int): (FromList[T], FromList[U]) = {
    val tuples = get(sampleSize)
    val gen1 = FromList(tuples map {_._1}).sequential
    val gen2 = FromList(tuples map {_._2}).sequential
    (gen1, gen2)
  }

  override def formatWith(f: ((T, U)) => String) = throw new UnsupportedOperationException
  override def formatOne[S >: (T, U)](v: S) = throw new UnsupportedOperationException
}
object TwoFromFunction {
  def apply[T, U](gen: Generator[T], genFun: T=>U): TwoFromFunction[T, U] = new TwoFromFunction(gen, genFun)
}
