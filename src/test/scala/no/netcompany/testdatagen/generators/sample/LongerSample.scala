package no.netcompany.testdatagen.generators.sample

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.netcompany.testdatagen.aggreg.SequenceOf
import no.netcompany.testdatagen.dsl.DslLikeSyntax
import no.netcompany.testdatagen.generators.misc.Names
import no.netcompany.testdatagen.generators.{Dates, FromList}
import no.netcompany.testdatagen.recordgen.ToSql

import scala.language.postfixOps

object LongerSample extends App with DslLikeSyntax {
  // These are the total number of records we will generate for the different categories
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
  val customerIds= from list ((sequential integers) get(customerFact * recordsBase))
  val birthDates= dates from (y=1921) to (y=1996) get(customerFact*recordsBase)
  val productIds= from list((sequential integers) to 100000 get(productFact*recordsBase))
  val orderIds = from list (sequential integers).get(orderFact * recordsBase) sequential
  val postSteder= from list (poststeder get(customerFact*recordsBase)) sequential

  // Populating the customer table - no dependencies
  val customerGenerator= ToSql(tableName="customer")
    .add("id", customerIds)
    .addQuoted("fnr", someNulls(percent=25, fnrFromDates(from list birthDates sequential)))
    .addQuoted("born", from list birthDates formatWith Dates.dateFormatter("yyyy-MM-dd") sequential)
    .addQuoted("name", uniqueWithFallback(rareNavn, norskeNavn)) // We want, for the test's sake, unique
               // names. We try to get "funny names" from the RareNavn-generator, but add standard names
               // from the NorskeNavn-generator when duplicates arise.
    .addQuoted("adr", adresser)
    .addQuoted("postnr", substring(postSteder, 0, 4))
    .addQuoted("poststed", substring(postSteder, 5))

  // and products - no dependencies either
  val productGenerator= toSql(tableName="product")
    .add("id", productIds)
    .addQuoted("name", weighted((60, Names(1)),
                                (40, Names(2))))

  // Orders are connected to customers through customerIds
  val orderGenerator= toSql(tableName="order")
    .add("id", orderIds)
    .addQuoted("status", from list("Pending", "Ready", "Delivered", "Closed"))
    .add("customer", customerIds)
    .addQuoted("orderDate", dates from(y=2010) to(y=2013) format "yyyy-MM-dd")

  // And order_lines connected to orders and products
  val orderLineGenerator= toSql(tableName="order_line")
    .add("order", orderIds)
    .add("product", productIds)
    .add("lineNo", sequential integers)
    .addQuoted("info",
        someNulls(60,
            weighted((10, fixed("Restock")),
                     (5, fixed("Check!")),
                     (20,
                       transformText(
                         concatenate(fixed("Amount: "),
                           (positive doubles) from 1 to 300 format "%5.2f",
                           from list(" l", " kg", "", " m")))
                         trim()))))

  // The generators are all set -- create result
  toFile(fileName=resultFile, noOfRecords = recordsBase) {
    SequenceOf().makeAbsolute().addWeighted(
        (customerFact, customerGenerator),
        (productFact, productGenerator),
        (orderFact, orderGenerator),
        (orderLineFact, orderLineGenerator))
  }
}
