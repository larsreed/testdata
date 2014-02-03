package no.mesan.testdatagen.aggreg

import scala.collection.immutable.List
import scala.util.Random

import no.mesan.testdatagen.{Generator, GeneratorImpl}

/**
 * This generator takes one or more generators as input, and selects randomly between
 * them for each value to generate. It is typed as a Generator[Any], since it can wrap
 * a free mix of generator types.
 * Each generator is given a weight -- the probability for each one is its own weight
 * relative to the sum of all weights.
 *
 * @author lre
 */
class WeightedGenerator[T] extends GeneratorImpl[T] with MultiGeneratorWithWeight[T] {

  private def getList[S](n:Int)(f: Generator[T]=>List[S]): List[S] = {
    val weightedList: List[(Int,List[S])]= {
      def create(sum: Int, soFar: List[(Int,List[S])], left: GenList): List[(Int,List[S])]= {
        if ( left.isEmpty ) soFar
        else {
          val w= sum + left.head._1
          val vals= f(left.head._2.filter(filterAll))
          create(w, (w, vals)::soFar, left.tail)
        }
      }
      create(0, Nil, generators)
    }
    val max= weightedList.head._1
    def getByWeight(i: Int): S = {
      val selected= Random.nextInt(max)
      def pick(rest: List[(Int,List[S])], lastList:List[S]): S = {
        if (rest.isEmpty || selected >= rest.head._1) lastList(i)
        else pick(rest.tail, rest.head._2)
      }
      pick(weightedList, Nil)
    }
    val res= for (i<- 0 to n-1) yield getByWeight(i)
    res.toList
  }

  override def get(n: Int): List[T] = getList(n){ g:Generator[T] => g.get(n) }

  override def getStrings(n: Int): List[String] = getList(n){ g:Generator[T] => g.getStrings(n) }

//    getList(true, n).map(x=> if (x==null) null else x.toString)

}

object WeightedGenerator {
  def apply[T](weighted: (Int, Generator[T])*): WeightedGenerator[T] = new WeightedGenerator().addTuples(weighted:_*)
  def apply[T](gs: List[Generator[T]]): WeightedGenerator[T] = new WeightedGenerator().add(gs:_*)
}
