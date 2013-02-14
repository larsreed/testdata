package no.mesan.testdatagen.generators

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.{ExtendedGenerator, FunSuite}

@RunWith(classOf[JUnitRunner])
class FromFileSuite extends FunSuite {

  trait Setup {
    val pfx= "src/test/scala/no/mesan/testdatagen/generators/"
    val ints= pfx + "ints.txt"
    val strings= pfx + "strings.txt"
    val empty= pfx + "empty.txt"
    val intGen= FromFile(ints)
    val strGen= FromFile(strings)
  }

  print {
    new Setup {
      println(new java.io.File(".").getCanonicalPath)
      println(intGen.get(25))
      // println(FromFileGenerator[Boolean](strings).reversed.get(25))
    }
  }


  test("negative get") {
    intercept[IllegalArgumentException] {
      new Setup {
    	  intGen.get(-1)
      }
    }
    intercept[IllegalArgumentException] {
      new Setup {
    	  intGen.getStrings(-1)
      }
    }
  }

  test("from/to not suported") {
    new Setup {
      val l: ExtendedGenerator[String] = strGen
      intercept[UnsupportedOperationException] {
        l.from("1").get(1)
      }
      intercept[UnsupportedOperationException] {
        l.to("1").get(1)
      }
    }
  }

  test("cannot get from empty file") {
    new Setup {
      intercept[IllegalArgumentException] {
        FromFile(empty).get(1)
      }
    }
  }

  test("reverted sequence") {
    new Setup {
      val res= FromFile(ints).reversed.get(8)
      val exp= List("1000000", "100000", "10000", "1000", "100", "10", "1", "1000000")
      assert(res === exp)
    }
  }

  test("0 sequential elements") {
    new Setup {
      val res= FromFile(strings).reversed.allLines().get(0)
      val exp= Nil
      assert(res === exp)
    }
  }

  test("0 random elements") {
    new Setup {
      val res= FromFile(strings).allLines().get(0)
      val exp= Nil
      assert(res === exp)
    }
  }

  test("random (may fail on rare occasions)") {
    new Setup {
      val res= FromFile(ints).get(250).toSet
      val exp= List("1000000", "100000", "10000", "1000", "100", "10", "1").toSet
      assert(res === exp)
    }
  }

  test("filter (may fail on rare occasions)") {
    new Setup {
      val res= FromFile(ints).filter(s=> s.toLong > 1000).get(100).toSet
      val exp= List("1000000", "100000", "10000").toSet
      assert(res === exp)
    }
  }

  test("format") {
    new Setup {
      val res= FromFile(ints).sequential.formatWith(s=> "%015d".format(s.toLong)).getStrings(3)
      val exp= List("000000000000001", "000000000000010", "000000000000100")
      assert(res === exp)
    }
  }

  test("random") {
    new Setup {
      val exp= List("1000000", "100000", "10000", "1000", "100", "10", "1").toSet
      val res= FromFile(ints).unique.get(exp.size).toSet
      assert(res === exp)
    }
  }
}
