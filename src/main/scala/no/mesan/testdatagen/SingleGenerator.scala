package no.mesan.testdatagen

/**
 * Default implementation of the ExtendedGenerator, introduces
 * protected variables lower, upper, isSequential, isRandom, isUnique,  isReversed.
 *
 * @author lre
 */
abstract class SingleGenerator[T] extends ExtendedGenerator[T] with GeneratorImpl[T] {

  override def format(f: String): this.type= formatWith((t:T) => f.format(t))

  /** Keeps the lower bound as Some(value) if set. */
  protected var lower: Option[T]= None
  override def from(min: T): this.type = { lower=Some(min); this }

  /** Keeps the upper bound as Some(value) if set. */
  protected var upper: Option[T]= None
  override def to(max: T): this.type= { upper=Some(max); this }

  /** False means random, true means sequential. */
  protected var isSequential= false
  /** Inverts isSequential. */
  protected final def isRandom= !isSequential
  override def sequential: this.type= { isSequential=true; this }

  /** True= unique random values. */
  protected var isUnique= false
  override def unique: this.type= { isUnique=true; isSequential= false; this }

  /** True= reversed, sequential values. */
  protected var isReversed= false
  override def reversed: this.type= { isReversed=true; sequential }
}
