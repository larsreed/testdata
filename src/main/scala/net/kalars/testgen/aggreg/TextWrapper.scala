package net.kalars.testgen.aggreg

import scala.collection.immutable.List

import net.kalars.testgen.{Generator, GeneratorImpl}

class TextWrapper(generator:Generator[_]) extends GeneratorImpl[String] {

  private var transformers: List[(String=>String)]= List((s=>s))

  def surroundWith(prefix:String="", suffix:String=""): this.type = {
    if (prefix!="") transform(s => prefix + s)
    if (suffix!="") transform(s => s + suffix)
    this
  }

  def substring(from:Int, to:Int= -1): this.type =
    if ( to>=0 ) transform { s => s.substring(from, to)}
    else transform { s => s.substring(from)}

  def transform(f: String=>String): this.type = {
    transformers ::= f
    this
  }

  def toUpper(): this.type = transform { s=> s.toUpperCase }

  def toLower(): this.type = transform { s=> s.toLowerCase }

  private def transformAll(s:List[String]): List[String]= {
    val func= transformers reduce(_ compose _)
    s map func
  }

  override def get(n: Int): List[String] = getStrings(n)

  override def getStrings(n: Int): List[String]=
    transformAll(generator.getStrings(n))

}

object TextWrapper {
  def apply(generator: Generator[_]): TextWrapper=  new TextWrapper(generator)
}
