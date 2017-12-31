package no.netcompany.testdatagen.utils

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import scala.util.Random

/**
 * A random function that answers true in n% of the cases.
 */
trait Percentage {
  /** A random function that answers true in n% of the cases. */
  def hit(n: Int): Boolean= Random.nextInt(100) < n
}

