package no.mesan.testdatagen

/** Utilities for handling streams. */
trait StreamUtils {

  /**
   * Combine a list of streams -- return a stream of Seqs with one element
   * from each input stream.
   */
  def combine[A](list: List[Stream[A]]): Stream[Seq[A]]= {
    val listOfSeqs= list.map(_.map(Seq(_)))
    listOfSeqs.reduceLeft((stream1, stream2)=> stream1 zip stream2 map {
        case (seq1, seq2) => seq1 ++ seq2
      })
  }

  /**
   * Combine a list of generators, return a stream of Seq[A] with tuples from each generator.
   */
  def combineGens[A](list: List[Generator[A]]): Stream[Seq[A]] = combine(list.map(_.gen))

  /**
   * Combine a list of generators, return a stream of Seq[String] with tuples from each generator.
   */
  def combineStringGens(list: List[Generator[_]]): Stream[Seq[String]] = combine(list.map(_.genStrings))

    /**
   * Combine a list of streams -- return a stream of entries with the first element
   * from each input stream, then the second and so on.
   */
  def interleave[A](list: List[Stream[A]]): Stream[A]= combine(list).flatten
}
