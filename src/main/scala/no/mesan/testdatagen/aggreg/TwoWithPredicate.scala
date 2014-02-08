package no.mesan.testdatagen.aggreg

import scala.collection.immutable.List

import no.mesan.testdatagen.{GeneratorImpl, Generator}
import no.mesan.testdatagen.generators.{FromList, Dates}

/**
 * This generator takes two generators and a predicate as input; it draws tuples from each
 * and checks those against the predicate.
 *
 * @author lre
 */
class TwoWithPredicate[T, U](left: Generator[T], right: Generator[U],
                             predicate: ((T,U))=>Boolean) extends GeneratorImpl[(T, U)]  {
  override def get(n: Int): List[(T, U)] = {
    def getAbunch(soFar:List[(T, U)]): List[(T, U)] =
      if (soFar.isEmpty || soFar.length<n)
        getAbunch(soFar ++ (left.get(n) zip right.get(n)).filter(predicate).filter(filterAll))
      else soFar.take(n)
    getAbunch(Nil)
  }

  def getFormatted(n: Int): List[(String, String)] =
    get(n).map { t=>
      (left.formatOne(t._1), right.formatOne(t._2))
    }

  def asListGens(n: Int): (FromList[T], FromList[U]) = {
    val tuples = get(n)
    (FromList(tuples map { _._1 }).sequential,
     FromList(tuples map { _._2 }).sequential)
  }

  def asFormattedListGens(n: Int): (FromList[String], FromList[String]) = {
    val tuples = getFormatted(n)
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
