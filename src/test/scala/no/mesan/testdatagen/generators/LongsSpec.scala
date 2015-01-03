package no.mesan.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec

import scala.language.postfixOps

@RunWith(classOf[JUnitRunner])
class LongsSpec extends FlatSpec {

  def generator= Longs()

  "The Longs generator" should "verify its boundaries" in {
    intercept[IllegalArgumentException] {
      generator from 10 to 5 gen
    }
  }

  it should "not accept step size 0" in {
    intercept[IllegalArgumentException] {
      (generator step 0 sequential) gen
    }
  }

  it should "guard against Long.MinValue/Max.Value" in {
    intercept[IllegalArgumentException] {
      generator from Long.MinValue get 1
    }
    intercept[IllegalArgumentException] {
      generator to Long.MaxValue get 1
    }
  }

  it should "generate numbers in sequence when asked" in {
    assert((generator from -2 to 2 sequential).get(5) === List(-2L, -1L, 0L, 1L, 2L))
    assert((generator from 1 to 6 step -1 sequential).get(5) === List(6L, 5L, 4L, 3L, 2L))
  }

  it should "generate random numbers within range" in {
    val res = (generator from -10 to 10) get 25
    assert(res.length === 25)
    assert(res.forall(i => i <= 10 && i >= -10))
  }

  it should "wrap around when sequences reach the limit" in {
    assert((generator step 2 from 1 to 6 sequential).gen.take(5).toList === List(1L, 3L, 5L, 1L, 3L))
  }

  it should "also wrap on absolute Max and Min" in {
    assert((generator from (Long.MaxValue - 3) sequential).get(5) ===
      List(Long.MaxValue-3, Long.MaxValue-2, Long.MaxValue-1, Long.MaxValue-3, Long.MaxValue-2))
    assert((generator step -1 from Long.MinValue + 1 to Long.MinValue + 3 sequential).get(5) ===
      List(Long.MinValue+3, Long.MinValue+2, Long.MinValue+1, Long.MinValue+3, Long.MinValue+2))
  }

  it should "handle small ranges" in {
    assert((generator from 19278 to 19278).get(2) === List(19278L, 19278L))
    assert((generator from 19278 to 19278 sequential).get(2) === List(19278L, 19278L))
    assert((generator from 19278 to 19279 sequential).get(2) === List(19278L, 19279L))
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

  it should "generate negative values when expected" in {
    val neg= Longs.negative().get(777)
    for (i<- neg) assert(i<0)

  }
}
