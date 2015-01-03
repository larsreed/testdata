# Testdata generator #
This project contains a set of tools to help generate test data. It is primarily a way to train myself in Scala (by porting and enhancing some very old AWK scripts :), but can hopefully be useful to someone (and has been to me)...

Look at the end of this docouments for more history and caveats.

## Author ##
Lars Reed, Mesan AS
Copyright: yes, see below

## Overview ##
The basic component is the `Generator[T]` trait, which is able to provide a (possibly filtered) list of instances of a given type, as well as the same list converted to strings.
On top of this is a set of generators based on the `ExtendedGenerator[T]` interface, containing methods to set up lists of basic data like strings and numbers.
Furthermore, there are some utility classes like `FieldConcatenator` and `WeightedGenerator` to assist in creating aggregate or more complex data. And finally, there are a set of classes to assist in creating complete data records, like `ToSql` for generating SQL inserts, and `ToCsv` to create simple flat file records.

### Sample usage ###
Here is an introductory example to give you a sense of what it's all about:


    object SimpleSample extends App with DslLikeSyntax {
      toFile(fileName = "orders.xml", noOfRecords = 1000) {
        toXmlElements(rootName = "order", recordName = "orderLine", nulls = SkipNull)
          .add("id", sequential integers)
          .add("productName", weighted((3, Names(1)), (2, Names(2))))
          .add("qty", someNulls(33, // 33% has no qty
                        concatenate(
                          doubles from 1 to 300 format "%5.2f",
                          fixed(" "),
                          from list("l", "kg", "", "m"))))
          .add("orderDate", dates from(y = 2012, m = 9) to(y = 2014, m = 11) format "yyyy-MM-dd")
      }
    }

This produces an XML-file, order.xml, with the following content:

    <order>
       <orderLine>
          <id>1</id>
          <productName>Shrpfljookmlvkpe</productName>
          <orderDate>2012-11-24</orderDate>
       </orderLine>
       <orderLine>
          <id>2</id>
          <productName>Ysmlyjyvygsrtbr Dnbldegkieivvdrut</productName>
          <qty>113,99 kg</qty>
          <orderDate>2012-10-20</orderDate>
       </orderLine>
       <orderLine>
          <id>3</id>
          <productName>Iphlifhmopaka</productName>
          <qty>87,77 kg</qty>
          <orderDate>2013-11-04</orderDate>
       </orderLine>
       <orderLine>
          <id>4</id>
          <productName>Afoubrksoomnhehtt Leoiom</productName>
          <qty>283,82 l</qty>
          <orderDate>2012-09-16</orderDate>
       </orderLine>
    </order>

For a more thorough example, scroll down...

## Core definitions ##

### Generator ###
The basic generator trait looks like this (details omitted):

    /** The bare minimum interface, mostly meant for "end-of-the-line" data generators. */
    trait BareGenerator[+T] {

      /** Provide a list of n entries. */
      def get(n: Int): List[T]

      /** Get n entries converted to strings. */
      def getStrings(n: Int): List[String]
    }

    /** The main testdata generator interface. */
    trait Generator[+T] extends BareGenerator[T] {
      /** The main function - Get a stream of entries. */
      def gen : Stream[T]

      /** Get a stream of entries converted to strings and formatted. */
      def genStrings: Stream[String]

      /** Make returned values unique. */
      def distinct: this.type

      /** Add a filter function */
      def filter(f: T => Boolean): this.type

      /** Add a formatting function. */
      def formatWith(f: T => String): this.type

      /** Run the defined formatter on one instance*/
      def formatOne[S>:T](v: S): String
    }

The elements here are:

* `gen`: The main function, providing a stream of data elements.
* `genStrings`: Returns the same stream as the previous, but each element is mapped via the formatter function (see below) and thus converted to string format.
* `get(n)`: Get `n` elements from the stream, as a list. This one, and the next, is extracted into the super trait `BareGenerator`, needed for a few instances at the end of the generator pipe where an infinite stream would be unpractical.
* `getStrings(n)`: like `genStrings`, but takes `n` entries from the stream.
* `filter(f)`: Adds a function that takes an instance of the generator's type and returns `true` if the instance should be included in the list. The function may be called several times to add multiple filters to apply, each and every filter must accept the instance to include it in the final list.
* `formatWith(f)`: Adds a formatting function that takes an instance of the given type T  and formats it as a string.
   The default formatting function is `toString`
* `formatOne(v)`: Uses the defined formatting function to format one value.
* `distinct`: requires that this generator produces only distinct values.
  **This should be used with caution!**  Obviously, if an "infinite" number of elements should contain no duplicates, "someone" has to remember all values past and spend time checking them. This is not for free...  Moreover, if the underlying value space is smaller than the desired number of outputs, it will usually enter an infinite loop, where the recipient keeps pulling and nothins comes out. E.g.: the `Booleans` generator has at most two possible values, the `Fixed` has only one, `FromList` is bounded, and filter functions may restrict almost any generator -- do not try to get 3 distinct Booleans...

#### GeneratorImpl & GeneratorFilters ####
`GeneratorImpl` is a simple trait containing a sufficient implementation of the `Generator` methods. It requires the implementing class to provide (at least) a `def getStream : Stream[T]` which is used to derive the other methods. It uses the separately usable `GeneratorFilters` trait that handles the filter functions. Implementing classes get the following additional members:

* `def filterAll(elem:T): Boolean`: applies all defined filters to a value.
    * A default filter function that accepts any input is included.
* `isDistinct` that tells you whether only unique values are requested
* `protected var formatter` which is the defined formatting function, defaulting to a simple toString.
with the formatter.

### ExtendedGenerator ###
The ExtendedGenerator trait extends the `Generator` trait with methods to control how data is generated.

    trait ExtendedGenerator[T] extends Generator[T] {
      /** Format by java.lang.String.format format string. */
      def format(f: String): this.type

      /** Lower bound.  */
      def from(min: T): this.type
      /** Upper bound. */
      def to(max: T): this.type

      /** Request sequential rather than random. */
      def sequential: this.type
    }


The general methods are:

* `format(s)`: A simpler way to define a formatting function, by providing a simple format string for java.lang.String.format, e.g. `'%08d'`.
* `from(T) / to(T)`: Define a lower/upper bound for generated values (these take a value of the output type, subclasses may provide variants).
* `sequential`: Signals that sequential values are to be generated (subclasses may define a step size). By default, random values are generated.
Generation starts (unless the generator has a step size) at the lower bound, generating towards the upper. If the upper bound is reached without providing the wanted *n* occurrences, it wraps around from the start. E.g.: when getting 6 numbers from `Ints() from 0 to 2 sequential` you get "0,1,2,0,1,2,0,...".

