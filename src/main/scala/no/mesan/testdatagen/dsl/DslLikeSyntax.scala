package no.mesan.testdatagen.dsl

// Copyright (C) 2015 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import scala.language.postfixOps

import no.mesan.testdatagen.aggreg._
import no.mesan.testdatagen.generators.norway._
import no.mesan.testdatagen.recordgen._
import no.mesan.testdatagen.{BareGenerator, Generator, ExtendedGenerator}
import no.mesan.testdatagen.generators._
import no.mesan.testdatagen.generators.misc._

import org.joda.time.DateTime

/** DSL-like building blocks. */
trait DslLikeSyntax {
  /** Prefix for integers/longs/doubles (>0). */
  def positive: Positive = new Positive
  /** Prefix for integers/longs/doubles (<0). */
  def negative: Negative = new Negative
  /** Prefix for integers/longs/doubles (sequences, default >0). */
  def sequential: Sequential = new Sequential
  /** Alias Ints(). */
  def integers: Ints = Ints()
  /** Alias Doubles(). */
  def doubles: Doubles = Doubles()
  /** Alias Booleans(). */
  def booleans= Booleans()
  /** Alias Chars(). */
  def chars: Chars = Chars()
  /** Alias Chars(). */
  def characters: Chars = chars
  /** Alias Strings. */
  def strings: Strings = Strings()
  /** Strings with length between 1 and 24. */
  def randomStrings: Strings= strings lengthBetween(1, 24)
  /** Name-like strings with 2-3 words. */
  def nameLike: Generator[String] = Names(2, 3)
  /** Norske for- og etternavn. */
  def norskeNavn: NorskeNavn = NorskeNavn()
  /** Norske fornavn. */
  def fornavn: NorskeNavn = norskeNavn kunFornavn
  /** Norske etternavn. */
  def etternavn: NorskeNavn = norskeNavn kunEtternavn
  /** Funny names :). */
  def rareNavn: ExtendedGenerator[String] = RareNavn()
  /** A-Z -- upper/lower -- add length(Between) as needed. */
  def letters: Strings = Strings.letters()
  /** 0-9/A-Z -- upper/lower -- add length(Between) as needed. */
  def alphanumerics: Strings = Strings.alphanum()
  /** ASCII 32-126 -- add length(Between) as needed. */
  def ascii= Strings.ascii()
  /** Alias Dates(). */
  def dates: Dates = Dates()
  /** Alias Dates().dateAndTime. */
  def dateAndTime: Dates = Dates().dateAndTime
  /** Alias Dates().timeOnly. */
  def times: Dates = Dates().timeOnly
  /** Dates > today. */
  def futureDates: Dates= Dates() from DateTime.now().plusDays(1)
  /** Dates < today. */
  def previousDates: Dates= Dates() to DateTime.now().minusDays(1)
  /** Alias Fixed(v). */
  def fixed[T](v: T): Fixed[T] = Fixed(v)
  /** Alias Fixed(v). */
  def just[T](v: T): Fixed[T] = fixed(v)
  /** Prefix for list/file/stream. */
  def from: FromBuilder = new FromBuilder
  /** Alias CarMakes(). */
  def cars: ExtendedGenerator[String] = CarMakes()
  /** Alias CreditCards(). */
  def creditCards: CreditCards = CreditCards()
  /** Alias CreditCards().visas. */
  def visas : CreditCards = CreditCards.visas
  /** Alias CreditCards().masterCards. */
  def masterCards: CreditCards= CreditCards.masterCards
  /** Alias Fibonaccis(). */
  def fibonaccis: Fibonaccis = Fibonaccis()
  /** Alias Guids(). */
  def guids: Guids = Guids()
  /** Alias MailAddresses(). */
  def mailAddresses: Generator[String] = MailAddresses()
  /** Alias Markov.english. */
  def englishMarkov: Markov = Markov.english()
  /** Alias Markov.norwegian. */
  def norskMarkov: Markov = Markov.norwegian()
  /** Alias Urls(). */
  def urls: Generator[String] = Urls()
  /** Alias Adresser(). */
  def adresser: Generator[String] = Adresser()
  /** Alias Fnr(). */
  def fnr: Fnr = Fnr()
  /** Alias Fnr(). */
  def fodselsnummer: Fnr = fnr
  /** Alias Fnr(g). */
  def fnrFromDates(g: ExtendedGenerator[DateTime]): Fnr = Fnr(g)
  /** Alias Orgnr(). */
  def orgnr: Orgnr = Orgnr()
  /** Alias Orgnr(). */
  def orgnummer: Orgnr = orgnr
  /** Alias Kjennemerker(). */
  def kjennemerker: Generator[String] = Kjennemerker()
  /** Alias Kommuner(). */
  def kommuner: ExtendedGenerator[String] = Kommuner()
  /** Alias Kommuner.kommunenr. */
  def kommunenummer: Generator[String] = Kommuner.kommunenr()
  /** Alias Land(). */
  def land: ExtendedGenerator[String] = Land()
  /** Alias Poststeder(). */
  def poststeder: ExtendedGenerator[String] = Poststeder()
  /** Alias Poststeder.postnummer. */
  def postnummer: Generator[String] = Poststeder.postnr()
  /** Alias FieldConcatenator().*/
  def concatenate(gs: Generator[Any]*): FieldConcatenator = FieldConcatenator(gs:_*)
  /** Alias FieldConcatenator().*/
  def concatenateWith(fieldSeparator: String)(gs: Generator[Any]*): FieldConcatenator =
    FieldConcatenator(fieldSeparator, gs:_*)
  /** Alias SomeNulls.*/
  def someNulls[T](percent:Int, generator: Generator[T]): SomeNulls[T] = SomeNulls(percent, generator)
  /** Alias TextWrapper.*/
  def transformText[T](generator: Generator[T]): TextWrapper = TextWrapper(generator)
  /** Alias TextWrapper.substring. */
  def substring(generator: Generator[_], from:Int, to:Int= -1)= TextWrapper(generator).substring(from, to)
  /** Alias TwoFromFunction.*/
  def twoFromFunction[T, U](gen: Generator[T])(genFun: T=>U): TwoFromFunction[T, U] =
    TwoFromFunction(gen, genFun)
  /** Alias TwoWithPredicate.*/
  def twoWithPredicate[T, U](left: Generator[T], right: Generator[U])(predicate: ((T,U))=>Boolean):
    TwoWithPredicate[T, U] = TwoWithPredicate(left, right, predicate)
  /** Alias UniqueWithFallback.*/
  def uniqueWithFallback[T](primary: Generator[T], fallback: Generator[T]): UniqueWithFallback[T]=
      UniqueWithFallback(primary, fallback)
  /** Alias WeightedGenerator.*/
  def weighted[T](weighted: (Int, Generator[T])*): WeightedGenerator[T] = WeightedGenerator(weighted:_*)
  /** Alias ToCsv.*/
  def toCsv(withHeaders: Boolean=true, delimiter:String= "\"", separator:String= ","): ToCsv =
    ToCsv(withHeaders, delimiter, separator)
  /** Alias ToFile.*/
  def toFile[T](fileName: String, noOfRecords: Int, append: Boolean=false, asStrings:Boolean= true,
                charSet: String=ToFile.defaultCharSet)(generator: BareGenerator[T]): List[Any] =
    ToFile(fileName, generator, append, charSet).write(noOfRecords, asStrings)
  /** Alias ToFixedWidth.*/
  def toFixedWidth(withHeaders: Boolean=true): ToFixedWidth = ToFixedWidth(withHeaders)
  /** Alias ToHtml.*/
  def toHtml(pageTitle: String="", nulls:NullHandler = EmptyNull): ToHtml = ToHtml(pageTitle, nulls)
  /** Alias ToJson.*/
  def toJson(header:String="", bare:Boolean=false, nulls: NullHandler= KeepNull): ToJson=
    ToJson(header, bare, nulls)
  /** Alias ToSql.*/
  def toSql(tableName: String, exec: String=";"): ToSql = ToSql(tableName, exec)
  /** Alias ToWiki.*/
  def toWiki= ToWiki()
  /** Alias ToXmlAttributes. */
  def toXmlAttributes(rootName: String="", recordName: String, nulls:NullHandler=EmptyNull): ToXmlAttributes =
      ToXmlAttributes(rootName, recordName, nulls)
  /** Alias ToXmlElements. */
  def toXmlElements(rootName: String="", recordName: String, nulls:NullHandler=EmptyNull): ToXmlElements =
      ToXmlElements(rootName, recordName, nulls)
}

