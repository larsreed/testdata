package no.mesan.testdatagen.generators

import no.mesan.testdatagen.StreamGenerator

/**
 * Generate the same value all time....
 * from/to/unique are not supported
 *
 * @author lre
 */

class Fixed[T](value: T) extends FromList[T] {
  fromList(List(value))

  override def filter(f: T=>Boolean) = {
    if (!f(value)) throw new IllegalArgumentException(s"filter does not accept single value $value")
    super.filter(f)
  }
}
object Fixed {
  def apply[T](v: T): Fixed[T] = new Fixed(v)
}
