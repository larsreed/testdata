package no.netcompany.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

/** Generate booleans. */
class Booleans extends FromList[Boolean] {

  fromList(List(false, true))

  /** How to format true/false in strings. */
  def format(falseString: String, trueString:String): this.type =
    formatWith((t:Boolean) => if (t) trueString else falseString)
}

object Booleans {
  def apply(): Booleans = new Booleans()
  def apply(falseString: String, trueString:String): Booleans =
    new Booleans().format(falseString, trueString)
}
