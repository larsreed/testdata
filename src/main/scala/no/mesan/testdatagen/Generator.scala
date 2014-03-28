package no.mesan.testdatagen

/**
 * This is the main testdata generator interface.
 * For default implementation -- see `GeneratorImpl`.
 * For more functions, see ExtendedGenerator.
 *
 * Most methods return this.type (ending up in the type of the
 * class implementing the trait), to allow _builders_ like
 * Ints() from(1) to(10) reversed.
 *
 * @tparam T Generated type
 * @author lre
 */
trait Generator[+T] {
  /** The main function - provide a list of n entries. */
  def get(n: Int): List[T]

  /** Get n entries converted to strings and formatted. */
  def getStrings(n: Int): List[String]

  /**
   * Add a filter function that takes an instance of the
   * generator's type and returns  true if the instance should be
   * included in the list. Should be applied before constructing
   * the final list (to ensure that get(n) actually contains n
   * elements). The function may be called several times to add
   * multiple filters to apply -- each filter must accept the
   * instance to include it in the final list.
   */
  def filter(f: T => Boolean): this.type

  /**
   * Adds a formatting function that takes an instance of the given
   * type T and formats it as a string.
   */
  def formatWith(f: T => String): this.type

  /** Runs the defined formatter on one instance*/
  def formatOne[S>:T](v: S): String
}


/**
 * An extended generator interface for limiting and ordering output.
 *
 * As the method definitions imply, generators will by default
 * pick _random_ sequences from their value space (the range of
 * the underlying data type(s), possibly limited by from/to-arguments).
 * When unique() is added, it will check to see that the same value is
 * never drawn twice.  For generators with a limited value space, you
 * should be careful with this setting -- FromList(1,2).unique.get(3)
 * will probably crash or run forever...
 * The methods sequential and reversed (and for some subclasses, a step
 * method) change this behaviour -- instead a series of increasing
 * (or decreasing) values will be produced.
 */
trait ExtendedGenerator[T] extends Generator[T] {

  /**
   * A simpler way to define a formatting function, by providing a
   * simple format string for java.lang.String.format.
   */
  def format(f: String): this.type

  /** Lower bound.  */
  def from(min: T): this.type

  /** Upper bound. */
  def to(max: T): this.type

  /**
   * Signals that sequential values are to be generated (subclasses may
   *  define a step size) -- by default, random values are generated.
   * Generation starts at the lower bound, generating towards the upper.
   * If the upper bound is reached without providing the wanted _n_ occurrences,
   * it wraps around from the start.
   */
  def sequential: this.type

  /**
   * Calling this sets the generation to random, and then checks to see
   * that each generated value is unique.
   */
  def unique: this.type

  /**
   * Calling this implies a call to sequential();
   * additionally, generation starts at the upper bound, stepping towards
   * the lower.
   */
  def reversed: this.type
}

/** A default implementation of the Generator interface. */
trait GeneratorImpl[T] extends Generator[T] {

  private var filterFuns: List[T => Boolean] = List(t => true)
  override def filter(f: T => Boolean): this.type = { filterFuns ::= f; this }

  /** Check that all filters are satisfied. */
  protected def filterAll(t: T): Boolean = filterFuns.forall(f => f(t))

  private var formatFun: T => String = t => if (t == null) null else t.toString
  override def formatWith(f: T => String): this.type = { formatFun = f; this }

  override def getStrings(n: Int): List[String] = get(n).map(formatFun)

  override def formatOne[S>:T](v: S): String = formatFun(v.asInstanceOf[T])
}

/**
 * A trait to help build delegates for ExtendedGenerators.
 * Used in a generator[T], wraps a delegate[G].
 *
 * @author lre
 */
trait ExtendedDelegate[G, T]   {
  self: ExtendedGenerator[T] =>

  /** The delegate. */
  protected var generator: ExtendedGenerator[G]

  /** Convert from this type to the generator's type. */
  protected def conv2gen(f: T): G = f.asInstanceOf[G]
  /** Convert from the generator's type to thiss type. */
  protected def conv2result(f: G): T= f.asInstanceOf[T]

  override def format(f: String): self.type = { generator.format(f); this }
  override def formatWith(f: T => String): self.type= { generator.formatWith(v=> f(conv2result(v))); this }
  override def from(min: T): self.type = { generator.from(conv2gen(min)); this }
  override def to(max: T): self.type = { generator.to(conv2gen(max)); this }
  override def sequential: self.type = { generator.sequential; this }
  override def unique: self.type = { generator.unique; this }
  override def reversed: self.type = { generator.reversed; this }
  override def filter(f: T=>Boolean): this.type = { generator.filter(v=> f(conv2result(v))); this }
  override def get(n: Int): List[T] = generator.get(n) map conv2result
  override def getStrings(n: Int): List[String] = generator.getStrings(n)
  override def formatOne[S>:T](v: S): String= generator.formatOne(v)
}

/** Default implementations for extended generators. */
trait ExtendedImpl[T] extends GeneratorImpl[T] with ExtendedGenerator[T] {

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
