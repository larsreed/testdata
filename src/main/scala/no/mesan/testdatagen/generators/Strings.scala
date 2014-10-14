package no.mesan.testdatagen.generators

import no.mesan.testdatagen.ExtendedImpl

import scala.annotation.tailrec
import scala.language.postfixOps
import scala.util.Random

/**
 * Generate Strings.
 * Special methods: length(n) -- exact string length
 *                  lengthBetween(from, to) -- interval of string lengths
 *                  chars(seq) -- accepted characters
 * Defaults: length 1, from a(s) to z(s)
 *
 *
 * @author lre
 */
class Strings extends ExtendedImpl[String] {

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

  def getStream: Stream[String] = {
    val chars= charRange.toList
    val min= lower.getOrElse("")
    val max= upper.getOrElse("\uFFFF" * maxLength)
    require(max>=min, "min>max")

    def getRandomly: Stream[String]= {
      def makeRandomString: String = {
        val width= if (minLength==maxLength) maxLength
                   else minLength + Random.nextInt(maxLength-minLength+1)
        val range= chars.length
        @tailrec def mkString(soFar: String): String= {
          if (soFar.length==width) soFar
          else mkString(soFar + chars(Random.nextInt(range)))
        }
        mkString("")
      }
      makeRandomString #:: getRandomly
    }

    val charCount: BigInt= chars length

    def next(len: Int, curr: BigInt): Stream[String] = {
      // Too long, start again
      if (len>maxLength) return next(minLength, 0)
      // Empty strings are easy
      if (len==0) return Stream.cons("", next(1, 0))
      // Have we exhausted all possibilites for this length?
      val possibilites= charCount pow len
      if (curr>=possibilites) return next(len+1, 0)
      // We are OK, generate the string.  This is a bit hairy, so let's explain:
      //   All characters may vary as an index into the chars-sequence, let's shorten
      //   charCount to C.  For a string of length len (shortened to L), there are
      //   C^L (where ^ denotes power) possible combinations. We count through each one, with the
      //   current entry (curr, denoted N).
      //   Then the last character is (N mod C), the next to last ((N div C) mod C), then
      //   ((N div C*C) mod C) and so on, generally ((N div C^idx) mod C).
      //   These indexes are mapped against the char range and converted to a string.
      val indexes= for (i<- len-1 to 0 by -1) yield (curr / (charCount pow i)) % charCount
      val res= indexes map { i => chars(i.toInt) }
      res.mkString #:: next(len, curr+1)
    }

    if (isSequential) next(minLength, 0)
    else getRandomly
  }
}

object Strings {
  val asciiUpperLower= "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
  val digits= "0123456789"
  def apply(length: Int=1):Strings = new Strings() length length
  def apply(length: Int, chars:Seq[Char]):Strings = new Strings() length length chars chars
}
