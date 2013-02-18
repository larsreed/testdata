package no.mesan.testdatagen.generators.norway

import no.mesan.testdatagen.Generator
import no.mesan.testdatagen.aggreg.{FieldConcatenator, TextWrapper, WeightedGenerator}
import no.mesan.testdatagen.generators.{FromFile, Strings}

/**
 * Generate Norwegian names...
 */
class NorskeNavn(allLines: Boolean) extends Generator[String] {
  private val fornavn= "fornavn.txt"
  private val etternavn= "etternavn.txt"

  forOgEtternavn

  protected var generator: Generator[String]= _

  def forOgEtternavn: this.type = {
    generator= TextWrapper(
        WeightedGenerator().add(40, create(1, 1, allLines)).
                            add(20, create(1, 2, allLines)).
                            add(25, create(2, 1, allLines)).
                            add(2,  create(2, 2, allLines)).
                            add(5,  create(2, 2, allLines)))
    this
  }

  def kunFornavn: this.type = {
    generator= TextWrapper(create(1, 0, allLines))
    this
  }

  def kunEtternavn: this.type = {
    generator= TextWrapper(create(0, 1, allLines))
    this
  }

  override def formatWith(f: String=>String): this.type = {
    generator.formatWith(f)
    this
  }

  override def filter(f: String=>Boolean): this.type = {
    generator.filter(f)
    this
  }

  override def get(n:Int): List[String] = generator.get(n)

  override def getStrings(n:Int): List[String] = generator.getStrings(n)

  private def create(antFor: Int, antEtter:Int, allLines:Boolean): Generator[String] = {
   val gen= new FieldConcatenator()
   for ( i<- 0 to antFor-1) {
     if (i>0) gen.add(Strings().length(1).chars(" "))
     gen.add(FromFile(fornavn, allLines))
   }
   for ( i<- 0+antFor to antFor+antEtter-1) {
     if (i>0) gen.add(Strings().length(1).chars(" "))
     gen.add(FromFile(etternavn, allLines))
   }
   gen
  }
}

object NorskeNavn {
  def apply(allLines:Boolean=true): NorskeNavn = new NorskeNavn(allLines)
}
