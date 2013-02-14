package no.mesan.testdatagen

trait Printer {
  def print(localPrint:Boolean)(printFun: =>Unit ) {
    val centralPrint = false
    if (localPrint||centralPrint) printFun
  }
}
