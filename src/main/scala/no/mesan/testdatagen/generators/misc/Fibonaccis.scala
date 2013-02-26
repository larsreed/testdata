package no.mesan.testdatagen.generators.misc

import scala.annotation.tailrec

import no.mesan.testdatagen.GeneratorImpl

/**
 * Generates Fibonacci numbers.  You cannot have a package of Scala generators without this :)
 *
 * @author lre
 */
class Fibonaccis extends GeneratorImpl[BigInt] {

  override def get(n: Int): List[BigInt] = {
    require(n>=0, "cannot get negative count")
    val pfx= List(BigInt(1), BigInt(1))
    if (n==0) return List[BigInt]()
    else if (n==1) return pfx.tail
    else if (n==2) return pfx

    @tailrec def next(left:Int, last2: BigInt, last1:BigInt, soFar:List[BigInt]): List[BigInt]=
      if (left<=0) soFar
      else {
        val nxt= last2+last1
        if (filterAll(nxt)) next(left-1, last1, nxt, nxt :: soFar)
        else next(left, last1, nxt, soFar)
      }
    next(n-2, BigInt(1), BigInt(1), pfx).reverse
  }
}

object Fibonaccis {
  def apply(): Fibonaccis= new Fibonaccis()
}
