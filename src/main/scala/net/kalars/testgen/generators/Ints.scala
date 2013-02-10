package net.kalars.testgen.generators

import scala.annotation.tailrec
import scala.util.Random

import net.kalars.testgen.SingleGenerator

/**
 * Generate ints.
 * Special methods: step(n) -- only used for sequential generation -- sets the step size (default 1)
 * Default limits: Integer.MIN/MAX_VALUE
 */
class Ints extends SingleGenerator[Int] {

  filter(x=> lower match { case Some(low)=>  x>=low;  case _=> true })
  filter(x=> upper match { case Some(high)=> x<=high; case _=> true })

  private var stepSize: Int= 1
  /** Step size, used only for sequential values. */
  def step(s: Int): Ints= {
    require(s!=0, "Step cannot be 0")
    stepSize= math.abs(s)
    this
  }

  override def get(n: Int): List[Int] = {
    require(n>=0, "cannot get negative count")
    val min= lower.getOrElse(Integer.MIN_VALUE)
    val max= upper.getOrElse(Integer.MAX_VALUE)
    require(max>=min, "from >= to")

    def getSequentially(): List[Int]= {
      var step= if (isReversed) -stepSize else stepSize
      @tailrec def next(last: Int, soFar:List[Int]): List[Int]=
        if (soFar.length>=n) soFar
        else {
          val k= if (last>max) min else if (last<min) max else last
          if ( filterAll(k) ) next(k+step, k::soFar)
          else next(k+step, soFar)
        }
      if (isReversed) next(max, Nil).reverse
      else next(min, Nil).reverse
    }

    @tailrec
    def getRandomly(soFar: List[Int]): List[Int]=
      if (soFar.length>=n) soFar
      else {
        val span= (max+0L)-(min+0L)
        val nxt= min + (if (span<=Integer.MAX_VALUE) Random.nextInt(span.toInt)
                        else Random.nextInt())
        if (filterAll(nxt)  && (!isUnique || !(soFar contains nxt)))
          getRandomly(nxt::soFar)
        else getRandomly(soFar)
      }

    if (isSequential) getSequentially
    else getRandomly(Nil)
  }

}

object Ints {
  def apply(from:Int=Integer.MIN_VALUE, to:Int=Integer.MAX_VALUE, step:Int=1): Ints=
    new Ints().from(from).to(to).step(step)
}
