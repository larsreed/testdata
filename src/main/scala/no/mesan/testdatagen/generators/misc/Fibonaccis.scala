package no.mesan.testdatagen.generators.misc

import no.mesan.testdatagen.GeneratorImpl

/**
 * Generates Fibonacci numbers.  You cannot have a package of Scala generators without this :)
 *
 * @author lre
 */
class Fibonaccis extends GeneratorImpl[BigInt] {

  def getStream: Stream[BigInt] = {
    def next(a: BigInt, b: BigInt): Stream[BigInt] = a #:: next(b,  a + b)
    next(BigInt(1), BigInt(1))
  }
}

object Fibonaccis {
  def apply(): Fibonaccis= new Fibonaccis()
}
