package no.mesan.testdatagen.generators.misc

import no.mesan.testdatagen.generators.Longs
import no.mesan.testdatagen.{GeneratorImpl, StreamGeneratorImpl}

import scala.language.postfixOps

/**
 * Generate simple GUIDs.  The rules of http://www.ietf.org/rfc/rfc4122.txt are NOT followed,
 * these are only random 128-bit integers.
 *
 * @author lre
 */
class Guids extends GeneratorImpl[Seq[Long]] with StreamGeneratorImpl[Seq[Long]] {
  private val p1Gen= Longs() from 0 to Int.MaxValue
  private val p2Gen= Longs() from 0 to 65535
  private val p3Gen= Longs() from 0 to 65535
  private val p4Gen= Longs() from 0

  def getStream: Stream[Seq[Long]]=
    p1Gen.gen.zip(p2Gen.gen).zip(p3Gen.gen).zip(p4Gen.gen).map(v=>
      Seq(v._1._1._1, v._1._1._2, v._1._2, v._2)) // ugly, but works...

  /** get values as BigInts rather than tuples. */
  def getBigInts: Stream[BigInt] = genStrings map (s=> BigInt(s.replaceAll("-", ""), 16))

  formatWith {
    case Seq(p1,p2,p3,p4)=> f"$p1%08x-$p2%04x-$p3%04x-$p4%016x"
  }
}

object Guids {
  def apply(): Guids = new Guids
}
