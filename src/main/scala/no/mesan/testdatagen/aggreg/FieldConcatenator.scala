package no.mesan.testdatagen.aggreg

import scala.collection.immutable.List

import no.mesan.testdatagen.{GeneratorImpl, Generator}

/**
 * The FieldConcatenator is given a set of generators with the add method.
 *  When get is called, it calls getString on each of its generators, and
 *  concatenates the output from each generator (in the same order as the
 *  add calls), and returns the list of concatenated strings.
 *
 * @author lre
 */
class FieldConcatenator extends GeneratorImpl[String] with MultiGenerator[Any] {

  override def get(n: Int): List[String] = {
    val allLists = generators.map{ g => g.getStrings(n) }.transpose
    allLists.map(l => l.reduceLeft(_ + _)).filter(filterAll)
  }
}

object FieldConcatenator {
  def apply(gs: Generator[Any]*): FieldConcatenator =  new FieldConcatenator() add(gs:_*)
}
