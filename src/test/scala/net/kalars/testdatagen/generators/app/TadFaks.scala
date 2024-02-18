package net.kalars.testdatagen.generators.app

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import net.kalars.testdatagen.aggreg.{SequenceOf, SomeNulls, TwoFromFunction, TwoWithPredicate, UniqueWithFallback}
import net.kalars.testdatagen.generators.{Booleans, Dates, FromList, Ints, Strings}
import net.kalars.testdatagen.generators.misc.Names
import net.kalars.testdatagen.generators.norway.{NorskeNavn, RareNavn}
import net.kalars.testdatagen.recordgen.{ToFile, ToSql}
import scala.language.{postfixOps, existentials}

import org.joda.time.DateTime

object TadFaks extends App {
  val noOfRecs= 30
  val brukerFact= 8
  val brukerMynFact= 11
  val funcFact= 6
  val rolleFact= 2
  val mynFact= 2
  val startId=10000

  val appGruppeGen= FromList("TVIST", "TVINN", "FAKS", "TASS")
  val brukerNavnList= UniqueWithFallback(RareNavn(), NorskeNavn()).get(noOfRecs*brukerFact).sorted
  val brukerIdGen= Strings(4, 'A' to 'Z')
  val brukerIds= UniqueWithFallback[String](
     TwoFromFunction(FromList(brukerNavnList).sequential, {
       s:String=> (s + brukerIdGen).toUpperCase.filter(c=> c>='A' && c<='Z').toString.substring(0,4)
     }).asListGens(noOfRecs*brukerFact)._2,
     brukerIdGen) get(noOfRecs*brukerFact)
  val brukerGenIDs= startId to (startId-1+brukerFact * noOfRecs) toList
  val funkIdList= startId to (startId-1+funcFact*noOfRecs) toList
  val (funkGenIds, eierFunkGenIds)=
    TwoFromFunction(FromList(funkIdList).sequential, { id:Int=> if (id==10000) null else {
      var n= 0
      do { n= FromList(funkIdList).get(1).head } while (n>=id)
      n
    }}).asListGens(funcFact*noOfRecs)

  val rolleGenIDs= 1000 to 1000-1+(rolleFact * noOfRecs) toList
  val myndighetIDs= 1000 to 1000-1+(mynFact * noOfRecs) toList
  val funkTyper= FromList("Dialog", "WS", "Prosess", "Batch", "App", "Annen")
  val myndighetTyper= FromList("Tollregion", "Tollsted", "Kontrollsted")
  val bools= Booleans("0", "1")
  val datoGen= Dates().from(y=2000).to(y=2015).dateAndTime.format("yyyy.MM.dd hh:mm:ss")

  val (fraDatoGen, tilDatoGenBase)= TwoWithPredicate(Dates().from(y = 2010).to(y = 2020).format("yyyy.MM.dd"),
    { (t:(DateTime, DateTime)) => (t._2 isAfter t._1) && t._1.getYear<=2014})
    .asFormattedListGens(brukerMynFact*noOfRecs)
  val tilDatoGen= SomeNulls(10, tilDatoGenBase sequential)

  val gBruker= ToSql.sybase("Bruker").
    add("genId", FromList(brukerGenIDs) sequential).
    addQuoted("brukerID", FromList(brukerIds).sequential).
    addQuoted("fulltNavn", FromList(brukerNavnList).sequential).
    add("aktiv", bools).
    addQuoted("datoEndret", datoGen)

  val gBrukerAttr= ToSql.sybase("BrukerAttributt").
    add("genId", Ints(from=1000).sequential).
    add("bruker_genId", FromList(brukerGenIDs)).
    addQuoted("nokkel", Names(1)).
    addQuoted("verdi", Names(1)).
    addQuoted("datoEndret", datoGen)

