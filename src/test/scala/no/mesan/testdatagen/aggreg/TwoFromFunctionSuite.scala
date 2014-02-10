package no.mesan.testdatagen.aggreg

import scala.language.postfixOps
import scala.util.Random

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

import no.mesan.testdatagen.generators.FromList
import no.mesan.testdatagen.{Percentage, Printer}

@RunWith(classOf[JUnitRunner])
class TwoFromFunctionSuite extends FunSuite with Printer with Percentage {

  trait Setup {
    val listGen= FromList("abc", "" ,"d", "ef")
    val xgen= TwoFromFunction(listGen, (v:String)=> v.length)
  }

  print(false) {
    new Setup {
      println(xgen.get(12))
      println(xgen.getStrings(12))
      println(xgen.getFormatted(12))
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
      assert(res forall(t=> t._1.length==t._2))
    }
  }

  test("actual values") {
    val N= 5000
    val M= 1000
    val idList= (M to (N+M-1)) toList
    val altList= Random.shuffle(idList)
    val no= idList.length
    val (g1, g2)=
      TwoFromFunction[Int,Any](FromList(idList).sequential, { id:Int=> if (id==M) null else {
        var n= 0
        do { n= altList(Random.nextInt(no)) } while (n>=id)
        n
      }}).asListGens(N)
    val gen1= g1.get(N)
    val gen2= g2.get(N)
    for (i <- idList) {
      assert(gen1(i-M)===i)
      assert(gen2(i-M)==null || gen2(i-M).asInstanceOf[Int]<gen1(i-M))
    }

  }
}
