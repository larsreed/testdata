package no.mesan.testdatagen.generators

import scala.annotation.tailrec

import no.mesan.testdatagen.{ExtendedImpl, RandomElem}

/** * Probably the most versatile of all the generators, the FromList takes a list of
 * "anything" as input and generates its values from that, it is typed (FromList[T]),
 * so you keep the type of the input list.

 * from/to are not supported.
 *
 * @author lre
 */
class FromList[T] extends ExtendedImpl[T] with RandomElem {

  override def from(f:T) = throw new UnsupportedOperationException
  override def to(f:T) = throw new UnsupportedOperationException

  protected var inputList: Seq[T]= null
  /** Enter the list of values. */
  def fromList(l: Seq[T]): this.type= { inputList= l; this }

  // FIXME Weighted values!

  override def get(n: Int): List[T] = {
    require(n>=0, "cannot get negative count")
    require(inputList.length>0, "cannot extract from empty list")
    val accepted= inputList.toList

    def getSequentially: List[T]= {
      val basis= accepted filter filterAll
      @tailrec def next(soFar:List[T]): List[T]=
        if (soFar.length>=n) soFar.take(n)
        else  next(basis ++ soFar)
      next(basis)
    }

    @tailrec def getRandomly(soFar: List[T]): List[T]=
      if (soFar.length>=n) soFar
      else {
        val nxt= randomFrom(accepted)
        if (filterAll(nxt)) getRandomly(nxt::soFar)
        else getRandomly(soFar)
      }

      if (isSequential) getSequentially
      else getRandomly(Nil)
  }

}

object FromList {
  def apply[T](): FromList[T] = new FromList()
  def apply[T](l: List[T]): FromList[T] = new FromList().fromList(l)
  def apply[T](ls: T*): FromList[T] = new FromList().fromList(ls.toList)
}
