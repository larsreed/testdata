package no.mesan.testdatagen

import scala.util.Random

/**
 * A random function that answers true in n% of the cases.
 *
 * @author lre
 */
trait Percentage {
  /** A random function that answers true in n% of the cases. */
  def hit(n: Int): Boolean= Random.nextInt(100) < n
}

