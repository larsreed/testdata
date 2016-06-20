package no.mesan.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.ExtendedGenerator

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

import scala.language.postfixOps

@RunWith(classOf[JUnitRunner])
class FromFileSpec extends FlatSpec {

  trait Setup {
    val pfx= "" // src/test/scala/no/mesan/testdatagen/generators/"
    val ints= pfx + "ints.txt"
    val strings= pfx + "strings.txt"
    val empty= pfx + "empty.txt"
    val intGen= FromFile(ints)
    val strGen= FromFile(strings)
  }

  "The FromFile generator" should "reject from/to" in {
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

  it should "reject input from empty files" in {
    new Setup {
      intercept[IllegalArgumentException] {
        FromFile(empty).get(1)
      }
    }
  }

  it should "be able to reverse the file input" in {
    new Setup {
      val res= FromFile(ints).sequential.get(8).reverse
      val exp= List("1", "1000000", "100000", "10000", "1000", "100", "10", "1")
      assert(res === exp)
    }
  }

  it should "be able to extract randomly (may fail on rare occasions)" in {
    new Setup {
      val res= FromFile(ints).get(250).toSet
      val exp= List("1000000", "100000", "10000", "1000", "100", "10", "1").toSet
      assert(res === exp)
    }
  }

  it should "be able to filter (may fail on rare occasions)" in {
    new Setup {
      val res= FromFile(ints).filter(s=> s.toLong > 1000).get(100).toSet
      val exp= List("1000000", "100000", "10000").toSet
      assert(res === exp)
    }
  }

  it should "be able to format input" in {
    new Setup {
      val res= FromFile(ints).sequential.formatWith(s=> f"${s.toLong}%015d").getStrings(3)
      val exp= List("000000000000001", "000000000000010", "000000000000100")
      assert(res === exp)
    }
  }

  it should "be able to extract randomly" in {
    new Setup {
      val exp= List("1000000", "100000", "10000", "1000", "100", "10", "1").distinct.sorted
      val res= {
        val ss: List[String] = (FromFile(ints) distinct).get(exp.size)
        ss.toSet.toList.sorted
      }
      assert(res === exp)
    }
  }
}
