package no.mesan.testdatagen.recordgen

import no.mesan.testdatagen.Generator
import java.util.regex.Pattern

class ToJson(nulls:NullHandler, header:String, bare:Boolean) extends StringRecordGenerator(nulls) {

  def addQuoted(fieldName: String, gen: Generator[_]): this.type = {
    add(new DoubleQuoteWithEscapeDataField(fieldName, gen))
    this
  }

  override def recordPrefix = NL + "  " + (if (!bare) "\"" + header + "\": " else "") + " { "
  override def recordSuffix = NL + "  }" + (if (bare) "" else ",")

  override def get(n:Int) = {
    val orgVals= super.get(n)
    if (bare) orgVals
    else List("{") :::
         (orgVals(n-1).replaceAll(",$", "") :: orgVals.take(n-1).reverse).reverse :::
         List((NL + "}"))
  }

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
  def apply(header:String="", bare:Boolean=false, nulls: NullHandler= KeepNull): ToJson=
    new ToJson(nulls, header, bare)
}
