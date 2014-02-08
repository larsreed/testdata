package no.mesan.testdatagen.aggreg

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import org.scalatest.FunSuite
import no.mesan.testdatagen.generators.FromList

import no.mesan.testdatagen.Printer

@RunWith(classOf[JUnitRunner])
class TwoFromFunctionSuite extends FunSuite with Printer {

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
}
