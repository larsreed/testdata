package net.kalars.testgen.recordgen

import net.kalars.testgen.Generator

class WikiDataField(name: String, generator: Generator[_])
      extends DataField(name, generator) {
  override def transform(s: String): String =
    if (s==null) null
    else s.replaceAll("[|]", "\\\\|").
           replaceAll("[\n\r]+" , """\\\\""")
}

class ToWiki() extends StringRecordGenerator(EmptyNull) {
  override protected def recordPrefix = "| "
  override protected def recordSuffix = " |"

  override protected def makeFields(rec: DataRecord): String = rec.map(_._2).mkString(" | ")

  override def add(fieldName: String, gen: Generator[_]): this.type = {
    add(new WikiDataField(fieldName, gen))
    this
  }

  override def get(n: Int): List[String] =
    ("|| " + fieldNames.mkString(" || ") + " ||") :: super.get(n)
}

object ToWiki {
  def apply(): ToWiki = new ToWiki()
}
