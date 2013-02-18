package no.mesan.testdatagen.recordgen

import no.mesan.testdatagen.Generator

class ToJson(nulls:NullHandler, header:String, bare:Boolean) extends StringRecordGenerator(nulls) {

  def addQuoted(fieldName: String, gen: Generator[_]): this.type = {
    add(new DoubleQuoteWithEscapeDataField(fieldName, gen))
    this
  }

  override def recordPrefix: String =
    newline + "  " + (if (!bare) "\"" + header + "\": " else "") + " { "
  override def recordSuffix: String =
    newline + "  }" + (if (bare) "" else ",")

  override def get(n:Int): List[String] = {
    val orgVals= super.get(n)
    if (bare) orgVals
    else List("{") :::
         (orgVals(n-1).replaceAll(",$", "") :: orgVals.take(n-1).reverse).reverse :::
         List((newline + "}"))
  }

  override def makeFields(rec: DataRecord): String=
    rec.foldLeft(""){
      (str, rec)=> rec match {
        case null => str
        case (key, null) => str + (if (str>"") "," else "") + newline +
                             "    \"" + key + "\": null"
        case (key, value) => str + (if (str>"") "," else "") + newline +
                             "    \"" + key + "\": " + value
     }
    }
}

object ToJson {
  def apply(header:String="", bare:Boolean=false, nulls: NullHandler= KeepNull): ToJson=
    new ToJson(nulls, header, bare)
}
