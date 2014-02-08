package no.mesan.testdatagen.aggreg

import scala.collection.immutable.List

import no.mesan.testdatagen.{Generator, GeneratorImpl}

/**
 * Traits for generators needing to support one or more nested generators.
 *
 * @author lre
 */
trait MultiGenerator[T] {
  var generators: List[Generator[T]] = Nil

  /** Add 0 or more generators. */
  def add(gs: Generator[T]*): this.type = {
    generators ++= gs.toList
    this
  }
}

trait MultiGeneratorWithWeight[T] {
  type GenList= List[(Int,Generator[T])]
  private var _generators: GenList = Nil
  def generators = _generators.reverse

  /** Add 0 or more generators with weight 1. */
  def add(gs: Generator[T]*): this.type = {
    gs.foreach{ g=> add(1, g) }
    this
  }

  /** Add 0 or more generators with weight 1. */
  def add(weight:Int, g: Generator[T]): this.type = {
    _generators ::= (weight, g)
    this
  }

  /** Add 0 or more weighted generators. */
  def addTuples(weighted: (Int, Generator[T])*): this.type = {
    _generators ++= weighted.toList.reverse
    this
  }
}
