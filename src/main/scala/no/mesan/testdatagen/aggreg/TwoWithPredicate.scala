package no.mesan.testdatagen.aggreg

import no.mesan.testdatagen.{Generator, GeneratorImpl}
import no.mesan.testdatagen.generators.FromList

/**
 * This generator takes two generators and a predicate as input; it draws tuples from each
 * and filters those against the predicate.
 *
 * @author lre
 */
class TwoWithPredicate[T, U](left: Generator[T], right: Generator[U],
                             predicate: ((T,U))=>Boolean)  extends GeneratorImpl[(T, U)] {

  def getStream: Stream[(T, U)]= left.gen zip right.gen filter predicate

  /** Use the generators' formatters to format the tuples. */
  def genFormatted: Stream[(String, String)] =
    getStream map { case (l, r) => (left.formatOne(l), right.formatOne(r))}

  /**
   * Return two ListGenerators corresponding to the parts of the generated tuples,
   * with a given sample size (number of occurrences read from the original).
   * TODO: Streams?
   */
  def asListGens(sampleSize: Int): (FromList[T], FromList[U]) = {
    val tuples = get(sampleSize)
    (FromList(tuples map { _._1 }).sequential,
     FromList(tuples map { _._2 }).sequential)
  }

  /**
   * Return two ListGenerators corresponding to the parts of the generated tuples,
   * with a given sample size (number of occurrences read from the original), formatted
   * by the respective generators.
   * TODO: Streams?
   */
  def asFormattedListGens(sampleSize: Int): (FromList[String], FromList[String]) = {
    val tuples = genFormatted.take(sampleSize).toList
    (FromList(tuples map { _._1 }).sequential,
     FromList(tuples map { _._2 }).sequential)
  }
}

object TwoWithPredicate {
  def apply[T, U](left: Generator[T], right: Generator[U],
                  predicate: ((T,U))=>Boolean): TwoWithPredicate[T, U] =
    new TwoWithPredicate[T, U](left, right, predicate)

  def apply[T](gen: Generator[T], predicate: ((T, T))=>Boolean): TwoWithPredicate[T, T] =
    new TwoWithPredicate[T, T](gen, gen, predicate)
}
