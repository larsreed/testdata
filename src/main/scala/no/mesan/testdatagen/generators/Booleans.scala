package no.mesan.testdatagen.generators

import scala.annotation.tailrec
import scala.util.Random

import no.mesan.testdatagen.SingleGenerator

/**
 * Generate booleans.
 * Special methods: format(falseString, trueString) -- alternative string representations
 * Default limits: false, true
 */
class Booleans extends FromList[Boolean] {

  fromList(List(false, true))

  def format(falseString: String, trueString:String): this.type =
    formatWith((t:Boolean) => if (t) trueString else falseString)
}

object Booleans {
  def apply(): Booleans = new Booleans()
  def apply(falseString: String, trueString:String): Booleans =
    new Booleans().format(falseString, trueString)
}
