package no.mesan.testdatagen

/**
 * A wrapper that requires all generated values to be unique.
 * TAKE CARE! If the underlying generator has too few possible values, this addon may make
 * the generator loop forever.
 * Also -- if the underlying generator has a smaller value space than the requested number
 * of entries, this will loop forever...
 */
class Unique[T](gen: Generator[T]) extends Generator[T] with GeneratorDelegate[T, T] {

  protected var generator: Generator[T]= gen

  private val seen= scala.collection.mutable.ListBuffer[T]()

  private def uniqueValue(elem: T): Boolean =
    if (seen contains elem) false
    else {
      seen += elem
      true
    }

  generator.filter(uniqueValue)
}

object Unique {
  def apply[T](gen: Generator[T]): Unique[T] = new Unique(gen)
}
