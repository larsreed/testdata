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

  protected var prefix= List[String]()
  protected var suffix= List[String]()

  def prepend(s: String):this.type = {
    prefix::= s
    this
  }

  def append(s: String):this.type = {
    suffix::= s
    this
  }

  /** Writes a list of strings to a named file. */
  protected def toFile(list: List[String]) {
    val writer = new OutputStreamWriter(
                   new FileOutputStream(fileName, append),
                   Charset.forName(charSet).newEncoder())
    val bufWriter= new BufferedWriter(writer)
    try {
      def out(s: String) {
        bufWriter.append(s)
        bufWriter.newLine
      }
      prefix.reverse.foreach {s => out(s)}
      list.foreach{s=> out(s)}
      suffix.reverse.foreach {s => out(s)}
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
  override def formatOne[S >: T](v: S): String = generator.formatOne(v)
}

object ToFile {
  val defaultCharSet="ISO-8859-1"

  def apply[T](fileName:String,
               generator: Generator[T],
               append:Boolean=false,
               charSet:String=defaultCharSet): ToFile[T]=
    new ToFile(fileName, generator, append, charSet)
}
