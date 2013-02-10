package net.kalars.testgen.recordgen

import scala.xml.{Attribute, NodeSeq, Null, Text}
import net.kalars.testgen.Generator
import scala.xml.NodeSeq

/**
 * Generate HTML, either a simple table or a complete page (the latter if a title is given).
 * The get method returns NodeSeqs, while getStrings
 * converts the result to a somewhat indented string format...
 */
class HtmlGenerator(pageTitle: String, nulls:NullHandler) extends XmlGenerator(nulls) {

  override def get(n: Int): List[NodeSeq] = {
    require(fields.size>0, "at least one generator must be given")
    def getRecord(rec: DataRecord)=  List[NodeSeq] {
      rec.map{
        case (tag, null)=> nulls match {
          case EmptyNull => <td>&nbsp;</td>
          case KeepNull=> <td>null</td>
          case SkipNull => null
        }
        case (tag,value)=> <td>{value}</td>
      }
    }
    val data= getRecords(n, KeepNull)
    val titles= fieldNames.map(s=> <th>{s}</th>)
    val xml= <table border="border">
    <tr>{titles}</tr>
    {data.map{ case nodes =>
    <tr>
      {getRecord(nodes)}
    </tr>}}</table>
    if ("" != pageTitle) List(<html>
  <head>
    <title>{pageTitle}</title>
  </head>
  <body>
    <h1>{pageTitle}</h1>
    {xml}
  </body>
</html>)
    else List(xml)
  }
}
object HtmlGenerator {
  def apply(pageTitle: String="", nulls:NullHandler=EmptyNull): HtmlGenerator =
    new HtmlGenerator(pageTitle, nulls)
}