  val gBrukerMyn= ToSql.sybase("BrukerMyndighet").
    add("genId", Ints(from=100).sequential).
    add("bruker_genId", FromList(brukerGenIDs)).
    add("myndighet_genId", FromList(myndighetIDs)).
    addQuoted("fra", fraDatoGen.sequential).
    addQuoted("til", tilDatoGen).
    addQuoted("datoEndret", datoGen)

  val gBrukerRolle= ToSql.sybase("BrukerRolle").
    add("genId", Ints(from=startId).sequential).
    addQuoted("datoEndret", datoGen).
    add("bruker_genId", FromList(brukerGenIDs)).
    add("rolle_genId", FromList(rolleGenIDs))

  val gEndrLogg= ToSql.sybase("FaksEndringslogg").
    add("genId", Ints(from=1000).sequential).
    addQuoted("brukerId", FromList(brukerIds)).
    addQuoted("datoTid", datoGen).
    addQuoted("kategori", Strings().lengthBetween(10, 40).chars(Strings.asciiUpperLower)).
    addQuoted("nokkel", Names(1)).
    addQuoted("gammelVerdi", Names(1, 3)).
    addQuoted("nyVerdi", Names(1, 3))

  val gFunk= ToSql.sybase("Funksjon").
    add("genId", funkGenIds ).
    addQuoted("funksjonsType", funkTyper).
    addQuoted("funksjonsNavn", Names(1)).
    add("aktiv", bools).
    add("eier_genId", SomeNulls(70, eierFunkGenIds)).
    addQuoted("datoEndret", datoGen)

  val gMyn= ToSql.sybase("Myndighet").
    add("genId", FromList(myndighetIDs).sequential).
    add("myndighetId", Ints(from=5000).sequential).
    addQuoted("myndighetNr", Strings().lengthBetween(2, 8).chars(Strings.digits)).
    addQuoted("myndighetNavn", Names(1, 3)).
    addQuoted("myndighetType", myndighetTyper).
    addQuoted("datoEndret", datoGen)

  val gRolle= ToSql.sybase("Rolle").
    add("genId", FromList(rolleGenIDs).sequential).
    addQuoted("rolleNavn", Names(1,2)).
    addQuoted("applikasjonsGruppe", appGruppeGen).
    addQuoted("datoEndret", datoGen)

  val gRolleAttr= ToSql.sybase("RolleAttributt").
    add("genId", Ints(from=1).sequential).
    add("rolle_genId", FromList(rolleGenIDs)).
    addQuoted("nokkel", Names(1)).
    addQuoted("verdi", Names(1))

  val gTilgang= ToSql.sybase("Tilgang").
    add("genId", Ints(from=startId).sequential).
    addQuoted("endretAv", FromList(brukerIds)).
    add("rettighet", Ints() from 0 to 3).
    addQuoted("datoEndret", datoGen).
    add("funksjon_genId", FromList(funkIdList)).
    add("rolle_genId", FromList(rolleGenIDs))

  val allGens= SequenceOf().makeAbsolute().
     addWeighted((funcFact,  gFunk),
               (rolleFact,  gRolle),
               (brukerFact,  gBruker),
               (mynFact,  gMyn),
               (9,  gBrukerAttr),
               (10, gBrukerRolle),
               (brukerMynFact, gBrukerMyn),
               (12, gEndrLogg),
               (3, gRolleAttr),
               (10, gTilgang))

  val sql= ToFile("faks.sql", allGens)
    .prepend("/* FAKS testdata */")
    .prepend(s"""|delete from Tilgang where genId>=$startId
                |go
                |delete from RolleAttributt where genId>=$startId
                |go
                |delete from FaksEndringslogg where genId>=1000
                |go
                |delete from BrukerMyndighet  where genId>=100
                |go
                |delete from BrukerAttributt  where genId>=1000
                |go
                |delete from Myndighet where genId>=1000
                |go
                |delete from Bruker where genId>=$startId
                |go
                |delete from Rolle where genId>=1000
                |go
                |delete from Funksjon where genId>=$startId
                |go""".stripMargin)
    .get(noOfRecs)
}