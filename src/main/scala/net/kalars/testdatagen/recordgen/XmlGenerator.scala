package net.kalars.testdatagen.recordgen

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import scala.xml.{Attribute, NodeSeq, Null, PrettyPrinter, Text}

/**
 * Generate XML, either a record sequence or records collected under
 * one common root.  The get method returns NodeSeqs, while getStrings
 * converts the result to a somewhat indented string format...
 */
abstract class XmlGenerator(rootName: String, nulls:NullHandler)
  extends DataRecordGenerator[NodeSeq](nulls) {

  override def get(n: Int): List[NodeSeq]= {
    val xml= super.get(n)
    if (rootName!="") List(<xml>
  {xml}
</xml>.copy(label=rootName))
    else xml
  }

  override def getStrings(n: Int): List[String] = {
    val data= get(n)
    val printer= new PrettyPrinter(1000, 3)
    data.map{ printer.formatNodes(_) }
  }
}

/**
 * Generate XML, either a record sequence or records collected under
 * one common root.  The get method returns NodeSeqs, while getStrings
 * converts the result to a somewhat indented string format...
 */
class ToXmlAttributes(rootName: String, recordName: String, nulls:NullHandler)
  extends XmlGenerator(rootName, nulls) {

  override def getStream: Stream[NodeSeq] = {
    require(fields.nonEmpty, "at least one generator must be given")
    def getRecord(rec: DataRecord): List[Attribute]= {
      rec.map{
        case (attr, null)=> nulls match {
          case EmptyNull => Attribute(attr, Text(""), Null)
          case KeepNull=> Attribute(attr, Text("null"), Null)
          case SkipNull => null
        }
        case (attr,value)=>  Attribute(attr, Text(value), Null)
      }.toList
    }
    val data= genRecords(KeepNull)
    data.map { nodes =>
      val elem =
          <xml/>.copy(label = recordName)
      (elem /: getRecord(nodes)) {
        case (x, null) => x
        case (x, attr) => x % attr
      }
    }
  }
}

/**
 * Generate XML, either a record sequence or records collected under
 * one common root.  The get method returns NodeSeqs, while getStrings
 * converts the result to a somewhat indented string format...
 *
 * Possible extension: generate attributes, not elements
 */
class ToXmlElements(rootName: String, recordName: String, nulls:NullHandler)
  extends XmlGenerator(rootName, nulls) {

  override def getStream: Stream[NodeSeq] = {
    require(fields.nonEmpty, "at least one generator must be given")
    def getRecord(rec: DataRecord): Seq[NodeSeq]= {
      rec.map{
        case (tag, null)=> nulls match {
          case EmptyNull => <xml/>.copy(label=tag)
          case KeepNull=> <xml>null</xml>.copy(label=tag)
          case SkipNull => null
        }
        case (tag,value)=>
          <xml>{value}</xml>.copy(label=tag)
      }
    }
    val data= genRecords(KeepNull)
    data.map { nodes =>
      <xml>
        {getRecord(nodes)}
      </xml>.copy(label = recordName)
    }
  }
}

object ToXmlElements {
  def apply(rootName: String="", recordName: String, nulls:NullHandler=EmptyNull): ToXmlElements =
    new ToXmlElements(rootName, recordName, nulls)
}

object ToXmlAttributes {
  def apply(rootName: String="", recordName: String, nulls:NullHandler=EmptyNull): ToXmlAttributes =
    new ToXmlAttributes(rootName, recordName, nulls)
}