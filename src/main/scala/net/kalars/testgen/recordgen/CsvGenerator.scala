package net.kalars.testgen.recordgen

import net.kalars.testgen.Generator
import java.util.regex.Pattern

class DelimitedDataField(name: String, generator: Generator[_], delimiter:String)
      extends DataField(name, generator) {
  override def prefix: String = delimiter
  override def suffix: String = delimiter
  override def transform(s: String): String =
    if (s==null) null
    else s.replaceAll(Pattern.quote(delimiter), "\\\\" + delimiter)
}

class CsvGenerator(withHeaders:Boolean, delimiter:String, separator:String)
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

object CsvGenerator {
  def apply(withHeaders: Boolean=true,
            delimiter:String= "\"",
            separator:String= ","): CsvGenerator = new CsvGenerator(withHeaders, delimiter, separator)
}
