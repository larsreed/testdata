package net.kalars.testdatagen.generators.sample

// Copyright (C) 2016 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import net.kalars.testdatagen.aggreg.FieldConcatenator
import net.kalars.testdatagen.dsl.DslLikeSyntax
import net.kalars.testdatagen.generators.FromFile

import scala.language.postfixOps

object PassphraseSample extends App with DslLikeSyntax {
  val gen = FieldConcatenator(" ",
      FromFile.iso88591("fornavn.txt"), FromFile.iso88591("verb.txt"),
      FromFile.iso88591("adj.txt"), FromFile.iso88591("subst.txt"))
  val res = gen getStrings 30
  res.foreach(println(_))
}