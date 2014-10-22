package no.mesan.testdatagen

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import scala.language.postfixOps

/**
 * This is the bare minimum interface, mostly ment for "end-of-the-line" data generators.
 * @tparam T Generated type
 */
trait BareGenerator[+T] {

  /** Provide a list of n entries. */
  def get(n: Int): List[T]

  /** Get n entries converted to strings. */
  def getStrings(n: Int): List[String]
}

/**
 * This is the main testdata generator interface.
 * For default implementation -- see `GeneratorImpl`.
 * For more functions, see ExtendedGenerator.
 *
 * Most methods return this.type (ending up in the type of the class implementing the trait),
 * to allow _builders_ like Ints() from(1) to(10) reversed.
 *
 * @tparam T Generated type
 */
trait Generator[+T] extends BareGenerator[T] {
  /** The main function - Get a stream of entries. */
  def gen : Stream[T]

  /** Get a stream of entries converted to strings and formatted. */
  def genStrings: Stream[String]

  /** Make returned values unique. */
  def distinct: this.type

  /**
   * Add a filter function that takes an instance of the generator's type and returns true if
   * the instance should be included in the list. Should be applied before constructing the final
   * list (to ensure that get(n) actually contains n elements).
   * The function may be called several times to add multiple filters to apply -- each filter
   * must accept the instance to include it in the final list.
   */
  def filter(f: T => Boolean): this.type

  /**
   * Add a formatting function that takes an instance of the given type T
   * and formats it as a string.
   */
  def formatWith(f: T => String): this.type

  /** Run the defined formatter on one instance*/
  def formatOne[S>:T](v: S): String
}


/**
 * An extended generator interface for limiting and ordering output.
 *
 * As the method definitions imply, generators will by default pick _random_ sequences from their
 * value space (the range of the underlying data type(s), possibly limited by from/to-arguments).
 */
trait ExtendedGenerator[T] extends Generator[T] {

  /**
   * A simpler way to define a formatting function, by providing a simple format string for
   * java.lang.String.format.
   */
  def format(f: String): this.type

  /** Lower bound.  */
  def from(min: T): this.type

  /** Upper bound. */
  def to(max: T): this.type

  /**
   * Signals that sequential values are to be generated (subclasses may define a step size)
   * -- by default, random values are generated.
   * Generation starts at the lower bound, generating towards the upper.
   * If the upper bound is reached without providing the wanted _n_ occurrences,
   * it wraps around from the start.
   */
  def sequential: this.type
}

trait GeneratorFilters[T] {
  private var filterFuns: List[T => Boolean] = List(t => true)

  /** Return the list of filters. */
  def allFilters = filterFuns

  def filter(f: T => Boolean): this.type = { filterFuns ::= f; this }
  /** Check that all filters are satisfied. */
  def filterAll(t: T): Boolean = allFilters.forall(f => f(t))
}

/** A default implementation of the Generator interface. */
trait GeneratorImpl[T] extends Generator[T] with GeneratorFilters[T] {
  self :  {
    def getStream : Stream[T]
  } =>

  private var formatFun: T => String = t => if (t == null) null else t.toString
  override def formatWith(f: T => String): this.type = { formatFun = f; this }
  override def formatOne[S>:T](v: S): String = formatFun(v.asInstanceOf[T])

  private var isDistinctGen= false
  /** Is this generator distinct? */
  def isDistinct: Boolean= isDistinctGen
  override def distinct: this.type= {
    isDistinctGen= true
    this
  }

  private def genFiltered= getStream filter (t => allFilters.forall(f => f(t)))

  override def gen: Stream[T]= if (isDistinctGen) genFiltered.distinct else genFiltered
  override def genStrings: Stream[String] = gen map formatOne

  override def get(n: Int): List[T] = {
    require(n>=0, "cannot get negative count")
    gen take n toList
  }

  override def getStrings(n: Int): List[String] = {
    require(n>=0, "cannot get negative count")
    genStrings take n toList
  }
}

/** Default implementations for extended generators. */
trait ExtendedImpl[T] extends GeneratorImpl[T] with ExtendedGenerator[T] {
  self :  {
    def getStream : Stream[T]
  } =>

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
}

/**
 * A trait to help build delegates for Generators.
 * Used in a generator[T], wraps a delegate[G].
 */
trait GeneratorDelegate[G, T, D<:Generator[G]]   {
  self: Generator[T] {
     def delegate: D
  } =>

  /** Convert from this type to the generator's type. */
  protected def conv2gen(f: T): G = f.asInstanceOf[G]
  /** Convert from the generator's type to this type. */
  protected def conv2result(f: G): T= f.asInstanceOf[T]

  override def gen: Stream[T]= delegate.gen map conv2result
  override def genStrings: Stream[String]= delegate.genStrings
  override def get(n: Int): List[T] = delegate.get(n) map conv2result
  override def getStrings(n: Int): List[String] = delegate.getStrings(n)

  override def formatWith(f: T => String): self.type= { delegate.formatWith(v=> f(conv2result(v))); this }
  override def formatOne[S>:T](v: S): String= delegate.formatOne(v)

  override def filter(f: T=>Boolean): this.type = { delegate.filter(v=> f(conv2result(v))); this }
  override def distinct: this.type= { delegate.distinct; this }
}

/**
 * A trait to help build delegates for ExtendedGenerators.
 * Used in a generator[T], wraps a delegate[G].
 */
trait ExtendedDelegate[G, T, D<:ExtendedGenerator[G]] extends GeneratorDelegate[G, T, D]  {
  self: ExtendedGenerator[T] {
     def delegate: D
  } =>

  override def format(f: String): self.type = { delegate.format(f); this }
  override def sequential: self.type = { delegate.sequential; this }

  override def from(min: T): self.type = { delegate.from(conv2gen(min)); this }
  override def to(max: T): self.type = { delegate.to(conv2gen(max)); this }
}
