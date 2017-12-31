package no.netcompany.testdatagen.generators.misc

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.netcompany.testdatagen.GeneratorImpl
import no.netcompany.testdatagen.generators.{FromList, Ints}

import scala.annotation.tailrec

/**
 * This generator by default generates 16-digit credit card numbers from Visa or MasterCard,
 * but you can instruct it otherwise through its generic apply method.
 * The last digit is generated using Luhn's algorithm
 * (http://en.wikipedia.org/wiki/Luhn_algorithm], see also
 * http://en.wikipedia.org/wiki/Credit_card_number).
 */
class CreditCards (prefixes: List[Long], length:Int) extends GeneratorImpl[Long] {

  private def digits(n:Long) = n.toString.map { _.toString.toInt }
  private def sum(s: Seq[Int]) = s.sum
  private def flip(n:Int) = if (n==2) 1 else 2

  @tailrec private def forAll(accum: Int, factor:Int, d:List[Int]): Int = d match {
     case Nil => accum
     case _ => forAll(accum + sum(digits(factor*d.head)), flip(factor), d.tail)
  }
  private def checkDigit(cardNo: Long)= {
    val chk= forAll(0, 2, digits(cardNo/10L).reverse.toList) % 10
    if (chk==0) 0 else 10-chk
  }
  // private def isValid(cardNo: Long)= checkDigit(cardNo) == cardNo%10

  def getStream: Stream[Long]= {
    val pfxGen= FromList(prefixes).gen
    val ints= Ints() from 0 to 9

    def one: Long= {
      val pfx= pfxGen.head
      val allInts= ints.get(length- pfx.toString.length) // up to 15 more digits
      val sample= allInts.foldLeft(pfx)(10L*_ + _)
      (sample/10L)*10L + checkDigit(sample)
    }
    def next: Stream[Long]= one #:: next
    next
  }
}

object CreditCards {
  val stdLength= 16
  private val masterCard= (51L to 55L).toList
  private val visa= List(4L)
  def apply(): CreditCards = new CreditCards(masterCard ::: visa, stdLength)
  def apply(prefixes: List[Long], length: Int): CreditCards= new CreditCards(prefixes, length)
  /** Generate Visa numbers only. */
  def visas : CreditCards = new CreditCards(visa, stdLength)
  /** Generate Mastercard numbers only. */
  def masterCards: CreditCards= new CreditCards(masterCard, stdLength)
}
