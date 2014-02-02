package no.mesan.testdatagen.recordgen

import no.mesan.testdatagen.{Generator, GeneratorImpl}
import java.util.regex.Pattern
import scala.language.postfixOps

/** 
 * The base class for record generators.
 *
 * @author lre
 */
abstract class DataRecordGenerator[T](nulls: NullHandler) extends GeneratorImpl[T] {
  /** An abstract record is a list of (name,value)-pairs. */
  type DataRecord= List[(String,String)]
  /** Mutable list of fields (in reverse order). */
  protected var fields: List[DataField]= List()
  /** Just the field names (in correct order). */
  protected def fieldNames: List[String]= (fields map (_.name)).reverse

  /** Add a new data field, instantiated outside. */
  def add(df: DataField): this.type = {
    fields= df :: fields
    this
  }

  /** Add a new data field with a given name and generator. */
  def add(fieldName: String, gen:Generator[_]): this.type =
    add(new DataField(fieldName, gen))

  /** Return a ToFile that overwrites its result. */
  def toFile(fileName: String, charSet:String=ToFile.defaultCharSet): Generator[T]= 
    ToFile(fileName, this, append = false, charSet)

  /** Return a ToFile that appends to its result. */
  def appendToFile(fileName: String, charSet:String=ToFile.defaultCharSet): Generator[T]= 
    ToFile(fileName, this, append = true, charSet)

  /** Return a list of n records. Each record consists of 1 value for each field. */
  protected def getRecords(n: Int, recordNulls:NullHandler): List[DataRecord] = {
    val fieldList= fields.reverse
    fieldList.map(_.getTuples(n, recordNulls)).transpose
  }
}

 /**
  * A specialized version for string handling.
 *
 * @author lre
  */
abstract class StringRecordGenerator(nulls: NullHandler)
   extends DataRecordGenerator[String](nulls) {
  protected def recordPrefix: String= ""
  protected def recordSuffix: String= ""

  /** Platform line ending. */
  protected def newline= util.Properties.lineSeparator

  /**
   * Implement this method to convert the list of fields in one record
   * to a result string.
   */
  protected def makeFields(rec: DataRecord): String

  /** Convert the fields, and add pre/suffix. */
  override def get(n: Int): List[String] = {
    require(fields.size>0, "at least one generator must be given")
    getRecords(n, nulls).map(rec=> recordPrefix + makeFields(rec) + recordSuffix )
  }
}

/** How to handle null values. */
sealed trait NullHandler
/** Include null values as an empty string/element/..., e.g. <foo/> in an XML record.. */
case object EmptyNull extends NullHandler
/** Exclude null fields entirely. */
case object SkipNull extends NullHandler
/** Include null fields with an explicit "null" representation. */
case object KeepNull extends NullHandler

/** 
 * Representation of one of the fields in a record -- with a name and a generator.
 *
 * @author lre
 */
case class DataField(name: String, generator: Generator[_])  {
  /** Added before value. */
  def prefix: String = ""
  /** Added after value. */
  def suffix: String= ""
  /** Transform the result -- default no-op. */
  def transform(s: String): String= s

  /**
   * Returns a list of (name, value) (null are treated as either
   * (name,  prefix+suffix), (name, null) or null, according to NullHandler.
   */
  def getTuples(n:Int, nulls:NullHandler): List[(String,String)] =
    generator.getStrings(n).map(s=> if (s==null) nulls match {
        case EmptyNull => (name, prefix + suffix)
        case KeepNull=> (name, null)
        case SkipNull => null
      }
    else (name, prefix + transform(s) + suffix))
}

/**
 * Data fields pre/suffixed with single quotes, single quotes within
 * the value are escaped with a backslash.
 *
 * @author lre
 */
class SingleQuoteWithEscapeDataField(name: String, generator: Generator[_])
      extends DataField(name, generator) {
  override def prefix: String = "'"
  override def suffix: String = "'"
  override def transform(s: String): String =
    if (s==null) null else s.replaceAll(Pattern.quote("\\"), "\\\\\\\\")
                            .replaceAll("[']", """\\'""")
}

/**
 * Data fields pre/suffixed with double quotes, backslashes and double quotes within
 * the value are escaped with a backslash.
 *
 * @author lre
 */
class DoubleQuoteWithEscapeDataField(name: String, generator: Generator[_])
      extends DataField(name, generator) {
  override def prefix: String = "\""
  override def suffix: String = "\""
  override def transform(s: String): String = 
    if (s==null) null else s.replaceAll(Pattern.quote("\\"), "\\\\\\\\")
                            .replaceAll("[\"]", """\\\"""")
}
