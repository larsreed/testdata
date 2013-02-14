package no.mesan.testdatagen.generators

import scala.annotation.tailrec
import scala.util.Random

import no.mesan.testdatagen.SingleGenerator

/**
 * Generate Strings.
 * Special methods: length(n) -- exact string length
 *                  lengthBetween(from, to) -- interval of string lengths
 *                  chars(seq) -- accepted characters
 * Defaults: length 1, from a(s) to z(s)
 *
 */
class Strings extends SingleGenerator[String] {

  filter(x=> lower match { case Some(low)=>  x>=low;  case _=> true })
  filter(x=> upper match { case Some(high)=> x<=high; case _=> true })

  private var minLength: Int= 1
  private var maxLength: Int= 1

  /** Exact string length. */
  def length(n: Int): this.type= {
    require(n>=0, "length cannot be <0")
    minLength= n
    maxLength= n
    this
  }

  /** Varying string length. */
  def lengthBetween(from: Int, to: Int): this.type= {
    require(from>=0 && to>=0 && from<=to,
        "length cannot be <0, from must be <= to")
    minLength= from
    maxLength= to
    this
  }

  private var charRange: Seq[Char]= ' ' to '~'

  /** Set character range. */
  def chars(seq: Seq[Char]): this.type = {
    charRange= seq
    this
  }

  override def get(n: Int): List[String] = {
    require(n>=0, "cannot get negative count")
    val chars= charRange.toList
    val min= lower.getOrElse("")
    val max= upper.getOrElse("\uFFFF" * maxLength)
    require(max>=min, "min>max")

    @tailrec
    def getRandomly(soFar: List[String]): List[String]= {
      def makeRandomString(): String = {
        val width= if (minLength==maxLength) maxLength
                   else minLength + Random.nextInt(maxLength-minLength+1)
        val range= chars.length
        def mkString(soFar: String): String= {
          if (soFar.length==width) soFar
          else mkString(soFar + chars(Random.nextInt(range)))
        }
        mkString("")
      }
      if (soFar.length>=n) soFar
      else {
        val nxt= makeRandomString
        if (filterAll(nxt) && (!isUnique || !(soFar contains nxt)))
          getRandomly(nxt::soFar)
        else getRandomly(soFar)
      }
    }

    def getSequentially(startLen: Int, endLen: Int, startChr: Int,
                        endChr: Int,  step: Int): List[String]= {
      // Recurse from shortest to largest [largest to smallest] acceptable string length
      @tailrec def recurseLengths(len: Int, accum: List[String]): List[String] = {
        // General stop criterion: n accepted strings (repeated in inner loops)
        if (accum.length>=n) accum.take(n)
        // Restart from smallest [longest] after reaching the end,
        // but abort if no strings were accepted during first pass
        else if (len<minLength || len>maxLength)
          if (accum.isEmpty) Nil
          else recurseLengths(startLen, accum)
        else {
          // Recurse through each position from right to left end of String for this length
          @tailrec
         def recursePositions(pos:Int, suffixes:List[String], accum:List[String]): List[String]= {
         // Stop at beginning of string (or we have enough)
           if (pos<0 || accum.length>=n) accum
           else {
             // Recurse through each character from min to max [max to min]
             @tailrec
              def recurseChars(char:Int, accum:List[String]): List[String]= {
                if (char<0 || char>=chars.length) accum
                else {
                  // Construct a new string from
                  //  1) positions before current -- min [max] char
                  //  2) current position: current char
                  //  3) positions after current: all combinations generated for
                  //     those positions in previous steps
                  val pfx= chars(startChr).toString * pos
                  // All possible (but accumulate only those acceptable by filter)
                  val list= suffixes.map(s=> pfx + chars(char) + s)
                  recurseChars(char+step, accum ++ list.filter(filterAll))
                }
              }
              val newAccum= recurseChars(if (pos==len-1) startChr else startChr+1, accum)
              val newSuffs= for {
                c <- startChr to endChr by step
                suff <- suffixes} yield chars(c) + suff
              recursePositions(pos-1, newSuffs.toList, newAccum)
            }
          }
          // So far there are no accumulated suffixes
          val newAccum= recursePositions(len-1, List(""), accum)
          recurseLengths(len + step, newAccum)
        }
      }
      recurseLengths(startLen, Nil)
    }

    if (isSequential && isReversed) getSequentially(maxLength, minLength, chars.length-1, 0, -1)
    else if (isSequential) getSequentially(minLength, maxLength, 0, chars.length-1, +1)
    else getRandomly(Nil)
  }
}

object Strings {
  def apply(length: Int=1):Strings = new Strings().length(length)
  def apply(length: Int, chars:Seq[Char]):Strings = new Strings().length(length).chars(chars)
}
