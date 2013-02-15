package no.mesan.testdatagen

object tst {
  val cardNo= 4066765001135426L // 4946560002988683L // 5401230301239296L
                                                  //> cardNo  : Long = 4066765001135426
    def digits(n:Long) = n.toString.map { _.toString.toInt }
                                                  //> digits: (n: Long)scala.collection.immutable.IndexedSeq[Int]
    def sum(s: Seq[Int]) = s.foldLeft(0)(_+_)     //> sum: (s: Seq[Int])Int
    def flip(n:Int) = if (n==2) 1 else 2          //> flip: (n: Int)Int
    def forAll(accum: Int, factor:Int, d:List[Int]): Int = d match {
       case Nil => accum
       case _ => forAll(accum + sum(digits(factor*d.head)), flip(factor), d.tail)
    }                                             //> forAll: (accum: Int, factor: Int, d: List[Int])Int
    val chk= forAll(0, 2, digits(cardNo/10L).reverse.toList)%10
                                                  //> chk  : Int = 4
    val res= if (chk==0) 0 else 10-chk            //> res  : Int = 6
    val valid= res == cardNo%10                   //> valid  : Boolean = true
  digits(cardNo/10L)                              //> res0: scala.collection.immutable.IndexedSeq[Int] = Vector(4, 0, 6, 6, 7, 6, 
                                                  //| 5, 0, 0, 1, 1, 3, 5, 4, 2)
    
}