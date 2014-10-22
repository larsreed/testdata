package no.mesan.testdatagen.recordgen

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import java.util.regex.Pattern

import no.mesan.testdatagen.Generator

/** A data field with special handling of pipes and line feeds. */
class WikiDataField(name: String, generator: Generator[_])
      extends DataField(name, generator) {
  override def transform(s: String): String =
    if (s==null) null
    else s.replaceAll("([|$~!])", """\\$1""")
          .replaceAll("[]]", """\\]""")
          .replaceAll("""\[""", """\\[""")
          .replaceAll("[\n\r]" , """ \\\\""")
}

/** Outputs data as a wiki table (Confluence wiki markup). */
class ToWiki() extends StringRecordGenerator(EmptyNull) {
  override protected def recordPrefix = "| "
  override protected def recordSuffix = " |"

  override protected def makeFields(rec: DataRecord): String = rec.map(_._2).mkString(" | ")

  override def add(fieldName: String, gen: Generator[_]): this.type = {
    add(new WikiDataField(fieldName, gen))
    this
  }

  override def get(n: Int): List[String] =
    s"|| ${fieldNames.mkString(" || ")} ||" :: super.get(n)
}

object ToWiki {
  def apply(): ToWiki = new ToWiki()
}
