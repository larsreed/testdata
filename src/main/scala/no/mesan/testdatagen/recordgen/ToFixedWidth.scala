package no.mesan.testdatagen.recordgen

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.Generator

/**
 * This generator produces data in fixed width fields, where each value is padded
 * with blanks (or truncated) to a fixed width.
 * The inherited add method cannot be used.
 */
class ToFixedWidth(withHeaders:Boolean) extends StringRecordGenerator(EmptyNull) {

  private def fix(s:String, width:Int)=
    if (s==null) " " * width
    else ("%-"+width+"."+width+"s").format(s)

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

  /** Add a field, and specify width. */
  def add(fieldName: String, gen: Generator[_], width: Int): ToFixedWidth.this.type = {
    add(new FixedDataField(fieldName, gen, width))
    ToFixedWidth.this
  }

  override def get(n: Int): List[String] = {
    if (withHeaders) fixHeader :: super.get(n)
    else super.get(n)
  }
}

object ToFixedWidth {
  def apply(withHeaders: Boolean=true): ToFixedWidth = new ToFixedWidth(withHeaders)
}
