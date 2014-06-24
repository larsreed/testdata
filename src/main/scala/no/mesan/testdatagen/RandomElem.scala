package no.mesan.testdatagen

import scala.util.Random

/**
 * Function to pick a random element from a list.
 * @author lre
 */
trait RandomElem {
  def randomFrom[T](l: Seq[T]): T = l(Random.nextInt(l.length))
}
