package net.kalars.testgen.generators

import net.kalars.testgen.Generator

/**
 * Generate the same value all time....
 * Special methods: from/to/unique -- not supported
 * Default limits: n/a
 */
object FixedGenerator {
  def apply[T](v: T): Generator[T] = new ListGenerator().fromList(List(v))
}
