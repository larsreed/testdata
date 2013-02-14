package no.mesan.testdatagen.recordgen

import no.mesan.testdatagen.Generator
import java.util.regex.Pattern

class ToJson(nulls:NullHandler, header:String) extends StringRecordGenerator(nulls) {

  def addQuoted(fieldName: String, gen: Generator[_]): this.type = {
    add(new DoubleQuoteWithEscapeDataField(fieldName, gen))
    this
  }

  override def recordPrefix = NL + "  " + (if (header> "") "\"" + header + "\": " else "") + " { "
  override def recordSuffix = NL + "  }"

  override def get(n:Int) = List("{") ::: super.get(n) ::: List((NL + "}"))

  override def makeFields(rec: DataRecord): String=
    rec.foldLeft(""){
      (str, rec)=> rec match {
        case null => str
        case (key, null) => str + (if (str>"") "," else "") + NL +
                             "    \"" + key + "\": null"
        case (key, value) => str + (if (str>"") "," else "") + NL +
                             "    \"" + key + "\": " + value
     }
    }
}

object ToJson {
  def apply(header: String="", nulls: NullHandler= KeepNull): ToJson=
    new ToJson(nulls, header)
}
