package no.mesan.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

/**
 * Generate the same value all time....
 * from/to are not supported
 */

class Fixed[T](value: T) extends FromList[T] {
  fromList(List(value))

  override def filter(f: T=>Boolean) = {
    if (!f(value)) throw new IllegalArgumentException(s"filter does not accept single value $value")
    super.filter(f)
  }
}
object Fixed {
  def apply[T](v: T): Fixed[T] = new Fixed(v)
}
