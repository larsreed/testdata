package net.kalars.testgen.generators.sample

import net.kalars.testgen.aggreg.{FieldConcatenator, SomeNulls, WeightedGenerator}
import net.kalars.testgen.generators.{Dates, Doubles, Fixed, FromList, Ints}
import net.kalars.testgen.generators.misc.Names
import net.kalars.testgen.recordgen.{SkipNull, XmlElementGenerator}

object SimpleSample extends App {
  XmlElementGenerator("order", "orderLine", SkipNull).
    add("id", Ints() from(1) sequential).
    add("productName", WeightedGenerator().
                         add(60, Names(1)).
                         add(40, Names(2))).
    add("qty", SomeNulls(3,
                 FieldConcatenator().
                   add(Doubles() from(1) to(300) format("%5.2f")).
                   add(Fixed(" ")).
                   add(FromList("l", "kg", "", "m")))).
    add("orderDate", Dates() from(y=2012, m=9) to(y=2013, m=1) format("yyyy-MM-dd")).
    toFile("orders.xml").
    getStrings(1000)
}