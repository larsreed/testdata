package no.mesan.testdatagen.aggreg

import scala.language.postfixOps

import scala.collection.immutable.List
import scala.collection.mutable.{Set => MutableSet}
import scala.collection.mutable.{LinkedList => MutableList}

import no.mesan.testdatagen.{GeneratorImpl, Generator}

/**
 * This generator takes two generators as input, it tries to get all values from the first,
 * but when duplicates are discovered, values are drawn from the second until a unigue value
 * is found.
 * "formatWith" is not supported.
 *
 * @author lre
 */
class UniqueWithFallback[T](primary: Generator[T], alt: Generator[T]) extends GeneratorImpl[T]  {

  override def formatWith(f: T => String) = throw new UnsupportedOperationException
  override def formatOne[S>:T](v: S): String = primary.formatOne(v)

  override def get(n: Int): List[T] = {
    val org= primary.get(n)
    val keys= MutableSet[T]()
    var altVals= List[T]()
    org.map { v=>
      var r= v
      while ( (keys contains r) || !filterAll(r) ) {
        if ( altVals isEmpty ) altVals= alt.get(n)
        r= altVals.head
        altVals= altVals.tail
      }
      keys.add(r)
      r
    }
  }
}

object UniqueWithFallback {
  def apply[T](primary: Generator[T], alt: Generator[T])= new UniqueWithFallback(primary, alt)
}