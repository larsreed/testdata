package no.mesan.testdatagen.recordgen

import no.mesan.testdatagen.Generator

/**
 * No prize for guessing the output format from this generator...
 *
 * There are two different add methods, the familiar add method, and a similar
 * addQuoted, the latter should be used for any values that need double-quoted
 * output (almost anything but ints, booleans and nested JSON; nulls are not quoted).
 *
 * The apply method has 3 parameters:
 * 1. header: This is the label for each record (ignored if bare, see below)
 * 2. bare: This is intended for nesting JSON-generators.
 *    If you want to generate embedded records, use bare=true for the inner generators
 * 3. null handler
 *
 * @author lre
 */
class ToJson(nulls:NullHandler, header:String, bare:Boolean) extends StringRecordGenerator(nulls) {

  /** Add a field that needs quoting of values. */
  def addQuoted(fieldName: String, gen: Generator[_]): this.type = {
    add(new DoubleQuoteWithEscapeDataField(fieldName, gen))
    this
  }

  override def recordPrefix: String =
    s"$newline  " + (if (!bare) s""""$header": """ else "") + " { "
  override def recordSuffix: String =
    s"$newline  }" + (if (bare) "" else ",")

  override def get(n:Int): List[String] = {
    val orgVals= super.get(n)
    if (bare) orgVals
    else List("{") :::
         (orgVals(n-1).replaceAll(",$", "") :: orgVals.take(n-1).reverse).reverse :::
         List(newline + "}")
  }

  override def makeFields(rec: DataRecord): String=
    rec.foldLeft(""){
      (str, rec)=> rec match {
        case null => str
        case (key, null) => str + (if (str>"") "," else "") +
                             s"""$newline    "$key": null"""
        case (key, value) => str + (if (str>"") "," else "") +
                             s"""$newline    "$key": $value"""
     }
    }
}

object ToJson {
  def apply(header:String="", bare:Boolean=false, nulls: NullHandler= KeepNull): ToJson=
    new ToJson(nulls, header, bare)
}