/** Positive sequential number generators. */
class Sequential {
  /** Ints >0. */
  def integers: Ints = Ints() from 1 sequential
  /** Longs >0. */
  def longs: Longs= Longs() from 1 sequential
  /** Dates from today. */
  def dates: Dates = Dates() from DateTime.now().withTimeAtStartOfDay sequential
}

/** Positive number generators. */
class Positive {
  /** Ints >0. */
  def integers: Ints = Ints() from 1
  /** Doubles >0. */
  def doubles: Doubles = Doubles() from 0 filter(_>0)
  /** Longs >0. */
  def longs: Longs = Longs() from 1
}

/** Positive number generators. */
class Negative {
  /** Ints <0. */
  def integers: Ints = new Ints() to -1
  /** Doubles <0. */
  def doubles: Generator[Double]= Doubles.negative() filter(_<0.0D)
  /** Longs <0. */
  def longs= Longs.negative() filter(_<0)
}

/** Suffixes for "from". */
class FromBuilder {
  /** from list(l). */
  def list[T](l: List[T]): FromList[T] = FromList(l)
  /** from list(x,y,z). */
  def list[T](ls: T*): FromList[T] = list(ls.toList)
  /** from file(f). */
  def file(resourceName: String,  encoding: String= "UTF-8")= FromFile(resourceName, encoding)
  /** from stream(s). */
  def stream[T](inputStream: Stream[T]): FromStream[T] = FromStream(inputStream)
  /** from markovFile(a,b,c). */
  def markovFile(fs: String*): Markov = Markov.apply(fs.toList)
}
