package no.mesan.testdatagen.recordgen

import no.mesan.testdatagen.{Generator, GeneratorImpl}

abstract class DataRecordGenerator[T](nulls: NullHandler) extends GeneratorImpl[T] {
  type DataRecord= List[(String,String)]
  protected var fields: List[DataField]= List()
  protected def fieldNames: List[String]= fields map(_.name) reverse

  def add(df: DataField): this.type = {
    fields= df :: fields
    this
  }

  def add(fieldName: String, gen:Generator[_]): this.type =
    add(new DataField(fieldName, gen))

  def toFile(fileName: String): Generator[T]= ToFile(fileName, this, false)

  def appendToFile(fileName: String): Generator[T]= ToFile(fileName, this, true)

  protected def getRecords(n: Int, recordNulls:NullHandler): List[DataRecord] = {
    val fieldList= fields.reverse
    fieldList.map(_.getTuples(n, recordNulls)).transpose
  }
}

abstract class StringRecordGenerator(nulls: NullHandler)
   extends DataRecordGenerator[String](nulls) {
  protected def recordPrefix: String= ""
  protected def recordSuffix: String= ""

  protected def NL= util.Properties.lineSeparator

  protected def makeFields(rec: DataRecord): String

  override def get(n: Int): List[String] = {
    require(fields.size>0, "at least one generator must be given")
    getRecords(n, nulls).map(rec=> recordPrefix + makeFields(rec) + recordSuffix )
  }
}

sealed trait NullHandler
case object EmptyNull extends NullHandler
case object SkipNull extends NullHandler
case object KeepNull extends NullHandler

case class DataField(name: String, generator: Generator[_])  {
  def prefix: String = ""
  def suffix: String= ""
  def transform(s: String): String= s
  def getTuples(n:Int, nulls:NullHandler): List[(String,String)] =
    generator.getStrings(n).map(s=> if (s==null) nulls match {
        case EmptyNull => (name, prefix + suffix)
        case KeepNull=> (name, null)
        case SkipNull => null
      }
    else (name, prefix + transform(s) + suffix))
}

class SingleQuoteWithEscapeDataField(name: String, generator: Generator[_])
      extends DataField(name, generator) {
  override def prefix: String = "'"
  override def suffix: String = "'"
  override def transform(s: String): String = if (s==null) null else s.replaceAll("[']", """\\'""")
}

class DoubleQuoteWithEscapeDataField(name: String, generator: Generator[_])
      extends DataField(name, generator) {
  override def prefix: String = "\""
  override def suffix: String = "\""
  override def transform(s: String): String = if (s==null) null else s.replaceAll("[\"]", """\\\"""")
}


