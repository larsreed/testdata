package net.kalars.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import net.kalars.testdatagen.GeneratorImpl

/**
 * This generator takes a stream as input, and uses that directly for output.
 */
class FromStream[T](inputStream: Stream[T]) extends GeneratorImpl[T]  {
  def getStream: Stream[T] = inputStream
}

object FromStream {
  def apply[T](inputStream: Stream[T]): FromStream[T] = new FromStream[T](inputStream)
}