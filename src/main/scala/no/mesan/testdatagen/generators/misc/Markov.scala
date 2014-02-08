package no.mesan.testdatagen.generators.misc

import no.mesan.testdatagen.Generator
import no.mesan.testdatagen.utils.IO
import scala.util.Random

/**
 * Simple Markov Chain generator.
 * Generates "gibberish" based on one or more input files.
 * A default input file (parameterless apply method) is provided containing text from
 * http://www.regjeringen.no/nb/dep/fad/dok/regpubl/stmeld/2008-2009/stmeld-nr-19-2008-2009-,
 * any file(s) may be read and concatenated to provide a basis.
 * The get methods return a string containing N words randomly selected from
 * the input.
 */
class Markov extends Generator[String] {

  type WordMap= Map[String, List[String]]

  var words: WordMap= Map[String, List[String]]()

  def build(wordList: List[String]): WordMap= {
    def build(map: WordMap, list: List[String]): WordMap =
      list match {
        case Nil => map
        case first :: Nil =>
          val occ= map.getOrElse(first, List[String]())
          map + (first->occ)
        case first :: next :: _ =>
          val occ= map.getOrElse(first, List[String]())
          build(map + (first -> (next :: occ)), list.tail)
      }
    build(words, wordList)
  }

  private def readFiles(files: List[String]): List[String] =
    for {file <- files
         line <- IO.sourceLines(IO.fileAsStream(file))
         word <- line.split("\\s+")}
        yield word

  def buildFrom(files: List[String]): this.type = {
    words= build(readFiles(files))
    this
  }

  def buildFromList(wordList: List[String]): this.type = {
    words= build(wordList)
    this
  }

  /** The main function - provide a list of n entries. */
  def get(n: Int): List[String] = {
    require(n>=0, "n cannot be negative")
    require(words.size > 0, "must load words")
    def selectNext(from: List[String]): String= from match {
      case Nil =>
        selectNext(words.keys.toList)
      case _ =>
        val n= Random.nextInt(from.length)
        from(n)
    }
    def getNext(word:String, soFar: List[String]): List[String] =
      if (soFar.length>=n) soFar
      else getNext(selectNext(words(word)), word::soFar)
    getNext(selectNext(List()), List()).reverse
  }

  override def getStrings(n: Int): List[String] = {
    val res= get(n)
    res.reduceLeft((total, word)=> total + " " + word) :: res.tail
  }

  override def filter(f: (String) => Boolean) = throw new UnsupportedOperationException("filter")
  override def formatWith(f: (String) => String) = throw new UnsupportedOperationException("formatWith")
  override def formatOne[S>:String](v: S): String = "%s".format(v)
}

object Markov {
  /** A list of files to read. */
  val inputFiles= List("markov.txt")

  def apply(): Markov = apply(inputFiles)
  def apply(fileList: List[String]): Markov = new Markov().buildFrom(fileList)
}