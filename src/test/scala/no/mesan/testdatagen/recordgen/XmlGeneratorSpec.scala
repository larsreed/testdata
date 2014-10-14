package no.mesan.testdatagen.recordgen

import no.mesan.testdatagen.aggreg.SomeNulls
import no.mesan.testdatagen.generators.misc.{Names, Urls}
import no.mesan.testdatagen.generators.{Chars, Dates, Ints, Strings}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class XmlGeneratorSpec extends FlatSpec {

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
    intercept[IllegalArgumentException] { ToXmlElements(recordName="x").get(1) }
  }

  it should "produce the expected number of records" in {
    new SetupElement {
      assert(rootGen.get(3).size === 1)
      assert(fragmentGen.get(4).size === 4, fragmentGen.get(4))
      assert(rootGen.getStrings(5).size === 1)
      assert(fragmentGen.getStrings(6).size === 6)
      val nGen= ToXmlElements(recordName="data").
                     add("id", idGen).
                     add("born", bornGen)
      assert(nGen.get(30).size === 30)
    }
  }

  it should "format its output as expected" in {
    new SetupElement {
      val res= rootGen.get(30).mkString(" ").replaceAll("[\n\r]", " ")
      assert(res.matches("<root>\\s+<data>.*</data>.*</data>\\s+</root>"), res)
      val nGen= ToXmlAttributes(recordName="data").
                     add("id", idGen).
                     add("born", bornGen)
      val v= nGen.getStrings(1)(0) replaceFirst("born=[^ ]+", "")
      // ?s is dotall-mode, accepts multiline data
      assert(v.matches("(?s)^\\s*<data\\s+id.*(/>|</data>)\\s*$"), "|" + v + "|")
    }
  }

  it should "keep nulls if expected" in {
    new SetupElement {
      val gen = ToXmlElements(recordName = "data", nulls = KeepNull).
          add("id", SomeNulls(100, idGen))
      val res = gen.get(1)(0).toString()
      assert(res.matches("(?s)^\\s*<data>\\s*<id>\\s*null\\s*</id>\\s*</data>\\s*$"), res)
    }
  }

  it should "skip nulls if expected" in {
    new SetupElement {
      val gen = ToXmlElements(recordName = "data", nulls = SkipNull).
          add("id", SomeNulls(100, idGen))
      val res = gen.get(1)(0).toString()
      assert(res.matches("(?s)^\\s*(<data>\\s*</data>|<data/>)\\s*$"), res)
    }
  }

  it should "use empty nulls if expected" in {
    new SetupElement {
      val gen = ToXmlElements(recordName = "data", nulls = EmptyNull).
          add("id", SomeNulls(100, idGen))
      val res = gen.get(1)(0).toString()
      assert(res.matches("(?s)^\\s*<data>\\s*(<id>\\s*</id>|<id/>)\\s*</data>\\s*$"), res)
    }
  }

  it should "quote special characters" in {
    new SetupElement {
      val gen = ToXmlElements(recordName = "i").
          add("x", Chars("<&\"' >").sequential)
      val res = gen.getStrings(6)
      val expected= List("&lt;", "&amp;", "&quot;", "'", " ", "&gt;")
      for (i<- 0 to expected.length-1)
        assert(res(i).matches("(?s)^\\s*<i>\\s*<x>\\s*"+expected(i)+"\\s*</x>\\s*</i>\\s*$"),
               i+":"+res(i))
    }
  }

  "The ToXmlAttribute generator" should "demand at least one generator" in {
    intercept[IllegalArgumentException] { ToXmlAttributes(recordName="x").get(1) }
  }

  it should "produce the expected number of records" in {
    new SetupAttribute {
      val res1= fragmentGen.get(4)
      assert(res1.size === 4, res1.toString)
      val res2= rootGen.get(3)
      assert(res2.size === 1, res2)
      assert(rootGen.getStrings(5).size === 1)
      assert(fragmentGen.getStrings(6).size === 6)
      val nGen= ToXmlAttributes(recordName="data").
                     add("id", idGen).
                     add("born", bornGen)
      assert(nGen.get(30).size === 30)
    }
  }

  it should "format its output as expected" in {
    new SetupAttribute {
      val res= rootGen.get(3).mkString(" ").replaceAll("[\n\r\t ]+", " ")
      assert(res.matches("<root>(\\s*<data[^>]*(></data>|/>))+\\s*</root>"), res)
      val nGen= ToXmlElements(recordName="data").
                     add("id", idGen).
                     add("born", bornGen)
      val v= nGen.getStrings(1)(0)
      // ?s is dotall-mode, accepts multiline data
      assert(v.matches("(?s)^\\s*<data>.*</id>.*</data>\\s*$"), "|" + v + "|")
    }
  }

  it should "keep nulls if expected" in {
    new SetupAttribute {
      val gen = ToXmlAttributes(recordName = "data", nulls = KeepNull).
          add("id", SomeNulls(100, idGen))
      val res = gen.get(1)(0).toString()
      assert(res.matches("(?s)^\\s*<data\\s*id=.null.\\s*(/>|></data>)\\s*$"), res)
    }
  }

  it should "skip nulls if expected" in {
    new SetupAttribute {
      val gen = ToXmlAttributes(recordName = "data", nulls = SkipNull).
          add("id", SomeNulls(100, idGen))
      val res = gen.get(1)(0).toString()
      assert(res.matches("(?s)^\\s*(<data>\\s*</data>|<data/>)\\s*$"), res)
    }
  }

  it should "use empty nulls if expected" in {
    new SetupAttribute {
      val gen = ToXmlAttributes(recordName = "data", nulls = EmptyNull).
          add("id", SomeNulls(100, idGen))
      val res = gen.get(1)(0).toString()
      assert(res.matches("(?s)^\\s*<data\\s*id=..\\s*(/>|></data>)\\s*$"), res)
    }
  }

  it should "quote special characters" in {
    new SetupAttribute {
      val gen = ToXmlAttributes(recordName = "i").
          add("x", Chars("<&\"' >").sequential)
      val res = gen.getStrings(6)
      val expected= List("&lt;", "&amp;", "&quot;", "'", " ", "&gt;")
      for (i<- 0 to expected.length-1)
        assert(res(i).matches("(?s)^\\s*<i x=."+expected(i)+".\\s*(/>|></i>)\\s*$"),
               i+":"+res(i))
    }
  }
}
