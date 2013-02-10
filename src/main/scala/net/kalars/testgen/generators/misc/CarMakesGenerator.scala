package net.kalars.testgen.generators.misc

import net.kalars.testgen.ExtendedGenerator
import net.kalars.testgen.generators.ListGenerator

/**
 * Generate car makes.
 * Special methods: from/to/unique -- not supported
 * Default limits: 102 given makes
 */
object CarMakesGenerator {
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
  def apply(): ExtendedGenerator[String] = new ListGenerator[String]().fromList(makes)
}
