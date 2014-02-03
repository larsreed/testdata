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
class SequenceOf[S, T] (convert: T=>S) extends GeneratorImpl[S] with MultiGenerator[T] {

  override def get(n: Int): List[S] =  generators.flatMap{ g => g.get(n).map(v=>convert(v)) }
  override def getStrings(n: Int): List[String] =  generators.flatMap(g => g.getStrings(n))
}

object SequenceOf {
  def apply[T](gs: Generator[T]*): SequenceOf[T, T] =  new SequenceOf[T, T]({x=>x}).add(gs:_*)
  def strings(gs: Generator[Any]*): SequenceOf[String, Any] =
    new SequenceOf[String, Any] (x=> if (x==null) null else x.toString).add(gs:_*)
}
