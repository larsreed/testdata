package no.mesan.testdatagen.recordgen

import no.mesan.testdatagen.Generator
import java.util.regex.Pattern

/**
 * Data fields pre/suffixed with a given delimiter, the same delimiter within
 * the value are escaped with a backslash.
 *
 * @author lre
 */
class DelimitedDataField(name: String, generator: Generator[_], delimiter:String)
      extends DataField(name, generator) {
  override def prefix: String = delimiter
  override def suffix: String = delimiter
  override def transform(s: String): String =
    if (s==null) null
    else s.replaceAll(Pattern.quote("\\"), "\\\\\\\\")
          .replaceAll(Pattern.quote(delimiter), "\\\\" + delimiter)
}

/**
 * This generator produces values separated by a comma (or another delimiter,
 * e.g. TAB), values are pre- and suffixed with a delimiter, by default a double quote.
 * By default, the first record contains field names (which can be excluded).
 */
class ToCsv(withHeaders:Boolean, delimiter:String, separator:String)
      extends StringRecordGenerator(EmptyNull) {
  override protected def makeFields(recs: DataRecord): String =
    recs.map(_._2).mkString(",")

  override def add(fieldName: String, gen: Generator[_]): this.type = {
    add(new DelimitedDataField(fieldName, gen, delimiter))
    this
  }

  override def get(n: Int): List[String] = {
    if (withHeaders)
      (delimiter + fieldNames.mkString(delimiter + separator + delimiter) + delimiter) :: super.get(n)
    else super.get(n)
  }
}

object ToCsv {
  def apply(withHeaders: Boolean=true,
            delimiter:String= "\"",
            separator:String= ","): ToCsv = new ToCsv(withHeaders, delimiter, separator)
}
