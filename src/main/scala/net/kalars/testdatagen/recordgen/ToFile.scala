package net.kalars.testdatagen.recordgen

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import net.kalars.testdatagen.BareGenerator

import java.io.{BufferedWriter, FileOutputStream, IOException, OutputStreamWriter}
import java.nio.charset.Charset

/**
 * This generator is typically the end of a chain, and called implicitly
 * by either toFile or appendToFile from a record generator,
 * but may also be called through the apply method.
 */
class ToFile[T](fileName:String,
                generator: BareGenerator[T],
                append:Boolean,
                charSet:String) extends BareGenerator[T] {

  protected var prefix: List[String] = List[String]()
  protected var suffix: List[String] = List[String]()

  def prepend(s: String):this.type = {
    prefix::= s
    this
  }

  def append(s: String):this.type = {
    suffix::= s
    this
  }

  /** Writes a list of strings to a named file. */
  protected def writeToFile(list: List[String]) {
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
    finally bufWriter.close
  }

  override def get(n: Int): List[T]= {
    val res= generator.get(n)
    writeToFile(res map {_.toString})
    res
  }

  override def getStrings(n: Int): List[String]= {
    val res= generator.getStrings(n)
    writeToFile(res)
    res
  }

  // Nicer name...
  def write(n:Int, strings:Boolean= true): List[Any] = if (strings) getStrings(n) else get(n)
}

object ToFile {
  val defaultCharSet="ISO-8859-1"

  def apply[T](fileName:String,
               generator: BareGenerator[T],
               append:Boolean=false,
               charSet:String=defaultCharSet): ToFile[T]=
    new ToFile(fileName, generator, append, charSet)
}