package net.kalars.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import net.kalars.testdatagen.ExtendedImpl
import net.kalars.testdatagen.utils.RandomElem

/**
 * Probably the most versatile of all the generators, the FromList takes a list of
 * "anything" as input and generates its values from that, it is typed (FromList[T]),
 * so you keep the type of the input list.
 *
 * from/to are not supported.
 */
class FromList[T] extends ExtendedImpl[T] with RandomElem {

  override def from(f:T) = throw new UnsupportedOperationException
  override def to(f:T) = throw new UnsupportedOperationException

  protected var inputList: Seq[T]= List()
  /** Enter the list of values. */
  def fromList(l: Seq[T]): this.type= { inputList= l; this }

  def getStream: Stream[T] = {
    require(inputList.nonEmpty, "cannot extract from empty list")

    def sequentially(inx: Int): Stream[T]= {
      if (inx >= inputList.length) inputList.head #:: sequentially(1)
      else inputList(inx) #:: sequentially(inx + 1)
    }

    def randomly: Stream[T]= {
      val nxt= randomFrom(inputList)
      nxt #:: randomly
    }

    if (isSequential) sequentially(0)
    else randomly
  }
}

object FromList {
  def apply[T](): FromList[T] = new FromList()
  def apply[T](l: List[T]): FromList[T] = new FromList().fromList(l)
  def apply[T](ls: T*): FromList[T] = new FromList().fromList(ls.toList)
  def weighted[T](elements: Seq[(Int, T)]): FromList[T]= {
    val list= for {
      (n, elem) <- elements
      _ <- 1 to n} yield elem
    apply(list:_*)
  }
}