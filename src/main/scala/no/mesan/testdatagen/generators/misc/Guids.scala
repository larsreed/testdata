package no.mesan.testdatagen.generators.misc

import no.mesan.testdatagen.{StreamUtils, GeneratorImpl}
import no.mesan.testdatagen.generators.Longs

import scala.language.postfixOps

/**
 * Generate simple GUIDs.  The rules of http://www.ietf.org/rfc/rfc4122.txt are NOT followed,
 * these are only random 128-bit integers.
 *
 * @author lre
 */
class Guids extends GeneratorImpl[Seq[Long]] with StreamUtils {
  private val p1Gen= Longs() from 0 to Int.MaxValue
  private val p2Gen= Longs() from 0 to 65535
  private val p3Gen= Longs() from 0 to 65535
  private val p4Gen= Longs() from 0

  def getStream: Stream[Seq[Long]]=  combineGens(List(p1Gen, p2Gen, p3Gen, p4Gen))

  /** get values as BigInts rather than tuples. */
  def genBigInts: Stream[BigInt] = genStrings map (s=> BigInt(s.replaceAll("-", ""), 16))

  formatWith {
    case Seq(p1,p2,p3,p4)=> f"$p1%08x-$p2%04x-$p3%04x-$p4%016x"
  }
}

object Guids {
  def apply(): Guids = new Guids
}
