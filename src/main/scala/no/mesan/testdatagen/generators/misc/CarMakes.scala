package no.mesan.testdatagen.generators.misc

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.ExtendedGenerator
import no.mesan.testdatagen.generators.FromList

/**
 * This generator selects from a list of about a 100 manufacturers of cars,
 * motor cycles etc, like "Porsche" and "Toyota".

 * from/to are not supported
 */
object CarMakes {
  lazy val makes= List("Alfa Romeo", "Aston Martin", "Atlas", "Audi", "Austin",
     "Autobianchi", "Bedford", "Bentley", "Buddy", "Bugatti", "Buick",
     "Cadillac", "Chevrolet", "Chrysler", "Citroen", "Daewoo", "Daihatsu",
     "Daimler", "Datsun", "De Soto", "Delorean", "Deutz", "Dodge", "Ducati",
     "Fargo", "Ferguson", "Ferrari", "Fiat", "Ford", "Fordson", "GM",
     "Hillman", "Honda", "Hummer", "Hymer", "Hyundai", "Isuzu", "Jaguar",
     "Jeep", "John Deere", "Kaessbohrer", "Kawasaki", "Kewet", "Koenigsegg",
     "Lada", "Lamborghini", "Lambretta", "Lancia", "Land Rover", "Lexus",
     "Leyland", "Lotus", "Magirus Deutz", "Maserati", "Massey-Ferguson",
     "Maur", "Mazda", "McLaren", "Mercedes Benz", "Mercury", "MG",
     "Mitsubishi", "Morgan", "Morris", "Moskwitch", "Moto Guzzi",
     "Oldsmobile", "Opel", "Packard", "Peugeot", "Piaggio", "Plymouth",
     "Pobeda", "Pontiac", "Porsche", "Renault", "Rolls-Royce", "Rover",
     "Saab", "Scania", "Seat", "Setra", "Simca", "Skoda", "Ssangyong",
     "Steyr", "Studebaker", "Subaru", "Suzuki", "Talbot", "Tempo", "Tesla",
     "Toyota", "Trabant", "Triumph", "Unimog", "Vauxhall", "Volga",
     "Volkswagen", "Volvo", "Yamaha", "Zundapp")
  def apply(): ExtendedGenerator[String] = FromList(makes)
}
