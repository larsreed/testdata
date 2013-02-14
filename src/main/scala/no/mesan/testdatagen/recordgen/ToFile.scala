package no.mesan.testdatagen.recordgen

import java.io.{File, PrintWriter}

import no.mesan.testdatagen.Generator

class ToFile[T](fileName:String, 
                         generator: Generator[T], 
                         append:Boolean, 
                         charSet:String) extends Generator[T] {
  
  require(!append, "TODO!!!!!!!!!!")

  def printToFile(f: java.io.File)(op: PrintWriter => Unit) {
    val p = new PrintWriter(f, charSet)
    try { op(p) } finally { p.close() }
  }

  protected def toFile(list: List[String]) {
    printToFile(new File(fileName))(p => {
      list.foreach(p.println)
    })
  }

  /** Get the next n entries. */
  override def get(n: Int): List[T]= {
    var res= generator.get(n)
    toFile(res map {_.toString})
    res
  }

  /** Get n entries converted to strings and formatted. */
  override def getStrings(n: Int): List[String]= {
    var res= generator.getStrings(n)
    toFile(res)
    res
  }

  override def filter(f: T => Boolean): this.type= { generator.filter(f); this }
  override def formatWith(f: T => String): this.type= { generator.formatWith(f); this }
}

object ToFile {
  def apply[T](fileName:String, 
               generator: Generator[T], 
               append:Boolean=false, 
               charSet:String="ISO-8859-1"): ToFile[T]=
    new ToFile(fileName, generator, append, charSet)
}
