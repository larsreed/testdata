package no.mesan.testdatagen.generators.sample

import no.mesan.testdatagen.generators.misc.Markov

import scala.language.postfixOps


object MarkovSample extends App {
  println(Markov.english() mkString 1000)
}
