package no.mesan.testdatagen.recordgen

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import org.scalatest.FunSuite
import no.mesan.testdatagen.aggreg.SomeNulls
import no.mesan.testdatagen.generators.{Chars, Dates, Ints, Strings}
import no.mesan.testdatagen.generators.misc.{Names, Urls}

import no.mesan.testdatagen.Printer

@RunWith(classOf[JUnitRunner])
class XmlGeneratorSuite extends FunSuite with Printer {

  trait Setup {
    val idGen= Ints().from(1).sequential
    val codeGen= Strings().chars('A' to 'Z').length(4)
    val nameGen= Names(2)
    val bornGen= SomeNulls(4, Dates().from(y=1950).to(y=2012).format("yyyy-MM-dd"))
    val urlGen= SomeNulls(3, Urls())
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

  print(false) {
    new SetupElement {
      println(rootGen.getStrings(120).mkString("\n  "))
    }
    new SetupAttribute {
      println(rootGen.getStrings(120).mkString("\n  "))
    }
  }


  test("negative get") {
    intercept[IllegalArgumentException] {
      new SetupElement {
        rootGen.get(-1)
      }
    }
    intercept[IllegalArgumentException] {
      new SetupElement {
        fragmentGen.getStrings(-1)
      }
    }
    intercept[IllegalArgumentException] {
      new SetupAttribute {
        rootGen.get(-1)
      }
    }
    intercept[IllegalArgumentException] {
      new SetupAttribute {
        fragmentGen.getStrings(-1)
      }
    }
  }

  test("needs one generator") {
    intercept[IllegalArgumentException] {
      ToXmlElements(recordName="x").get(1)
    }
    intercept[IllegalArgumentException] {
      ToXmlAttributes(recordName="x").get(1)
    }
  }

  test("count elem") {
    new SetupElement {
      assert(rootGen.get(3).size === 1)
      assert(fragmentGen.get(4).size === 4, fragmentGen.get(4))
      assert(rootGen.getStrings(5).size === 1)
      assert(fragmentGen.getStrings(6).size === 6)
    }
  }
  test("count attr") {
    new SetupAttribute {
      assert(rootGen.get(3).size === 1)
      assert(fragmentGen.get(4).size === 4, fragmentGen.get(4))
      assert(rootGen.getStrings(5).size === 1)
      assert(fragmentGen.getStrings(6).size === 6)
    }
  }

  test("count no root elem") {
    new SetupElement {
      val nGen= ToXmlElements(recordName="data").
                     add("id", idGen).
                     add("born", bornGen)
      assert(nGen.get(30).size === 30)
    }
  }
  test("count no root attr") {
    new SetupAttribute {
      val nGen= ToXmlAttributes(recordName="data").
                     add("id", idGen).
                     add("born", bornGen)
      assert(nGen.get(30).size === 30)
    }
  }

  test("format elem") {
    new SetupElement {
      val res= rootGen.get(30).mkString(" ").replaceAll("[\n\r]", " ")
      assert(res.matches("<root>\\s+<data>.*</data>.*</data>\\s+</root>"), res)
    }
  }
  test("format attr") {
    new SetupAttribute {
      val res= rootGen.get(3).mkString(" ").replaceAll("[\n\r\t ]+", " ")
      assert(res.matches("<root>(\\s*<data[^>]*(></data>|/>))+\\s*</root>"), res)
    }
  }

  test("format no root attr") {
    new SetupAttribute {
      val nGen= ToXmlAttributes(recordName="data").
                     add("id", idGen).
                     add("born", bornGen)
      val v= nGen.getStrings(1)(0)
      // ?s is dotall-mode, accepts multiline data
      assert(v.matches("(?s)^\\s*<data\\s+id.*(/>|</data>)\\s*$"), "|" + v + "|")
    }
  }

  test("format no root elem") {
    new SetupElement {
      val nGen= ToXmlElements(recordName="data").
                     add("id", idGen).
                     add("born", bornGen)
      val v= nGen.getStrings(1)(0)
      // ?s is dotall-mode, accepts multiline data
      assert(v.matches("(?s)^\\s*<data>.*</id>.*</data>\\s*$"), "|" + v + "|")
    }
  }

  test("keepNulls elem") {
    new SetupElement {
      val gen = ToXmlElements(recordName = "data", nulls = KeepNull).
          add("id", SomeNulls(1, idGen))
      val res = gen.get(1)(0).toString()
      assert(res.matches("(?s)^\\s*<data>\\s*<id>\\s*null\\s*</id>\\s*</data>\\s*$"), res)
    }
  }
  test("keepNulls attr") {
    new SetupAttribute {
      val gen = ToXmlAttributes(recordName = "data", nulls = KeepNull).
          add("id", SomeNulls(1, idGen))
      val res = gen.get(1)(0).toString()
      assert(res.matches("(?s)^\\s*<data\\s*id=.null.\\s*(/>|></data>)\\s*$"), res)
    }
  }

  test("skipNulls elem") {
    new SetupElement {
      val gen = ToXmlElements(recordName = "data", nulls = SkipNull).
          add("id", SomeNulls(1, idGen))
      val res = gen.get(1)(0).toString()
      assert(res.matches("(?s)^\\s*(<data>\\s*</data>|<data/>)\\s*$"), res)
    }
  }
  test("skipNulls attr") {
    new SetupAttribute {
      val gen = ToXmlAttributes(recordName = "data", nulls = SkipNull).
          add("id", SomeNulls(1, idGen))
      val res = gen.get(1)(0).toString()
      assert(res.matches("(?s)^\\s*(<data>\\s*</data>|<data/>)\\s*$"), res)
    }
  }

  test("emptyNulls elem") {
    new SetupElement {
      val gen = ToXmlElements(recordName = "data", nulls = EmptyNull).
          add("id", SomeNulls(1, idGen))
      val res = gen.get(1)(0).toString()
      assert(res.matches("(?s)^\\s*<data>\\s*(<id>\\s*</id>|<id/>)\\s*</data>\\s*$"), res)
    }
  }
  test("emptyNulls attr") {
    new SetupAttribute {
      val gen = ToXmlAttributes(recordName = "data", nulls = EmptyNull).
          add("id", SomeNulls(1, idGen))
      val res = gen.get(1)(0).toString()
      assert(res.matches("(?s)^\\s*<data\\s*id=..\\s*(/>|></data>)\\s*$"), res)
    }
  }

  test("quoting elem") {
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
  test("quoting attr") {
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
