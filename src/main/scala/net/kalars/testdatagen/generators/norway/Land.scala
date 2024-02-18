package net.kalars.testdatagen.generators.norway

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import net.kalars.testdatagen.ExtendedGenerator
import net.kalars.testdatagen.generators.FromFile

/** Generate Norwegian country names. */
object Land {
  def apply(): ExtendedGenerator[String] = FromFile.iso88591("land.txt")
}