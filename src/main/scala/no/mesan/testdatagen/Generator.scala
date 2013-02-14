package no.mesan.testdatagen

/**
 * This is the main testdata generator interface.
 * For default implementation -- see GeneratorImpl.
 * For more functions, see ExtendedGenerator.
 */
trait Generator[+T] {
  /** Get the next n entries. */
  def get(n: Int): List[T]

  /** Get n entries converted to strings and formatted. */
  def getStrings(n: Int): List[String]

  /** Add a filter function. */
  def filter(f: T => Boolean): this.type

  /** Set a formatting function. */
  def formatWith(f: T => String): this.type
}


/**
 * An extended generator interface for limiting and ordering output.
 * SingleGenerator implements this interface.
 */
trait ExtendedGenerator[T] extends Generator[T] {

  /** Set a formatting string. */
  def format(f: String): this.type

  /** Lower bound.  */
  def from(min: T): this.type

  /** Upper bound. */
  def to(max: T): this.type

  /** Generate sequential, not random. */
  def sequential: this.type

  /** Generate unique, random values. */
  def unique: this.type

  /** Generate reversed sequential values. */
  def reversed: this.type
}


/** A default implementation of the Generator interface. */
trait GeneratorImpl[T] extends Generator[T] {

  private var filterFuns: List[T => Boolean] = List((t => true))
  /** Add a filter function. */
  override def filter(f: T => Boolean): this.type = { filterFuns ::= f; this }

  /** Check that all filters are satisfied. */
  protected def filterAll(t: T): Boolean = filterFuns.forall(f => f(t))

  private var formatFun: T => String = (t => if (t==null) null else t.toString)
  /** Set a formatting function. */
  override def formatWith(f: T => String): this.type = { formatFun = f; this }

  /** Get n entries converted to strings and formatted. */
  override def getStrings(n: Int): List[String] = get(n).map(formatFun)
}

/** A trait to help build delegates for ExtendedGenerators. */
trait ExtendedDelegate[G, T]   {
  self: ExtendedGenerator[T] =>

  protected var generator: ExtendedGenerator[G]

  def conv2gen(f: T): G = f.asInstanceOf[G]
  def conv2result(f: G): T= f.asInstanceOf[T]

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
}
