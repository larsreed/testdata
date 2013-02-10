package net.kalars.testgen.generators.norway

import net.kalars.testgen.Generator
import net.kalars.testgen.aggreg.FieldConcatenator
import net.kalars.testgen.generators.{Ints, Strings}

/**
 * Generate Norwegian car number plates.
 * Special methods:
 * Default limits: Always random, the letters IMOQ are never used.
 */
object KjennemerkeGenerator {
  def apply(): Generator[String] = new FieldConcatenator().
         add(Strings().length(2).chars(('A' to 'Z').filter(c=> ! ("IMOQ" contains c)))).
         add(Ints().from(10000).to(99999))
}
