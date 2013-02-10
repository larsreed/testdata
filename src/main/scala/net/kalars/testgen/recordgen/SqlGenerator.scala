package net.kalars.testgen.recordgen

import scala.collection.immutable.List
import net.kalars.testgen.Generator

class SqlGenerator(tableName: String, exec: String) extends StringRecordGenerator(KeepNull) {
  override protected def recordPrefix =
    "insert into " + tableName + " (" + fieldNames.mkString(", ") + ") values ("

  override protected def recordSuffix = ")" + exec

  override protected def makeFields(rec: DataRecord): String = rec.map(_._2).mkString(", ")

  def addQuoted(fieldName: String, gen: Generator[_]): this.type = {
    add(new SingleQuoteWithEscapeDataField(fieldName, gen))
    this
  }
}

object SqlGenerator {
  def apply(tableName: String, exec: String=";"): SqlGenerator = new SqlGenerator(tableName, exec)
}
