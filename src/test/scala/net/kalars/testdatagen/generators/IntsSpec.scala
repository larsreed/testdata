package net.kalars.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

import scala.language.postfixOps

@RunWith(classOf[JUnitRunner])
class IntsSpec extends FlatSpec {

  def generator= Ints()

  "The Ints generator" should "verify its boundaries" in {
    intercept[IllegalArgumentException] {
      generator from 10 to 5 gen
    }
  }

  it should "not accept step size 0" in {
    intercept[IllegalArgumentException] {
      (generator step 0 sequential) gen
    }
  }

  it should "generate numbers in sequence when asked" in {
    assert((generator from -2 to 2 sequential).get(5) === List(-2, -1, 0, 1, 2))
    assert((generator from 1 to 6 step -1 sequential).get(5) === List(6, 5, 4, 3, 2))
  }

  it should "generate random numbers within range" in {
    val res = (generator from -10 to 10) get 25
    assert(res.length === 25)
    assert(res.forall(i => i <= 10 && i >= -10))
  }

  it should "wrap around when sequences reach the limit" in {
    assert((generator step 2 from 1 to 6 sequential).gen.take(5).toList === List(1, 3, 5, 1, 3))
  }

  it should "also wrap on absolute Max and Min" in {
    assert((generator from (Int.MaxValue-3) sequential).get(5) ===
      List(Int.MaxValue-3, Int.MaxValue-2, Int.MaxValue-1, Int.MaxValue-3, Int.MaxValue-2))
    assert((generator step -1 to (Int.MinValue+3) sequential).get(5) ===
      List(Int.MinValue+3, Int.MinValue+2, Int.MinValue+1, Int.MinValue+3, Int.MinValue+2))
  }

  it should "handle small ranges" in {
    assert((generator from 19278 to 19278).get(2) === List(19278, 19278))
    assert((generator from 19278 to 19278 sequential).get(2) === List(19278, 19278))
    assert((generator from 19278 to 19279 sequential).get(2) === List(19278, 19279))
  }

  it should "apply filters correctly" in {
    val res = generator filter(i => (i % 2) == 0) get 10
    assert(res.length === 10)
    assert(res.forall(i => (i % 2) == 0))

    val res2 = generator.from(0).sequential.filter(i => (i % 2) == 0).get(9)
    assert(res2.length === 9)
    assert(res2.forall(i => (i % 2) == 0))
  }

  it should "be able to use a custom formatter" in {
    val res = (generator step 2 from -2 to 10 formatWith(i => f"$i%02d") sequential) getStrings 7
    assert(res === List("-2", "00", "02", "04", "06", "08", "10"))
    val res3 = (generator step 2 from -2 to 10 format "%02d" sequential) getStrings 7
    assert(res3 === List("-2", "00", "02", "04", "06", "08", "10"))
  }

  it should "allow formatter even for empty sequences" in {
    val res2 = (generator step 2 from -2 to 10 formatWith(i => f"$i%02d") sequential) getStrings 0
    assert(res2 === Nil)
  }
}