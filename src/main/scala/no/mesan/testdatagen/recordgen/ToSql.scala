package no.mesan.testdatagen.recordgen

import scala.collection.immutable.List
import no.mesan.testdatagen.Generator

/**
 * Often, you will need to put test data into a data base.
 * This generator tries to help you...  It generates records of the form
 * "insert into tableName (field1, field2,...) values (value1, value2, ...);"
 *
 * To facilitate quoting, you must call the alternative addQuoted method for values
 * that need quotes -- they are then single quoted (and embedded single quotes escaped):
 *
 * The apply method needs to know the table name, you may optionally use a record
 * separator different from ";".
 *
 * @author lre
 */
class ToSql(tableName: String, exec: String) extends StringRecordGenerator(KeepNull) {
  override protected def recordPrefix =
    "insert into " + tableName + " (" + fieldNames.mkString(", ") + ") values ("

  override protected def recordSuffix = ")" + exec

  override protected def makeFields(rec: DataRecord): String = rec.map(_._2).mkString(", ")

  def addQuoted(fieldName: String, gen: Generator[_]): ToSql.this.type = {
    add(new SingleQuoteWithDoubleEscapeDataField(fieldName, gen))
    this
  }
}

object ToSql {
  def apply(tableName: String, exec: String=";"): ToSql = new ToSql(tableName, exec)
  def sybase(tableName: String): ToSql= apply(tableName, "\ngo")
}
