package net.kalars.testdatagen.generators.app

import net.kalars.testdatagen.aggreg.TextWrapper
import net.kalars.testdatagen.generators.{Fixed, Ints, Strings}
import net.kalars.testdatagen.recordgen.ToXmlElements
import net.kalars.testdatagen.utils.StreamUtils

import scala.language.postfixOps

object TryXml extends App with StreamUtils {

  val g0= Fixed("zzzzz")
  val g1= Strings(length = 4)
  val g2= Ints() from 1

  val x1= ToXmlElements(recordName = "x1") add("a", g1) add("z", g0) add("b", g2)
  val x2= ToXmlElements(recordName = "") add("c", g1) add("d", g2)
  val c1= <xml><![CDATA[<ting />]]></xml>


  val repl= Strings.letters(length = 15).get(1).head
  val x3= ToXmlElements(recordName = "x1") add("a", g1) add("z", g0) add("f", Fixed(repl)) add("b", g2)
  val s= x3.gen zip x2.gen map { v =>
    println(v._1.toString, repl, v._2.toString)
    v._1.toString.replaceAllLiterally(repl, v._2.toString.replaceAll("<[/]?>", ""))
  }
  println(s take 4 toList)

  // x1.genStrings map { s=> }

}