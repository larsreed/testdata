package net.kalars.testgen.generators

import scala.annotation.tailrec
import scala.util.Random

import net.kalars.testgen.SingleGenerator

/**
 * Generate booleans.
 * Special methods: format(falseString, trueString) -- alternative string representations
 * Default limits: false, true
 */
class Booleans extends ListGenerator[Boolean] {

  fromList(List(false, true))

  def format(falseString: String, trueString:String): this.type =
    formatWith((t:Boolean) => if (t) trueString else falseString)
}

object Booleans {
  def apply(): Booleans = new Booleans()
  def apply(falseString: String, trueString:String): Booleans =
    new Booleans().format(falseString, trueString)
}
