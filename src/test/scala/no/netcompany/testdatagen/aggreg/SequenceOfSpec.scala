package no.netcompany.testdatagen.aggreg

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import scala.language.postfixOps

import no.netcompany.testdatagen.generators.{Chars, FromList, Ints}

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SequenceOfSpec extends FlatSpec {

  trait Setup {
    val noOfGens= 3
    val xgen= SequenceOf.strings(FromList(1, 2, 3) sequential,
                                 Chars("abc") sequential,
                                 Ints() from 4 sequential)
  }

  "A SequenceOf" should "return an apporximately correct number of records" in{
    new Setup {
      private val size = xgen.get(30).size
      assert(size>=28 && size<=32)
    }
  }

  it should "generate the right contents" in {
    new Setup {
      val exp= List("1", "2", "3", "a", "b", "c", "4", "5", "6")
      val res1= xgen.get(9)
      val res2= xgen.getStrings(9)
      assert(exp==res1, "1" + res1)
      assert(exp==res2, "2" + res2)
    }
  }

  it should "respect the given weights" in {
    val xgen= SequenceOf[Any]().addWeighted((5, FromList(1, 2, 3) sequential),
                                            (3, Chars("abc") sequential),
                                            (2, Ints() from 4 sequential))
    val exp= List("1", "2", "3", "1", "2", "3", "a", "b", "c", "a", "4", "5")
    val res= xgen.getStrings(12)
    assert(exp===res)
  }

  it should "use absolute counts when requested" in {
    val xgen= SequenceOf[Any]().makeAbsolute().
        addWeighted((5, FromList(1, 2, 3) sequential),
                    (3, Chars("abc") sequential),
                    (2, Ints() from 4 sequential))
    val exp= List("1", "2", "3", "1", "2", "a", "b", "c", "4", "5")
    val res= xgen.getStrings(1)
    assert(exp===res)
  }
}