All the methods return `this.type` (ending up in the type of the class implementing the trait), to allow fluent generator building, like `Ints() from 1 to 10 sequential`. As the method definitions imply, generators will by default pick *random* sequences from their value space (the range of the underlying data type(s), possibly limited by from/to-arguments). The method `sequential` (and for some subclasses, a `step` method) change this behaviour, instead a series of increasing (or decreasing) values will be produced.
Sometimes you will find it necessary to have two generators A and B draw from the same, limited value space. In that case, you could use an initial generator to fill a list with the desired values, and then use `FromList`s to produce the final values.

#### ExtendedImpl ####
ExtendedImpl is a trait extending `GeneratorImpl[T]` that implements all `ExtendedGenerator`-methods. Like `GeneratorImpl` it requires a `def getStream: Stream[T]` in its defining class.
Subclasses get the following `protected` members:

* `var lower: Option[T]`
* `var upper: Option[T]`
* `var isSequential: Boolean`
* `final def isRandom= !isSequential`

#### GeneratorDelegate / ExtendedDelegate ####
The final basic building blocks are the traits `GeneratorDelegate[G, T]` / `ExtendedDelegate[G, T]`.
These traits

* implements all the interface methods by calling a delegate generator
* requires the implementing type to have a `var generator:Generator[G]` (resp. `ExtendedGenerator[G]`) containing the actual generator
* allow you to to change the definition of `def conv2gen(f: T): G` and / or `conv2result(f: G): T` that are used to convert between the types of the generator itself and the delegate converter (by default both are implemented with `asInstanceOf`)

## Basic generators ##

### Booleans ###
This class has the rather simple job of generating `Boolean`s... It uses `FromList` under the hood.

* `from` and `to` are not supported
* an additional method `format(falseString, trueString)` is available for the conversion in `getStrings`
* Apply methods:
    * `Booleans()`
    * `Booleans(trueString, falseString)`

### Ints ###
returns ints from the entire range. The only special method is `step(Int)` to define step size for sequences.  A negative step size means starting from the max value, working towards the min value.
Apply methods (defaults for all parameters):

* `Ints(from:Int=Int.MinValue+1, to:Int=Int.MaxValue-1, step:Int=1)`

### Longs ###
returns longs from the entire *positive* range. The only special method is `step(Long)` to define step size for sequences, as for `Ints`, a negative step means starting at max.

Apply methods (defaults for all parameters):

* `Longs(from:Long=0, to:Long=Long.MaxValue-1, step:Long=1)` (note default 0 for start)
* `Longs.negative(from:Long=0, to:Long=Long.MaxValue-1, step:Long=1)` (only returns 0 or negative)
* `Longs.anyLong()` (returns both positive and negative values)

### Chars ###
uses `ExtendedDelegate` and a 1-character `Strings`-generator to do its work. It adds the `chars(Seq[Char])` method also supported by Strings to add a range of available characters. Accepts a string (`chars("aeiouy")`), an interval (`chars('a' to 'z')`) etc.

Apply methods:

* `Chars()`
* `Chars(seq)`

### Doubles ###
returns doubles from the entire *positive* range. The only special method is `step(Double)` to define step size for sequences, as for `Ints`, a negative step means starting at max.

Apply methods:

* `Doubles()`
* `Doubles().negative()` returns only negative values

### Dates ###
A basic enough data type, this is still one of the more complex generators. It uses JodaTime for date/time representation, although conversions for `java.util.Date` are available. There are a lot of special methods:

