package no.mesan.testdatagen.recordgen

import java.io.{BufferedWriter, FileOutputStream, IOException, OutputStreamWriter}
import java.nio.charset.Charset

import no.mesan.testdatagen.Generator

/**
 * This generator is typically the end of a chain, and called implicitly
 * by either toFile or appendToFile from a record generator,
 * but may also be called through the apply method.
 *
 * @author lre
 */
class ToFile[T](fileName:String,
                         generator: Generator[T],
                         append:Boolean,
                         charSet:String) extends Generator[T] {

  /** Writes a list of strings to a named file. */
  protected def toFile(list: List[String]) {
    val writer = new OutputStreamWriter(
                   new FileOutputStream(fileName, append), 
                   Charset.forName(charSet).newEncoder())
    val bufWriter= new BufferedWriter(writer)
    try {
      list.foreach{ s=>
        bufWriter.append(s)
        bufWriter.newLine
      }
    }
    catch {
      case e: IOException => println("Error: " + e)
    }
    finally {
      bufWriter.close
    }
  }

  override def get(n: Int): List[T]= {
    var res= generator.get(n)
    toFile(res map {_.toString})
    res
  }

  override def getStrings(n: Int): List[String]= {
    var res= generator.getStrings(n)
    toFile(res)
    res
  }

  override def filter(f: T => Boolean): this.type= { generator.filter(f); this }
  override def formatWith(f: T => String): this.type= { generator.formatWith(f); this }
}

object ToFile {
  val defaultCharSet="ISO-8859-1"

  def apply[T](fileName:String,
               generator: Generator[T],
               append:Boolean=false,
               charSet:String=defaultCharSet): ToFile[T]=
    new ToFile(fileName, generator, append, charSet)
}
