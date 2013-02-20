package no.mesan.testdatagen.generators

import no.mesan.testdatagen.Generator

/**
 * Generate the same value all time....
 * from/to/unique are not supported
 *
 * @author lre
 */
object Fixed {
  def apply[T](v: T): Generator[T] = FromList(List(v))
}
