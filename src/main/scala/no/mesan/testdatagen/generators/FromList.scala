package no.mesan.testdatagen.generators

import scala.annotation.tailrec
import scala.util.Random

import no.mesan.testdatagen.SingleGenerator

/**
 * Generate values based on lists.
 * Special methods: from/to/unique -- not supported
 */
class FromList[T] extends SingleGenerator[T] {

  override def from(f:T) = throw new UnsupportedOperationException
  override def to(f:T) = throw new UnsupportedOperationException

  var inputList: List[T]= null
  /** Enter the list of values. */
  def fromList(l: List[T]): this.type= { inputList= l; this }

  override def get(n: Int): List[T] = {
    require(n>=0, "cannot get negative count")
    require(inputList.length>0, "cannot extract from empty list")
    val accepted= inputList filter filterAll

    def getSequentially(): List[T]= {
      val basis= if (isReversed) accepted.reverse else accepted
      @tailrec def next(soFar:List[T]): List[T]=
        if (soFar.length>=n) soFar.take(n)
        else {
          next(basis ++ soFar)
        }
      next(basis)
    }

    @tailrec
    def getRandomly(soFar: List[T]): List[T]=
      if (soFar.length>=n) soFar
      else {
        val nxt= accepted(Random.nextInt(accepted.length))
        if (!isUnique || !(soFar contains nxt))
          getRandomly(nxt::soFar)
        else getRandomly(soFar)
      }

    require(!isRandom || !isUnique || n<= accepted.size, "too few elements: " + accepted)
    if (isSequential) getSequentially
    else getRandomly(Nil)
  }

}

object FromList {
  def apply[T](): FromList[T] = new FromList()
  def apply[T](l: List[T]): FromList[T] = new FromList().fromList(l)
  def apply[T](ls: T*): FromList[T] = new FromList().fromList(ls.toList)
}
