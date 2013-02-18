package no.mesan.testdatagen

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PercentageSuite extends FunSuite with Printer with Percentage {

  test("all") {
    for (i<-0 to 5000) assert(hit(100))
  }

  test("none") {
    for (i<-0 to 5000) assert(!hit(0))
  }

  test("some") {
    var count=0
    for (i<-0 to 15000) if (hit(50)) count+=1
    assert(count>6000, "count=" + count)
    assert(count<9000, "count=" + count)
  }

  print(false) {
    var count=0
    for (i<-0 to 15000) if (hit(50)) count+=1
    println(count)
  }
}