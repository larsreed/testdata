package no.mesan.testdatagen.generators.sample

import no.mesan.testdatagen.aggreg.{FieldConcatenator, SomeNulls, TextWrapper, WeightedGenerator}
import no.mesan.testdatagen.generators.{Dates, Doubles, Fixed, FromList, Ints}
import no.mesan.testdatagen.generators.misc.Names
import no.mesan.testdatagen.generators.norway.{Adresser, Fnr, Poststeder}
import no.mesan.testdatagen.recordgen.ToSql

object LongerSample extends App {
  val totalOrders= 200
  val totalProducts= totalOrders/2
  val totalCustomers= totalOrders/3
  val totalOrderLines= totalOrders*3

  val resultFile= "orders.sql"

  val customerIds= FromList(Ints() from (1) to(10000) unique() get(totalCustomers))
  val productIds= FromList(Ints() from (1) to(100000) unique() get(totalProducts))
  val orderIds= FromList(Ints() from (1) to(1000) unique() get(totalOrders)) sequential
  val postSteder= FromList(Poststeder() get(totalCustomers)) sequential

  val customerGenerator= 
    ToSql("customer")
    .add("id", customerIds)
    .addQuoted("fnr", SomeNulls(12, Fnr()))
    .addQuoted("adr", Adresser())
    .addQuoted("postnr", TextWrapper(postSteder).substring(0, 4))
    .addQuoted("poststed", TextWrapper(postSteder).substring(5))

  val productGenerator=
    ToSql("product")
    .add("id", productIds)
    .addQuoted("name", WeightedGenerator()
                         .add(60, Names(1))
                         .add(40, Names(2)))

  val orderGenerator=
    ToSql("order")
    .add("id", orderIds)
    .addQuoted("status", FromList("Pending", "Ready", "Delivered", "Closed"))
    .add("customer", customerIds)
    .addQuoted("orderDate", Dates() from(y=2010) to(y=2013) format("yyyy-MM-dd"))

  val orderLineGenerator=
    ToSql("order_line")
    .add("order", orderIds)
    .add("product", productIds)
    .add("lineNo", Ints() from(1) sequential)
    .addQuoted("info", 
        SomeNulls(33, TextWrapper(
                 FieldConcatenator()
                   .add(Doubles() from(1) to(300) format("%5.2f"))
                   .add(Fixed(" "))
                   .add(FromList("l", "kg", "", "m"))).trim))

  customerGenerator toFile(resultFile) get(totalCustomers)
  productGenerator appendToFile(resultFile) get(totalProducts)
  orderGenerator appendToFile(resultFile) get(totalOrders)
  orderLineGenerator appendToFile(resultFile) get(totalOrderLines)
}
