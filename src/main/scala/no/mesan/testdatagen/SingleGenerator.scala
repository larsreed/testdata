package no.mesan.testdatagen

abstract class SingleGenerator[T] extends ExtendedGenerator[T] with GeneratorImpl[T] {

  /** Set a formatting string. */
  override def format(f: String): this.type= formatWith((t:T) => f.format(t))

  protected var lower: Option[T]= None
  /** Lower bound. */
  override def from(min: T): this.type = { lower=Some(min); this }

  protected var upper: Option[T]= None
  /** Upper bound. */
  override def to(max: T): this.type= { upper=Some(max); this }

  protected var isSequential= false
  protected final def isRandom= !isSequential
  /** Generate sequential, not random. */
  override def sequential: this.type= { isSequential=true; this }

  protected var isUnique= false
  /** Generate unique, random values. */
  override def unique: this.type= { isUnique=true; isSequential= false; this }

  protected var isReversed= false
  /** If sequential, generate from top. */
  override def reversed: this.type= { isReversed=true; sequential }
}
