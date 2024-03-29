package net.kalars.testdatagen.aggreg

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import net.kalars.testdatagen.{BareGenerator, Generator}

import scala.collection.immutable.List

/**
 * The SequenceOf takes a list of generators. It is itself not a generator, but it supports
 * a the get(n)/getStrings(n)-methods.
 * When get/getString is called, it calls get(String) on each of its generators in sequence.
 * The number of returned records for get(N) (and equally getStrings) will vary.
 * In non-absolute mode (default), a number "close to N" (+/- 1) will be returned.
 * In absolute mode, the number will be (N*sum(weights)).
 */
class SequenceOf[S, T] (convert: S=>T) extends BareGenerator[T] with MultiGeneratorWithWeight[S] {
  private var absolute= false

  def makeAbsolute(newVal:Boolean= true): this.type = {
    absolute= newVal
    this
  }

  private def number(n: Int, weight:Int, totWeight:Int): Int= scala.math.round((n*weight*1.0)/totWeight).asInstanceOf[Int]

  private def getList[U](n: Int)(f: (Int, Generator[S]) => List[U]): List[U] =  {
    val tot= if (absolute) 1 else generators.foldLeft(0)((zum, tuple) => zum + tuple._1)
    generators.flatMap{ tuple =>
      val (w, g)= tuple
      f(number(n, w, tot), g)
    }
  }
  override def get(n: Int): List[T] =  getList(n){ (no, g)=> g.get(no).map(convert) }
  override def getStrings(n: Int): List[String] =  getList(n){ (no, g)=> g.getStrings(no)}
}

object SequenceOf {
  private def stringer(x:Any)= if (x==null) null else x.toString
  def apply(): SequenceOf[Any, String] =  new SequenceOf[Any, String](stringer)
  def apply[T](gs: Generator[T]*): SequenceOf[T, T] =  new SequenceOf[T, T]({x=>x}).add(gs:_*)
  def strings(gs: Generator[Any]*): SequenceOf[Any, String] =
    new SequenceOf[Any, String](stringer).add(gs:_*)
}