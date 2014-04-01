package no.mesan.testdatagen

import scala.language.postfixOps

/**
 * A delegate generator that reverses the order of the values returned.
 * Wouldn't work too well with streams...
 */
class Reverse[T](gen: Generator[T]) extends Generator[T] with GeneratorDelegate[T, T] {

  protected var generator: Generator[T]= gen

  override def get(n: Int): List[T]= super.get(n) reverse
  override def getStrings(n: Int): List[String]= super.getStrings(n) reverse
}

object Reverse {
  def apply[T](gen: Generator[T]): Reverse[T] = new Reverse(gen)
}
