package enigma

sealed abstract class ReflectorWiring(val wiring: String)

sealed trait Reflector {
  val wiring: String

  val sequence: Map[Char, Char] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray.zip(wiring.toCharArray).toMap

  def encode(letter: Char): Char = sequence(letter)
}

object Reflector {
  case object B extends ReflectorWiring("YRUHQSLDPXNGOKMIEBFZCWVJAT") with Reflector
  case object C extends ReflectorWiring("FVPJIAOYEDRZXWGCTKUQSBNMHL") with Reflector
}
