package no.mesan.testdatagen.recordgen

import scala.xml.{Attribute, NodeSeq, Null, PrettyPrinter, Text}

/**
 * Generate XML, either a record sequence or records collected under
 * one common root.  The get method returns NodeSeqs, while getStrings
 * converts the result to a somewhat indented string format...
 *
 * @author lre
 */
abstract class XmlGenerator(nulls:NullHandler) extends DataRecordGenerator[NodeSeq](nulls) {
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
 *
 * @author lre
 */
class ToXmlAttributes(rootName: String, recordName: String,
    nulls:NullHandler) extends XmlGenerator(nulls) {

  override def get(n: Int): List[NodeSeq] = {
    require(fields.size>0, "at least one generator must be given")
    def getRecord(rec: DataRecord): List[Attribute]= {
      rec.map{
        case (attr, null)=> nulls match {
          case EmptyNull => Attribute(attr, Text(""), Null)
          case KeepNull=> Attribute(attr, Text("null"), Null)
          case SkipNull => null
        }
        case (attr,value)=>  Attribute(attr, Text(value), Null)
      }
    }
    val data= getRecords(n, KeepNull)
    val xml= data.map{ case nodes =>
      val elem=
<xml/>.copy(label=recordName)
       (elem /: getRecord(nodes)) {
         case (elem, null) => elem
         case (elem, attr) => elem % attr
       } }
    if (rootName!="") List(<xml>
  {xml}
</xml>.copy(label=rootName))
    else xml
  }
}

/**
 * Generate XML, either a record sequence or records collected under
 * one common root.  The get method returns NodeSeqs, while getStrings
 * converts the result to a somewhat indented string format...
 *
 * Possible extension: generate attributes, not elements
 *
 * @author lre
 */
class ToXmlElements(rootName: String, recordName: String,
    nulls:NullHandler) extends XmlGenerator(nulls) {

  override def get(n: Int): List[NodeSeq] = {
    require(fields.size>0, "at least one generator must be given")
    def getRecord(rec: DataRecord): List[NodeSeq]= {
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
    val data= getRecords(n, KeepNull)
    val xml= data.map{ case nodes => <xml>
    {getRecord(nodes)}
  </xml>.copy(label=recordName)}
    if (rootName!="") List(<xml>
  {xml}
</xml>.copy(label=rootName))
    else xml
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
