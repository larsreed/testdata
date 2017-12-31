package no.netcompany.testdatagen.generators.sample

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.netcompany.testdatagen.dsl.DslLikeSyntax
import no.netcompany.testdatagen.generators.misc.Names
import no.netcompany.testdatagen.recordgen.SkipNull

import scala.language.postfixOps

object SimpleSample extends App with DslLikeSyntax {
  toFile(fileName = "orders.xml", noOfRecords = 1000) {
    toXmlElements(rootName = "order", recordName = "orderLine", nulls = SkipNull)
      .add("id", sequential integers)
      .add("productName", weighted((3, Names(1)), (2, Names(2))))
      .add("qty", someNulls(33, // 33% has no qty
                    concatenate(
                      doubles from 1 to 300 format "%5.2f",
                      fixed(" "),
                      from list("l", "kg", "", "m"))))
      .add("orderDate", dates from(y = 2012, m = 9) to(y = 2014, m = 11) format "yyyy-MM-dd")
  }
}
