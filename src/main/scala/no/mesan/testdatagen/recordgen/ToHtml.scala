package no.mesan.testdatagen.recordgen

import scala.xml.NodeSeq
import scala.xml.NodeSeq.seqToNodeSeq

/**
 * Generate HTML, either a simple table or a complete page (the latter if a title is given).
 * The get method returns NodeSeqs, while getStrings
 * converts the result to a somewhat indented string format...
 *
 * @author lre
 */
class ToHtml(pageTitle: String, nulls:NullHandler) extends XmlGenerator(nulls) {

  override def get(n: Int): List[NodeSeq] = {
    require(fields.size>0, "at least one  must be given")

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
object ToHtml {
  def apply(pageTitle: String="", nulls:NullHandler=EmptyNull): ToHtml =
    new ToHtml(pageTitle, nulls)
}
