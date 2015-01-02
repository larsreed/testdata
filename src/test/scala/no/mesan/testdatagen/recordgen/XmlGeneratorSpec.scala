package no.mesan.testdatagen.recordgen

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.aggreg.SomeNulls
import no.mesan.testdatagen.generators._
import no.mesan.testdatagen.generators.misc.{Names, Urls}
import no.mesan.testdatagen.generators.norway.NorskeNavn
import org.junit.runner.RunWith
import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class XmlGeneratorSpec extends FlatSpec with Matchers {

  trait Setup {
    val idGen= Ints().from(1).sequential
    val codeGen= Strings().chars('A' to 'Z').length(4)
    val nameGen= Names(2)
    val bornGen= SomeNulls(25, Dates().from(y=1950).to(y=2012).format("yyyy-MM-dd"))
    val urlGen= SomeNulls(35, Urls())
  }

  trait SetupElement extends Setup {
    val rootGen= ToXmlElements("root", "data").
                     add("id", idGen).
                     add("userId", codeGen).
                     add("name", nameGen).
                     add("homePage", urlGen).
                     add("born", bornGen)
    val fragmentGen= ToXmlElements("", "data", SkipNull).
                     add("id", idGen).
                     add("userId", codeGen).
                     add("name", nameGen).
                     add("born", bornGen)
  }

  trait SetupAttribute extends Setup {
    val rootGen= ToXmlAttributes("root", "data").
                     add("id", idGen).
                     add("userId", codeGen).
                     add("name", nameGen).
                     add("homePage", urlGen).
                     add("born", bornGen)
    val fragmentGen= ToXmlAttributes("", "data", SkipNull).
                     add("id", idGen).
                     add("userId", codeGen).
                     add("name", nameGen).
                     add("born", bornGen)
  }

  "The ToXmlElements generator" should "demand at least one generator" in {
    a [IllegalArgumentException] should be thrownBy { ToXmlElements(recordName="x").get(1) }
  }

  it should "produce the expected number of records" in {
    new SetupElement {
      rootGen.get(3).size should be (1)
      fragmentGen.get(4).size should be (4)
      rootGen.getStrings(5).size should be (1)
      fragmentGen.getStrings(6).size should be (6)
      val nGen= ToXmlElements(recordName="data").
                     add("id", idGen).
                     add("born", bornGen)
       nGen.get(30).size should be (30)
    }
  }

  it should "format its output as expected" in {
    new SetupElement {
      rootGen.get(30).mkString(" ").replaceAll("[\n\r]", " ") should
        include regex "<root>\\s+<data>.*</data>.*</data>\\s+</root>"
      val nGen= ToXmlAttributes(recordName="data").
                     add("id", idGen).
                     add("born", bornGen)
      // ?s is dotall-mode, accepts multiline data
      nGen.getStrings(1)(0) replaceFirst("born=[^ ]+", "") should
        include regex "(?s)^\\s*<data\\s+id.*(/>|</data>)\\s*$"
    }
  }

  it should "keep nulls if expected" in {
    new SetupElement {
      val gen = ToXmlElements(recordName = "data", nulls = KeepNull).
          add("id", SomeNulls(100, idGen))
      gen.get(1)(0).toString() should include regex "(?s)^\\s*<data>\\s*<id>\\s*null\\s*</id>\\s*</data>\\s*$"
    }
  }

  it should "skip nulls if expected" in {
    new SetupElement {
      val gen = ToXmlElements(recordName = "data", nulls = SkipNull).
          add("id", SomeNulls(100, idGen))
      gen.get(1)(0).toString() should include regex "(?s)^\\s*(<data>\\s*</data>|<data/>)\\s*$"
    }
  }

  it should "use empty nulls if expected" in {
    new SetupElement {
      val gen = ToXmlElements(recordName = "data", nulls = EmptyNull).
          add("id", SomeNulls(100, idGen))
      gen.get(1)(0).toString() should include regex "(?s)^\\s*<data>\\s*(<id>\\s*</id>|<id/>)\\s*</data>\\s*$"
    }
  }

  it should "quote special characters" in {
    new SetupElement {
      val gen = ToXmlElements(recordName = "i").
          add("x", Chars("<&\"' >").sequential)
      val res = gen.getStrings(6)
      val expected= List("&lt;", "&amp;", "&quot;", "'", " ", "&gt;")
      for (i<- 0 to expected.length-1)
        res(i) should include regex "(?s)^\\s*<i>\\s*<x>\\s*"+expected(i)+"\\s*</x>\\s*</i>\\s*$"
    }
  }

  it should "handle nesting" in {
    val intGen= Ints() from 1
    val gen1= ToXmlAttributes(recordName="subAttr").add("int", intGen)
    val gen2= ToXmlElements(recordName="subElem").add("fix", Fixed("<"))
    val gen3= ToXmlElements(recordName = "combined")
      .add("name", NorskeNavn())
      .add("embeddedAttr", gen1)
      .add("long", Longs())
      .add("embeddedElem", gen2)
    // println(gen3.get(4))
    // TODO Assert
  }

  "The ToXmlAttribute generator" should "demand at least one generator" in {
    a[IllegalArgumentException] should be thrownBy { ToXmlAttributes(recordName="x").get(1) }
  }

  it should "produce the expected number of records" in {
    new SetupAttribute {
      fragmentGen.get(4).size should be (4)
      rootGen.get(3).size should be (1)
      rootGen.getStrings(5).size should be (1)
      fragmentGen.getStrings(6).size should be (6)
      val nGen= ToXmlAttributes(recordName="data").
                     add("id", idGen).
                     add("born", bornGen)
      nGen.get(30).size should be (30)
    }
  }

  it should "format its output as expected" in {
    new SetupAttribute {
      rootGen.get(3).mkString(" ").replaceAll("[\n\r\t ]+", " ") should
        include regex "<root>(\\s*<data[^>]*(></data>|/>))+\\s*</root>"
      val nGen= ToXmlElements(recordName="data").
                     add("id", idGen).
                     add("born", bornGen)
      // ?s is dotall-mode, accepts multiline data
      nGen.getStrings(1)(0) should include regex "(?s)^\\s*<data>.*</id>.*</data>\\s*$"
    }
  }

  it should "keep nulls if expected" in {
    new SetupAttribute {
      val gen = ToXmlAttributes(recordName = "data", nulls = KeepNull).
          add("id", SomeNulls(100, idGen))
      gen.get(1)(0).toString() should include regex "(?s)^\\s*<data\\s*id=.null.\\s*(/>|></data>)\\s*$"
    }
  }

  it should "skip nulls if expected" in {
    new SetupAttribute {
      val gen = ToXmlAttributes(recordName = "data", nulls = SkipNull).
          add("id", SomeNulls(100, idGen))
      gen.get(1)(0).toString() should include regex "(?s)^\\s*(<data>\\s*</data>|<data/>)\\s*$"
    }
  }

  it should "use empty nulls if expected" in {
    new SetupAttribute {
      val gen = ToXmlAttributes(recordName = "data", nulls = EmptyNull).
          add("id", SomeNulls(100, idGen))
      gen.get(1)(0).toString() should include regex "(?s)^\\s*<data\\s*id=..\\s*(/>|></data>)\\s*$"
    }
  }

  it should "quote special characters" in {
    new SetupAttribute {
      val gen = ToXmlAttributes(recordName = "i").
          add("x", Chars("<&\"' >").sequential)
      val res = gen.getStrings(6)
      val expected= List("&lt;", "&amp;", "&quot;", "'", " ", "&gt;")
      for (i<- 0 to expected.length-1)
        res(i) should include regex "(?s)^\\s*<i x=."+expected(i)+".\\s*(/>|></i>)\\s*$"
    }
  }
}
