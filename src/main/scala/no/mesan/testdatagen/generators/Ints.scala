package no.mesan.testdatagen.generators

import scala.annotation.tailrec
import scala.util.Random

import no.mesan.testdatagen.SingleGenerator

/**
 * Generate Ints.
 * Special methods: step(n) -- only used for sequential generation -- sets the step size (default 1)
 * Default limits: Int.MinValue+1 .. Int.MaxValue-1
 *
 * @author lre
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
    val min= lower.getOrElse(Int.MinValue+1)
    val max= upper.getOrElse(Int.MaxValue-1)
    require(max>=min, "from >= to")
    require(max<Int.MaxValue, "max < " + Int.MaxValue)
    require(min>Int.MinValue, "min > " + Int.MinValue)

    var step= if (isReversed) -stepSize else stepSize
    val span= (max+0L)-(min+0L)

    def getSequentially(): List[Int]= {
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
        val nxt= min + (if (span<=Int.MaxValue) Random.nextInt(span.toInt)
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
  def apply(from:Int=Int.MinValue+1, to:Int=Int.MaxValue-1, step:Int=1): Ints=
    new Ints().from(from).to(to).step(step)
}
