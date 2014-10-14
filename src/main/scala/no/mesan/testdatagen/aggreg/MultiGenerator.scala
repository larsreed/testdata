package no.mesan.testdatagen.aggreg

import no.mesan.testdatagen.Generator

import scala.collection.immutable.List

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
  type GenList= List[(Int, Generator[T])]
  private var _generators: GenList = Nil
  def generators: List[(Int, Generator[T])] = _generators.reverse

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
  def addWeighted(weighted: (Int, Generator[T])*): this.type = {
    _generators ++= weighted.toList.reverse
    this
  }

  /** Return the sum of all weights. */
  def sumOfWeights: Int = _generators.map(_._1).foldLeft(0)(_+_)
}
