package no.mesan.testdatagen.generators

import no.mesan.testdatagen.{StreamGeneratorImpl, ExtendedDelegate, ExtendedGenerator}
import no.mesan.testdatagen.utils.IO

/**
 * This generator reads lines from an input file and creates a list of values,
 * from which a delegate FromList can take its values. The values may be typed
 * (does not currently work as expected...), even though they are read as strings.
 *
 * Special methods: from/to/unique -- not supported
 * Default limits: n/a
 *
 * @author lre
 */
class FromFile[T](fileName: String, encoding:String) extends ExtendedGenerator[T]
    with ExtendedDelegate[T, T]
    with StreamGeneratorImpl[T] {

  private val listGen=new FromList[T]()
  protected var generator: ExtendedGenerator[T]= listGen // For the trait

  /** Not supported. */
  override def from(f:T) = throw new UnsupportedOperationException
  /** Not supported. */
  override def to(f:T) = throw new UnsupportedOperationException

  def allFilters: List[T => Boolean]= listGen.allFilters

  def getStream : Stream[T] =  listGen.fromList(getContents).getStream

  override def get(n:Int): List[T]= super[StreamGeneratorImpl].get(n)

  private def getContents: List[T] = {
    val source = IO.fileAsStream(fileName, encoding).getLines
    var res: List[T]= Nil
    var i= 0
    while (source.hasNext) {
      val s= source.next
      val v = s.asInstanceOf[T] // Does not actually work :-(
      res ::= v
      i+=1
    }
    res.reverse
  }
}

object FromFile {
  // Only String is actually working at the moment
  def apply(resourceName: String,  encoding: String= "UTF-8"): FromFile[String] =
    new FromFile[String](resourceName, encoding)
}
