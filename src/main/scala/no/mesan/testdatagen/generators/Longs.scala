package no.mesan.testdatagen.generators

import scala.annotation.tailrec
import scala.util.Random

import no.mesan.testdatagen.ExtendedImpl

/**
 * Generate longs.
 * Special methods: step(n) -- only used for sequential generation -- sets the step size (default 1)
 * Default limits: 0 .. Long.MaxValue-1
 *
 * @author lre
 */
class Longs extends ExtendedImpl[Long] {

  filter(x=> lower match { case Some(low)=>  x>=low;  case _=> true })
  filter(x=> upper match { case Some(high)=> x<=high; case _=> true })


  private var stepSize: Long= 1
  /** Step size, used only for sequential values. */
  def step(step: Long): this.type= {
    require(step!=0, "Step cannot be 0")
    stepSize= step
    this
  }

  override def get(n: Int): List[Long] = {
    require(n>=0, "cannot get negative count")
    val min= lower.getOrElse(0L)
    val max= upper.getOrElse(Long.MaxValue-1)
    require(max>=min, "from >= to")
    require(max<Long.MaxValue, "max < " + Long.MaxValue)
    require(min>Long.MinValue, "min > " + Long.MinValue)

    val span= BigInt(max) - BigInt(min) +1
    val isReversed= stepSize<0

    def sequentially(): List[Long]= {
      @tailrec def next(last: Long, soFar:List[Long]): List[Long]=
        if (soFar.length>=n) soFar
        else {
          val k= if (last>max) min else if (last<min) max else last
          if ( filterAll(k) ) next(k+stepSize, k::soFar)
          else next(k+stepSize, soFar)
        }
      if (isReversed) next(max, Nil).reverse
      else next(min, Nil).reverse
    }

    @tailrec
    def randomly(soFar: List[Long]): List[Long]=
      if (soFar.length>=n) soFar
      else {
        val nxt= (min + (BigInt(Random.nextLong()) mod span)).toLong
        if (filterAll(nxt)) randomly(nxt::soFar)
        else randomly(soFar)
      }

    if (isSequential) sequentially
    else randomly(Nil)
  }
}

object Longs {
  def apply(from:Long=0, to:Long=Long.MaxValue-1, step:Long=1): Longs=
    new Longs() from from to to step step
}
