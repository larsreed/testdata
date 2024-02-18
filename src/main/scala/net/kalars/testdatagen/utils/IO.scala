package net.kalars.testdatagen.utils

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import scala.io.Source
import java.io.FileNotFoundException

/**
 * IO utilities
 */
object IO {
  def fileAsStream(fileName:String, encoding: String= "ISO-8859-1"): Source = {
    try { // Try direct file access first, otherwise class path
      Source.fromFile(fileName, encoding)
    }
    catch {
      case _: FileNotFoundException =>
        Source.fromInputStream(getClass.getClassLoader.getResourceAsStream(fileName), encoding)
    }
  }

  def sourceLines(input: Source): List[String] = {
    val source = input.getLines
    var res: List[String]= Nil
    while (source.hasNext) res ::= source.next()
    res.reverse
  }
}