* `timeOnly`: generates different times with date part omitted
* `dateAndTime`: include both date and time in the output (default is just date)
* `setStdTime(h: Int, min:Int, s:Int, ms:Int)`: set the standard time parts used when generating date only (default 0,0,0,0)
* `setStdDate(y: Int, m:Int, d:Int)`: set the standard date parts used when generating time only (default today's date)
* `from(java.util.Date)` / `to(java.util.Date)`: limit using JDK Dates rather than Joda DateTimes.
* `from(y: Int=1753, m: Int=1, d:Int=1, hh: Int=0, mm: Int=0, ss:Int=0, ms: Int=0)` / `to(y:Int=9999, m: Int=12, d:Int= -31, hh: Int=23, mm: Int=59, ss:Int=59, ms: Int=999)`: These are designed to be used with named arguments, like `from(y=1980, m=1) to(y=1999, m=12)`
* `step(y: Int=0, m: Int=0, d:Int=0, hh: Int=0, mm: Int=0, ss:Int=0, ms:Int=0)`: Another candidate for named arguments, this method sets the interval for sequential generation.  Do _not_ use negative values, apply the `reversed` method to count backwards.
* `step(Period)`: use a Joda period directly
* `format(DateTimeFormatter)`: Use one of Joda's formatters
* `Dates.dateFormatter(formatString)`: returns a partial function to format dates according to a given string
* `reversed(rev:Boolean=true)`: means to start at the max value (if true), working with negative periods towards the start.  Implies a `sequential` call.
* `getJavaDates(n:Int)` / `genJavaDates`: same as `get`/`gen`, but converts to `java.util.Date`

### Strings ###
The Strings generator generate strings, would you believe it...
Special methods:

* `length(n)` / `lengthBetween(from, to)`: sets the required length (default 1) of the generated strings
* `chars(Seq[Char])`: defines the default characters to build strings from (default printable ASCII, i.e. space to `~`).
Note that there are many ways to define a `Seq[Char]`, e.g. `"aeiuoy"`, `'a' to 'z'`, `'a' to 'z' filter {x=> !"aeiouy".contains(x)}`

Apply methods:

* `Strings(length: Int=1)`: default method, exact length may be given
* `Strings(length: Int, chars:Seq[Char])`: supply length and character set
* `Strings.letters(length: Int=1)`: use only upper/lowercase a..z
* `Strings.alfanum(length: Int=1)`: use only upper/lowercase a..z or digits
* `Strings.ascii(length: Int=1)`: use characters between ASCII space and tilde



### Fixed ###
This generator may seem superfluous... It takes a single value, and returns that same value repeatedly. But it is meant for aggregating values, see `FieldConcatenator` for an example.
This "generator" is actually just an apply method taking a single value; it is backed by `FromList`.

### FromFile ###
This generator reads lines from an input file and creates a list of values, from which a delegate `FromList` can take its values. The values may be typed (does not currently work as expected...), even though they are read as strings.
Specialities:
The `from` and `to` methods are not supported.

Apply method:

* `FromFile(resourceName: String, encoding: String= "UTF-8")`: A file name must be given, the encoding to be used defaults to UTF-8. The file to be read is first searched for on the classpath, then as a regular file name.

### FromList ###
Probably the most versatile of all the generators, the FromList takes a list of "anything" as input and generates its values from that, it is typed (`FromList[T]`), so you keep the type of the input list.
To name a few possible uses:

* If you already have a list of the values you want to pick from, the FromList takes care of the rest... This is what the Boolean (short list :), fixed (even shorter) and FromFile generators do. You could just as well use it for a list of Person objects or DOM trees...
* As mentioned above, if you need to reuse the same values in several generators, e.g. if you need "foreign keys" from one generator to another, you could generate the needed values in a list, and use that list for the other generators.
* You could use it to scramble existing data. E.g.: do a `select Id, FirstName, LastName, Address, CreditCardNumber from Customer` in your code, keep the field values in each their list, and the generate a number of `insert`s picking random values from the lists.

Specialities:

* `from` and `to` are not supported
* The method `fromList(list)` must be called before you generate values (unless you use the apply method with a list argument).


Apply methods:

* `FromList()` &ndash; then you *must* call `fromList` afterwards
* `FromList(List[T])`
* `FromList(T*)` e.g. `FromList(1,2,4,8,16,32,64)`
* `FromList.weighted(Seq[Tuple2[Int, T]])` helps you build weighted choices for simple values. It builds an input list from a sequence of `(weight, value)`-tuples, e.g. `FromList.weighted(List((10, "A"), (20, "B"), (20, "C")))` which will return approx. 20% As, 40% Bs and 40% Cs (for random generation; 10 As followed by 20 Bs and then 20 Cs for sequential).

### FromStream ###
This is the simplest implementation of all.  It simply wraps any `Stream` as a `Generator`.

Apply methods:

* `FromStream(Stream[T])`

## Aggregates ##
There will often be a need to handle more complex data than what the basic generators can produce. A set of generators are provided to facilitate building of aggregate constructs.

### MultiGenerator / MultiGeneratorWithWeight ###
These traits are used by several of the follow generators, supplying miscellaneous `add`-methods to facilitate combinations and aggregates.

### TextWrapper ###
This generator takes any other generator as input, always uses its `genStrings` as input, thus acting as a "text converter", and adds methods to manipulate the resulting text.

Special methods:

* `substring(from:Int, to:Int=-1)`: (if `to` is omitted, the rest of the string is used)
* `toLower` / `toUpper` / `trim`: as in `java.lang.String`
* `surroundWith(prefix:String="", suffix:String="")` pre/suffixes the result string with fixed strings
* `transform(f: String=> String)`: add your own string transformer function
* `substitute(regexp:String, to:String)`: perform substitution of regexp

Apply methods:

* `TextWrapper(generator)`

### FieldConcatenator ###
You saw the FieldConcatenator in action in the introductory example:

    FieldConcatenator().
      add(Doubles() from 1 to 300 format "%5.2f").
      add(Fixed(" ")).
      add(FromList(List("l", "kg", "", "m")))

The FieldConcatenator is given a set of generators with the `add` method. When producing its output, it collects one entry from each of the generators, concatenates the output from each (in the same order as the `add` calls), and returns the list of concatenated strings (in the above example strings like "12.04 kg").

Apply methods:

* `FieldConcatenator(generator*)` takes the generators to add, fields are separated by the empty string
* `FieldConcatenator(fieldSeparator: String, generator*)` takes a field separator in addition to the generators

### SomeNulls ###
This generator takes another generator and a percentage as input. Values are retrieved from the original generator to retrieve its values, and then replaces approximately N% of the occurrences (decided by a random generator) with `null`. N==0 means no nulls, N==100 means only nulls, N==50 approximately 50% nulls etc.

Methods:

* `nullFactor(percent:Int)` as described above

Apply methods:

* `SomeNulls(percent, generator)`: supply both the factor and the generator

### WeightedGenerator ###
This generator takes one or more generators as input, and selects randomly between them for each value to generate. Each generator is given a weight &ndash;  the probability for each one is its own weight relative to the sum of all weights.

* `add(weight: Int, gen: Generator[_])`

*Note*: If what you want is a certain distribution between a few, given values, you'll probably be better off with `FromList.weighted` (which see).

Apply methods:

* `WeightedGenerator[T]((Int,Generator[T])*)`: add one or more tuples of weight + generator
* `WeightedGenerator[T](List(Generator[T]))`: add one or more generators with weight 1

### SequenceOf ###
This is one of the few `BareGenerator`s, thus, it will only produce lists, not streams.
It takes a list of generators (which may be weighted, the default weight is 1); when producing output, the input generators are called in sequence, each adding a set of records to the result.  In default mode, each generator contributes a number of records relative to its weight, the total will then be close to N.  In absolute mode &ndash; after calling `makeAbsolute` &ndash; each generator contributes `N*weight` records.

This might not seem very useful, but when generating to file (etc), it is usually easier to collect the individual  generators in this generator, rather than specifying `ToFile/append` for each input.

It takes two type parameters:

1. The type of the input generators (which could be `Any`, if the generator types vary)
2. The type of the generated values (typically `String` if the input is `Any`, otherwise the input type)
The constructor needs a method to convert from the first to the second type.

Add methods:

* `add(generator*)`
* `add(weight:Int, Generator[T])`
* `addWeighted(weighted: (Int, Generator[T])*)`

Apply/object methods:

* `SequenceOf[T](generator[T]*)`: adds 1 or more generators with weight 1, with an identity conversion function
* `SequenceOf.strings(generator[Any]*)` and `SequenceOf()`: create a `SequenceOf[Any,String]`, the first with a set of weight 1 generators

### TwoFromFunction ###
This generator takes a generator and a generator function as input. It generates values from the generator, and feeds values to the generator function to obtain a derived value.  The two values are then returned as a tuple. It does not support the `formatWith` or `formatOne` functions.

Special methods:

* `genFormatted: Stream[(String, String)]`: The `genStrings` method does not support formatting, but this method returns String tuples formatted according to the input generator's formatting function.
* `asListGens(n: Int): (FromList[T], FromList[U])`: Runs `get(n)`, and returns `FromList`-generators for the original and derived values respectively.

Apply method:

* `TwoFromFunction[T, U](gen: Generator[T], genFun: T=>U): TwoFromFunction[T, U]`

### TwoWithPredicate ###
This generator draws tuples from two generators, it also takes a predicate function to determine if the generated tuple should be included. This generator is included to support generation of interdependent fields, e.g. "fromDate & toDate" (where the predicate ensures toDate>=fromDate) etc.

Special methods:

* `genFormatted: Stream[(String, String)]`: The `genStrings` method does not support formatting, but this method returns String tuples formatted according to the input generators' formatting functions.
* `asListGens(n: Int): (FromList[T], FromList[U]`: Runs `get(n)`, and returns `FromList`-generators for the two different value lists.
* `def asFormattedListGens(n: Int): (FromList[String], FromList[String])`: a combination of the previous two

Apply methods:

* `TwoWithPredicate[T, U](left: Generator[T], right: Generator[U], predicate: ((T,U))=>Boolean): TwoWithPredicate[T, U]`
* `TwoWithPredicate[T](gen: Generator[T], predicate: ((T, T))=>Boolean): TwoWithPredicate[T, T]` (uses the same generator twice)


### UniqueWithFallback ###
takes a primary generator and a secondary generator.  It tries to get unique values from the primary generator, but for each duplicate value obtained, it repeatedly gets a value from the secondary generator until a unique value is found.  The `formatWith`function is not supported, formatting is done by the primary generator.
As with the `distinct` operator, use this conservatively -- it consume memory proportinately to the number of elements retrieved.  Also make sure the fallback generator is able to produce distinct values.
If the primary generator produces values in some sorted order, thus, duplicate detection can be reduced to checking whether a value is equal to the previous, memory consumption is not an isseue. You can signal this by calling `isSorted(true)` before generating values.

Apply methods:

* `UniqueWithFallback[T](primary: Generator[T], fallback: Generator[T])`

## Specialized generators ##

### Names ###
Creates "name-like" strings   words containing A-Zs with random length between 3 and 20 with an uppercase first letter. You can see some sample output in the product names in the introductory example. This is only a wrapper object around a `Strings` generator, its only parameter is an int telling how many space-separated words to create in each string.

### CarMakes ###
This generator selects from a list of about a 100 manufacturers of cars, motor cycles etc, like "Porsche" and "Toyota". No class, just an apply method without parameters returning a `FromList`.

### Urls ###
Builds fake URLs using a http/https prefix, "://", sometimes "www.", a lowercase string (a-z) of length 4-10, and a suffix of .com/no/org/net/co.uk/gov.
No class, just an apply method without parameters.

### MailAddresses ###
Builds email addresses using this pattern:

1. A name of 3-8 letters (a-z)
Sometimes expanded with "." and another name (4-9 letters)
2. "@"
3. A name of 4-9 characters
4. "."
5. com/no/org/net/co.uk/gov

### Guids ###
This generator is a (perhaps too) simple generator for GUIDs, basically 128-bits integers, NOT following the rules laid out in [http://www.ietf.org/rfc/rfc4122.txt](http://www.ietf.org/rfc/rfc4122.txt) etc.
There are 3 different output types (and only a default apply method):

* `gen`/`get(n)`: returns elements of type `Seq[Int]`, with 4 positive longs; with 32, 16, 16 and 64 bits respectively (negative numbers are not generated)
* `genStrings` / `getStrings(n)`: the numbers are formatted as hex strings in the format "hhhhhhhh-hhhh-hhhh-hhhhhhhhhhhhhhhh" (unless you call `formatWith` with another formatter).
If you need something like the standard Windows references, you can use a `TextWrapper`, e.g. `TextWrapper(Guids()) surroundWith("{", "}") toUpper`, which returns strings like `{0B9F2CC4-7A26-DF88-57BFACEB0A6152C3}`
* `genBigInts`: the values are returned as actual 128-bit integers, like 135552048303739552162038533024056166383

### CreditCards ###
This generator by default generates 16-digit credit card numbers from Visa or MasterCard, but you can instruct it otherwise through its generic apply method. The last digit is generated using Luhn's algorithm ([http://en.wikipedia.org/wiki/Luhn_algorithm](http://en.wikipedia.org/wiki/Luhn_algorithm), see also [http://en.wikipedia.org/wiki/Credit_card_number](http://en.wikipedia.org/wiki/Credit_card_number)).
There are 4 different "apply" methods:

* `CreditCards()`: 16 digit MasterCard/Visa
* `CreditCards.visas`: 16 digit Visa numbers (starts with 4)
* `CreditCards.masterCards`: 16 digit MasterCard numbers (starts with 51..55)
* `CreditCards(prefixes: List[Long], length: Int)`: you decide the prefixes and length; make sure you leave room for the check digit.

### Markov ###
A generator for Markov chain text generation, i.e. random text based on existing text (see [http://en.wikipedia.org/wiki/Markov_chain#Markov_text_generators](http://en.wikipedia.org/wiki/Markov_chain#Markov_text_generators)).  A sample...:

> all this work, you join the Cat sitting on their faces. There ought
> to ask help thinking over to the same thing a railway station.)
> However, I've offended tone.  'I never learnt it.' said Alice; 'living
> at her head!' or detach or other; but it by the Mock Turtle: 'nine the
> use in her voice of me? They're dreadfully ugly child: but tea.
> 'I shall have of that?' she did Alice waited till at the King,
> 'or you'll understand you,' (she was reading, but now I had to see
> anything would change to see that rate!

**Methods**:
* `buildFrom(files: List[String])`: tries to interpret each file name either as a resource to load from the classpath or a regular file name; reads all contents from all files to produce the source structure
* `buildFromList(words: List[String])`: builds the source structure from all the given strings
* `mkString(n: Int)`: generates `n` words and concatenates them with spaces

**Apply methods**:
* `Markov(file:String)`: build from a single file
* `Markov(files:List[String])`: build from a list of files
* `Markov.norwegian`: builds from a Norwegian governmental report in "markov-no.txt" -- source: [http://www.regjeringen.no/nb/dep/fad/dok/regpubl/stmeld/2008-2009/stmeld-nr-19-2008-2009-](http://www.regjeringen.no/nb/dep/fad/dok/regpubl/stmeld/2008-2009/stmeld-nr-19-2008-2009-)
* `Markov.english`: builds from an excerpt from "Alice in Wonderland" (Lewis Carroll)

Se also the `MarkovSample` class.

### Fibonaccis ###
You cannot write a set of generators in a functional language without a Fibonacci sequence generator. Thus...
This generator is the txt book example of a `Stream`, written as a single line of code, supported by `BigInt` , thus calculating Fibonacci(500)==1394232245616978801397243828704072839500702565876973072641089629483255716228632906915576 seemingly correct.
You may use `filter` and `formatWith` as for other generators.

### Special Norwegian generators ###
These generators create data specific to Norwegian domains. They use Norwegian names, as they would be of limited value outside of Norway anyway.

#### Kjennemerker ####
No class, just an apply method without parameters.
Generates strings resembling Norwegian car license plates   2 uppercase letters (not I, M, O or Q) followed by 5 digits (use a TextWrapper to shorten them if you need).

#### Fnr ####
Generates legal "fodselsnummer", Norwegian "social security numbers" ([http://no.wikipedia.org/wiki/F%C3%B8dselsnummer](http://no.wikipedia.org/wiki/F%C3%B8dselsnummer)). These are strings of 11 digits:

1. Birth date -- "ddmmyy"
2. A random 3-digit ID code
3. 2 check digits (using two mod11 algorithms)

There are several rules pertaining to these numbers, some of which are supported by the generator.

* The date part usually starts with 01-31. However, temporary numbers called D-numbers are issued, they add 40 to the birth day (i.e. 41-71). Add a call to `withDnr(n)` to generate such numbers.   `n` is a percentage between 0 (default, no dnrs) and 100 (all D-numbers).
* If you only want certain dates, use the apply method or constructor with an `ExtendedGenerator[DateTime]` argument (e.g. a `Dates` generator).
* The 3-digit ID is given in intervals signifying century. This is not supported by the generator.
* The 3-digit ID is odd for men, even for women. Call `boysOnly` or `girlsOnly` if you are a sexist.
* The algorithm for the check numbers leads to not all IDs being valid. The generator ensures that all values are valid.

Apply methods:

* Standard no-args
* `Fnr(ExtendedGenerator[DateTime])`: (e.g. `Dates`) to govern which dates are produced. See the long sample at the end of the article.

#### Orgnr ####
Generates legal "organisasjonsnummer", Norwegian "organization numbers" ([http://www.brreg.no/samordning/organisasjonsnummeret.html](http://www.brreg.no/samordning/organisasjonsnummeret.html)). These are strings of 9 digits:
1. The first digit is 8 or 9
2. The last digit is a mod11 check digit

Apply methods: Standard no-args

#### Land ####
(Country generator.)
This generator (object) reads the supplied "land.txt" file containing country names in Norwegian spelling, and uses a `FromFile` to supply values.

#### Poststeder ####
(Postal code generator.)
This one is also based on a `FromFile` reading the supplied "postnr.txt" which contains Norwegian postal codes, formatted as "NNNN Ssss....", where NNNN is the 4-digit code, followed by a space, then the name.   There are 3 alternative invocations:

* `Poststeder`: returns full strings as described above
* `Poststeder.postnr`: returns the numeric code only
* `Poststeder.poststed`: returns the name only

Source: [http://www.bring.no/hele-bring/produkter-og-tjenester/brev-og-postreklame/andre-tjenester/postnummertabeller](http://www.bring.no/hele-bring/produkter-og-tjenester/brev-og-postreklame/andre-tjenester/postnummertabeller)

#### Kommuner ####
(County generator.)
Another one based on a `FromFile`, reading "kommuner.txt" which contains Norwegian county names, formatted as "NNNN Ssss....", where NNNN is a 4-digit code, followed by a space, then the name. There are 3 alternative invocations:

* `Kommuner`: returns full strings as described above
* `Kommuner.kommunenr`: returns the numeric code only
* `Kommuner.kommunenavn`: returns the name only

Source: [http://www.bring.no/hele-bring/produkter-og-tjenester/brev-og-postreklame/andre-tjenester/postnummertabeller](http://www.bring.no/hele-bring/produkter-og-tjenester/brev-og-postreklame/andre-tjenester/postnummertabeller)

#### NorskeNavn ####
(Generator for Norwegian names.)
First a bit about the background for this generator... Over the years, I have "scraped" lists of names from the net --  tax lists, participants in conferences and sports events etc, and tried to normalize and uniquify these lists (this bears the risk of having first and last names mixed up, and wrong capitalization...). Then I removed first and last names that only appeared once in any combination, to avoid generating names that identifies a single person. To these lists, I added the list from Norwegian SSB (Statistics Norway) of the most popular names, from these I have extracted the lists of names in "fornavn.txt" (a little short of 5000 first names) and "etternavn.txt" (about 8300 last names). The lists have no notion of gender, so you might well end up with names like "Ann Abdul Hansen"...

To generate names:

1. You start with the the apply method `NorskeNavn`
2. and may optionally add `forOgEtternavn` (default, both first and last names, creating names with 1 or 2 of each), `kunFornavn` (single first names only) or `kunEtternavn` (single last names only)
3. the standard `filter` and `formatWith` may also be used.

#### RareNavn ####
This is a simpler name generator, picking from a list of about 100 names. These names are meant to sound "funny", read the right way, they form other words or expressions, e.g. "Buster Minal" and "Mary Christmas"... :)

#### Adresser ####
A generator to create strings that look like Norwegian street addresses. It uses surnames (from `NorskeNavn.kunEtternavn`) and places (from `Poststeder.poststed`), and optionally a house number (sometimes with a letter suffix).
Note that the class itself does *not* implement a generator interface, but its `generator(withNumbers:Boolean)` returns a generator (as does the apply method).

Apply method: `Adresser(withNumbers: Boolean=true)`

Samples:

    Vogts gate 62
    Vossskroken 87C
    Cowards plass 105

## Record builders ##
Generating values is all well and fine, and you may want to use the previous generators in contexts of your own. But often, you will want to use the test data in another context. The generators that follow help you in building not values but data structures, and perhaps saving them to a file.

### Core classes ###
There are a few base classes that the other record generators are based on. The main concept is that you create a structure by adding a list of *fields* (the order in which they are added is normally important),  each field has a *name* (even for the few generators that do not use it) and a value generator, typically one of the generators above. The `gen`-methods for the record generators call the `genStrings`-method on each field's generator, and assembles records from the combined results.

The main class is the `abstract class DataRecordGenerator[T](nulls: NullHandler)` which implements `Generator[T]`. It contains the following methods:

* `add(fieldName: String, gen:Generator[_])`   adds a named field (as previously mentioned, the order in which you call `add` becomes the order of the fields).
* `add(DataField)`: specialized subclasses of `DataField` (see below) may need to be built outside the DataRecordGenerator and added "as is".
* `toFile` / `appendToFile`: these methods, if called, must be the last call on the record generator, because they return a `ToFile` (which see), not the generator itself, to allow the result to be saved to a file.


Subclasses may also use the protected variable `fields` , which is the generator list, as well as the utility method `fieldNames` which returns the ordered list of field names. And, most importantly, `protected def genRecords(recordNulls: NullHandler): Stream[DataRecord]`, a method that combines the input streams to create a stream of data records.

**`StringRecordGenerator`** is a subclass specialized for generating strings, with a notion of pre/suffixes, and an overridable `newline` method that defaults to the current platform line ending.

The `NullHandler` in the constructor is a sealed trait describing how `null` values in the input should be handled (some strategies may be less relevant for some record formats):

* `EmptyNull`: include the element as an empty string/element/..., e.g. `<foo/>` in an XML record.
* `SkipNull`: exclude empty fields entirely
* `KeepNull`: include null fields with an explicit "null" representation.

Each field (i.e. name+generator) is represented by a `case class DataField(name: String, generator: Generator[_])` or one of its subclasses. In addition to its constructor arguments, it contains the overridable methods

* `prefix` / `suffix`: how to add "something" before or after the field value
* `transform(String)`: how to transform the value from the generator's `genStrings` to the output string
* `genTuples(Int, NullHandler)`: the method that calls the generator and produces a stream of tuples in a `(name,value)` format.

A couple of subclasses are provided   `SingleQuoteWithEscapeDataField` and `DoubleQuoteWithEscapeDataField`, they encapsulate their values with single/double quotes, and escapes their respective quotes with a backslash. `DelimitedDataField` has the same kind of behaviour for a general delimiter.

### ToCsv ###
This generator produces values separated by a comma (or another delimiter, e.g. TAB), values are pre- and suffixed with a delimiter, by default a double quote. By default, the first record contains field names (which can be excluded). Access it through the apply method `ToCsv` with the following optional parameters

1. `withHeaders: Boolean=true`: include header record
2. `delimiter:String= "\""`: how to enclose each value
3. `separator:String= ","`: how to separate each field

The output would typically look like this:

    "id","userId","ssn","mail","active"
    "1","SSQH","23040852859","oviydeo@nvyebr.org","false"
    "2","RYZJ","14088638868","pwrdsi@rvyhjvimz.gov","false"
    "3","UODG","08039917611","uex.hshuka@anqfj.net","false"

### ToFixedWidth ###
This generator produces data in fixed width fields, where each value is padded with blanks (or truncated) to a fixed width. The inherited `add` method cannot be used, use `add(fieldName: String, gen: Generator[_], width: Int)` to add fields. The apply method is `ToFixedWidth(withHeaders: Boolean=true)` (as the output of a header record is optional).

Sample output:

    rec u  ssn
    HEADEFB17046606698
    VALUNE 01027711576

`skipNull` is not supported for this generator.

### XML ###
There are two different XML generators, and a generator for HTML. As opposed to other record generators, these differentiate between the `get` and `getStrings` methods, the latter pretty-prints the result to create a more readable layout.

#### ToXmlElements ####
The output from this generator is a set of data records, optionally enclosed in a root record. The apply method `ToXmlElements()` has 3 named parameters:

1. `rootName:String`: if this parameter has a value, a root record with that name is generated, enclosing the other records.
2. `recordName:String`: must be given, sets the name of the base element for each record
3. `nullHandler`: see above, default `EmptyNull`

The introductory example shows sample output from this.

#### ToXmlAttribute ####
Much like the previous, but the fields are represented as *attributes* on the (empty) data record, rather than enclosed elements. Parameters are like the previous.
Sample without root record and with empty nulls:

    <data homePage="" userId="RKGG" id="1" name="Gleihoy Tmfsmr" born=""></data>
    <data homePage="http://eeofau.net" userId="EALP" id="2" name="Jnnadfpnfbjjv Jsokovknm" born=""></data>
    <data homePage="https://jdje.net" userId="GBDB" id="3" name="Gmbgbsnmatmiij Kafkdyydk" born=""></data>

#### ToHtml ####
This one formats its output as an HTML table, optionally as a complete HTML document.
The apply methods has two optional parameters:

* `pageTitle:String`: if this parameter has a value, a complete HTML document with that title and heading is generated, enclosing the table.
* `nullHandler`: see above, default `EmptyNull`.
`EmptyNull` is handled with a `<td>&nbsp; </td>` cell, `SkipNull` should not be used.

Sample output:

    <html>
       <head>
          <title>Brukere</title>
       </head>
       <body>
          <h1>Brukere</h1>
          <table border="border">
             <tr>
                <th>id</th>
                <th>userId</th>
                <th>ssn</th>
                <th>born</th>
                <th>name</th>
                <th>homePage</th>
             </tr>
             <tr>
                <td>1</td>
                <td>XNAO</td>
                <td>05068636754</td>
                <td>1986-06-05</td>
                <td>Anna George Lund</td>
                <td> </td>
             </tr>
          </table>
       </body>
    </html>

### ToWiki ###
Another way to output a table is to use the wiki generator. No moving parts here, it simply generates markup syntax like this:

    || id || userId || ssn || mail || active ||
    | 1 | ZYFR | 27020785859 | ueaqefjn@iojuwy.no | X |
    | 2 | AIUS | 16021276441 | lgdnjli.luccdz@uogugujjq.gov | |
    | 3 | WRCB | 05086007810 | | X |

### ToJson ###
No prize for guessing the output format from this generator... There are two different add methods, the familiar `add` method, and a similar `addQuoted`, the latter should be used for any values that need double-quoted output (almost anything but ints, booleans and nested JSON; nulls are not quoted). The apply method has 3 parameters:

1. `header:String`: This is the label for each record (ignored if `bare`, see below)
2. `bare:Boolean=false`: This is intended for nesting JSON-generators. If you want to generate embedded records, use `bare=true` for the inner generators, e.g.:

        val addressGen= ToJson(bare=true).addQuoted("line1", ....)...
        val customerGen= ToJson(header="customer").add("address", addressGen) ...
        // => "customer": { "address": { "line1": ... }, ...}

3. `nulls: NullHandler= KeepNull`: as described above

**Note**: untested IRL!

### ToSql ###
Often, you will need to put test data into a data base. This generator tries to help you... It generates records of the form `insert into tableName (field1, field2,...) values (value1, value2, ...);`.

* To facilitate quoting, you must call the alternative add method for values that need quotes  they are then single quoted (and embedded single quotes escaped): `addQuoted(fieldName: String, gen: Generator[_])`
* The apply method needs to know the table name, you may optionally use a record separator different from ";":
`ToSql(tableName: String, exec: String=";")`
* There is a Sybase shortcut available, `ToSql.sybase(tableName)`, it sets the record separator to `\ngo`.

Sample output:

    insert into User (id, userId, born, name, mail)
    values (1, 'UGRY', 1951-11-25, 'Vgdfhpgyp Fvtniivskmjkeaaol', 'udkad@efrghssgn.gov');
    insert into User (id, userId, born, name, mail)
    values (2, 'HWTG', null, 'Tykpieydmkdnsir Lioesngyhfrvatbsbe', 'idboyjq@cakcrrrny.no');
    insert into User (id, userId, born, name, mail)
    values (3, 'RBWC', 1951-08-15, 'Kmdiklyumethyf Paabya', null);

### ToFile ###
This generator is typically the end of a chain, and called implicitly by either `toFile` or `appendToFile` from a record generator, but may also be called through the apply method

    apply[T](fileName:String,
             generator: Generator[T],
             append:Boolean=false,
             charSet:String="ISO-8859-1")

You may also add (one or more) calls to `prepend(String)`& `append(String)` to add text at beginning or the end of the file.

When `get` (or `getStrings`) is called (no "gen" stream methods here), values are obtained from the embedded generator, and written/appended to the named file.  That name may seem odd, so there is an alias available: `write(n:Int, strings:Boolean=true)`.

## Miscellaneous ##

### Percentage ###
This little trait provides a `hit(percent:Int)` method that randomly returns `true` in about `percent` % of its calls (the argument should be an int in the range 0..100). Used by `SomeNulls` and `Fnr`.

### RandomElem ###
contains `def randomFrom[T](l: Seq[T]): T` that returns a random element from its input sequence.

### StreamUtils ###
Contains methods to combine streams:

* `combine[A](list: Seq[Stream[A]]): Stream[Seq[A]]`: the input is a sequence of streams. The input is a stream of sequences, each output sequence contains one element from each input stream.  So if you feed it with 3 streams, providing As, Bs and Cs, respectively, the output is a stream of `Seq(A, B, C)`.
* `interleave[A](list: Seq[Stream[A]]): Stream[A]`: this is the flattened version of the previous, so in the example given, this will return a stream of `A, B, C, A, B, C, A, ...`
* `combineGens[A](list: Seq[Generator[A]]): Stream[Seq[A]]`: runs `combine` on the `gen`s from each generator.
* `combineStringGens[A](list: Seq[Generator[A]]): Stream[Seq[String]]`: runs `combine` on the `genString`s from each generator.

## "DSL-like" syntax
Recently, I have made some additions to make the configuration a bit more readable. Consider this *experimental*...  You may use the following constructs bye adding the `with DslLikeSyntax` trait:
| Write | To Get |
| ------|--------|
| from list (ls) | `FromList(ls)` -- `ls` is a list of arguments or a list |
| from file (name[,encoding]) | `FromFile(name, encoding)` |
| from stream (streamVar) | `FromStream(streamVar)` |
| from markovFile (file name(s)) | `Markov.apply(fs.toList)` |
| positive integers | `Ints() from 1` |
| positive longs, positive doubles | as above |
| negative integers/longs/doubles | as expected... |
| sequential integers | `Ints() from 1 sequential` |
| sequential longs/dates | you guessed it |
| integers | `Ints()` |
| doubles | `Doubles()` |
| booleans | `Booleans()` |
| chars | `Chars()` |
| characters | `Chars()` |
| strings | `Strings()` |
| randomStrings | Strings with length between 1 and 24 |
| nameLike | Name-like strings with 2-3 words |
| norskeNavn | `NorskeNavn()` |
| fornavn | Norwegian first names |
| etternavn | Norwegian last names |
| rareNavn | Funny names :) |
| letters | A-Z upper/lower |
| alphanumerics | 0-9, A-Z upper/lower |
| ascii | ASCII 32-126 |
| dates | `Dates()` |
| dateAndTime | `Dates().dateAndTime` |
| times | `Dates().timeOnly` |
| futureDates | Dates &gt; today |
| previousDates | Dates &lt; today |
| fixed(v) | `Fixed(v)` |
| just(v) | Same as fixed |
| cars | `CarMakes()` |
| creditCards | `CreditCards()` |
| visas | `CreditCards.visas` |
| masterCards | `CreditCards.masterCards` |
| fibonaccis | `Fibonaccis()` |
| guids | `Guids()` |
| mailAddresses | `MailAddresses()` |
| englishMarkov | `Markov.english()` |
| norskMarkov | `Markov.norwegian()` |
| urls | `Urls()` |
| adresser | `Adresser()` |
| fnr or fodselsnummer | `Fnr()` |
| fnrFromDates(dateGen) | `Fnr(dateGen)` |
| orgnr or orgnummer | `Orgnr()` |
| kjennemerker | `Kjennemerker()` |
| kommuner | `Kommuner()` |
| kommunenummer | `Kommuner.kommunenr()` |
| land | `Land()` |
| poststeder | `Poststeder()` |
| postnummer | `Poststeder.postnr()` |
| concatenate(generators | `FieldConcatenator(generators)` |
| concatenateWith(fieldSep)(generators) | `FieldConcatenator(fieldSep, generators)` |
| someNulls(percent, generator) | `SomeNulls(percent, generator)` |
| transformText(generator) | `TextWrapper(generator)` |
| substring(generator, from[, to])| `TextWrapper(generator).substring(from, to)` |
| twoFromFunction(generator)(function) | `TwoFromFunction(gen, genFun)` |
| twoWithPredicate(left, right)(predicate) | `TwoWithPredicate(left, right, predicate)` |
| uniqueWithFallback(primary, fallback) | `UniqueWithFallback(primary, fallback)` |
| weighted( (n, gen) ...) | `WeightedGenerator(...)` |
| toCsv(...) | `ToCsv(withHeaders, delimiter, separator)` |
| toFile(fileName, noOfRecords ...)(generator) | `ToFile(fileName, generator,...).write` |
| toFixedWidth(...): ToFixedWidth | `ToFixedWidth(...)` |
| toHtml(...) | `ToHtml(...)` |
| toJson(...) | `ToJson(...)` |
| toSql(tableName ...) | `ToSql(tableName ...)` |
| toWiki | `ToWiki()` |
| toXmlAttributes(...) | `ToXmlAttributes(...)` |
| toXmlElements(...) | `ToXmlElements(...)` |

## Extended example ##
For this last sample, we'll look at generation of data for several SQL tables. The data structure to fill looks like this:

    Address:
      Street
      Postal code (Norwegian: name & number)
    Customer
      ID
      SSN (Norwegian: fodselsnummer) (optional)
      Date of birth
      Name (Norwegian...)
      Address (embedded)
    Product
      ID
      Name
    Order
      ID
      State (one of 'Pending', 'Ready', 'Delivered, 'Closed')
      Customer (by ID)
      Order date (optional)
      Set of Order lines
    Order line
      Order (by ID)
      Line no. (sequential)
      Product (by ID)
      Info (optional, string)

The code:

    package no.mesan.testdatagen.generators.sample

    // Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

    import no.mesan.testdatagen.aggreg.SequenceOf
    import no.mesan.testdatagen.dsl.DslLikeSyntax
    import no.mesan.testdatagen.generators.misc.Names
    import no.mesan.testdatagen.generators.{Dates, FromList}
    import no.mesan.testdatagen.recordgen.ToSql

    import scala.language.postfixOps

    object LongerSample extends App with DslLikeSyntax {
      // These are the total number of records we will generate for the different categories
      val recordsBase= 100
      val orderFact= 2
      val productFact= 3
      val customerFact= 1
      val orderLineFact= orderFact*3

      // We generate one script for all data
      val resultFile= "orders.sql"

      // To be able to reuse values between records, we generate some values in advance.
      // specifically IDs (for foreign keys) and dates (for correlation between birth dates
      // and "fodselsnummer")
      val customerIds= from list ((sequential integers) get(customerFact * recordsBase))
      val birthDates= dates from (y=1921) to (y=1996) get(customerFact*recordsBase)
      val productIds= from list((sequential integers) to 100000 get(productFact*recordsBase))
      val orderIds: FromList[Int] = from list (sequential integers).get(orderFact * recordsBase) sequential
      val postSteder= from list (poststeder get(customerFact*recordsBase)) sequential

      // Populating the customer table - no dependencies
      val customerGenerator= ToSql(tableName="customer")
        .add("id", customerIds)
        .addQuoted("fnr", someNulls(percent=25, fnrFromDates(from list birthDates sequential)))
        .addQuoted("born", from list birthDates formatWith Dates.dateFormatter("yyyy-MM-dd") sequential)
        .addQuoted("name", uniqueWithFallback(rareNavn, norskeNavn)) // We want, for the test's sake, unique
                   // names. We try to get "funny names" from the RareNavn-generator, but add standard names
                   // from the NorskeNavn-generator when duplicates arise.
        .addQuoted("adr", adresser)
        .addQuoted("postnr", substring(postSteder, 0, 4))
        .addQuoted("poststed", substring(postSteder, 5))

      // and products - no dependencies either
      val productGenerator= toSql(tableName="product")
        .add("id", productIds)
        .addQuoted("name", weighted((60, Names(1)),
                                     (40, Names(2))))

      // Orders are connected to customers through customerIds
      val orderGenerator= toSql(tableName="order")
        .add("id", orderIds)
        .addQuoted("status", from list("Pending", "Ready", "Delivered", "Closed"))
        .add("customer", customerIds)
        .addQuoted("orderDate", dates from(y=2010) to(y=2013) format "yyyy-MM-dd")

      // And order_lines connected to orders and products
      val orderLineGenerator= toSql(tableName="order_line")
        .add("order", orderIds)
        .add("product", productIds)
        .add("lineNo", sequential integers)
        .addQuoted("info",
            someNulls(60,
                weighted((10, fixed("Restock")),
                         (5, fixed("Check!")),
                         (20,
                           transformText(
                             concatenate(fixed("Amount: "),
                               (positive doubles) from 1 to 300 format "%5.2f",
                               from list (" l", " kg", "", " m")))
                             trim))))

      // The generators are all set -- create result
      toFile(fileName=resultFile, noOfRecords = recordsBase) {
        SequenceOf().makeAbsolute().addWeighted(
            (customerFact, customerGenerator),
            (productFact, productGenerator),
            (orderFact, orderGenerator),
            (orderLineFact, orderLineGenerator))
      }
    }

The output looks like this (excerpt):

    insert into customer (id, fnr, born, name, adr, postnr, poststed)
    values (328069543, '03059613994', '1996-05-03', 'Rita Letter', 'Flottums vei 68A', '7435', 'Trondheim');
    insert into customer (id, fnr, born, name, adr, postnr, poststed)
    values (1325854638, '10035955203', '1959-03-10', 'Frank Lispiking', 'Furres vei 14', '4134', 'Jasenfjorden');
    insert into customer (id, fnr, born, name, adr, postnr, poststed)
    values (1732690652, null, '1940-07-10', 'Tomm Hendt', 'Strammensveien 75', '2820', 'Nordre Toten');
    insert into customer (id, fnr, born, name, adr, postnr, poststed)
    values (460134634, '23074791654', '1947-07-23', 'Mona Mee', 'Nordarnoysveien 10E', '6927', 'Ytroygrend');
    insert into customer (id, fnr, born, name, adr, postnr, poststed)
    values (1112030020, '11043457464', '1934-04-11', 'Kjell Erstuen', 'Venneslaskroken 24', '4884', 'Grimstad');
    ...
    insert into product (id, name) values (78471, 'Jhtb Iktaeyaihjrluso');
    insert into product (id, name) values (91325, 'Zsughfsy Bmpkfag');
    insert into product (id, name) values (76771, 'Uopal');
    insert into product (id, name) values (39087, 'Gbdnypsynojehsj');
    insert into product (id, name) values (24766, 'Kmjh Smyo');
    insert into product (id, name) values (93930, 'Kvjtdasoshbgonsvpl Otu');
    insert into product (id, name) values (60291, 'Ubhemiague');
    ...
    insert into order (id, status, customer, orderDate)
    values (673, 'Closed', 5327, '2011-08-10');
    insert into order (id, status, customer, orderDate)
    values (225, 'Ready', 318, '2012-05-30');
    insert into order (id, status, customer, orderDate)
    values (918, 'Pending', 2871, '2012-07-20');
    insert into order (id, status, customer, orderDate)
    values (734, 'Pending', 5468, '2010-03-31');
    insert into order (id, status, customer, orderDate)
    values (769, 'Closed', 3827, '2011-09-05');
    ...
    insert into order_line (order, product, lineNo, info)
    values (483, 49561, 11, 'Amount: 296,06');
    insert into order_line (order, product, lineNo, info)
    values (408, 24766, 12, null);
    insert into order_line (order, product, lineNo, info)
    values (297, 47355, 107, 'Restock');
    insert into order_line (order, product, lineNo, info)
    values (898, 18387, 14, null);
    insert into order_line (order, product, lineNo, info)
    values (703, 99956, 15, null);
    insert into order_line (order, product, lineNo, info)
    values (227, 33004, 16, null);
    insert into order_line (order, product, lineNo, info)
    values (319, 32357, 17, null);
    ...

## Notes ##

### History ###

**Ca 1992**: Created AWK scripts for simple SQL data generation.
**Ca 2011**: Started converting into Scala.
**2013**: Enhanced interface (sequential generation, complex generators etc).
**2014**: Introduced streams as primary mechanism. Introduced FlatSpec for tests, though not very idomatically. Tried to find an excuse to make it reactive, but it doesn't seem to match too good with the sequential nature of the problem...

### Caveats ###
Of the output formats, only SQL and to a certain extent CSV, has been used "in production". Try it carefully!

### TODO ###

1. FromFile   type checking does not work
2. Xml: nesting not available
3. Analyzing SQL DDL and/or domain classes to generate a skeleton for test data generators?  After all, that's where it all started...
4. Why does formatOne have to accept supertypes?

### LICENSE ###
This project, with all contained files, is covered by GNU GENERAL PUBLIC LICENSE, v2.  See the file LICENSE.txt for details.
