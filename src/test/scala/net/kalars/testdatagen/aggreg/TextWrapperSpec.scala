package net.kalars.testdatagen.aggreg

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import net.kalars.testdatagen.generators.{Fixed, Strings}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import org.scalatest.{FlatSpec, FunSuite}

@RunWith(classOf[JUnitRunner])
class TextWrapperSpec extends FlatSpec {

  trait Setup {
    val xgen= TextWrapper(Strings().chars('A' to 'Z').sequential).surroundWith("* ", "\n")
    val fix= Fixed("ABCDEFG")
  }

  "The TextWrapper" should "be able to take right substring" in {
    new Setup {
      assert(TextWrapper(fix).substring(3).get(1)(0)==="DEFG")
    }
  }

  it should "be able to take middle substring" in {
    new Setup {
      assert(TextWrapper(fix).substring(2,4).get(1)(0)==="CD")
    }
  }

  it should "handle user specified transforms" in {
    new Setup {
      assert(TextWrapper(fix).transform(_.toLowerCase).get(1)(0)==="abcdefg")
    }
  }

  it should "handle transforms in the order specified" in {
    new Setup {
      assert(TextWrapper(fix).transform(_.toLowerCase).
          substring(1,  4).
          surroundWith("(", ")").
          get(1)(0)==="(bcd)")
      assert(TextWrapper(fix).transform(_.toLowerCase).
          surroundWith("(", ")").
          substring(1,  4).
          get(1)(0)==="abc")
    }
  }

  it should "be able to change case" in {
    new Setup {
      assert(TextWrapper(fix).toLower.
          get(1)(0)==="abcdefg")
      assert(TextWrapper(fix).toLower.toUpper.
          get(1)(0)==="ABCDEFG")
    }
  }

  it should "be able to ad pre- and suffixes" in {
    val v = TextWrapper(Strings().chars("ABC").
        sequential.
        formatWith(s => s.toLowerCase)).
      surroundWith("<", ">")

    val res = v.getStrings(12)
    assert(res.forall(s => s.matches("<[a-c]>")), res)
  }

  it should "be able to replace text" in {
    val v = TextWrapper(Strings().chars("ABC").sequential).
                substitute("[ABC]", "g")

    val res = v.getStrings(12)
    assert(res.forall(s => s.matches("^g+$")), res)
  }
}