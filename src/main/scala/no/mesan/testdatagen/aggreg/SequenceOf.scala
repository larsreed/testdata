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
class SequenceOf[S, T] (convert: T=>S) extends GeneratorImpl[S] with MultiGeneratorWithWeight[T] {

  private def total= generators.foldLeft(0)((zum, tuple)=> zum + tuple._1)
  private def round(n: Int, weight:Int, totWeight:Int)= scala.math.round((n*weight)/totWeight)
  private def getList[U](n: Int)(f: (Int, Generator[T]) => List[U]): List[U] =  {
    val tot= total
    generators.flatMap{ tuple =>
      val (w, g)= tuple
      f(round(n, w, tot), g)
    }
  }
  override def get(n: Int): List[S] =  getList(n){ (no, g)=> g.get(no).map(convert(_)) }
  override def getStrings(n: Int): List[String] =  getList(n){ (no, g)=> g.getStrings(no)}
}

object SequenceOf {
  def apply[T](gs: Generator[T]*): SequenceOf[T, T] =  new SequenceOf[T, T]({x=>x}).add(gs:_*)
  def strings(gs: Generator[Any]*): SequenceOf[String, Any] =
    new SequenceOf[String, Any] (x=> if (x==null) null else x.toString).add(gs:_*)
}
