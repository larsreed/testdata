package no.mesan.testdatagen.aggreg

import scala.collection.mutable.{Set => MutableSet}
import scala.language.postfixOps

import no.mesan.testdatagen.{Generator, GeneratorImpl}

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

  private var sortedIn= false
  /**
   * Call this if input is sorted -- that greatly enhances the capability of this generator,
   * as it does not have to remember all previous values.
   */
  def sortedInput(isSorted: Boolean = true): this.type= {
    sortedIn= isSorted
    this
  }

  // Keep track...
  private val haveSeen= MutableSet[T]()
  private var lastSeen: Option[T]= None

  // Hold onto state
  private var altGen= alt.gen

  private def isDuplicate(newVal: T): Boolean =
    if (sortedIn) {
      if (lastSeen == Some(newVal)) return true
      lastSeen= Some(newVal)
      false
    }
    else {
      if (haveSeen.contains(newVal)) return true
      haveSeen += newVal
      false
    }

  def getStream : Stream[T]=
    primary.gen.map{t=>
      var v= t
      while (isDuplicate(v)) {
        v= altGen.head
        altGen= altGen.tail
      }
      v
   }
}

object UniqueWithFallback {
  def apply[T](primary: Generator[T], fallback: Generator[T]): UniqueWithFallback[T]=
    new UniqueWithFallback(primary, fallback)
}
