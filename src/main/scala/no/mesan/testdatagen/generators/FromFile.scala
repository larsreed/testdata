package no.mesan.testdatagen.generators

import java.io.FileNotFoundException

import scala.io.Source

import no.mesan.testdatagen.{ExtendedDelegate, ExtendedGenerator}

/**
 * Generate values based on lists.
 * Special methods: from/to/unique -- not supported
 * Default limits: n/a
 */
class FromFile[T](fileName: String, encoding:String) extends ExtendedGenerator[T]
  with ExtendedDelegate[T, T] {

  private var listGen=new FromList[T]()
  var generator: ExtendedGenerator[T]= listGen // For the trait

  override def from(f:T) = throw new UnsupportedOperationException
  override def to(f:T) = throw new UnsupportedOperationException

  private var filterFuns: List[T => Boolean] = List((t => true))
  override def filter(f: T => Boolean): this.type = { filterFuns ::= f; this }

  private var readAll= false
  def allLines(all:Boolean=true): this.type = { readAll= all; this }

  private def fileAsStream(fileName:String) = {
    try {
      Source.fromFile(fileName, encoding)
    }
    catch {
      case ugh: FileNotFoundException =>
        Source.fromInputStream(getClass.getClassLoader().getResourceAsStream(fileName), encoding)
    }
  }

  private def getContents(n:Int): List[T] = {
    val source = fileAsStream(fileName).getLines
    var res: List[T]= Nil
    var i= 0
    while (source.hasNext && (i<n || readAll)) {
      val s= source.next
      val v = s.asInstanceOf[T] // Does not actually work :-(
      if (filterFuns.forall(f=> f(v))) {
        res ::= v
        i+=1
      }
    }
    res.reverse
  }

  override def get(n: Int): List[T] = listGen.fromList(getContents(n)).get(n)
  override def getStrings(n: Int): List[String] = listGen.fromList(getContents(n)).getStrings(n)
}

object FromFile {
  // Only String is actually working at the moment
  def apply(resourceName: String, allLines:Boolean=false,
      encoding: String= "ISO-8859-1"): FromFile[String] =
    (new FromFile[String](resourceName, encoding)).allLines(allLines)
}
