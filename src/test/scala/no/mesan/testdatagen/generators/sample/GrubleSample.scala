package no.mesan.testdatagen.generators.sample

// Copyright (C) 2017 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.dsl.DslLikeSyntax
import no.mesan.testdatagen.generators.{FromList, Strings}

import scala.language.postfixOps

object GrubleSample extends App with DslLikeSyntax {
     val ls = Strings(1, "ABDEFGHIJKLMNOPRSTUVY").distinct.get(5).sorted
     val cats = FromList(
       "Firmaer",
       "Filmer",
       "Drikker",
       "Forfattere",
       "Matretter",
       "Politikere",
       "Bøker",
       "Kongelige",
       "Butikker",
       "Turistmål",
       "Kunstverk",
       "Klesplagg",
       "Spill",
       "Sanger",
       "Fisker & fugler",
       "Pattedyr",
       "Fjell & elver",
       "Band",
       "Land",
       "Grunnstoff",
       "Byer",
       "Bilmerker",
       "Artister",
       "TV-serier",
       "Frukt & grønt").distinct.get(5).sorted

     for (s<- ls) print("\t" + s)
     for (s<- cats) print("\n" +s)
}
