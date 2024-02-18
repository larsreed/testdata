package net.kalars.testdatagen.utils

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import scala.util.Random

/**
 * Function to pick a random element from a list.
 */
trait RandomElem {
  def randomFrom[T](l: Seq[T]): T = l(Random.nextInt(l.length))
}