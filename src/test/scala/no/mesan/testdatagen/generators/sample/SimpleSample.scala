package no.mesan.testdatagen.generators.sample

import no.mesan.testdatagen.aggreg.{FieldConcatenator, SomeNulls, WeightedGenerator}
import no.mesan.testdatagen.generators.{Dates, Doubles, Fixed, FromList, Ints}
import no.mesan.testdatagen.generators.misc.Names
import no.mesan.testdatagen.recordgen.{SkipNull, ToXmlElements}
import scala.language.postfixOps


object SimpleSample extends App {
  ToXmlElements("order", "orderLine", SkipNull)
    .add("id", Ints() from 1 sequential)
    .add("productName", WeightedGenerator(
                         (3, Names(1)),
                         (2, Names(2))))
    .add("qty", SomeNulls(33, // 33% has no qty
                 FieldConcatenator()
                   .add(Doubles() from 1 to 300 format "%5.2f")
                   .add(Fixed(" "))
                   .add(FromList("l", "kg", "", "m"))))
    .add("orderDate", Dates() from(y=2012, m=9) to(y=2014, m=11) format "yyyy-MM-dd")
    .toFile("orders.xml")
    .getStrings(1000)
}
