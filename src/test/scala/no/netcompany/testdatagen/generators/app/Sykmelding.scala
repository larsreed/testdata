package no.netcompany.testdatagen.generators.app

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.netcompany.testdatagen.aggreg.{SequenceOf, SomeNulls, TwoFromFunction, TwoWithPredicate, UniqueWithFallback}
import no.netcompany.testdatagen.dsl.DslLikeSyntax
import no.netcompany.testdatagen.generators.misc.Names
import no.netcompany.testdatagen.generators.norway.{NorskeNavn, RareNavn}
import no.netcompany.testdatagen.generators.{Booleans, Dates, FromList, Ints, Strings}
import no.netcompany.testdatagen.recordgen.{ToCsv, ToFile, ToSql}
import org.joda.time.DateTime

import scala.language.{existentials, postfixOps}

object Sykmelding extends App with DslLikeSyntax {
  val brukerGen=  from list("luxmip", "gormh", "marits", "larsr", "ingek", "christofferd", "haraldk")
  val fraDatoGen= dates from(y=2015) to (y=2015) format "yyyy/MM/dd"
  val dagerGen= integers from 1 to 11
  val egenGen= booleans format(" ", "X")
  val barnGen= booleans format(" ", "X")
  ToFile("c:/temp/fravar.txt", ToCsv(separator = "\t")
    .add("ID", brukerGen)
    .add("F.o.m. dato", fraDatoGen)
    .add("Dager", dagerGen)
    .add("Egenmelding", egenGen)
    .add("Barn", barnGen))
    .get(255)
}
