package no.mesan.testdatagen

import java.util.regex.Pattern

object tst {
   val s= "\\e\\\\"                               //> s  : java.lang.String = \e\\
   s.replaceAll(Pattern.quote("\\"), "\\\\\\\\")  //> res0: java.lang.String = \\e\\\\
}