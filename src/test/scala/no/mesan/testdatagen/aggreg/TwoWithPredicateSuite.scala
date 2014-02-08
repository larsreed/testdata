package no.mesan.testdatagen.aggreg

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import org.scalatest.FunSuite
import no.mesan.testdatagen.generators.{Dates, Ints}

import no.mesan.testdatagen.Printer
import org.joda.time.DateTime

@RunWith(classOf[JUnitRunner])
class TwoWithPredicateSuite extends FunSuite with Printer {

  trait Setup {
    val intGen= Ints() from -50 to 50 format("%08d")
    def lt(t: (Int, Int))= t._1 <= t._2
    val xgen= TwoWithPredicate[Int](intGen, lt)
  }

  print(false) {
    new Setup {
      println(xgen.get(120))
      println(xgen.getFormatted(120))
    }
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      new Setup { xgen.get(-1) }
    }
    intercept[IllegalArgumentException] {
      new Setup { xgen.getStrings(-1) }
    }
    intercept[IllegalArgumentException] {
      new Setup { xgen.getFormatted(-1) }
    }
  }

  test("count") {
    new Setup { assert(xgen.get(30).size === 30) }
  }

  test("contents") {
    new Setup {
      val res= xgen.get(1000)
      assert(res forall(t=> t._1<=t._2))
    }
  }

  test("listGens satisfies predicate"){
      new Setup {
        val (genA, genB)= xgen.asListGens(1000)
        val res= genA.get(1000) zip genB.get(1000)
        assert(res forall(t=> t._1<=t._2))
      }
    }

  test("formatted output"){
      new Setup {
        val yGen= TwoWithPredicate(Ints() from 100 to 5000 format("x%08d"), lt)
        val res= yGen.getFormatted(1000)
        res.foreach { t=>
          assert(t._1 matches "^x[0-9]{8}")
          assert(t._2 matches "^x[0-9]{8}")
        }
      }
    }

  test("formatted listGens satisfies predicate") {
    def dtLe(t:(DateTime, DateTime)) = (t._2 isAfter t._1)
    val fromToDateGen= TwoWithPredicate(Dates().from(y=2010).to(y=2020).format("yyyy.MM.dd"), dtLe)
    val (fromDateGen, toDateGen)= fromToDateGen.asFormattedListGens(200)
    val res1= fromDateGen.get(200)
    val res2= toDateGen.get(200)
    for (i<- 0 to 199) assert(res1(i)<=res2(i))
  }
}
