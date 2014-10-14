package no.mesan.testdatagen.aggreg

import no.mesan.testdatagen.generators.{Dates, Ints}
import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TwoWithPredicateSpec extends FlatSpec {

  trait Setup {
    val intGen= Ints() from -50 to 50 format "%08d"
    def lt(t: (Int, Int))= t._1 <= t._2
    val xgen= TwoWithPredicate[Int](intGen, lt)
  }

  "TwoWithPredicate" should "filter according to predicate" in {
    new Setup {
      val res= xgen.get(1000)
      assert(res forall(t=> t._1<=t._2))
    }
  }

  it should "produce listGens that adheres to predicate" in {
      new Setup {
        val (genA, genB)= xgen.asListGens(1000)
        val res= genA.get(1000) zip genB.get(1000)
        assert(res forall(t=> t._1<=t._2))
      }
    }

  it should "format output according to spec" in {
      new Setup {
        val yGen= TwoWithPredicate(Ints() from 100 to 5000 format "x%08d", lt)
        val res= yGen.genFormatted.take(1000)
        res.foreach { t=>
          assert(t._1 matches "^x[0-9]{8}")
          assert(t._2 matches "^x[0-9]{8}")
        }
      }
    }

  it should "produce listGens with format that satisfies predicate" in {
    def dtLe(t:(DateTime, DateTime)) = t._2 isAfter t._1
    val fromToDateGen= TwoWithPredicate(Dates().from(y=2010).to(y=2020).format("yyyy.MM.dd"), dtLe)
    val (fromDateGen, toDateGen)= fromToDateGen.asFormattedListGens(200)
    val res1= fromDateGen.get(200)
    val res2= toDateGen.get(200)
    for (i<- 0 to 199) assert(res1(i)<=res2(i))
  }
}
