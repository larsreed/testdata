package no.mesan.testdatagen.generators.sample

import no.mesan.testdatagen.aggreg.{FieldConcatenator, SequenceOf, SomeNulls, TextWrapper, UniqueWithFallback, WeightedGenerator}
import no.mesan.testdatagen.generators.misc.Names
import no.mesan.testdatagen.generators.norway.{Adresser, Fnr, NorskeNavn, Poststeder, RareNavn}
import no.mesan.testdatagen.generators.{Dates, Doubles, Fixed, FromList, Ints}
import no.mesan.testdatagen.recordgen.{ToFile, ToSql}

import scala.language.postfixOps

object LongerSample extends App {
  // These are the total of numbers we will generate for the different categories
  val recordsBase= 100
  val orderFact= 2
  val productFact= 3
  val customerFact= 1
  val orderLineFact= orderFact*3

  // We generate one script for all data
  val resultFile= "orders.sql"

  // To be able to reuse values between records, we generate some values in advance.
  // specifically IDs (for foreign keys) and dates (for correlation between birth dates
  // and "fodselsnummer")
  val customerIds= FromList((Ints() from 1 distinct).get(customerFact*recordsBase))
  val birthDates= Dates() from (y=1921) to (y=1996) get(customerFact*recordsBase)
  val productIds= FromList((Ints() from 1 to 100000 distinct).get(productFact*recordsBase))
  val orderIds= FromList((Ints() from 1 distinct).get(orderFact*recordsBase)) sequential
  val postSteder= FromList(Poststeder() get(customerFact*recordsBase)) sequential

  // Populating the customer table - no dependencies
  val customerGenerator= ToSql(tableName="customer")
    .add("id", customerIds)
    .addQuoted("fnr", SomeNulls(percent=25, Fnr(FromList(birthDates) sequential)))
    .addQuoted("born", FromList(birthDates) formatWith Dates.dateFormatter("yyyy-MM-dd") sequential)
    .addQuoted("name", UniqueWithFallback(RareNavn(), NorskeNavn())) // We want, for the test's sake, unique
               // names. We try to get "funny names" from the RareNavn-generator, but add standard names
               // from the NorskeNavn-generator when duplicates arise.
    .addQuoted("adr", Adresser())
    .addQuoted("postnr", TextWrapper(postSteder).substring(0, 4))
    .addQuoted("poststed", TextWrapper(postSteder).substring(5))

  // and products - no dependencies either
  val productGenerator= ToSql(tableName="product")
    .add("id", productIds)
    .addQuoted("name", WeightedGenerator()
                         .add(60, Names(1))
                         .add(40, Names(2)))

  // Orders are connected to customers through customerIds
  val orderGenerator= ToSql(tableName="order")
    .add("id", orderIds)
    .addQuoted("status", FromList("Pending", "Ready", "Delivered", "Closed"))
    .add("customer", customerIds)
    .addQuoted("orderDate", Dates() from(y=2010) to(y=2013) format "yyyy-MM-dd")

  // And order_lines connected to orders and products
  val orderLineGenerator= ToSql(tableName="order_line")
    .add("order", orderIds)
    .add("product", productIds)
    .add("lineNo", Ints() from 1 sequential)
    .addQuoted("info",
        SomeNulls(60,
            WeightedGenerator()
              .add(10, Fixed("Restock"))
              .add(5, Fixed("Check!"))
              .add(20,  TextWrapper(FieldConcatenator()
                        .add(Fixed("Amount: "))
                        .add(Doubles() from 1 to 300 format "%5.2f")
                        .add(FromList(" l", " kg", "", " m")))
                        .trim)))

  // The generators are all set -- create result
  val allGenerators= SequenceOf().makeAbsolute().addWeighted(
    (customerFact, customerGenerator),
    (productFact, productGenerator),
    (orderFact, orderGenerator),
    (orderLineFact, orderLineGenerator)
  )

  ToFile(fileName=resultFile, generator=allGenerators) write recordsBase
}
