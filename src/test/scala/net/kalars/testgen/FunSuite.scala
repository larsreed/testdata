package net.kalars.testgen

class FunSuite extends org.scalatest.FunSuite {
  def print(printFun: =>Unit ) {
    if (true) printFun
  }
}