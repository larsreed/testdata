package no.mesan.testdatagen.aggreg

import scala.collection.immutable.List

import no.mesan.testdatagen.{Generator, GeneratorImpl}

/**
 * The SequenceOf takes a list of generator.
 *  When get/getString is called, it calls getString on each of its generators in sequence,
 *  with M generators, a get(N) will return n*m entries.
 *
 * @author lre
 */
class SequenceOf extends MultiGenerator[String, Any] {

  override def get(n: Int): List[String] = {
    generators.reverse.flatMap(g => g.getStrings(n))
  }
}

object SequenceOf {
  def apply(gs: Generator[Any]*): SequenceOf =  new SequenceOf().add(gs:_*)
}
