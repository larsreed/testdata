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
class WeightedGenerator extends GeneratorImpl[Any] {

  type GenList= List[(Int,Generator[_])]
  type ExpandedList= List[(Int,List[_])]

  private var generators: GenList= Nil

  /** Add a generator with a relative weight. */
  def add(weight: Int, gen: Generator[_]): WeightedGenerator= {
    generators ::= (weight, gen)
    this
  }


  private def getList(strings: Boolean, n: Int): List[Any] = {
    val weightedList: ExpandedList= {
      def create(sum: Int, soFar: ExpandedList, left: GenList): ExpandedList= {
        if ( left.isEmpty ) soFar
        else {
          val w= sum + left.head._1
          val valGen= left.head._2.filter(filterAll)
          val vals= if(strings) left.head._2.filter(filterAll).getStrings(n)
                    else left.head._2.filter(filterAll).get(n)
          create(w, (w, vals)::soFar, left.tail)
        }
      }
      create(0, Nil, generators)
    }
    val max= weightedList.head._1
    def getByWeight(i: Int): Any = {
      val selected= Random.nextInt(max)
      def pick(rest: ExpandedList, lastList:List[_]): Any = {
        if (rest.isEmpty || selected >= rest.head._1) lastList(i)
        else pick(rest.tail, rest.head._2)
      }
      pick(weightedList, Nil)
    }
    val res= for (i<- 0 to n-1) yield getByWeight(i)
    res.toList
  }

  override def get(n: Int): List[Any] = getList(false, n)

  override def getStrings(n: Int): List[String] =
    getList(true, n).map(x=> if (x==null) null else x.toString)

}

object WeightedGenerator {
  def apply(): WeightedGenerator = new WeightedGenerator()
}
