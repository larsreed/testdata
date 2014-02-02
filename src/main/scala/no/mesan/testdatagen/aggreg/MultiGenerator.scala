package no.mesan.testdatagen.aggreg

import scala.collection.immutable.List

import no.mesan.testdatagen.{Generator, GeneratorImpl}

/**
 * A common superclass for generators needing to support one or more nested generators.
 *
 * @author lre
 */
abstract class MultiGenerator[T, U] extends GeneratorImpl[T] {
  var generators: List[Generator[U]] = Nil

  /** Add another generator. */
  def add(g: Generator[U]): this.type = { generators ::= g; this }

  /** Add many generators. */
  def add(gs: Generator[U]*): this.type = { generators ++= gs; this }
}


