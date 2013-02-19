package no.mesan.testdatagen.generators.norway

import no.mesan.testdatagen.GeneratorImpl
import no.mesan.testdatagen.generators.Ints

/**
 * Generate Norwegian "organisasjonsnummer" (organization numbers).
 */
class Orgnr() extends GeneratorImpl[Int] {

  private val intGen= Ints() from(80000000) to(90000000)

  override def get(n:Int): List[Int]= {
    require(n>=0, "cannot get negative count")
    val fakt= List(3, 2, 7, 6, 5, 4, 3, 2)

    def genNext(soFar: List[Int]): List[Int]= {
      if (soFar.length>=n) soFar // Done!
      else {
        val first8= intGen.get(1)(0)
        val nxt= first8.toString.map(_.toString.toInt)
                       .zip(fakt)
                       .foldLeft(0){ (sum, par)=> sum + (par._1*par._2)} % 11
        if (nxt==1) genNext(soFar) // Cannot work
        else if (nxt==0) genNext((first8*10) :: soFar)
        else genNext((first8*10 + (11-nxt)) :: soFar)
      }
    }
    genNext(List())
  }
}

object Orgnr {
  def apply(): Orgnr = new Orgnr()
}
