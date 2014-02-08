package no.mesan.testdatagen.generators.misc

import no.mesan.testdatagen.GeneratorImpl
import no.mesan.testdatagen.generators.{Ints, Longs}
import scala.language.postfixOps

/**
 * Generate simple GUIDs.  The rules of http://www.ietf.org/rfc/rfc4122.txt are NOT followed,
 * these are only random 128-bit integers.
 *
 * @author lre
 */
class Guids extends GeneratorImpl[Seq[Long]] {
  private val p1Gen= Ints() from 0
  private val p23Gen= Ints() from 0 to 65535
  private val p4Gen= Longs() from 0

  override def get(n:Int): List[Seq[Long]] = {
    val p1= p1Gen get n map(_.toLong)
    val p2= p23Gen get n map(_.toLong)
    val p3= p23Gen get n map(_.toLong)
    val p4= p4Gen.get(n)
    List(p1, p2, p3, p4) transpose
  }

  /** get values as BigInts rather than tuples. */
  def getBigInts(n:Int):List[BigInt] = {
    val res= getStrings(n)
    res map (s=> BigInt(s.replaceAll("-", ""), 16))
  }

  formatWith{
    case Seq(p1,p2,p3,p4)=> f"$p1%08x-$p2%04x-$p3%04x-$p4%016x"
  }

  override def filter(f: Seq[Long]=>Boolean)= throw new UnsupportedOperationException
}

object Guids {
  def apply(): Guids = new Guids
}
