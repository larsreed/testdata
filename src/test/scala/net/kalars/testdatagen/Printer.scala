package net.kalars.testdatagen

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

trait Printer {
  def print(localPrint:Boolean)(printFun: =>Unit ) {
    val centralPrint = false
    if (localPrint||centralPrint) printFun
  }
}