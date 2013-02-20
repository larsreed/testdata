package no.mesan.testdatagen.generators

import scala.annotation.tailrec
import scala.util.Random
import no.mesan.testdatagen.SingleGenerator
import no.mesan.testdatagen.ExtendedDelegate
import no.mesan.testdatagen.ExtendedGenerator

/**
 * Generate Ints.
 * Special methods: step(n) -- only used for sequential generation -- sets the step size (default 1)
 * Default limits: Int.MinValue+1 .. Int.MaxValue-1
 *
 * @author lre
 */
class Ints extends ExtendedGenerator[Int] with ExtendedDelegate[Long, Int] {

  private val gen= Longs() from((Int.MinValue+1).toLong) to((Int.MaxValue-1).toLong)
  protected var generator: ExtendedGenerator[Long]= gen

  override protected def conv2gen(f: Int): Long = f+0L
  override protected def conv2result(f: Long): Int= f.toInt

  /** Step size, used only for sequential values. */
  def step(s: Int): this.type= {
	  gen.step(s)
	  this
  }
}

object Ints {
  def apply(from:Int=Int.MinValue+1, to:Int=Int.MaxValue-1, step:Int=1): Ints=
    new Ints().from(from).to(to).step(step)
}
