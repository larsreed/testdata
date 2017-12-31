package no.netcompany.testdatagen.recordgen

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import scala.xml.NodeSeq
import scala.xml.NodeSeq.seqToNodeSeq

/**
 * Generate HTML, either a simple table or a complete page (the latter if a title is given).
 * The get method returns NodeSeqs, while getStrings
 * converts the result to a somewhat indented string format...
 */
class ToHtml(pageTitle: String, nulls:NullHandler) extends XmlGenerator("", nulls) {

  override def get(n: Int): List[NodeSeq]= {
    val titles= fieldNames.map(s=> <th>{s}</th>)
    val xml= super.get(n)
    val html= <table border="border">
        <tr>{titles}</tr>
      {xml}
    </table>

    if ("" != pageTitle)
List(<html>
  <head>
    <title>{pageTitle}</title>
  </head>
  <body>
    <h1>{pageTitle}</h1>
    {html}
  </body>
</html>)
    else
      List(html)
  }

  override def getStream: Stream[NodeSeq] = {
    require(fields.nonEmpty, "at least one  must be given")

    def getRecord(rec: DataRecord)=  Seq[NodeSeq] {
      rec.map{
        case (tag, null)=> nulls match {
          case EmptyNull => <td>&nbsp;</td>
          case KeepNull=> <td>null</td>
          case SkipNull => null
        }
        case (tag,value)=> <td>{value}</td>
      }
    }
    val data= genRecords(KeepNull)
    data.map{ case nodes =>
    <tr>
      {getRecord(nodes)}
    </tr>}
  }
}
object ToHtml {
  def apply(pageTitle: String="", nulls:NullHandler = EmptyNull): ToHtml =
    new ToHtml(pageTitle, nulls)
}
