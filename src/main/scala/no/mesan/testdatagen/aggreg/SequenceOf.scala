package no.mesan.testdatagen.aggreg

import scala.collection.immutable.List

import no.mesan.testdatagen.{Generator, GeneratorImpl}

/**
 * The SequenceOf takes a list of generators.
 * When get/getString is called, it calls get(String) on each of its generators in sequence.
 * The number of returned records for get(N) (and equally getStrings) will vary.
 * In non-absolute mode (default), a number "close to N" (+/- 1) will be returned.
 * In absolute mode, the number will be (N*sum(weights)).
 *
 * @author lre
 */
class SequenceOf[S, T] (convert: T=>S) extends GeneratorImpl[S] with MultiGeneratorWithWeight[T] {
  private var absolute= false

  def makeAbsolute(newVal:Boolean= true): this.type = {
    absolute= newVal
    this
  }

  private def number(n: Int, weight:Int, totWeight:Int)= scala.math.round((n*weight)/totWeight)
  private def getList[U](n: Int)(f: (Int, Generator[T]) => List[U]): List[U] =  {
    val tot= if (absolute) 1 else generators.foldLeft(0)((zum, tuple) => zum + tuple._1)
    generators.flatMap{ tuple =>
      val (w, g)= tuple
      f(number(n, w, tot), g)
    }
  }
  override def get(n: Int): List[S] =  getList(n){ (no, g)=> g.get(no).map(convert) }
  override def getStrings(n: Int): List[String] =  getList(n){ (no, g)=> g.getStrings(no)}
}

object SequenceOf {
  def apply[T](gs: Generator[T]*): SequenceOf[T, T] =  new SequenceOf[T, T]({x=>x}).add(gs:_*)
  def strings(gs: Generator[Any]*): SequenceOf[String, Any] =
    new SequenceOf[String, Any] (x=> if (x==null) null else x.toString).add(gs:_*)
}
