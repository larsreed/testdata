package net.kalars.testgen.recordgen

import net.kalars.testgen.Generator

class FixedWidthGenerator(withHeaders:Boolean) extends StringRecordGenerator(EmptyNull) {
  private def fix(s:String, width:Int)=
    if (s==null) " " * width
    else
      ("%-"+width+"."+width+"s").format(s)

  class FixedDataField(name: String, generator: Generator[_], awidth:Int)
        extends DataField(name, generator) {
    val width= awidth
    override def transform(s: String): String = fix(s, width)
  }

  private def fixHeader= {
    fields.map {
      case df =>
        val v= df.asInstanceOf[FixedDataField]
        fix(v.name, v.width)
    }.reverse.mkString("")
  }

  override protected def makeFields(recs: DataRecord): String =
    recs.map(_._2).mkString("")

  override def add(fieldName: String, gen: Generator[_])= throw new UnsupportedOperationException

  def add(fieldName: String, gen: Generator[_], width: Int): this.type = {
    add(new FixedDataField(fieldName, gen, width))
    this
  }

  override def get(n: Int): List[String] = {
    if (withHeaders) fixHeader :: super.get(n)
    else super.get(n)
  }
}

object FixedWidthGenerator {
  def apply(withHeaders: Boolean=true): FixedWidthGenerator = new FixedWidthGenerator(withHeaders)
}
