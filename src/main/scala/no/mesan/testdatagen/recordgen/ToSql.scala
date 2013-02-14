package no.mesan.testdatagen.recordgen

import scala.collection.immutable.List
import no.mesan.testdatagen.Generator

class ToSql(tableName: String, exec: String) extends StringRecordGenerator(KeepNull) {
  override protected def recordPrefix =
    "insert into " + tableName + " (" + fieldNames.mkString(", ") + ") values ("

  override protected def recordSuffix = ")" + exec

  override protected def makeFields(rec: DataRecord): String = rec.map(_._2).mkString(", ")

  def addQuoted(fieldName: String, gen: Generator[_]): ToSql.this.type = {
    add(new SingleQuoteWithEscapeDataField(fieldName, gen))
    this
  }
}

object ToSql {
  def apply(tableName: String, exec: String=";"): ToSql = new ToSql(tableName, exec)
}
