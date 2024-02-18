package net.kalars.testdatagen.aggreg

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import net.kalars.testdatagen.generators.FromList
import net.kalars.testdatagen.utils.Percentage
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

import scala.language.postfixOps
import scala.util.Random

@RunWith(classOf[JUnitRunner])
class TwoFromFunctionSpec extends FlatSpec with Percentage {

  trait Setup {
    val listGen= FromList("abc", "" ,"d", "ef")
    val xgen= TwoFromFunction(listGen, (v:String)=> v.length)
    TwoFromFunction(FromList("abc", "" ,"d", "ef"), (v:String)=> v.length)
  }

  "TwoFromFunction" should "produce correct contents" in {
    new Setup {
      val res= xgen.get(1000)
      assert(res forall(t=> t._1.length==t._2))
    }
  }

  it should "produce expected values" in {
    val N= 5000
    val M= 1000
    val idList= M until N + M toList
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