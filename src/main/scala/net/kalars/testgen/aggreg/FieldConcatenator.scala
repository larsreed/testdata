package net.kalars.testgen.aggreg

import scala.collection.immutable.List

import net.kalars.testgen.{Generator, GeneratorImpl}

class FieldConcatenator extends GeneratorImpl[String] {
  private var generators: List[Generator[_]] = Nil

  def add(g: Generator[_]): FieldConcatenator = { generators ::= g; FieldConcatenator.this }

  override def get(n: Int): List[String] = {
    val lists = generators.reverse.map(g => g.getStrings(n)).transpose
    lists.map(l => l.reduceLeft(_ + _)).filter(filterAll)
  }
}

object FieldConcatenator {
  def apply(): FieldConcatenator = new FieldConcatenator
}
